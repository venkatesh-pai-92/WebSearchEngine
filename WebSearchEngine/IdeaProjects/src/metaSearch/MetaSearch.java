package metaSearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Venkatesh on 02-02-2017.
 */
public class MetaSearch {
    double avgCw = 0.0;

    public List<MetaSearchResult> getMetasearchResults(List<String> terms) {
        SearchEngine searchEngine = new SearchEngine();
        List<SearchEngine> searchEngineList = searchEngine.getActiveSearchEngines(terms);
        for (SearchEngine se : searchEngineList) {
            se.start();
        }
        double sumCw = 0.0;
        HashMap<String, Integer> termS = new HashMap<>();
        for (SearchEngine se : searchEngineList) {
            try {
                se.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sumCw += se.cw;
        }
        avgCw = sumCw / searchEngineList.size();
        for (String term : terms) {
            int termcount = 0;
            for (SearchEngine se : searchEngineList) {
                List<TermStatistics> stats = se.termStatisticsList.stream()
                        .filter(item -> item.term.equals(term))
                        .collect(Collectors.toList());
                if (stats.size() > 0) {
                    termcount++;
                }
            }
            termS.put(term, termcount);
        }
        for (SearchEngine se : searchEngineList) {
            se.averageCw = avgCw;
            for (TermStatistics t : se.termStatisticsList) {
                for (Map.Entry<String, Integer> entry : termS.entrySet()) {
                    if (t.term.equals(entry.getKey())) {
                        t.cf = entry.getValue();
                    }
                }
            }
            se.c = searchEngineList.size();
            se.computeScore();
        }
        Collections.sort(searchEngineList, new Comparator<SearchEngine>() {
            @Override
            public int compare(SearchEngine res1, SearchEngine res2) {
                if (res1.searchEngineScore > res2.searchEngineScore)
                    return -1;
                else if (res1.searchEngineScore < res2.searchEngineScore)
                    return 1;
                else
                    return 0;
            }
        });


        List<MetaSearchResult> metaSearchResultList = new ArrayList<>();
        for (SearchEngine se : searchEngineList) {
            try {
                JSONArray resultList = (JSONArray) se.jsonObject.get("resultList");
                Iterator<JSONObject> iterator = resultList.iterator();
                while (iterator.hasNext()) {
                    MetaSearchResult metaSearchResult = new MetaSearchResult();
                    JSONObject resultObject = iterator.next();
                    int rank = ((Long) resultObject.get("rank")).intValue();
                    String url = (String) resultObject.get("url");
                    double score = (double) resultObject.get("score");
                    metaSearchResult.rank = rank;
                    metaSearchResult.url = url;
                    metaSearchResult.score = score;
                    metaSearchResult.configUrl = se.configUrl;
                    metaSearchResult.mergedScore = (score + 0.4 * score * se.normalisedScore)/1.4;
                    metaSearchResultList.add(metaSearchResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(metaSearchResultList, new Comparator<MetaSearchResult>() {
            @Override
            public int compare(MetaSearchResult res1, MetaSearchResult res2) {
                if (res1.mergedScore > res2.mergedScore)
                    return -1;
                else if (res1.mergedScore < res2.mergedScore)
                    return 1;
                else
                    return 0;
            }
        });

        return metaSearchResultList;
    }
}
