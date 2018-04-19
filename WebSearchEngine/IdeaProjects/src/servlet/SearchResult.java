package servlet;

import advertisement.AdvertisementResult;
import advertisement.AdvertisementSearch;
import database.DatabaseConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import imageSearch.ImageResult;
import imageSearch.ImageSearch;
import indexer.Stemmer;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import queryProcessing.QueryElements;
import queryProcessing.QueryProcessor;
import queryProcessing.QueryFunctions;

import javax.servlet.http.HttpSession;

import indexer.Dictionary;

// Servlet to return results in JSON format
public class SearchResult extends HttpServlet {

    HttpSession session;
    Connection conn = null;
    int score = 1;
    String view;
    DatabaseConnection databaseConnection = new DatabaseConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        QueryFunctions queryFunctions = new QueryFunctions();
        List<String> queryWordList = new ArrayList<>();
        List<String> quotedWordList = new ArrayList<>();
        String queryString = null;
        LevenShtein levenShtein = new LevenShtein();
        ResultSet resultSet = null;
        Boolean isAndQuery = false;
        Boolean isSiteQuery = false;
        JSONObject receivedJSON = null;
        int queryType = 1;
        PrintWriter output = response.getWriter();
        String language = "en";
        HashMap<String, Integer> dictionarySuggession = new HashMap<String, Integer>();
        HashMap<String, Integer> databaseSuggession = new HashMap<String, Integer>();
        QueryProcessor qp = new QueryProcessor();
        QueryElements queryElements = new QueryElements();
        List<ImageResult> imageResultList = new ArrayList<>();
        ImageSearch searchImage = new ImageSearch();
        boolean imageSearch = false;
        AdvertisementSearch adSearch= new AdvertisementSearch();
        try {
            //gets the query string from the input box of index.jsp page
            queryString = request.getParameter("query");
            queryString = queryString.replaceAll(",", "");
            int k = Integer.parseInt(request.getParameter("k"));
            score = Integer.parseInt(request.getParameter("score"));
            imageSearch = Boolean.parseBoolean(request.getParameter("imageSearch"));
            language = request.getParameter("language");
            queryElements = qp.processQuery(queryString, language);
            queryElements.maxResults = k;
            if (score == 1) {
                view = "features_tfidf";
            } else if (score == 2) {
                view = "features_bm25";
            } else if (score == 3) {
                view = "features_bm25_pagerank";
            }


            if (queryElements.quotedWordList.size() > 0)
                isAndQuery = true;

            if (isAndQuery) {
                resultSet = conjunctiveSearch(queryElements, language);
                if (!imageSearch) {
                    receivedJSON = queryFunctions.CreateJSON(resultSet, 2, queryElements);
                } else {
                    imageResultList = searchImage.search(queryElements.queryString, resultSet, queryElements.maxResults, language);
                }
            } else {
                resultSet = disjunctiveSearch(queryElements, language);
                if (!imageSearch) {
                    receivedJSON = queryFunctions.CreateJSON(resultSet, 1, queryElements);
                } else {
                    imageResultList = searchImage.search(queryElements.queryString, resultSet, queryElements.maxResults, language);
                }
            }


            //queryWordList = obtainQueryWords(queryString);
            ArrayList<String> englishWordsArray = Dictionary.getInstance().getEnglishWords();
            int englishCount = 0, germanCount = 0;
            dictionarySuggession = levenShtein.getDictionaySuggestions(queryElements.unstemmedTerms);
//            for (Map.Entry<String, Integer> sug : dictionarySuggession.entrySet()) {
//                if (englishWordsArray.contains(sug.getKey())) {
//                    englishCount++;
//                } else {
//                    germanCount++;
//                }
//            }
//            if (englishCount >= germanCount) {
//                language = "en";
//            } else {
//                language = "de";
//            }

//            databaseSuggession = levenShtein.getDatabaseSuggestions(queryElements.unstemmedTerms, language);
//
//            queryWordList = new ArrayList<>();
//            for (Map.Entry<String, Integer> sug : databaseSuggession.entrySet()) {
//                queryWordList.add(sug.getKey());
//            }

            List<AdvertisementResult> adResultList = adSearch.getAdvertisements(queryElements.unstemmedTerms);

            HttpSession session = request.getSession(true);
            session.setAttribute("k", queryElements.maxResults);
            session.setAttribute("score", score);
            session.setAttribute("language", language);
            session.setAttribute("dictionarySuggession", dictionarySuggession);
            session.setAttribute("queryString", queryElements.queryString);
            session.setAttribute("adResultList", adResultList);
            RequestDispatcher rd = null;
            if (!imageSearch) {
                session.setAttribute("jsonObject", receivedJSON);
                System.out.println(receivedJSON.toJSONString());
                rd = request.getRequestDispatcher("Search.jsp");
            } else {
                session.setAttribute("imageResultList", imageResultList);
                rd = request.getRequestDispatcher("ImageSearch.jsp");
            }

            rd.forward(request, response);

        } catch (ClassNotFoundException | SQLException e1) {
        }
    }

    protected void doPost(HttpServletRequest request1,
                          HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request1, response);
        //JSONObject json=request1.getAttribute("jsonString");
        //response.setContentType("text/html;charset=euc-kr");
        //session = request1.getSession(true);
        //session.setAttribute("jsonString", request1.getAttribute("jsonString"));
        //response.sendRedirect("Search.jsp");
    }

    public List<String> obtainQueryWords(String query) {
        QueryFunctions queryFunctions = new QueryFunctions();
        Pattern patern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = patern.matcher(query);
        while (matcher.find()) {
            query = query.replace(matcher.group(1), "");
            query = query.replace(" ", "");
        }
        query = query.replaceAll("\"", "");
        List<String> queryWordList = queryFunctions.doStemming(query);
        return queryWordList;
    }

    public ResultSet conjunctiveSearch(QueryElements queryElements, String language) throws ClassNotFoundException, SQLException {
        QueryProcessor queryProcessor = new QueryProcessor();
        QueryFunctions queryFunctions = new QueryFunctions();
        ResultSet resultSet = null;
        //gets the database connection
        conn = databaseConnection.getConnection();
        if (conn != null) {
            try {
                String sitesWhereClause = "";
                String quotedWhereClause = "";
                String synonymWhereClause = "";
                String completeWhereClause = "";
                if (queryElements.quotedWordList.size() > 0) {
                    for (String quotedWord : queryElements.quotedWordList) {
                        quotedWhereClause += "AND f.docid IN (SELECT docid FROM features WHERE term = '" + quotedWord + "') ";
                    }
                }
                if (queryElements.sites.size() > 0) {
                    for (String sites : queryElements.sites) {
                        sitesWhereClause += "AND d.url LIKE '%" + sites + "%' ";
                    }
                }
                if (queryElements.synonyms.size() > 0) {
                    for (String tildeWord : queryElements.synonyms) {
                        List<String> words = queryProcessor.deriveSynonyms(tildeWord, language);
                        List<String> synonyms = new ArrayList<>();
                        for (String word : words) {
                            synonyms.add(word);
                        }
                        synonymWhereClause += "AND f.term IN (";
                        for (int i = 0; i < synonyms.size(); i++) {
                            if (i > 0) {
                                synonymWhereClause += ",";
                            }
                            synonymWhereClause += "'" + synonyms.get(i) + "'";
                        }
                        synonymWhereClause += ") ";
                    }
                }
                completeWhereClause = quotedWhereClause + synonymWhereClause + sitesWhereClause;
                StringBuilder disjunctiveQuery = new StringBuilder("SELECT distinct d.docid, d.url, SUM(f.score) as score,d.snippet_information from " + view + " f ,  documents d WHERE f.docid = d.docid  AND f.term in (");
                for (int i = 0; i < queryElements.queryWordList.size(); i++) {
                    if (i > 0) {
                        disjunctiveQuery.append(",");
                    }
                    disjunctiveQuery.append("?");
                }
                disjunctiveQuery.append(") " + completeWhereClause + " AND language ='" + language + "' GROUP by d.docid,d.url,d.snippet_information  ORDER by score DESC;");
                PreparedStatement preparedStatement = null;
                preparedStatement = conn.prepareStatement(disjunctiveQuery.toString());

                //Inserting the values in the queryWordList into preparedStatement
                for (int index = 0; index < queryElements.queryWordList.size(); index++) {
                    preparedStatement.setString(index + 1, queryElements.queryWordList.get(index));
                }
                System.out.println("query: " + preparedStatement);
                resultSet = preparedStatement.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultSet;
    }

    public ResultSet disjunctiveSearch(QueryElements queryElements, String language) throws ClassNotFoundException, SQLException {
        QueryProcessor queryProcessor = new QueryProcessor();
        QueryFunctions queryFunctions = new QueryFunctions();
        ResultSet resultSet = null;
        Stemmer stemmer = new Stemmer();
        //gets the database connection
        conn = databaseConnection.getConnection();
        if (conn != null) {
            try {
                String sitesWhereClause = "";
                String quotedWhereClause = "";
                String synonymWhereClause = "";
                String completeWhereClause = "";
                String queryWhereClause = "";
                if (queryElements.quotedWordList.size() > 0) {
                    for (String quotedWord : queryElements.quotedWordList) {
                        quotedWhereClause += "AND f.docid IN (SELECT docid FROM features WHERE term = '" + quotedWord + "') ";
                    }
                }
                if (queryElements.sites.size() > 0) {
                    for (String sites : queryElements.sites) {
                        if(queryElements.synonyms.size()>0||queryElements.queryWordList.size()>0) {
                            sitesWhereClause += "AND d.url LIKE '%" + sites + "%' ";
                        }else{
                            sitesWhereClause += " d.url LIKE '%" + sites + "%' ";
                        }
                    }
                }
                if (queryElements.synonyms.size() > 0) {
                    for (String tildeWord : queryElements.synonyms) {
                        List<String> words = queryProcessor.deriveSynonyms(tildeWord, language);
                        List<String> synonyms = new ArrayList<>();
                        for (String word : words) {
//                            if (!queryElements.unstemmedTerms.contains(word)) {
//                                queryElements.unstemmedTerms.add(word);
//                            }
                            word = stemmer.stemString(word).trim();
                            synonyms.add(word);
                        }
                        if (queryElements.queryWordList.size() > 0) {
                            synonymWhereClause += "OR f.term IN (";
                        } else {
                            synonymWhereClause += " f.term IN (";
                        }
                        for (int i = 0; i < synonyms.size(); i++) {
                            if (i > 0) {
                                synonymWhereClause += ",";
                            }
                            synonymWhereClause += "'" + synonyms.get(i) + "'";
                        }
                        synonymWhereClause += ")";
                    }
                }
                if (queryElements.queryWordList.size() > 0) {
                    queryWhereClause += "f.term in (";
                    for (int i = 0; i < queryElements.queryWordList.size(); i++) {
                        if (i > 0) {
                            queryWhereClause += ",";
                        }
                        queryWhereClause += "?";
                    }
                    queryWhereClause+=") ";
                }
                completeWhereClause = queryWhereClause + quotedWhereClause + synonymWhereClause + sitesWhereClause;
                StringBuilder disjunctiveQuery = new StringBuilder("SELECT distinct d.docid, d.url, SUM(f.score) as score,d.snippet_information from " + view + " f ,  documents d WHERE f.docid = d.docid  AND (");
                disjunctiveQuery.append(" " + completeWhereClause + ") AND language ='" + language + "' GROUP by d.docid,d.url,d.snippet_information  ORDER by score DESC;");
                PreparedStatement preparedStatement = null;
                preparedStatement = conn.prepareStatement(disjunctiveQuery.toString());

                //Inserting the values in the queryWordList into preparedStatement
                for (int index = 0; index < queryElements.queryWordList.size(); index++) {
                    preparedStatement.setString(index + 1, queryElements.queryWordList.get(index));
                }
                System.out.println("query: " + preparedStatement);
                resultSet = preparedStatement.executeQuery();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultSet;
    }
}
