
package crawler;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class Bm25Compute {
    static Connection connection = null;
    static Statement statement = null;
    static final double Bm25_k = 1.5;
    static final double Bm25_b = 0.75;
    
    public static void main(final String[] args) throws Exception {
        idfBM25Compute();
        bm25ScoreCompute();
        bm25PageRankScoreCompute();
         
     }
    
    // Method to compute inverse document frequency
    public static void idfBM25Compute() throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        //gets the db connection
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement1 = null;
        try {
            if (connection != null) {
                // Computation of idf and storing in features table of DB
                System.out.println("Computing idf--BM25");
                String score = "UPDATE features set idf_bm25_score = log(((SELECT count(distinct docid) FROM documents)-doc_frequency+0.5)/(doc_frequency+0.5))";
                statement = connection.createStatement();
                statement.executeUpdate(score);
                System.out.println("idf--BM25 computation COMPLETED and UPDATED");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (preparedStatement1 != null) {
                preparedStatement1.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public static void bm25ScoreCompute() throws ClassNotFoundException, SQLException {
        DatabaseConnection st = new DatabaseConnection();
        connection = st.getConnection();
        try {
            if (connection != null) {
                System.out.println("computing bm25 score...");
                String sqlQuery = "UPDATE features SET bm25_score=(idf_bm25_score*(term_frequency*("+Bm25_k+"+1))/(term_frequency+"+Bm25_k+"*(1-"+Bm25_b+"+"+Bm25_b+"*(SELECT length FROM documents WHERE features.docid=documents.docid)/(SELECT avg(length) FROM documents))))";
                statement = connection.createStatement();
                statement.executeUpdate(sqlQuery);
                System.out.println("bm25 score computation COMPLETED");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public static void bm25PageRankScoreCompute() throws ClassNotFoundException, SQLException {
        DatabaseConnection st = new DatabaseConnection();
        connection = st.getConnection();
        try {
            if (connection != null) {
                System.out.println("computing bm25-Pagerank score...");
                String sqlQuery = "UPDATE features set bm25_pagerank=((0.75*bm25_score)+(0.25*page_rank))";
                statement = connection.createStatement();
                statement.executeUpdate(sqlQuery);
                System.out.println("bm25-Pagerank score computation COMPLETED");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
