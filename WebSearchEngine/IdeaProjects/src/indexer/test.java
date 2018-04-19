package indexer;

import database.StoreInformation;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Venkatesh on 14-01-2017.
 */
public class test {
    public static void main(String args[]) {
        try{
            Indexer in=new Indexer();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new URL("http://dbis.informatik.uni-kl.de/index.php/en/").openStream()));
            StringBuffer sb = new StringBuffer();
            //Read the contents and storing it in string
            String inputLine=null;
            while ((inputLine=bufferReader.readLine()) != null) {
                sb.append(inputLine+"\n");
            }
            String htmlText=sb.toString();

            // BUffered Reader session closes.
            bufferReader.close();
            //escape utils
            htmlText = StringEscapeUtils.unescapeHtml4(htmlText);
            Charset.forName("UTF-8").encode(htmlText);
            StoreInformation storeInformation=new StoreInformation();
            htmlText = storeInformation.updateImagePosition(htmlText,1);
            htmlText = in.removeHtmlTags(htmlText).toLowerCase();
            System.out.println(htmlText);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
