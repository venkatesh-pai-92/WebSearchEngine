package queryProcessing;

import database.DatabaseConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Main class 
public class Main {

    static DatabaseConnection databaseConnection = new DatabaseConnection();
    
    

    public static void main(String[] args) throws Exception {
        ResultSet resultSet = null;
        List<String> queryWordList = new ArrayList<>();
        QueryFunctions queryFunctions = new QueryFunctions();
        Scanner scannerInput = new Scanner(System.in);
        String queryString = null;
        int maxResults = 0;
        int queryType=1;
        while (true) {
            
            
            System.out.println("\n Please enter the query string terms \n");

            // Read the input terms
            queryString = scannerInput.nextLine();
            if (scannerInput.hasNextLine()) {
                scannerInput.nextLine();
            }
            
            //Do Stemming of Input terms
            queryWordList = queryFunctions.doStemming(queryString);
            System.out.println("\n Please enter the number of documents you want to be accessed ");
            maxResults = scannerInput.nextInt();
            if (scannerInput.hasNextLine()) {
                scannerInput.nextLine();
            }
            System.out.println("Enter 'Con' for Conjunctive Query or 'Dis' for Disjunctive query");
            OUTER:
            while (true) {
                String type = scannerInput.nextLine().trim().toLowerCase();
                // Cases for the conj or disj query according to the user Input
                switch (type) {
                    case "con":
                        queryType=2;
                        resultSet = queryFunctions.andQueryProcess(queryWordList);
                        break OUTER;
                    case "dis":
                        queryType=1;
                        resultSet = queryFunctions.orQueryProcess(queryWordList);
                        break OUTER;
                    default:
                        System.out.println("Restriction!! Only conj or dis queries are allowed");
                        break;
                }
            }

            int rank = 1;
            System.out.println("Results:\n");
             while (resultSet.next() && rank <= maxResults) {
                System.out.printf("Rank: %-20d URL: %s \t\t tf_idf_score: %s\n", rank, resultSet.getString(2), resultSet.getString(3));
                rank++;
            }
        }
    }
}
