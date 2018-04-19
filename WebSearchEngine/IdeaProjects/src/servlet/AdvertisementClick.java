package servlet;

import advertisement.AdvertisementResult;
import database.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by Venkatesh on 29-01-2017.
 */
@WebServlet(name = "AdvertisementClick")
public class AdvertisementClick extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Id="";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int adId = Integer.parseInt(request.getParameter("id"));
        String url = request.getParameter("url");
        DatabaseConnection databaseConnection = new DatabaseConnection();
        PreparedStatement preparedStatement1=null,preparedStatement2 =null;
        ResultSet resultSet1=null;
        try {
            Connection connection = databaseConnection.getConnection();
            if(connection!=null){
                String getAdSql = "SELECT * FROM advertisement WHERE ad_id = ? ;";
                preparedStatement1= connection.prepareStatement(getAdSql);
                preparedStatement1.setInt(1,adId);
                resultSet1 = preparedStatement1.executeQuery();
                AdvertisementResult adResult = new AdvertisementResult();
                while(resultSet1.next()){
                    adResult.adId = resultSet1.getInt("ad_id");
                    adResult.customerName = resultSet1.getString("user_name");
                    adResult.nGrams = resultSet1.getString("n_grams");
                    adResult.url = resultSet1.getString("ad_url");
                    adResult.description = resultSet1.getString("ad_description");
                    adResult.budget = resultSet1.getDouble("ad_budget");
                    adResult.moneyPerClick = resultSet1.getDouble("money_per_click");
                    adResult.imageUrl = resultSet1.getString("ad_image_url");
                    adResult.clicksCount = resultSet1.getInt("ad_click_count");
                    if(resultSet1.getDate("last_click_timestamp")!=null) {
                        adResult.lastClickTime = resultSet1.getDate("last_click_timestamp");
                    }
                }
                int updatedCount = ++adResult.clicksCount;
                double moneyAfterDeduction = adResult.budget - adResult.moneyPerClick;
                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();
                Timestamp timestamp= new Timestamp(now.getTime());
                String updateSql = "UPDATE advertisement set ad_click_count = ? , ad_budget = ? , last_click_timestamp = ? WHERE ad_id = ?";
                preparedStatement2 = connection.prepareStatement(updateSql);
                preparedStatement2.setInt(1,updatedCount);
                preparedStatement2.setDouble(2,moneyAfterDeduction);
                preparedStatement2.setTimestamp(3,timestamp);
                preparedStatement2.setInt(4,adId);
                preparedStatement2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        response.sendRedirect(url);

    }
}
