
package servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import database.DatabaseConnection;
import indexer.Dictionary;
import indexer.Stemmer;


public class LevenShtein {

    static DatabaseConnection db = new DatabaseConnection();
    static Connection conn = null;
    static Statement stmt = null;



    public static HashMap<String, Integer> getDictionaySuggestions(List<String> splitQueryString) throws ClassNotFoundException, SQLException {
        conn = db.getConnection();
        stmt = conn.createStatement();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String createExtensionSql = "Create extension if not exists  fuzzystrmatch;";
        stmt.executeUpdate(createExtensionSql);
        HashMap<String, Integer> dictionarSuggession = new HashMap<String, Integer>();
        for (String word : splitQueryString) {
            String sql = "SELECT DISTINCT term, levenshtein (term,'" + word + "') AS distance FROM dictionary WHERE levenshtein (term, '" + word + "') <=2 ORDER BY distance  LIMIT 1;";

            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String term = resultSet.getString(1);
                int distance = resultSet.getInt(2);
                dictionarSuggession.put(term, distance);
            }
        }
        for (Map.Entry<String, Integer> sug : dictionarSuggession.entrySet()) {
            System.out.println("term: " + sug.getKey() + "  distance: " + sug.getValue());
        }
        return dictionarSuggession;
    }

    public static HashMap<String, Integer> getDatabaseSuggestions(List<String> splitQueryString, String language) throws ClassNotFoundException, SQLException {
        conn = db.getConnection();

        PreparedStatement preparedStatement1 = null;
        ResultSet resultSet1 = null;
        HashMap<String, Integer> suggession1 = new HashMap<String, Integer>();
        //----------------------------------
        Stemmer stemmerObject = new Stemmer();
        List<String> queryWordList = new ArrayList<>();

        String stemmedWord = null;
        for (String queryWord : splitQueryString) {
            if (language == "en") {
                if (queryWord.length() != 1 && !"".equals(queryWord)) {
                    char[] charArray = queryWord.toCharArray();
                    stemmerObject.add(charArray, queryWord.length());
                    //Does the stemming
                    stemmerObject.stem();
                    stemmedWord = stemmerObject.toString();
                    queryWordList.add(stemmedWord);
                }
            } else {
                queryWordList.add(queryWord);
            }
        }
        //-----------------------------------------
        for (String word : queryWordList) {
            String sql1 = "SELECT DISTINCT term, levenshtein (term,'" + word + "') AS distance FROM features WHERE levenshtein (term, '" + word + "') <=2 ORDER BY distance  LIMIT 1;";

            preparedStatement1 = conn.prepareStatement(sql1);
            resultSet1 = preparedStatement1.executeQuery();
            while (resultSet1.next()) {
                String term = resultSet1.getString(1);
                int distance = resultSet1.getInt(2);
                suggession1.put(term, distance);
            }
        }
        for (Map.Entry<String, Integer> sug : suggession1.entrySet()) {
            System.out.println("term: " + sug.getKey() + "  distance: " + sug.getValue());
        }
        return suggession1;
    }



}
