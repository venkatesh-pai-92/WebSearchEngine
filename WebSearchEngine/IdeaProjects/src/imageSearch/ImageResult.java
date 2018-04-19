package imageSearch;

import indexer.Stemmer;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class ImageResult {
    public int pageIndex;
    public int position;
    public double imageScore;
    public String imageType;
    public String alt;
    public String image;
    public String src;
    public String url;

    public double calculateScore(String query, String snippetInformation, double score) {
        int imageWindowSize = 500;
        double imageScore;
        String content = snippetInformation;
        String[] queryTerms = query.split(" ");

        List<Integer> indexPositions = new ArrayList<Integer>();

        for (int i = 0; i < queryTerms.length; i++) {

            int lastIndex = 0;
            int count = 0;

            while (lastIndex != -1) {
                lastIndex = content.indexOf(queryTerms[i], lastIndex);
                if (lastIndex != -1) {
                    count++;
                    indexPositions.add(lastIndex);
                    lastIndex += queryTerms[i].length();
                }
            }
        }
        if (indexPositions.size() < 1) {
            imageScore = 0;
        } else {
            // find out least position of query word from image
            int nearestIndex = 0, nearestDifference = -1;
            for (int index = 0; index < indexPositions.size(); index++) {
                nearestDifference = Math.abs(indexPositions.get(nearestIndex) - position);
                int currentDifference = Math.abs(indexPositions.get(index) - position);

                if (currentDifference < nearestDifference)
                    nearestIndex = index;
            }
            if (nearestDifference == -1 || nearestDifference > imageWindowSize) {
                imageScore = 0;
            } else {
                float x = nearestDifference / content.length();

                //lamda=0.5f and score_weight=0.8f
                imageScore = ((0.5f * Math.exp(-0.5f * x)) * 0.8f + score * (1 - 0.8f)) / 2;
            }
        }

        for (int i = 0; i < queryTerms.length; i++) {
            String[] splittedSource = src.split("/");
            String imageName = splittedSource[splittedSource.length - 1];
            if (imageName.contains(queryTerms[i])) {
                imageScore += 2.0;
            }
        }
        return imageScore;
    }
}
