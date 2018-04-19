package imageSearch;

import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Venkatesh on 07-01-2017.
 */
public class ImageSearch {
    public List<ImageResult> search(String queryString, ResultSet resultSet, int resultSize, String language) {
        Connection connection = null;
        List<ImageResult> imageResultList = new ArrayList<>();
        PreparedStatement preparedStatement1 = null;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        List<Result> resultList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Result result = new Result();
                result.documentId = resultSet.getInt(1);
                result.url = resultSet.getString(2);
                result.score = resultSet.getDouble(3);
                result.snipppet_information = resultSet.getString(4);
                resultList.add(result);
            }
            connection = databaseConnection.getConnection();
            for(Result result:resultList){
                String getImagesSql = "SELECT * FROM images WHERE docid = " + result.documentId;
                preparedStatement1=connection.prepareStatement(getImagesSql);
                ResultSet res = preparedStatement1.executeQuery();
                while (res.next()) {
                    ImageResult imageResult = new ImageResult();
                    imageResult.src= res.getString(2);
                    imageResult.alt= res.getString(3);
                    imageResult.pageIndex= res.getInt(4);
                    imageResult.position= res.getInt(5);
                    imageResult.imageType= res.getString(6);
                    imageResult.image= res.getString(7);
                    imageResult.imageScore=imageResult.calculateScore(queryString,result.snipppet_information,result.score);
                    imageResult.url = result.url;
                    imageResultList.add(imageResult);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Collections.sort(imageResultList,new Comparator<ImageResult>() {
            @Override
            public int compare(ImageResult res1, ImageResult res2) {
                if (res1.imageScore > res2.imageScore)
                    return -1;
                else if (res1.imageScore < res2.imageScore)
                    return 1;
                else
                    return 0;
            }
        });

        while (imageResultList.size() > resultSize) {
            imageResultList.remove(imageResultList.size()-1);
        }

        return imageResultList;
    }
}
