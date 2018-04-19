package advertisement;

import database.DatabaseConnection;
import imageSearch.ImageResult;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Venkatesh on 28-01-2017.
 */
public class AdvertisementSearch {

    //gets the advertisements
    public List<AdvertisementResult> getAdvertisements(List<String> terms){
        List<AdvertisementResult> adResultList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        ResultSet resultSet=null;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try {

            connection = databaseConnection.getConnection();
            StringBuilder termsClause =new StringBuilder();
            for (int i = 0; i < terms.size(); i++) {
                if (i > 0) {
                    termsClause.append(",");
                }
                termsClause.append("%"+terms.get(i)+"%");
            }

            if(connection!=null){
                String getAdSql="SELECT * FROM advertisement WHERE n_grams LIKE ANY ('{"+termsClause.toString()+"}') AND ad_budget>0;";
                preparedStatement1=connection.prepareStatement(getAdSql);
                resultSet = preparedStatement1.executeQuery();
                while (resultSet.next()){
                    AdvertisementResult adResult = new AdvertisementResult();
                    adResult.nGrams = resultSet.getString("n_grams");
                    adResult.moneyPerClick = resultSet.getDouble("money_per_click");
                    adResult.score = calculateScore(adResult,terms);
                    if(adResult.score>0) {
                        adResult.adId = resultSet.getInt("ad_id");
                        adResult.customerName = resultSet.getString("user_name");
                        adResult.url = resultSet.getString("ad_url");
                        adResult.description = resultSet.getString("ad_description");
                        adResult.budget = resultSet.getDouble("ad_budget");
                        adResult.imageUrl = resultSet.getString("ad_image_url");
                        adResult.clicksCount = resultSet.getInt("ad_click_count");
                        if (resultSet.getDate("last_click_timestamp") != null) {
                            adResult.lastClickTime = resultSet.getDate("last_click_timestamp");
                        }
                        adResultList.add(adResult);
                    }
                }
                //sorting based on score
                Collections.sort(adResultList,new Comparator<AdvertisementResult>() {
                    @Override
                    public int compare(AdvertisementResult res1, AdvertisementResult res2) {
                        if (res1.score > res2.score)
                            return -1;
                        else if (res1.score < res2.score)
                            return 1;
                        else
                            return 0;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return adResultList;
    }
    public double calculateScore(AdvertisementResult adResult,List<String> terms){
        //checks how many n grams matches..splits the ngram sets.
        int nGramMatchedCount = 0;
        String query = StringUtils.join(terms," ");
        String[] nGramsSet = adResult.nGrams.split(",");
        for(String ngram: nGramsSet){
            if(query.contains(ngram.trim())){
                nGramMatchedCount++;
            }
        }
        double score = nGramMatchedCount * adResult.moneyPerClick;
        return score;
    }
}
