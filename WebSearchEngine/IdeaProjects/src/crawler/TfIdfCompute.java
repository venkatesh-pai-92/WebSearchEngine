package crawler;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TfIdfCompute {
    Connection connection = null;
    Statement statement = null;
    
    // Method to compute the frequency of the document
    public void docFrequencyCompute() throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        //gets the db connection
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement1 = null;
        try {
            if (connection != null) {
                //Store document frequency in features table of DB
                String docfrequency = "UPDATE features AS ft set doc_frequency=(SELECT count(*) FROM features WHERE term=ft.term)";
                preparedStatement1 = connection.prepareStatement(docfrequency);
                preparedStatement1.executeUpdate();
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
    
    // Method to compute inverse document frequency
    public void idfCompute() throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        //gets the db connection
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement1 = null;
        try {
            if (connection != null) {
                // Computation of idf and storing in features table of DB
                String idfScore = "UPDATE features ft set idf_score = (log(u.N) - log(ft.doc_frequency)) FROM (SELECT count(distinct docid) AS N FROM documents) u,features f2 WHERE ft.docid= f2.docid AND ft.term=f2.term";
                preparedStatement1 = connection.prepareStatement(idfScore);
                preparedStatement1.executeUpdate();
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
    
     
    // Method to compute tfIdf 
      public void tfIdfCompute() throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        //gets the db connection
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement1 = null;
        try {
            if (connection != null) {
                //Computation tfidf and storing in features table
                String tfIdfScore = "UPDATE features SET tf_idf_score = (idf_score)*(term_frequency)";
                preparedStatement1 = connection.prepareStatement(tfIdfScore);
                preparedStatement1.executeUpdate();
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
    
}
