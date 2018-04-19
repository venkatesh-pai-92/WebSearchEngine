package metaSearch;

import database.DatabaseConnection;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Venkatesh on 02-02-2017.
 */
public class SearchEngine extends Thread {
    public int groupId;
    public String configUrl;
    public boolean isActive;
    public int cw;
    public double averageCw;
    public int c;
    public String queryUrl;
    public double searchEngineScore = 0.0;
    public double RminScore = 0.0;
    public double RmaxScore = 0.0;
    public double normalisedScore = 0.0;
    public List<TermStatistics> termStatisticsList;
    private static final int k = 10;
    private static final int score = 1;
    private static final double parameter = 0.7;
    public static final double b = 0.4;
    public JSONObject jsonObject;

    public List<SearchEngine> getActiveSearchEngines(List<String> terms) {
        List<SearchEngine> searchEngineList = new ArrayList<>();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        PreparedStatement preparedStatement1 = null, preparedStatement2 = null, preparedStatement3 = null;
        Connection connection = null;
        ResultSet resultSet1 = null, resultSet2 = null;
        try {
            // get the search engines according to sum of term statastics if they are present
            connection = databaseConnection.getConnection();
            String statSql = "SELECT t.group_id,SUM(term_score) as score,s.config_url,s.active from searchengines_term_statastics t, searchengines s where term in (";
            for (int i = 0; i < terms.size(); i++) {
                if (i > 0) {
                    statSql += ",";
                }
                statSql += "'" + terms.get(i) + "'";
            }
            statSql += ") ";
            statSql += "AND t.group_id in (SELECT DISTINCT group_id FROM searchengines WHERE active=true) and t.group_id=s.group_id group by t.group_id,s.config_url,s.active order by score desc;";
            String termSql = "SELECT ts.group_id,ts.term,ts.term_score FROM searchengines_term_statastics ts,searchengines s WHERE s.group_id=ts.group_id AND ts.group_id = ? AND ts.term = ? AND s.active=true;  ";
            preparedStatement1 = connection.prepareStatement(statSql);
            resultSet1 = preparedStatement1.executeQuery();
            while (resultSet1.next()) {
                SearchEngine se = new SearchEngine();
                se.groupId = resultSet1.getInt("group_id");
                se.configUrl = resultSet1.getString("config_url");
                se.isActive = resultSet1.getBoolean("active");
                //se.cw = resultSet1.getInt(4);
                se.queryUrl = se.configUrl + "json?query=" + StringUtils.join(terms, "+") + "&k=" + k + "&score=" + score;
                se.termStatisticsList = new ArrayList<>();
                for (String term : terms) {
                    preparedStatement2 = connection.prepareStatement(termSql);
                    preparedStatement2.setInt(1, se.groupId);
                    preparedStatement2.setString(2, term);
                    resultSet2 = preparedStatement2.executeQuery();
                    while (resultSet2.next()) {
                        TermStatistics ts = new TermStatistics();
                        ts.term = resultSet2.getString("term");
                        ts.termScore = resultSet2.getDouble("term_score");
                        ts.isScoreComputed = true;
                        se.termStatisticsList.add(ts);
                    }
                }
                searchEngineList.add(se);
            }
            //rounding off to nearest value. Multiplying by parameter 0.7
            int searchEngineCount = searchEngineList.size();
            searchEngineCount = ((Long) Math.round(searchEngineCount*parameter)).intValue();

            while(searchEngineList.size()>searchEngineCount){
                searchEngineList.remove(searchEngineList.size()-1);
            }
            //if term stats not available send to all the search engines
            if(searchEngineList.size() ==0){
                String sql = "SELECT * FROM searchengines WHERE active = true ORDER BY group_id;";
                preparedStatement3 = connection.prepareStatement(sql);
                resultSet1 = preparedStatement3.executeQuery();
                while (resultSet1.next()) {
                    SearchEngine se = new SearchEngine();
                    se.groupId = resultSet1.getInt("group_id");
                    se.configUrl = resultSet1.getString("config_url");
                    se.isActive = resultSet1.getBoolean("active");
                    //se.cw = resultSet1.getInt(4);
                    se.queryUrl = se.configUrl + "json?query=" + StringUtils.join(terms, "+") + "&k=" + k + "&score=" + score;
                    se.termStatisticsList = new ArrayList<>();
                    searchEngineList.add(se);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return searchEngineList;
    }

    public synchronized void start() {
        try {
            Scanner scanner = new Scanner(new URL(queryUrl).openStream(), "UTF-8").useDelimiter("\\A");
            String result;
            if (scanner.hasNext()) {
                result = scanner.next();
                jsonObject = (JSONObject) JSONValue.parse(result);
                JSONArray stat = (JSONArray) jsonObject.get("stat");
                Iterator<JSONObject> iterator = stat.iterator();
                while (iterator.hasNext()) {
                    JSONObject statObject = iterator.next();
                    String term = (String) statObject.get("term");
                    List<TermStatistics> tempList = termStatisticsList.stream()
                            .filter(item -> item.term.equals(term))
                            .collect(Collectors.toList());
                    if (tempList.size() == 0) {
                        int docFrequency = ((Long) statObject.get("df")).intValue();
                        TermStatistics termStatistics = new TermStatistics();
                        termStatistics.term = term;
                        termStatistics.docFrequency = docFrequency;
                        if (termStatistics.docFrequency > 0) {
                            termStatisticsList.add(termStatistics);
                        }
                    }
                }
                cw = ((Long) jsonObject.get("cw")).intValue();
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTermStatastics(int groupId, String term, double termScore) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try {
            connection = databaseConnection.getConnection();
            String sql = "INSERT INTO searchengines_term_statastics(group_id,term, term_score) VALUES (?,?,?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setString(2, term);
            preparedStatement.setDouble(3, termScore);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void computeScore() {
        for (TermStatistics stat : termStatisticsList) {
            double I = (Math.log((c + 0.5) / stat.cf) / Math.log(c + 1.0));
            if(!stat.isScoreComputed) {
                double T = (stat.docFrequency / (stat.docFrequency + 50 + 150 * cw / averageCw));
                stat.termScore = b + (1 - b) * T * I;
                searchEngineScore += stat.termScore;
                stat.isScoreComputed =true;
                //saving the term statastics
                saveTermStatastics(groupId, stat.term, stat.termScore);
            }
            searchEngineScore += stat.termScore;
            RminScore += b + (1 - b) * (0.0) * I;
            RmaxScore += b + (1 - b) * (1.0) * I;
            normalisedScore = (searchEngineScore - RminScore) / (RmaxScore - RminScore);
        }
    }
}
