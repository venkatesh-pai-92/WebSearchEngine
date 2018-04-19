package jaccard;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class Jaccard {
    Connection connnection;
    static Statement statement = null;
    static DatabaseConnection databaseConnection = new DatabaseConnection();

    public static void calculateJaccard() throws SQLException, ClassNotFoundException {
        System.out.println("Calculates jaccard");
        Connection connection = databaseConnection.getConnection();
        if (connection != null) {
            String computeJaccardSql = "INSERT INTO jaccards SELECT document1, document2 , (CAST(intersectionTable AS double precision)/ unionTable) AS jaccard FROM " +
                    "(SELECT document1, document2, " +
                    "(SELECT COUNT(hash) FROM shingles WHERE shingles.docid = document1 OR shingles.docid = document2) AS unionTable," +
                    "(SELECT COUNT(s1.hash) FROM shingles s1, shingles s2  WHERE s1.docid = document1 AND s2.docid = document2 AND s1.hash = s2.hash) AS intersectionTable " +
                    "FROM documentPairs GROUP BY document1,document2) AS pairs";
            connection.createStatement().execute(computeJaccardSql);
        }
    }

    public static void createFunctionSimilarDocuments() throws SQLException, ClassNotFoundException {
        System.out.println("Creates User Defined function that Determines similarity between documents.");
        Connection connection = databaseConnection.getConnection();
        if (connection != null) {
            String createFunctionSql = "CREATE OR REPLACE FUNCTION similarDocuments(integer, double precision) RETURNS " +
                    "TABLE(docid int, jaccard double precision) LANGUAGE SQL AS $$  SELECT document2 AS docid, jaccard FROM jaccards WHERE document1 = $1 AND jaccard >= $2 $$";
            connection.createStatement().execute(createFunctionSql);
        }
    }

    public static void minHashing() throws SQLException, ClassNotFoundException {
        int[] n_values = new int[]{1, 4, 16, 32};
        System.out.println("");
        Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement1 = null, preparedStatement2 = null;
        if (connection != null) {
            try {
                String createFunctionSql = "CREATE OR REPLACE FUNCTION createMinShingles(integer) RETURNS TABLE(docid integer, hash integer) LANGUAGE SQL AS $$ " +
                        " SELECT  shingles.docid, V.hash AS minShingles  FROM shingles,(SELECT docid, hash , row_number() OVER (PARTITION BY docid ORDER BY hash )AS row_num FROM  shingles) V WHERE shingles.docid = V.docid  AND V.row_num <=$1  GROUP BY V.hash,shingles.docid ORDER BY shingles.docid  $$;";
                connection.createStatement().execute(createFunctionSql);

                for (int n : n_values) {
                    String updateJaccard_N_Sql = "Update jaccards Set jaccard" + n + "=jac.jaccard From" +
                            "(SELECT document1, document2 , (CAST(intersectionTable AS double precision)/ unionTable) AS jaccard FROM" +
                            "(SELECT document1, document2," +
                            "(SELECT COUNT(hash) FROM (Select * from createMinShingles(" + n + ")) as minShingle WHERE minShingle.docid = document1 OR minShingle.docid = document2) AS unionTable," +
                            "(SELECT COUNT(s1.hash) FROM (Select * from createMinShingles(" + n + ")) as s1, (Select * from createMinShingles(" + n + ")) as s2  WHERE s1.docid = document1 AND s2.docid = document2 AND s1.hash = s2.hash) AS intersectionTable" +
                            " FROM documentPairs GROUP BY document1,document2) AS pairs) as jac where jac.document1=jaccards.document1 AND jac.document2=jaccards.document2 AND jaccards.jaccard=jaccards.jaccard;";

                    preparedStatement1 = connection.prepareStatement(updateJaccard_N_Sql);
                    preparedStatement1.executeUpdate();

                    String computeReportSQL = "Insert into minHashReport SELECT '" + n + "' as n, AVG(absolute_error) AS average," +
                            "percentile_cont(0.5) WITHIN GROUP (ORDER BY absolute_error) As median," +
                            "percentile_cont(0.25) WITHIN GROUP (ORDER BY absolute_error) AS first_quartile," +
                            "percentile_cont(0.75) WITHIN GROUP (ORDER BY  absolute_error) AS third_quartile " +
                            "FROM ( SELECT abs(j.jaccard - j.jaccard" + n + ") AS absolute_error FROM jaccards j) as report ";
                    preparedStatement2 = connection.prepareStatement(computeReportSQL);
                    preparedStatement2.executeUpdate();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        calculateJaccard();
        minHashing();
    }

}
