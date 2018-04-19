package queryProcessing;

import java.util.ArrayList;
import java.util.List;


public class QueryElements {

    public List<String> queryWordList = new ArrayList<>();
    public List<String> quotedWordList = new ArrayList<>();
    public List<String> sites = new ArrayList<>();
    public List<String> synonyms = new ArrayList<>();
    public List<String> unstemmedTerms = new ArrayList<>();
    public String language="en";
    public String queryString="";
    public int maxResults=0;
}
