
package queryProcessing;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import indexer.Stemmer;
import snippet.CreateSnippet;
import snippet.OutputSnippet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static queryProcessing.Main.databaseConnection;



public class QueryFunctions {

    Connection conn = null;

    public JSONObject CreateJSON(ResultSet resultSet, int queryType, QueryElements queryElements ) throws SQLException, ClassNotFoundException {
        conn = databaseConnection.getConnection();
        //root JSON Object {}
        JSONObject rootObject = new JSONObject();
        //resultList
        JSONArray resultListArray = new JSONArray();
        // QueryInfo
        JSONArray queryArray = new JSONArray();
        //stat
        JSONArray statArray = new JSONArray();
        //cw
        JSONObject cwObject = new JSONObject();

        int rank = 1;
        String snippetInformation="";
        //computing the result List array
        while (resultSet.next() && rank <= queryElements.maxResults) {
            JSONObject resultListObject = new JSONObject();
            resultListObject.put("rank", rank);
            resultListObject.put("url", resultSet.getString(2));
            resultListObject.put("score", resultSet.getFloat(3));
            snippetInformation = resultSet.getString(4);
            OutputSnippet outputSnippet = CreateSnippet.createSnippetText(snippetInformation,queryElements.unstemmedTerms);
            outputSnippet.snippetText = outputSnippet.snippetText.replaceAll("(?<=.{"+800+"})\\b.*", "...");
            //putting the resultListObject to resultListArray
            resultListObject.put("snippet", outputSnippet.snippetText);
            String missingText=StringUtils.join(outputSnippet.missingTerms, ",");
            resultListObject.put("missing", missingText);
            resultListArray.add(resultListObject);
            rank++;
        }

        for (String term : queryElements.unstemmedTerms) {
            String query = "SELECT COUNT(*) FROM features fs WHERE fs.term = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, term);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                JSONObject statObject = new JSONObject();
                statObject.put("term", term);
                statObject.put("df", resultset.getInt(1));
                statArray.add(statObject);
            }
        }



        //computing the queryInfo array
        JSONObject queryInfoObject = new JSONObject();
        queryInfoObject.put("k", queryElements.maxResults);
        queryInfoObject.put("query", queryElements.queryString);
        queryArray.add(queryInfoObject);
        //Putting all these arrays into root JSON Object
        rootObject.put("resultList", resultListArray);
        rootObject.put("query", queryArray);
        rootObject.put("stat", statArray);
        //cw
        String query = "SELECT SUM(length) FROM documents;";
        PreparedStatement preparedStatement1 = conn.prepareStatement(query);
        ResultSet resultset1 = preparedStatement1.executeQuery();
        while (resultset1.next()) {
            //Storing the collection frequency in JSONObject
            rootObject.put("cw", resultset1.getInt(1));
        }

