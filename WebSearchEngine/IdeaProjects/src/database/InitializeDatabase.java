
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import indexer.Dictionary;


public class InitializeDatabase {

    static Connection connection = null;
    static Statement statement = null;

    public static void main(final String[] args) throws Exception {
        createTables();
    }

    public static void createTables() throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        //gets the db connection
        connection = databaseConnection.getConnection();
        try {

            if (connection != null) {
                //Create table if not exists the table
                statement = connection.createStatement();
                String featuresTableSql = "CREATE TABLE IF NOT EXISTS features(docid integer,term character varying,term_frequency double precision,doc_frequency integer,idf_score double precision,idf_bm25_score double precision,tf_idf_score double precision,page_rank double precision,bm25_score double precision,bm25_pagerank double precision,crawl_date date,language character varying(2))";
                String documentsTableSql = "CREATE TABLE IF NOT EXISTS documents(docid integer,url character varying,crawl_date date,page_rank double precision,length integer,language character varying(2), author character varying, title character varying, description character varying, keywords character varying, snippet_information character varying)";
                String linksTableSql = "CREATE TABLE IF NOT EXISTS links(from_docid integer,to_docid integer)";
                String crawler_queueTableSql = "CREATE TABLE IF NOT EXISTS crawler_queue(docid integer,url character varying,visited boolean)";
                String dictionaryTableSql = "CREATE TABLE IF NOT EXISTS dictionary(termid serial,term character varying,language character varying(2))";
                String imagesTableSql = "CREATE TABLE IF NOT EXISTS images(docid integer, src character varying, alt character varying, page_index integer, position integer, type character varying, image character varying)";
                String jaccardsTableSql = "CREATE TABLE IF NOT EXISTS jaccards(document1 integer, document2 integer, jaccard double precision, jaccard1 double precision,jaccard4 double precision, jaccard16 double precision, jaccard32 double precision)";
                String shinglesTableSql = "CREATE TABLE IF NOT EXISTS shingles(docid integer, shingle character varying,  hash integer)";
                String minHashReportTableSql = "CREATE TABLE IF NOT EXISTS minHashReport(n integer,average double precision,median double precision,first_quartile double precision,third_quartile double precision)";
                String advertisementTableSql = "CREATE TABLE IF NOT EXISTS advertisement(ad_id serial,user_name character varying, n_grams character varying,ad_url character varying,ad_description character varying,ad_budget double precision, money_per_click double precision, ad_image_url character varying,ad_click_count integer DEFAULT 0,last_click_timestamp time);";
                String searchenginesTableSql = "CREATE TABLE IF NOT EXISTS searchengines(group_id integer,config_url character varying,active boolean);";
                String searchengine_featuresTableSql = "CREATE TABLE IF NOT EXISTS searchengines_term_statastics(term_id serial,group_id integer ,term character varying,term_score double precision);";

                String features_tfidfViewSql = "CREATE OR REPLACE VIEW features_tfidf AS SELECT docid,term,term_frequency,tf_idf_score AS score FROM features";
                String features_bm25ViewSql = "CREATE OR REPLACE VIEW features_bm25 AS SELECT docid,term,term_frequency,bm25_score AS score FROM features";
                String features_bm25_pagerankViewSql = "CREATE OR REPLACE VIEW features_bm25_pagerank AS SELECT docid,term,term_frequency,bm25_pagerank AS score FROM features";
                String documentPairsViewSql = "CREATE OR REPLACE VIEW documentPairs AS SELECT d1.docid AS document1, d2.docid AS document2 FROM documents d1 CROSS JOIN documents d2 WHERE d1.docid <> d2.docid";

                String features_termIndexSql = "CREATE INDEX features_term ON features(term)";
                String features_docidIndexSql = "CREATE INDEX features_docid ON features(docid)";
                String documents_docidIndexSql = "CREATE INDEX documents_docid ON documents(docid)";
                String documents_languageIndexSql = "CREATE INDEX documents_language ON documents(language)";
                String documents_urlIndexSql = "CREATE INDEX documents_url ON documents(url)";
                String shingles_hashIndexSql = "CREATE INDEX shingles_hash ON shingles(hash)";
                String dictionary_termIndexSql = "CREATE INDEX dictionary_term ON dictionary(term)";

                //Execute the corresponding the create table query
                statement.executeUpdate(featuresTableSql);
                System.out.println("features Table Created");
                statement.executeUpdate(documentsTableSql);
                System.out.println("documents Table Created");
                statement.executeUpdate(linksTableSql);
                System.out.println("links Table Created");
                statement.executeUpdate(crawler_queueTableSql);
                System.out.println("crawler_queue Table Created");
                statement.executeUpdate(dictionaryTableSql);
                System.out.println("Dictionary Table Created");
                statement.executeUpdate(imagesTableSql);
                System.out.println("images Table Created");
                statement.executeUpdate(jaccardsTableSql);
                System.out.println("jaccards Table Created");
                statement.executeUpdate(shinglesTableSql);
                System.out.println("shingles Table Created");
                statement.executeUpdate(minHashReportTableSql);
                System.out.println("minHashReport Table Created");
                statement.executeUpdate(advertisementTableSql);
                System.out.println("advertisement Table Created");
                statement.executeUpdate(searchenginesTableSql);
                System.out.println("searchengines Table Created");
                statement.executeUpdate(searchengine_featuresTableSql);
                System.out.println("searchengine_features Table Created");

                statement.executeUpdate(features_tfidfViewSql);
                System.out.println("features_tfidf View Created");
                statement.executeUpdate(features_bm25ViewSql);
                System.out.println("features_bm25 View Created");
                statement.executeUpdate(features_bm25_pagerankViewSql);
                System.out.println("features_bm25_pagerank View Created");
                statement.executeUpdate(documentPairsViewSql);
                System.out.println("documentPairs View Created");

                statement.executeUpdate(features_termIndexSql);
                System.out.println("features_termIndexSql Index Created");
                statement.executeUpdate(features_docidIndexSql);
                System.out.println("features_docidIndexSql Index Created");
                statement.executeUpdate(documents_docidIndexSql);
                System.out.println("documents_docidIndexSql Index Created");
                statement.executeUpdate(documents_languageIndexSql);
                System.out.println("documents_languageIndexSql Index Created");
                statement.executeUpdate(documents_urlIndexSql);
                System.out.println("documents_urlIndexSql Index Created");
                statement.executeUpdate(shingles_hashIndexSql);
                System.out.println("shingles_hashIndex Index Created");
                statement.executeUpdate(dictionary_termIndexSql);
                System.out.println("dictionary_term Index Created");

                addToDictionary();
            }
            statement.close();
            connection.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    private static void addToDictionary() throws ClassNotFoundException, SQLException {
        System.out.println("Adding to Dictionary...");
        DatabaseConnection databaseConnection = new DatabaseConnection();
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = null;
        ArrayList<String> englishWordsArray = Dictionary.getInstance().getEnglishWords();
        String insertQuery = "INSERT INTO dictionary(term, language) VALUES( ? , ?)";
        preparedStatement = connection.prepareStatement(insertQuery);
        for (String word : englishWordsArray) {
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, "en");
            preparedStatement.executeUpdate();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
        System.out.println("Added words to Dictionary...");
    }

    public void dropTables() throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        //gets the db connection
        connection = databaseConnection.getConnection();
        try {
            if (connection != null) {
                statement = connection.createStatement();
                String dropViewSql = "DROP VIEW IF EXISTS features_tfidf ; DROP VIEW IF EXISTS features_bm25;DROP VIEW IF EXISTS features_bm25_pagerank;DROP VIEW IF EXISTS documentPairs;";
                String dropTableSql = "DROP TABLE IF EXISTS features;DROP TABLE IF EXISTS documents;DROP TABLE IF EXISTS links;DROP TABLE IF EXISTS crawler_queue;DROP TABLE IF EXISTS images;DROP TABLE  IF EXISTS dictionary;DROP TABLE  IF EXISTS jaccards;DROP TABLE  IF EXISTS shingles;DROP TABLE IF EXISTS minHashReport;DROP TABLE IF EXISTS advertisement;DROP TABLE IF EXISTS searchengines;DROP TABLE IF EXISTS searchengines_term_statastics;";
                statement.executeUpdate(dropViewSql);
                System.out.println("Views Deleted");
                statement.executeUpdate(dropTableSql);
                System.out.println("Tables Deleted");
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
