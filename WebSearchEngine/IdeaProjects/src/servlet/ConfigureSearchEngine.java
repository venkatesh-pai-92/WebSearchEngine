package servlet;

import database.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Venkatesh on 01-02-2017.
 */
@WebServlet(name = "ConfigureSearchEngine")
public class ConfigureSearchEngine extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        DatabaseConnection databaseConnection = new DatabaseConnection();
        PreparedStatement preparedStatement1 = null;
        Connection connection = null;
        try {
            if (action.equalsIgnoreCase("Add")) {
                int groupId = Integer.parseInt(request.getParameter("group-id"));
                String configUrl = request.getParameter("config-url");
                boolean isActive = Boolean.parseBoolean(request.getParameter("is-active"));
                connection = databaseConnection.getConnection();
                String insertSql = "INSERT into searchengines VALUES (?,?,?);";
                preparedStatement1 = connection.prepareStatement(insertSql);
                preparedStatement1.setInt(1, groupId);
                preparedStatement1.setString(2, configUrl);
                preparedStatement1.setBoolean(3, isActive);
                preparedStatement1.execute();

            }
            else if (action.equalsIgnoreCase("Update")) {
                int groupId = Integer.parseInt(request.getParameter("group-id"));
                String configUrl = request.getParameter("config-url");
                boolean isActive = Boolean.parseBoolean(request.getParameter("is-active"));
                connection = databaseConnection.getConnection();
                String updateSql = "UPDATE searchengines SET config_url = ?, active = ? WHERE group_id = ?;";
                preparedStatement1 = connection.prepareStatement(updateSql);
                preparedStatement1.setString(1, configUrl);
                preparedStatement1.setBoolean(2, isActive);
                preparedStatement1.setInt(3, groupId);
                preparedStatement1.executeUpdate();


            }
            else if (action.equalsIgnoreCase("Delete")) {
                int deleteId = Integer.parseInt(request.getParameter("delete-id"));
                connection = databaseConnection.getConnection();
                String deleteSql = "DELETE FROM searchengines WHERE group_id = ?;";
                preparedStatement1 = connection.prepareStatement(deleteSql);
                preparedStatement1.setInt(1, deleteId);
                preparedStatement1.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        response.sendRedirect("configureSearchEngine.jsp");
    }
}
