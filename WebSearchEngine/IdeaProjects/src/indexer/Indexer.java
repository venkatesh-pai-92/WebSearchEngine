package indexer;

import database.DatabaseConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import database.StoreInformation;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringEscapeUtils;

public class Indexer {

    // Method performing the parsing of our URL.
    public boolean indexing(String URL, int docid) throws IOException, ClassNotFoundException, SQLException {
        StoreInformation storeInformation = new StoreInformation();
        String language = null;
        URL givenURL = new URL(URL);
        //Buffered object to read the content of URL from character input Stream
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(givenURL.openStream()));
        StringBuffer stringBuffer = new StringBuffer();
        //Read the contents and storing it in string
        String inputLine=null;
        while ((inputLine=bufferReader.readLine()) != null) {
            stringBuffer.append(inputLine+"\n");
        }
        String htmlText=stringBuffer.toString();
        // BUffered Reader session closes.
        bufferReader.close();

        //save images
        storeInformation.saveImages(URL,docid);

        //escape utils
        htmlText = StringEscapeUtils.unescapeHtml4(htmlText);
        Charset.forName("UTF-8").encode(htmlText);

        //save metadata
        storeInformation.saveMetaData(htmlText,docid);
        htmlText = storeInformation.updateImagePosition(htmlText,docid);

        // Remove the HTML tags of our stored content
        htmlText = removeHtmlTags(htmlText).toLowerCase();




        //String removedTagsHtml = removeHtmlTags(htmlText);

        // Our stored content will be split by spaces and then the words are stored in a string.
        String[] words = htmlText.split(" ");
        language = MultiLanguageSupport.detectLanguage(words);
        storeInformation.insertIntoDictionary(words,language);
        //Our words will be stemmed and stored in a Hashmap.
        HashMap<String, Integer> stemmedWords = doStemming(words, language);

        //Storing the resulting values in features table.
        storeInformation.saveInFeatures(stemmedWords, docid, URL, language);
        storeInformation.saveInDocuments(URL, docid, htmlText);


//        //jtidy clean
//        Document doc = null;
//
//        doc = jTidyClean(htmlText);
//        XPath xpath = XPathFactory.newInstance().newXPath();
        // Value returned is true if it is parsed properly.
        return true;
    }

    //Method for stemming of words
    public HashMap<String, Integer> doStemming(String[] words, String language) {
        ArrayList<String> stopWordsArray = new ArrayList<>();
        if (language == "en") {
            //Adding all the stopwords as Array List
            stopWordsArray.addAll(Arrays.asList(Dictionary.englishStopWordsList));
        } else {
            stopWordsArray.addAll(Arrays.asList(Dictionary.germanStopWordsList));
        }
        HashMap<String, Integer> stemmedWords = new HashMap<>();
        String stemmedword = "";

        //Initializaion of object for stemmer class
        Stemmer stemmerObject = new Stemmer();
        if (language == "en") {
            for (String word : words) {
                // If a word is not a stop word , then stemming of the word has to be continued
                if (!stopWordsArray.contains(word) && (word.length() != 1)) {
                    char[] w = word.toCharArray();
                    stemmerObject.add(w, word.length());
                    stemmerObject.stem();
                    stemmedword = stemmerObject.toString();
                }
                if (stemmedWords.containsKey(stemmedword)) {
                    int wordCount = stemmedWords.get(stemmedword) + 1;
                    stemmedWords.put(stemmedword, wordCount);
                } else {
                    stemmedWords.put(stemmedword, 1);
                }
            }

        } else {
            for (String word : words) {
                // If a word is not a stop word , then stemming of the word has to be continued
                if (!stopWordsArray.contains(word)) {
                    if (stemmedWords.containsKey(word)) {
                        int wordCount = stemmedWords.get(word) + 1;
                        stemmedWords.put(word, wordCount);
                    } else {
                        stemmedWords.put(word, 1);
                    }
                }
            }
        }
        return stemmedWords;
    }

    // Method to clean our content stored in HTML text using Jtidy
    private Document jTidyClean(String htmlText) throws UnsupportedEncodingException {
        // Create instance
        Tidy tidy = new Tidy();
        tidy.setMakeClean(true);
        tidy.setXHTML(true);
        tidy.setSmartIndent(true);
        Document doc = tidy.parseDOM(new ByteArrayInputStream(htmlText.getBytes()), null);
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //tidy.pprint(doc, outputStream);
        //StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append(new String(outputStream.toByteArray(), "UTF-8"));
        //String stringXHTML = stringBuilder.toString();
        return doc;
    }

    // Method to remove HTML tags from our stored content
    public String removeHtmlTags(String htmlText) {
        htmlText = htmlText.replaceAll("<(noscript|script|style)[^>]*>.*?[^<]*</(script|noscript|style)>", " ");
        //htmlText = htmlText.replaceAll("<style.*?</style>", " ");
        htmlText = htmlText.replaceAll("<[^>]*>", " ");
        htmlText = htmlText.replaceAll("[^a-zA-Z0-9]", " ");
        htmlText = htmlText.replaceAll("\\s+", " ");
        htmlText= htmlText.toLowerCase().trim();
        return htmlText;

    }

    public String removeHtmlTags1(String htmlText) {
        htmlText = htmlText.replaceAll("<(script|style)[^>]*>[^<]*</(script|style)>", " ")
                .replaceAll("(?!<\\s*img[^>]*>)<[^>]*>", " ")
                .replaceAll("(\\n{1,2})(\\s*\\n)+", "$1").replaceAll("\\.", " ").replaceAll("&nbsp", "")
                .replaceAll("[-+.^:,]", " ").replaceAll("\\s+", " ");
        return htmlText;
    }

}
