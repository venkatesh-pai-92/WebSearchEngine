/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import crawler.Crawler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.postgresql.util.Base64;
import org.w3c.tidy.Tidy;

/**
 * @author Venkatesh
 */
public class StoreInformation {
    Connection connection = null;
    Statement statement = null;

    public void saveInFeatures(HashMap<String, Integer> stemmedWords, int documentId, String websiteUrl, String language) throws ClassNotFoundException, SQLException {
        Boolean isUrlPresent = false;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        ResultSet resultSet = null;

        connection = databaseConnection.getConnection();
        try {
            if (connection != null) {
                String insertQuery = "INSERT INTO features(docid, term ,term_frequency,doc_frequency,idf_score,tf_idf_score,crawl_date,language) VALUES( ? , ? , ? , ? , ? , ? , ?, ?)";
                preparedStatement = connection.prepareStatement(insertQuery);

                String selectURL = "SELECT url,crawl_date FROM documents WHERE url = ?";
                preparedStatement1 = connection.prepareStatement(selectURL);
                preparedStatement1.setString(1, websiteUrl);
                resultSet = preparedStatement1.executeQuery();
                //Logic to stop adding same websiteUrl into the tables by checking websiteUrl and corresponding crawled date
                java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
                while (resultSet.next()) {
                    //Set isUrlPresent true if same websiteUrl present
                    isUrlPresent = (websiteUrl.equals(resultSet.getString(1)) && sqlDate.equals(resultSet.getDate(2)));
                    if (isUrlPresent = true) {
                        break;
                    }
                }
                //If isUrlPresent is false insert the websiteUrl into table
                if (!isUrlPresent) {
                    for (String word : stemmedWords.keySet()) {
                        preparedStatement.setInt(1, documentId);
                        preparedStatement.setString(2, word);
                        preparedStatement.setDouble(3, 1 + Math.log(stemmedWords.get(word))); // Absolute Term Frequency= 1+log(Term_count)
                        preparedStatement.setInt(4, 0);
                        preparedStatement.setDouble(5, 0.0);
                        preparedStatement.setDouble(6, 0.0);
                        preparedStatement.setDate(7, sqlDate);
                        preparedStatement.setString(8, language);
                        preparedStatement.executeUpdate();
                    }

                }
            }
        } catch (Exception e) {
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void saveInDocuments(String websiteUrl, int documentId, String snippetInformation) throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement preparedStatement3 = null;
        PreparedStatement preparedStatement4 = null;
        ResultSet resultSet = null;
        ResultSet resultSet1 = null;
        Boolean isUrlPresent = false;
        String language = null;
        try {
            if (connection != null) {
                String storesql = "INSERT INTO documents(docid, url ,crawl_date,language,snippet_information) VALUES( ? , ? , ? , ?, ?)";
                String checksql = "SELECT docid,url,crawl_date FROM documents WHERE url = ?";
                String languagesql = "SELECT language FROM features WHERE docid = ? LIMIT 1";
                preparedStatement1 = connection.prepareStatement(storesql);
                java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
                preparedStatement2 = connection.prepareStatement(checksql);
                preparedStatement2.setString(1, websiteUrl);
                resultSet = preparedStatement2.executeQuery();
                //Logic to stop adding same websiteUrl into the tables
                while (resultSet.next()) {
                    //Set isUrlPresent true if same websiteUrl present
                    isUrlPresent = websiteUrl.equals(resultSet.getString(2));
                    if (isUrlPresent = true) {
                        break;
                    }
                }
                preparedStatement4 = connection.prepareStatement(languagesql);
                preparedStatement4.setInt(1, documentId);
                resultSet1 = preparedStatement4.executeQuery();
                while (resultSet1.next()) {
                    language = resultSet1.getString(1);
                }
                //If isUrlPresent true update the same websiteUrl with updated crawled date
                if (isUrlPresent) {
                    String updatesql = "UPDATE documents SET crawl_date=? WHERE url=?";
                    preparedStatement3 = connection.prepareStatement(updatesql);
                    preparedStatement3.setDate(1, sqlDate);
                    preparedStatement3.setString(2, websiteUrl);
                    preparedStatement3.executeUpdate();
                } //If isUrlPresent is false insert into document tables
                else {
                    preparedStatement1.setInt(1, documentId);
                    preparedStatement1.setString(2, websiteUrl);
                    preparedStatement1.setDate(3, sqlDate);
                    preparedStatement1.setString(4, language);
                    preparedStatement1.setString(5, snippetInformation);
                    preparedStatement1.execute();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            if (preparedStatement1 != null) {
                preparedStatement1.close();
            }
            if (preparedStatement2 != null) {
                preparedStatement2.close();
            }
            if (preparedStatement3 != null) {
                preparedStatement3.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void saveInLinks(int fromDocumentId, int toDocumentId) throws ClassNotFoundException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        connection = databaseConnection.getConnection();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement1 = null, preparedStatement2 = null;
        try {
            String storeIds = "insert into links(from_docid,to_docid) values(? , ? )";
            String isThereQuery = "select * from links where from_docid = ? and to_docid = ?";
            preparedStatement2 = connection.prepareStatement(isThereQuery);
            preparedStatement2.setInt(1, fromDocumentId);
            preparedStatement2.setInt(2, toDocumentId);
            resultSet = preparedStatement2.executeQuery();
            if (!resultSet.next()) {
                preparedStatement1 = connection.prepareStatement(storeIds);
                preparedStatement1.setInt(1, fromDocumentId);
                preparedStatement1.setInt(2, toDocumentId);
                preparedStatement1.executeUpdate();
                preparedStatement1.addBatch();
            }

        } catch (Exception e) {
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

    //saves metadata
    public void saveMetaData(String html, int documentId) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement1 = null;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        String descriptionTag = "<\\s*meta\\s*name\\s*=\\s*\"description\"\\s*content\\s*=\\s*\"(.*)\"\\s*/>";
        String keywordsTag = "<\\s*meta\\s*name\\s*=\\s*\"keywords\"\\s*content\\s*=\\s*\"(.*)\"\\s*/>";
        String authorTag = "<\\s*meta\\s*name\\s*=\\s*\"author\"\\s*content\\s*=\\s*\"(.*)\"\\s*>";

        Pattern patternDescription = Pattern.compile(descriptionTag);
        Matcher matcherDescription = patternDescription.matcher(html);
        String description = "";
        if (matcherDescription.find()) {
            description = matcherDescription.group(1);
        }
        Pattern patternKeywords = Pattern.compile(keywordsTag);
        Matcher matcherKeywords = patternKeywords.matcher(html);
        String keywords = "";
        if (matcherKeywords.find()) {
            keywords = matcherKeywords.group(1);
        }

        Pattern patternAuthor = Pattern.compile(authorTag);
        Matcher matcherAuthor = patternAuthor.matcher(html);
        String author = "";

        if (matcherAuthor.find()) {
            author = matcherAuthor.group(1);
        }

        Pattern titlePattern = Pattern.compile("<\\s*title\\s*>(.*)<\\s*/title\\s*>");
        Matcher matcherTitle = titlePattern.matcher(html);
        String title = "";
        if (matcherTitle.find()) {
            title = matcherTitle.group(1);
        }
        connection = databaseConnection.getConnection();
        try {

            String updateSql = "UPDATE documents SET author = ?, title = ? , description = ?, keywords = ? WHERE docid = ?";
            preparedStatement1 = connection.prepareStatement(updateSql);
            preparedStatement1.setString(1, author);
            preparedStatement1.setString(2, title);
            preparedStatement1.setString(3, description);
            preparedStatement1.setString(4, keywords);
            preparedStatement1.setInt(5, documentId);
            preparedStatement1.executeUpdate();
        } catch (Exception e) {
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

    public void saveImages(String websiteUrl, int documentId) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement1 = null;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        connection = databaseConnection.getConnection();
        try {
            if (connection != null) {
                String storeImageSql = "INSERT INTO images VALUES(?,?,?,?,?,?,?)";
                InputStream input = new URL(websiteUrl).openStream();
                Tidy tidy=new Tidy();
                tidy= Crawler.setJtidyParameters(tidy);
                Document document = (Document) tidy.parseDOM(input, null);
                NodeList nodeList = document.getElementsByTagName("img");
                for (int index = 0; index < nodeList.getLength(); index++) {
                    Node node = nodeList.item(index);
                    String src = node.getAttributes().getNamedItem("src").getNodeValue();
                    String alt = "";
                    if (node.getAttributes().getNamedItem("alt") != null) {
                        alt = node.getAttributes().getNamedItem("alt").getNodeValue();
                    }

                    URL currentURL = new URL(websiteUrl);
                    URL imgUrl = new URL(currentURL, src);
                    URLConnection urlConnection = imgUrl.openConnection();

                    preparedStatement1 = connection.prepareStatement(storeImageSql);
                    preparedStatement1.setInt(1, documentId);
                    preparedStatement1.setString(2, imgUrl.toString());
                    preparedStatement1.setString(3, alt);
                    preparedStatement1.setInt(4, index);
                    preparedStatement1.setInt(5, -1);

                    InputStream is = urlConnection.getInputStream();

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] bytes = buffer.toByteArray();

                    String imageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
                    if (imageType != null && imageType.equalsIgnoreCase("application/xml"))
                        imageType = "image/svg+xml";
                    preparedStatement1.setString(6, imageType);
                    preparedStatement1.setString(7, Base64.encodeBytes(bytes));
                    preparedStatement1.execute();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String updateImagePosition(String html, int documentId) {
        PreparedStatement preparedStatement1 = null;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try {
            connection = databaseConnection.getConnection();
            int offset = 0;
            String updatePositionSql = "UPDATE images SET position = ? WHERE docid = ? AND page_index = ?";
            String notImagesRegex = "<\\s*img[^>]*>";
            Pattern pattern = Pattern.compile(notImagesRegex);
            Matcher matcher = pattern.matcher(html);
            int index = 0;
            while (matcher.find()) {
                int position = matcher.start() - offset;
                offset += matcher.end() - matcher.start();
                preparedStatement1 = connection.prepareStatement(updatePositionSql);
                preparedStatement1.setInt(1, position);
                preparedStatement1.setInt(2, documentId);
                preparedStatement1.setInt(3, index);
                preparedStatement1.execute();
                index++;
            }
            matcher.reset();
            html = matcher.replaceAll(" ");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return html;
    }

    public void insertIntoDictionary(String[] words,String language) throws  SQLException,ClassNotFoundException{
        DatabaseConnection databaseConnection = new DatabaseConnection();
        PreparedStatement preparedStatement = null;
        connection=databaseConnection.getConnection();
        if(connection!=null){
            try{
                String sql="INSERT INTO dictionary (term,language)" +
                        "SELECT ?, ? " +
                        "WHERE NOT EXISTS (SELECT term from dictionary where term = ?)";
                for(int index=0;index<words.length;index++){
                    preparedStatement=connection.prepareStatement(sql);
                    preparedStatement.setString(1,words[index]);
                    preparedStatement.setString(2,language);
                    preparedStatement.setString(3,words[index]);
                    preparedStatement.execute();
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        }
    }
}
