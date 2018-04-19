package metaSearch;

/**
 * Created by Venkatesh on 03-02-2017.
 */
public class TermStatistics {
    public String term;
    public int docFrequency;
    public int cf;
    public double termScore = 0.0;
    public boolean isScoreComputed = false;
}
