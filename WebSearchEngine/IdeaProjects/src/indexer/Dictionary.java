package indexer;

// List of stopwords from snowball.tartarus.org/algorithms.english/stop.txt
import java.io.*;
import java.net.URL;
import java.util.*;

import static java.util.Arrays.*;

public class Dictionary {

    static String[] englishStopWordsList = {"i", "me", "my", "myself", "we", "us", "ours", "ourselves",
        "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her",
        "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves",
        "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were",
        "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "will", "would", "shall",
        "should", "can", "could", "may", "might", "must", "ought", "i'm", "you're", "he's", "she's",
        "it's", "we're", "they're", "i've", "you've", "we've", "they've", "i'd", "you'd", "he'd",
        "she'd", "we'd", "they'd", "i'll", "you'll", "he'll", "she'll", "we'll", "they'll", "isn't",
        "aren't", "wasn't", "weren't", "hasn't", "haven't", "hadn't", "doesn't", "don't",
        "didn't", "won't", "wouldn't", "shan't", "shouldn't", "can't", "cannot", "couldn't",
        "mustn't", "let's", "that's", "who's", "what's", "here's", "there's", "when's",
        "where's", "why's", "how's", "a", "an", "the", "and", "but", "if", "or",
        "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against",
        "between", "into", "through", "during", "before", "after", "above", "below", "to",
        "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once",
        "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most",
        "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "that", "too", "very", "one", "every", "least",
        "less", "many", "one", "every", "least", "less", "many", "now", "ever", "never", "say", "says", "said", "also",
        "get", "go", "goes", "just", "made", "make", "put", "see", "seen", "whether", "like", "well", "back", "even",
        "still", "way", "take", "since", "another", "however", "two", "three", "four", "five", "first", "second",
        "new", "old", "high", "long"};

    private List<String> englishWords = new ArrayList<String>();
    

   // public static ArrayList<String> englishWordlist = new ArrayList<String>();



