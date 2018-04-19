/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Venkatesh
 */
public class MultiLanguageSupport {

    public static String detectLanguage(String[] words) {
        HashMap<String, Integer> wordsAndCount = new HashMap<>();
        for (int j = 0; j < words.length; j++) {
            // If a word is not a stop word , then stemming of the word has to be continued
            String word = words[j];
            if (wordsAndCount.containsKey(word)) {
                int wordCount = wordsAndCount.get(word);
                wordsAndCount.remove(word);
                wordsAndCount.put(word, wordCount + 1);
            } else {
                wordsAndCount.put(word, 1);
            }
        }
        int number_english_words_detected = 1;
        int total_words = 1;
        ArrayList<String> englishWordsArray = Dictionary.getInstance().getEnglishWords();
        for (String word : wordsAndCount.keySet()) {
            int wordCount = wordsAndCount.get(word);
            total_words += wordCount;
            if (englishWordsArray.contains(word)) {
                number_english_words_detected += wordCount;
            }
        }
        float quotient = (float) number_english_words_detected / (float) total_words;
        if (quotient > 0.3f) {
            return "en";
        } else {
            return "de";
        }
    }
}
