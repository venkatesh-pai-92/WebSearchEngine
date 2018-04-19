/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Venkatesh
 */
public class CrawlerThread extends Thread {
    static Queue<String> BigQueue = new LinkedList<String>();
    
    static int docid;
    static int depth = 0;
    static int depthMax = 3;
    static int docMax = 60;
    
    public void run(){
        
//        final Boolean restart = databaseConnection.restartOrNot();
//
//        while (!BigQueue.isEmpty() && depth < depthMax && parsed.size() < docMax) {
//            String mainURL = null;
//            
//            // If Database connection has to restart
//            if (restart) {
//                
//             // Since Database connection is restarting
//             // clear everything stored in our queue
//                BigQueue.clear();
//                
//             // All the unparesed Urls will be parsed by adding it to Queue
//                ArrayList<String> notParsedUrls = databaseConnection.getNotParsedurls();
//                BigQueue.addAll(notParsedUrls);
//            }
//            mainURL = BigQueue.poll();
//            if ((mainURL).endsWith("/")) {
//                mainURL = mainURL.substring(0, (mainURL).length() - 1);
//            }
//            
//            final int returnedDocid = databaseConnection.storeCrawlerQueue(mainURL, docid);
//            if (returnedDocid >= docid) {
//                docid++;
//            }
//            boolean hasParsed = databaseConnection.hasParsedlink(mainURL);
//            
//            // If URL is not parsed
//            if (!hasParsed) {
//                try {
//                    Indexer indexer = new Indexer();
//                    if (indexer.indexing(mainURL, returnedDocid)) {
//                        databaseConnection.updateCrawlerQueue(mainURL, returnedDocid);
//                        System.out.println(mainURL + " Parsing Done");
//                        
//                        // After URL is parsed add it to parsed List
//                        parsed.add(mainURL);
//                        //saves into the documents table if parsed
//                        storeInformation.saveInDocuments(mainURL, returnedDocid);
//                        //checks is the domain matches
//                        String urlDomain = domainCheck(mainURL);
//                        if (urlDomain.equalsIgnoreCase(hostDomain) || canLeave) {
//                           
//                            Queue<String> tempQueue = new LinkedList<String>();
//                            tempQueue.addAll(getLinksFromSite(mainURL));
//                            BigQueue.addAll(repeatedlinksRemove(tempQueue, parsed));
//                            for (String str : BigQueue) {
//                                if ((str).endsWith("/")) {
//                                    str = str.substring(0, (str).length() - 1);
//                                }
//                                final int storedDocid = databaseConnection.storeCrawlerQueue(str, docid);
//                                if (storedDocid >= docid) {
//                                    docid++;
//                                }
//                                //Saves into the link table
//                                storeInformation.saveInLinks(returnedDocid, storedDocid);
//                            }
//                        }
//                    }
//                } catch (final IOException ioe) {
//                    System.err.println("IOException: " + ioe);
//                    databaseConnection.updateCrawlerQueue(mainURL, returnedDocid);
//
//                }
//            }
//        }
    }
    
}
