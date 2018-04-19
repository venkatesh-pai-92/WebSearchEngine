package servlet;

import crawler.Crawler;
import database.DatabaseConnection;
import org.postgresql.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Venkatesh on 27-01-2017.
 */
@WebServlet(name = "RegisterAdvertisement")
public class RegisterAdvertisement extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String userName = request.getParameter("user_name");
        String n_grams = request.getParameter("n_grams");
        String url = request.getParameter("url");
        String description = request.getParameter("description");
        double budget = Double.parseDouble(request.getParameter("budget"));
        double money_per_click = Double.parseDouble(request.getParameter("money_per_click"));
        String image_url = request.getParameter("image_url");
        PreparedStatement preparedStatement1 = null;
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection=null;
        try {
            connection = databaseConnection.getConnection();
            if (connection != null) {
                String saveAdSql = "INSERT INTO advertisement(user_name,n_grams,ad_url,ad_description,ad_budget,money_per_click,ad_image_url) VALUES(?,?,?,?,?,?,?)";
                preparedStatement1=connection.prepareStatement(saveAdSql);
                preparedStatement1.setString(1,userName);
                preparedStatement1.setString(2,n_grams);
                preparedStatement1.setString(3,url);
                preparedStatement1.setString(4,description);
                preparedStatement1.setDouble(5,budget);
                preparedStatement1.setDouble(6,money_per_click);
                preparedStatement1.setString(7,image_url);
                preparedStatement1.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        RequestDispatcher rd = null;
//        rd = request.getRequestDispatcher("adRegistrationCompleted.jsp");
//        rd.forward(request, response);
        response.sendRedirect("adRegistrationCompleted.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
