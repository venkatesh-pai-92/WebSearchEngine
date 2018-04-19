package crawler;

// Import all the required packages in our JAVA File

import jaccard.Jaccard;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import database.DatabaseConnection;
import database.InitializeDatabase;
import database.StoreInformation;
import indexer.Indexer;
import shingles.CreateShingles;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {

// Creating a Queue for processing and storing
    static Queue<String> BigQueue = new LinkedList<String>();
    static List<String> parsed = new ArrayList<>();

    // Initialization of object for DatabaseConnection class and storeInformation class and tfidf class
    static DatabaseConnection databaseConnection = new DatabaseConnection();
    static StoreInformation storeInformation = new StoreInformation();
    static InitializeDatabase initializeDatabase = new InitializeDatabase();
    static TfIdfCompute tfIdfCompute = new TfIdfCompute();
    static PageRank pageRankCompute = new PageRank();
    static Bm25Compute bm25Compute = new Bm25Compute();
    static CreateShingles createShingles = new CreateShingles();
    static Jaccard jaccard = new Jaccard();

    // Initialization of depth and doc variables
    static int docid;
    static int depth = 0;
    static int depthMax = 3;
    //static int docMax = 60;

    // Declaration of Crawler main class
//    public static void main(final String[] args) throws Exception {
//        //crawl();
//    }
    public static void crawl(int docMax) throws ClassNotFoundException, SQLException {
        initializeDatabase.dropTables();
        initializeDatabase.createTables();
        // Crawler processing starts from the given URL
        String initialUrl = "http://dbis.informatik.uni-kl.de/index.php/en/";
        String travelUrl = "http://www.theexpeditioner.com/the-top-50-travel-blogs/";
        String tennisUrl = "http://theultimatetennisblog.com/";
        // Add the given URL to the created Queue
        BigQueue.add(initialUrl);
        BigQueue.add(travelUrl);
        BigQueue.add(tennisUrl);
        // Check the domain of the given URL
        final String hostDomain = domainCheck(initialUrl);
        docid = databaseConnection.receiveDocumentId() + 1;
        Boolean canLeave = false;
        Scanner in = new Scanner(System.in);

        // User input whether he wants to leave the domain or not
        System.out.println("Would you like to exit the domain yes/no:");
        Goto:
        while (true) {
            String answer = in.nextLine().trim().toLowerCase();
            switch (answer) {
                case "yes":
                    canLeave = true;
                    break Goto;
                case "no":
                    canLeave = false;
                    break Goto;
                default:
                    System.out.println("Yes or No is only allowed. Request you to try again");
                    break;
            }
        }

//        List<CrawlerThread> theadList=new ArrayList<CrawlerThread>();
//        for(int i=0;i<=5;i++){
//            CrawlerThread crawlerThread=new CrawlerThread();
//            crawlerThread.start();
//            try{
//                crawlerThread.sleep(200);
//            }catch(Exception e){
//            }
//        }
        final Boolean restart = databaseConnection.restartOrNot();
        if (restart) {

             // Since Database connection is restarting
            // clear everything stored in our queue
            BigQueue.clear();

            // All the unparesed Urls will be parsed by adding it to Queue
            ArrayList<String> notParsedUrls = databaseConnection.getNotParsedurls();
            BigQueue.addAll(notParsedUrls);
        }

        while (!BigQueue.isEmpty() && depth < depthMax && parsed.size() < docMax) {
            String mainURL = null;

            // If Database connection has to restart
            mainURL = BigQueue.poll();
            if ((mainURL).endsWith("/")) {
                mainURL = mainURL.substring(0, (mainURL).length() - 1);
            }

            final int returnedDocid = databaseConnection.storeCrawlerQueue(mainURL, docid);
            if (returnedDocid >= docid) {
                docid++;
            }
            boolean hasParsed = databaseConnection.hasParsedlink(mainURL);

            // If URL is not parsed
            if (!hasParsed) {
                try {
                    Indexer indexer = new Indexer();
                    if (indexer.indexing(mainURL, returnedDocid)) {
                        databaseConnection.updateCrawlerQueue(mainURL, returnedDocid);
                        System.out.println(mainURL + " Saved Features");

                        // After URL is parsed add it to parsed List
                        parsed.add(mainURL);
                        //saves into the documents table if parsed
                        //storeInformation.saveInDocuments(mainURL, returnedDocid);
                        //checks is the domain matches
                        String urlDomain = domainCheck(mainURL);
                        if (urlDomain.equalsIgnoreCase(hostDomain) || canLeave) {

                            Queue<String> tempQueue = new LinkedList<String>();
                            List<String> nonRepeatedLinksQueue = new ArrayList<>();
                            URL url = new URL(mainURL);
                            InputStream input = new URL(mainURL).openStream();
                            Tidy tidy = new Tidy();
                            tidy=setJtidyParameters(tidy);
                            XPath xpath = XPathFactory.newInstance().newXPath();
                            Document document = (Document) tidy.parseDOM(input, null);
                            nonRepeatedLinksQueue = getLinksfromSite(url, document, xpath);
                            //tempQueue.addAll(getLinksFromSite(mainURL));
                            //nonRepeatedLinksQueue.addAll(repeatedlinksRemove(tempQueue, parsed));
                            BigQueue.addAll(nonRepeatedLinksQueue);
                            for (String str : nonRepeatedLinksQueue) {
                                if ((str).endsWith("/")) {
                                    str = str.substring(0, (str).length() - 1);
                                }
                                final int storedDocid = databaseConnection.storeCrawlerQueue(str, docid);
                                if (storedDocid >= docid) {
                                    docid++;
                                }
                                //Saves into the link table
                                storeInformation.saveInLinks(returnedDocid, storedDocid);
                            }
                        }
                    }
                } catch (final IOException ioe) {
                    System.err.println("IOException: " + ioe);
                    databaseConnection.updateCrawlerQueue(mainURL, returnedDocid);

                }
            }
        }
        databaseConnection.updateDocumentsLength();
        //Computes the tf_idf scores, document frequency and idf scores
        tfIdfCompute.docFrequencyCompute();
        tfIdfCompute.idfCompute();
        tfIdfCompute.tfIdfCompute();
        pageRankCompute.computePageRank();
        bm25Compute.idfBM25Compute();
        bm25Compute.bm25ScoreCompute();
        bm25Compute.bm25PageRankScoreCompute();

        //Create shingles and compute jaccard
        createShingles.createShinglesForAllDocuments();
        jaccard.calculateJaccard();
        jaccard.createFunctionSimilarDocuments();
        jaccard.minHashing();

    }

    // Method to check the domain
    private static String domainCheck(String CheckURL) {
        String domainString = null;

        // Remove the string before the first dot as it is an unwanted part
        if (!CheckURL.isEmpty()) {
            String tempString = CheckURL.substring(CheckURL.indexOf(".") + 1,
                    CheckURL.length());

            //Obtain the string in between first and second dot
            if (tempString.indexOf(".") > 0) {
                domainString = tempString.substring(0, tempString.indexOf("."));
            } else {
                domainString = CheckURL.substring(CheckURL.indexOf("/") + 2,
                        CheckURL.indexOf("."));
            }

            // Domain String is received
            return domainString;
        } else {

            // If domain is not received then URL has to be checked
            return CheckURL;
        }
    }

    // Method for crawler to search outgoing links
    private static List<String> getLinksFromSite(String url) {
        final List<String> connnectedURL = new ArrayList<String>();
        final StringBuilder readStr = new StringBuilder();
        String tempreadStr = null;
        try {
            System.out.println("Outgoing links for the url:\n " + url);

            // Given URL is read and the corresponding outgoing links are fetched
            final BufferedReader input = new BufferedReader(
                    new InputStreamReader(new URL(url).openStream()));
            while (null != (tempreadStr = input.readLine())) {
                readStr.append(tempreadStr);
            }
            final Pattern hrf = (Pattern.compile("href=\"(.*?)\""));
            final Matcher m1 = hrf.matcher(readStr);
            String href = null;

            while (m1.find()) {
                href = m1.group(1);

                // Get the outgoing links of a page
                if (href.contains(("http://")) || (href.contains(("https://")))) {
                    connnectedURL.add(href);
                }
            }
            System.out.println("Outgoing links: " + connnectedURL.size());
        } catch (final Exception e) {
            System.err.println("Exception in getLinksFromSite: " + e);
        }
        return connnectedURL;
    }

    //Method to remove id links repeat
    private static Queue<String> repeatedlinksRemove(Queue<String> BigQueue, List<String> listParsed) {
        try {
            for (String stringParsed : listParsed) {
                Iterator<String> queueIterator = BigQueue.iterator();
                while (queueIterator.hasNext()) {
                    String url = queueIterator.next();
                    if ((url).endsWith("/")) {
                        url = url.substring(0, (url).length() - 1);
                    }
                    String trimedUrl = url.substring(url.indexOf(":") + 1, url.length());
                    String trimedStringParsed = stringParsed.substring(stringParsed.indexOf(":") + 1, stringParsed.length());
                    String endUrl = url.substring(url.length() - 3);
                    // Unwanted links with pdf,png,css have to be ignored
                    if (endUrl.equalsIgnoreCase("pdf") || endUrl.equalsIgnoreCase("ico") || endUrl.equalsIgnoreCase("png") || endUrl.equalsIgnoreCase("css")) {
                        queueIterator.remove();
                    } else if (stringParsed.equalsIgnoreCase(url) || trimedStringParsed.equalsIgnoreCase(trimedUrl)) {
                        queueIterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in repeatedlinksRemove method: " + e);
        }
        return BigQueue;

    }

    private static List<String> getLinksfromSite(URL mainUrl, Document doc, XPath xpath) {
        List<String> allUrls = new ArrayList<>();
        try {
            String expression = "//a[@href]";
            NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                String outgoingLink = node.getAttributes().getNamedItem("href").getNodeValue();
                URL url = null;
                url = normalizeURL(mainUrl, outgoingLink);
                if (removeUnwantedURL(url)) {
                    if (!allUrls.contains(url.toString())) {
                        allUrls.add(url.toString());
                    }
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allUrls;
    }

    private static URL normalizeURL(URL currentUrl, String outgoingLink) throws MalformedURLException, URISyntaxException {
        URI uri = null;
        outgoingLink = outgoingLink.toLowerCase();
        URL url = new URL(currentUrl, outgoingLink);
        uri = url.toURI();
        uri = uri.normalize();
        String fragment = null;
        fragment = uri.getRawFragment();
        if (uri != null)
            outgoingLink = uri.toString();
        if (fragment != null && fragment.length() > 0)
            outgoingLink = outgoingLink.substring(0, outgoingLink.indexOf("#" + fragment));
        outgoingLink = outgoingLink.substring(0, outgoingLink.length() - (outgoingLink.endsWith("/") ? 1 : 0));
        URL ret = new URL(outgoingLink);
        return ret;
    }

    private static boolean removeUnwantedURL(URL url) {
        boolean ret = true;
        String urlStart = url.getProtocol();
        if (!(urlStart.equalsIgnoreCase("http") || urlStart.equalsIgnoreCase("https")))
            ret = false;
        String file = url.getPath();
        String[] tokens = file.split("/(?=[^/]+$)");
        if (tokens.length >= 1) {
            String fileName = tokens[tokens.length - 1];
            String[] fileParts = fileName.split("\\.(?=[^\\.]+$)");
            if (fileParts.length >= 2) {
                String fileExtention = fileParts[tokens.length - 1];
                if (!(fileExtention.equalsIgnoreCase("html") || fileExtention.equalsIgnoreCase("htm") || fileExtention.equalsIgnoreCase("shtml")
                        || fileExtention.equalsIgnoreCase("php")))
                    ret = false;
            }
        }
        return ret;
    }

    // Method for Jtidy to clean the String
    private static String jTidyClean(String htmlText) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setMakeClean(true);
        tidy.setXHTML(true);
        tidy.setSmartIndent(true);
        Document doc = tidy.parseDOM(new ByteArrayInputStream(htmlText.getBytes()), null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.pprint(doc, outputStream);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(new String(outputStream.toByteArray(), "UTF-8"));
        String stringXHTML = stringBuilder.toString();
        return stringXHTML;
    }

    public static Tidy setJtidyParameters(Tidy tidy){
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setShowWarnings(false);
        tidy.setShowErrors(0);
        tidy.setXHTML(true);
        tidy.setQuiet(true);
        return  tidy;
    }

}
