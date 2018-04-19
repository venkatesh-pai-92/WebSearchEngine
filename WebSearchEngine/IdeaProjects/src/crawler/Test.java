package crawler;

import org.postgresql.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Venkatesh on 11-01-2017.
 */
public class Test {
    public static void main(String args[]){
        try {
            //URL givenURL = new URL("http://dbis.informatik.uni-kl.de/index.php/en");
            //BufferedReader bufferReader = new BufferedReader(new InputStreamReader(givenURL.openStream()));
            InputStream input = new URL("http://dbis.informatik.uni-kl.de/index.php/en").openStream();
//            String htmlText = "";
//            //Read the contents and storing it in string
//            while ((bufferReader.readLine()) != null) {
//                htmlText = htmlText + bufferReader.readLine();
//            }
            Document document = (Document) new Tidy().parseDOM(input, null);
            NodeList nodeList = document.getElementsByTagName("img");
            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                String src = node.getAttributes().getNamedItem("src").getNodeValue();
                System.out.println(src);
                String alt = "";
                if (node.getAttributes().getNamedItem("alt") != null) {
                    alt = node.getAttributes().getNamedItem("alt").getNodeValue();
                }
                System.out.println(alt);
                //Check
                URL currentURL = new URL("http://dbis.informatik.uni-kl.de/index.php/en");
                URL imgUrl = new URL(currentURL,src);
                URLConnection urlConnection = imgUrl.openConnection();


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
                System.out.println(imageType);
                System.out.println(Base64.encodeBytes(bytes));
                System.out.println();
                input.close();
            }




        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
