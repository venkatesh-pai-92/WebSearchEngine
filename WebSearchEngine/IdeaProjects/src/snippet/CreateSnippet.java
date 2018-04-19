package snippet;

import indexer.Stemmer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class CreateSnippet {

    int begin = 0;
    int end = 0;
    int wordCount = 0;
    int relevantTermCount = 0;
    int queryTermCount = 0;
    int distinctTermCount = 0;
    List<String> queryTerms;
    String[] contentWords;
    boolean[] isTermPresent;
    static final int maximumWordCount = 32;
    static final int minimumWordCount = 8;

    private CreateSnippet(String[] contentWords, int begin, int end, List<String> queryTerms) {
        isTermPresent = new boolean[queryTerms.size()];
        for (int index = 0; index < isTermPresent.length; index++) {
            isTermPresent[index] = false;
        }

        this.queryTerms = queryTerms;
        this.contentWords = contentWords;
        this.begin = begin;
        this.end = end;
        wordCount = end - begin + 1;
        queryTermCount = queryTerms.size();
        for (int i = begin; i <= end; i++) {
            for (int j = 0; j < queryTerms.size(); j++) {
                if (contentWords[i].equalsIgnoreCase(queryTerms.get(j))) {
                    relevantTermCount++;
                    isTermPresent[j] = true;
                }
            }
        }
        for (int index = 0; index < isTermPresent.length; index++) { //Count distinct queryTerms
            if (isTermPresent[index])
                distinctTermCount++;
        }
    }

    public static OutputSnippet createSnippetText(String contentWords, List<String> queryTerms) {
        Stemmer stemmer = new Stemmer();
        OutputSnippet outputSnippet = new OutputSnippet();
        outputSnippet.missingTerms = new ArrayList<>();
        String snippetText = "";
        try {
            String[] words = contentWords.split(" ");
            for (String term : queryTerms) {
                String stemterm = stemmer.stemString(term);
                boolean containerContainsContent = StringUtils.containsIgnoreCase(contentWords, term);
                if(!containerContainsContent)
                    outputSnippet.missingTerms.add(term);
//                if (!contentWords.contains(stemterm)) {
//                    outputSnippet.missingTerms.add(term);
//                }
            }
            HashMap<String,Integer> missingTerms =new HashMap<>();
            List<Integer> indexPositions = new ArrayList<>();
            for (int index = 0; index < words.length; index++) {
                for (String queryTerm : queryTerms) {
                    //int termCount = 0;
                    if (words[index].equalsIgnoreCase(queryTerm)) {
                        indexPositions.add(index);
                        //termCount++;
                    }
                }
            }

            Set<CreateSnippet> snippets = new HashSet<>();
            for (Integer index : indexPositions) {
                int minPosition = Math.max(0, index - 4);
                int maxPosition = Math.min(words.length, index + 3);
                if (maxPosition - minPosition + 1 < minimumWordCount) {
                    if (minPosition == 0)
                        maxPosition += minimumWordCount - maxPosition - minPosition + 1;
                    else if (maxPosition == maximumWordCount)
                        minPosition -= minimumWordCount - maxPosition - minPosition + 1;
                }
                snippets.add(new CreateSnippet(words, minPosition, maxPosition, queryTerms));
            }

            boolean cont = true;

            while (cont) {
                cont = false;
                List<CreateSnippet> temporaryList = new ArrayList<>(snippets);
                Collections.sort(temporaryList, CreateSnippet.sortFromBegininig);
                for (int i = 1; i < temporaryList.size(); i++) {
                    CreateSnippet combinedSnippet = temporaryList.get(i - 1).combine(temporaryList.get(i));
                    if (combinedSnippet != null) {
                        if (snippets.add(combinedSnippet)) cont = true;
                    }
                }
            }

            //Get best snippets
            List<CreateSnippet> snippetListByScore = new ArrayList<>(snippets);
            Collections.sort(snippetListByScore, CreateSnippet.sortByScore);
            List<CreateSnippet> selectedSnippets = new ArrayList<>();
            int count = 0;
            int i = 0;
            while (count < maximumWordCount && i < snippetListByScore.size()) {
                CreateSnippet tempSnippet = snippetListByScore.get(i);
                i++;
                if (count + tempSnippet.wordCount <= maximumWordCount) {
                    selectedSnippets.add(tempSnippet);
                    count += tempSnippet.wordCount;
                }
            }

            Collections.sort(selectedSnippets, CreateSnippet.sortFromBegininig);

            for (CreateSnippet chosenSnippet : selectedSnippets) {
                snippetText += chosenSnippet.getSnippetText() + "... ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        outputSnippet.snippetText = snippetText;

        return outputSnippet;
    }

    private CreateSnippet combine(CreateSnippet otherSnippet) {
        int minPosition = Math.min(begin, otherSnippet.begin);
        int maxPosition = Math.max(end, otherSnippet.end);
        if (maxPosition - minPosition + 1 <= maximumWordCount)
            return new CreateSnippet(contentWords, minPosition, maxPosition, queryTerms);
        else return null;
    }

    private String getSnippetText() {
        String snippetText = "";
        for (int index = begin; index <= end; index++) {
            snippetText += contentWords[index] + " ";
        }
        for (String queryTerm : queryTerms) {
            snippetText = snippetText.replaceAll(queryTerm.toLowerCase(), "<b>" + queryTerm + "</b>");
        }
        return snippetText;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CreateSnippet)) return false;
        CreateSnippet other = (CreateSnippet) obj;
        if (this.begin == other.begin && this.end == other.end) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return (int) Math.round(0.5 * (begin + end) * (begin + end + 1) + end);
    }

    private static Comparator<CreateSnippet> sortByScore = new Comparator<CreateSnippet>() {
        public int compare(CreateSnippet snippet1, CreateSnippet snippet2) {
            double score = snippet2.getScore() - snippet1.getScore();
            if (score < 0)
                return -1;
            if (score == 0)
                return 0;
            if (score > 0)
                return 1;
            return 0;
        }
    };

    private static Comparator<CreateSnippet> sortFromBegininig = new Comparator<CreateSnippet>() {
        public int compare(CreateSnippet snippet1, CreateSnippet snippet2) {
            return snippet1.begin - snippet2.begin;
        }
    };

    private double getScore() {           ;
        return (((double) distinctTermCount / queryTermCount)*((double)relevantTermCount/wordCount));
    }
}