        return rootObject;
    }

    public JSONObject CreateJSONResults(ResultSet resultSet, int queryType, QueryElements queryElements ) throws SQLException, ClassNotFoundException {
        conn = databaseConnection.getConnection();
        //root JSON Object {}
        JSONObject rootObject = new JSONObject();
        //resultList
        JSONArray resultListArray = new JSONArray();
        // QueryInfo
        JSONArray queryArray = new JSONArray();
        //stat
        JSONArray statArray = new JSONArray();
        //cw
        JSONObject cwObject = new JSONObject();

        int rank = 1;
        String snippetInformation="";
        //computing the result List array
        while (resultSet.next() && rank <= queryElements.maxResults) {
            JSONObject resultListObject = new JSONObject();
            resultListObject.put("rank", rank);
            resultListObject.put("url", resultSet.getString(2));
            resultListObject.put("score", resultSet.getFloat(3));
            resultListArray.add(resultListObject);
            rank++;
        }

        for (String term : queryElements.unstemmedTerms) {
            Stemmer stemmer = new Stemmer();
            String stemmedTerm = stemmer.stemString(term).trim();
            String query = "SELECT COUNT(*) FROM features fs WHERE fs.term = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, stemmedTerm);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                JSONObject statObject = new JSONObject();
                statObject.put("term", term);
                statObject.put("df", resultset.getInt(1));
                statArray.add(statObject);
            }
        }

        //computing the queryInfo array
        JSONObject queryInfoObject = new JSONObject();
        queryInfoObject.put("k", queryElements.maxResults);
        queryInfoObject.put("query", queryElements.queryString);
        queryArray.add(queryInfoObject);
        //Putting all these arrays into root JSON Object
        rootObject.put("resultList", resultListArray);
        rootObject.put("query", queryArray);
        rootObject.put("stat", statArray);
        //cw
        String query = "SELECT SUM(length) FROM documents;";
        PreparedStatement preparedStatement1 = conn.prepareStatement(query);
        ResultSet resultset1 = preparedStatement1.executeQuery();
        while (resultset1.next()) {
            //Storing the collection frequency in JSONObject
            rootObject.put("cw", resultset1.getInt(1));
        }

        return rootObject;
    }

    public List<String> doStemming(String query) {
        Stemmer stemmerObject = new Stemmer();
        List<String> queryWordList = new ArrayList<>();
        String stemmedWord = null;
        //Splits the query by spaces
        String[] queryWords = query.split(" ");
        for (String queryWord : queryWords) {
            if (queryWord.length() != 1 && !"".equals(queryWord)) {
                char[] charArray = queryWord.toCharArray();
                stemmerObject.add(charArray, queryWord.length());
                //Does the stemming
                stemmerObject.stem();
                stemmedWord = stemmerObject.toString();
                queryWordList.add(stemmedWord);
            }
        }
        return queryWordList;

    }

    //The conjunctive query buuilding and executing method and returns the resultset
    public ResultSet andQueryProcess(List<String> queryWordList) throws ClassNotFoundException, SQLException {
        //gets the db connection
        Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (connection != null) {
                //conjunctiveQuery string builder
                StringBuilder conjunctiveQuery = new StringBuilder("SELECT DISTINCT d.docid, d.url, SUM(f.tf_idf_score) as score FROM features f ,  documents d WHERE f.docid = d.docid  AND f.term in (");
                for (int index = 0; index < queryWordList.size(); index++) {
                    if (index > 0) {
                        conjunctiveQuery.append(",");
                    }
                    conjunctiveQuery.append("?");
                }
                conjunctiveQuery.append(")").append(" GROUP by d.docid,d.url HAVING count(f.term)=").append(queryWordList.size()).append(" ORDER by score desc");
                System.out.println("query:" + conjunctiveQuery.toString());
                preparedStatement = connection.prepareStatement(conjunctiveQuery.toString());
                //Inserting the values in the queryWordList into preparedStatement
                for (int index = 0; index < queryWordList.size(); index++) {
                    preparedStatement.setString(index + 1, queryWordList.get(index));
                }
                //Executes the query and obtains the resultset
                resultSet = preparedStatement.executeQuery();
            }

        } catch (Exception e) {
        }
        return resultSet;
    }

    //The disjunctive query buuilding and executing method and returns the resultset
    public ResultSet orQueryProcess(List<String> queryWordList) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = null;
        //gets the db connection
        Connection connection = databaseConnection.getConnection();
        ResultSet resultSet = null;
        try {
            if (connection != null) {
                //Disjunctive query processing
                StringBuilder disjunctiveQuery = new StringBuilder("SELECT distinct d.docid, d.url, SUM(f.tf_idf_score) as score from features f ,  documents d WHERE f.docid = d.docid  AND f.term in (");
                for (int i = 0; i < queryWordList.size(); i++) {
                    if (i > 0) {
                        disjunctiveQuery.append(",");
                    }
                    disjunctiveQuery.append("?");
                }
                disjunctiveQuery.append(") GROUP by d.docid,d.url  ORDER by score DESC;");
                System.out.println("query:" + disjunctiveQuery.toString());
                preparedStatement = connection.prepareStatement(disjunctiveQuery.toString());
                //Inserting the values in the queryWordList into preparedStatement
                for (int index = 0; index < queryWordList.size(); index++) {
                    preparedStatement.setString(index + 1, queryWordList.get(index));
                }
                //Executes the query and obtains the resultset
                resultSet = preparedStatement.executeQuery();

            }
        } catch (Exception e) {
        }
        return resultSet;
    }

}