    ArrayList<String> EnglishWords = new ArrayList<String>();
    private static Dictionary wc = null;
    private Dictionary()
    {
        int k = 0;
        String sCurrentLine;

        try {
            //Below line Only when generating war
            String filePath = getClass().getResource("EnglishWords").getPath();
            //Below line only when generating jar
            //String filePath = "EnglishWords";
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while ((sCurrentLine = br.readLine()) != null) {
                EnglishWords.add(sCurrentLine);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            /*System.out.println("File not Found");
            System.out.println(new File("EnglishWords").getAbsolutePath());*/
        }

    }
    public static Dictionary getInstance() {
        if (wc == null) {
            wc = new Dictionary();
        }
        return wc;
    }
    public ArrayList<String> getEnglishWords()
    {
        return EnglishWords;
    }



    static String[] germanStopWordsList = {"ab", "aber", "ähnlich", "allein", "allem", "allen", "aller", "allerdings", "allerlei", "alles", "allmählich",
        "allzu", "als", "alsbald", "also", "am", "an", "and", "ander", "andere", "anderem", "anderen", "anderer", "andererseits", "anderes", "anderm", "andern",
        "andernfalls", "anders", "anstatt", "auch", "auf", "aus", "ausgenommen", "ausser", "außer", "ausserdem", "außerhalb", "bald", "bei", "beide", "beiden",
        "beiderlei", "beides", "beim", "beinahe", "bereits", "besonders", "besser", "beträchtlich", "bevor", "bezüglich", "bin", "bis", "bisher", "bist",
        "bloß", "bsp.", "bzw", "ca", "ca.", "content", "da", "dabei", "dadurch", "dafür", "dagegen", "daher", "dahin", "damals",
        "damit", "danach", "daneben", "dann", "daran", "darauf", "darin", "darüber", "darüberhinaus", "darum", "darunter", "das", "daß", "dass", "davon", "davor",
        "dazu", "dein", "deine", "deinem", "deiner", "deines", "dem", "demnach", "demselben", "den", "denen", "denn", "dennoch", "denselben", "der", "derart", "derartig", "derem", "deren", "derer", "derjenige", "derjenigen", "derselbe", "derselben", "derzeit", "des", "deshalb", "desselben", "dessen", "desto", "deswegen", "dich", "die", "diejenige", "dies", "diese", "dieselbe", "dieselben", "diesem", "diesen", "dieser", "dieses", "diesseits", "dir", "direkt", "direkte", "direkten", "direkter", "doch", "dort", "dorther", "dorthin", "drauf", "drin", "dr�ber", "drunter", "du", "dunklen", "durch", "durchaus", "eben", "ebenfalls", "ebenso", "eher", "eigenen", "eigenes", "eigentlich", "ein", "eine", "einem", "einen", "einer", "einerseits", "eines", "einfach", "einf�hren", "einf�hrte", "einf�hrten", "eingesetzt", "einig", "einige", "einigem", "einigen", "einiger", "einigermaßen", "einiges", "einmal", "eins", "einseitig", "einseitige", "einseitigen", "einseitiger", "einst", "einstmals", "einzig", "entsprechend", "entweder", "er", "erst", "es", "etc", "etliche", "etwa", "etwas", "euch", "euer", "eure", "eurem", "euren", "eurer", "eures", "falls", "fast", "ferner", "folgende", "folgenden", "folgender", "folgendes", "folglich", "fuer", "für", "gab", "ganze", "ganzem", "ganzen", "ganzer", "ganzes", "gänzlich", "gar", "gegen", "gem�ss", "ggf", "gleich", "gleichwohl", "gleichzeitig", "gl�cklicherweise", "hab", "habe", "haben", "haette", "hast", "hat", "h�tt", "hatte", "h�tte", "hatten", "h�tten", "hattest", "hattet", "heraus", "herein", "hier", "hiermit", "hiesige", "hin", "hinein", "hinten", "hinter", "hinterher", "h�chstens", "http", "ich", "igitt", "ihm", "ihn", "ihnen", "ihr", "ihre", "ihrem", "ihren", "ihrer", "ihres", "im", "immer", "immerhin", "in", "indem", "indessen", "infolge", "innen", "innerhalb", "ins", "insofern", "inzwischen", "irgend", "irgendeine", "irgendwas", "irgendwen", "irgendwer", "irgendwie", "irgendwo", "ist", "ja", "j�hrig", "j�hrige", "j�hrigen", "j�hriges", "je", "jed", "jede", "jedem", "jeden", "jedenfalls", "jeder", "jederlei", "jedes", "jedoch", "jemand", "jene", "jenem", "jenen", "jener", "jenes", "jenseits", "jetzt", "kam", "kann", "kannst", "kaum", "kein", "keine", "keinem", "keinen", "keiner", "keinerlei", "keines", "keineswegs", "klar", "klare", "klaren", "klares", "klein", "kleinen", "kleiner", "kleines", "koennen", "koennt", "koennte", "koennten", "komme", "kommen", "kommt", "konkret", "konkrete", "konkreten", "konkreter", "konkretes", "k�nnen", "k�nftig", "leider", "man", "manche", "manchem", "manchen", "mancher", "mancherorts", "manches", "manchmal", "mehr", "mehrere", "mein", "meine", "meinem", "meinen", "meiner", "meines", "mich", "mir", "mit", "mithin", "muessen", "muesst", "muesste", "muss", "mu�", "m�ssen", "musst", "mu�t", "m��t", "musste", "m�sste", "m��te", "mussten", "m�ssten", "nach", "nachdem", "nachher", "nachhinein", "n�chste", "nahm", "n�mlich", "nat�rlich", "neben", "nebenan", "nehmen", "nicht", "nichts", "nie", "niemals", "niemand", "nirgends", "nirgendwo", "noch", "n�tigenfalls", "nun", "nur", "ob", "oben", "oberhalb", "obgleich", "obschon", "obwohl", "oder", "oft", "per", "plötzlich", "schließlich", "schon", "sehr", "sehrwohl", "sein", "seine", "seinem", "seinen", "seiner", "seines", "seit", "seitdem", "seither", "selber", "selbst", "sich", "sicher", "sicherlich", "sie", "sind", "so", "sobald", "sodass", "soda�", "soeben", "sofern", "sofort", "sogar", "solange", "solch", "solche", "solchem", "solchen", "solcher", "solches", "soll", "sollen", "sollst", "sollt", "sollte", "sollten", "solltest", "somit", "sondern", "sonst", "sonstwo", "sooft", "soviel", "soweit", "sowie", "sowohl", "tatsächlich", "tatsächlichen",
        "tatsächlicher", "tatsächliches", "trotzdem", "übel", "über", "überall", "überallhin", "überdies", "übermorgen", "übrig", "übrigens", "ueber", "um", "umso", "unbedingt", "und", "unmöglich", "unmögliche", "unmöglichen", "unmöglicher", "uns", "unser", "unsere", "unserem", "unseren", "unserer", "unseres", "unter", "usw", "viel", "viele", "vielen", "vieler", "vieles", "vielleicht", "vielmals", "völlig", "vom", "von", "vor", "voran", "vorher", "vorüber", "während", "währenddessen", "wann", "war", "wär", "wäre", "waren", "wären", "warst", "warum", "was", "weder", "weil", "wei�", "weiter", "weitere", "weiterem", "weiteren", "weiterer", "weiteres", "weiterhin", "welche", "welchem", "welchen", "welcher", "welches", "wem", "wen", "wenig", "wenige", "weniger", "wenigstens", "wenn", "wenngleich", "wer", "werde", "werden", "werdet",
        "weshalb", "wessen", "wichtig", "wie", "wieder", "wieso", "wieviel", "wiewohl", "will", "willst", "wir", "wird", "wirklich", "wirst", "wo", "wodurch", "wogegen", "woher", "wohin", "wohingegen", "wohl", "wohlweislich", "womit", "woraufhin", "woraus", "worin", "wurde", "w�rde", "wurden", "w�rden", "zB", "z.B.", "zahlreich", "zeitweise", "zu", "zudem", "zuerst", "zufolge", "zugleich", "zuletzt", "zum", "zumal", "zur", "zur�ck", "zusammen", "zuviel", "zwar", "zwischen"};
}
