package queryProcessing;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;

import edu.mit.jwi.item.POS;
import indexer.Stemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueryProcessor {

    public QueryElements processQuery(String queryString, String language){
        QueryElements qe=new QueryElements();
        qe.queryString=queryString;
        qe.language=language;
        queryString = queryString.toLowerCase();
        String[] queryTerms = queryString.split(" ");
        Stemmer stem = new Stemmer();
        for (int i = 0; i < queryTerms.length; i++) {
            if(queryTerms[i].startsWith("site:")){ //Site operator
                qe.sites.add(queryTerms[i].substring(5));
            }else if(queryTerms[i].startsWith("\"") && queryTerms[i].endsWith("\""))
            {
                qe.quotedWordList.add(queryTerms[i].substring(1,queryTerms[i].length()-1));
                qe.unstemmedTerms.add(queryTerms[i].substring(1,queryTerms[i].length()-1));
            }else if(queryTerms[i].startsWith("~")){
                qe.synonyms.add(queryTerms[i].substring(1));
                qe.unstemmedTerms.add(queryTerms[i].substring(1));
            }else {
                qe.unstemmedTerms.add(queryTerms[i]);
                queryTerms[i] = stem.stemString(queryTerms[i]).trim();
                qe.queryWordList.add(queryTerms[i]);
            }
        }
        return qe;
    }

    public List<String> deriveSynonyms(String word,String language)  throws IOException {
        Set<String> synonyms = new HashSet<>();
        Set<IWordID> wordIds = new HashSet<>();
        if(language.equalsIgnoreCase("en")){
            String path = "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
            IDictionary dict;
            String home = System.getenv ("WNHOME") ;
            if(home == null)
                home = path;
            URL url = new URL("file",null, home);
            dict = new Dictionary(url);
            dict.open();
            IIndexWord noun = dict.getIndexWord (word, POS.NOUN);
            IIndexWord adj = dict.getIndexWord (word, POS.ADJECTIVE);
            IIndexWord adv = dict.getIndexWord (word, POS.ADVERB);
            IIndexWord verb = dict.getIndexWord (word, POS.VERB);
            if(noun != null)
                wordIds.addAll(noun.getWordIDs());
            if(adj != null)
                wordIds.addAll(adj.getWordIDs());
            if(adv != null)
                wordIds.addAll(adv.getWordIDs());
            if(verb != null)
                wordIds.addAll(verb.getWordIDs());

            for (IWordID wordId : wordIds) {
                IWord w = dict.getWord(wordId);
                synonyms.add(w.getLemma());
                ISynset synset = w.getSynset();
                for (IWord iWord : synset.getWords()) {
                    synonyms.add(iWord.getLemma());
                }
            }
            return new ArrayList<>(synonyms);
        }
        //German
        else if(language.equalsIgnoreCase("de")){
            String filePath = getClass().getResource("germanSynonyms").getPath();
            //String filePath ="germanSynonyms";
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String sCurrentLine;
            Pattern p = Pattern.compile("(^|;)"+word.toLowerCase()+"($|;)");
            try {
                while ((sCurrentLine = br.readLine()) != null) {
                    String line = sCurrentLine;
                    line = line.toLowerCase();
                    line = line.replaceAll("(\\s\\([^)]*\\))", "");
                    Matcher m = p.matcher(line);
                    if(m.find())
                    {
                        String[] split = line.split(";");
                        for (String s : split) {
                            synonyms.add(s);
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>(synonyms);
        }
        return new ArrayList<>(synonyms);
    }

}
