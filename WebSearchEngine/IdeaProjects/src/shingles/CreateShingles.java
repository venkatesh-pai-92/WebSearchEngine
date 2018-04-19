package shingles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import database.DatabaseConnection;
import org.apache.commons.lang3.StringUtils;


public class CreateShingles {

    static Queue<String> shingling_queue;
    Connection connnection;
    static DatabaseConnection databaseConnection = new DatabaseConnection();

    public static void createShinglesForAllDocuments() throws ClassNotFoundException {
        try {
            System.out.println("Calculating shingles for all documents");
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement1 = null, preparedStatement2 = null, preparedStatement3 = null;
            ResultSet resultSet1 = null, resultSet2 = null;
            if (connection != null) {
                String getDocIdSql = "SELECT docid FROM documents ORDER BY docid ;";
                String insertShingleSql = "INSERT INTO shingles(docid,shingle,hash) VALUES(?,?,?);";
                String getSnippetSql = "SELECT snippet_information FROM documents WHERE docid=?;";

                preparedStatement1 = connection.prepareStatement(getDocIdSql);

                resultSet1 = preparedStatement1.executeQuery();

                while (resultSet1.next()) {
                    shingling_queue = new LinkedList<>();
                    preparedStatement2 = connection.prepareStatement(insertShingleSql);
                    preparedStatement3 = connection.prepareStatement(getSnippetSql);
                    preparedStatement3.setInt(1, resultSet1.getInt(1));

                    resultSet2 = preparedStatement3.executeQuery();
                    if (resultSet2.next()) {
                        String snippet_information = resultSet2.getString(1);
                        Scanner sc = new Scanner(snippet_information);
                        while (sc.hasNext()) {
                            if (shingling_queue.size() < 4) {
                                shingling_queue.add(sc.next());
                            }
                            if (shingling_queue.size() == 4) {
                                String shingle = StringUtils.join(shingling_queue, ";");
                                preparedStatement2.setInt(1, resultSet1.getInt(1));
                                preparedStatement2.setString(2, shingle);
                                preparedStatement2.setInt(3, shingle.hashCode());
                                preparedStatement2.addBatch();
                                shingling_queue.poll();
                            }
                        }
                        preparedStatement2.executeBatch();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void createFunctionSimilarDocuments() throws SQLException, ClassNotFoundException {
        Connection connection = databaseConnection.getConnection();
        if (connection != null) {
            String createFunctionSql = "CREATE OR REPLACE FUNCTION similarDocuments(integer, double precision) RETURNS " +
                    "TABLE(docid int, jaccard double precision) LANGUAGE SQL AS $$  SELECT document2 AS docid, jaccard FROM jaccards WHERE document1 = $1 AND jaccard >= $2 $$";
            connection.createStatement().execute(createFunctionSql);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException,SQLException {
        //createShinglesForAllDocuments();
        //calculateJaccard();
        createFunctionSimilarDocuments();
    }
}
