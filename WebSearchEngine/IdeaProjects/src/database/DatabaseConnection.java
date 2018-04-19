package database;

import java.sql.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class DatabaseConnection {

    Connection connection = null;
    Statement statement = null;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/WebSearchEngine", "postgres", "vv@2813");
        return connection;
    }

    public boolean restartOrNot() throws SQLException, ClassNotFoundException {
        boolean restart = false;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (connection != null) {
                String documents = "SELECT url FROM documents";
                preparedStatement = connection.prepareStatement(documents);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    restart = true;
                }
            }
        } catch (Exception e) {
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return restart;
    }

    public int storeCrawlerQueue(String url, int docid) throws ClassNotFoundException, SQLException {
        Boolean flag = false;
        int tdocid = 0;
        DatabaseConnection st = new DatabaseConnection();
        connection = st.getConnection();
        ResultSet resultSet = null;
        PreparedStatement ps1 = null, ps2 = null;
        try {
            if (connection != null) {
                String storesql = "insert into crawler_queue(docid, url,visited) values( ? , ? , ? )";
                ps1 = connection.prepareStatement(storesql);
                String checksql = "SELECT docid,url FROM crawler_queue WHERE url = ?";
                ps2 = connection.prepareStatement(checksql);
                ps2.setString(1, url);
                resultSet = ps2.executeQuery();
                //Set flag true if same URL present
                while (resultSet.next()) {
                    flag = url.equals(resultSet.getString(2));
                    if (flag == true) {
                        //Get the docid of the URL if already present
                        tdocid = resultSet.getInt(1);
                        break;
                    }

                }
                if (!flag) {
                    ps1.setInt(1, docid);
                    ps1.setString(2, url);
                    ps1.setBoolean(3, false);
                    ps1.execute();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (ps1 != null) {
                ps1.close();
            }
            if (ps2 != null) {
                ps2.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        if (flag) {
            return tdocid;
        } else {
            return docid;
        }
    }
    public void updateCrawlerQueue(String url, int docid) throws ClassNotFoundException, SQLException {
        DatabaseConnection st = new DatabaseConnection();
        connection = st.getConnection();
        ResultSet resultSet = null;
        PreparedStatement ps1 = null;
        try {
            if (connection != null) {
                String storesql = "update crawler_queue set visited = ? where docid = ?";
                ps1 = connection.prepareStatement(storesql);
                ps1.setBoolean(1, true);
                ps1.setInt(2, docid);
                ps1.execute();
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (ps1 != null) {
                ps1.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void updateDocumentsLength() throws ClassNotFoundException, SQLException {
        DatabaseConnection st = new DatabaseConnection();
        connection = st.getConnection();
        try {
            if (connection != null) {
                String sqlQuery = "UPDATE documents SET length=(SELECT COUNT(*) FROM features  WHERE features.docid=documents.docid)";
                statement = connection.createStatement();
                statement.executeUpdate(sqlQuery);
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

    public boolean hasParsedlink(String url) throws ClassNotFoundException, SQLException {
        boolean hasParsed = false;
        DatabaseConnection con = new DatabaseConnection();
        connection = con.getConnection();
        ResultSet resultSet = null;
        PreparedStatement ps1 = null;
        try {
            if (connection != null) {
                String storesql = "select * from documents where url = ?";
                ps1 = connection.prepareStatement(storesql);
                ps1.setString(1, url);
                resultSet = ps1.executeQuery();
                while (resultSet.next()) {
                    hasParsed = true;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (ps1 != null) {
                ps1.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return hasParsed;
    }

    public ArrayList<String> getNotParsedurls() throws ClassNotFoundException, SQLException {
        ArrayList<String> notParsedURLs = new ArrayList<String>();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (connection != null) {
                String allurls = "Select url from crawler_queue where visited='false'";
                preparedStatement = connection.prepareStatement(allurls);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    notParsedURLs.add(resultSet.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return notParsedURLs;
    }
    
    public int receiveDocumentId() throws ClassNotFoundException, SQLException {
        
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DatabaseConnection a = new DatabaseConnection();
        connection = a.getConnection();
        int documentId = 0;
        try {
            if (connection != null) {
                String maxDocumentId = "SELECT MAX(docid) FROM documents";
                preparedStatement = connection.prepareStatement(maxDocumentId);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    documentId = resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return documentId;
    }

}
