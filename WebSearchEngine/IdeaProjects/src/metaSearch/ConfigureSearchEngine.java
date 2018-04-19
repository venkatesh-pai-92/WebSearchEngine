package metaSearch;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Venkatesh on 01-02-2017.
 */
public class ConfigureSearchEngine {
    public int groupId;
    public String configUrl;
    public boolean isActive;

    public List<ConfigureSearchEngine> getSearchEngines(){
        List<ConfigureSearchEngine> configureSearchEngineList = new ArrayList<>();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        PreparedStatement preparedStatement1 = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = databaseConnection.getConnection();
            String sql = "SELECT * FROM searchengines ORDER BY group_id;";
            preparedStatement1 = connection.prepareStatement(sql);
            resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()){
                ConfigureSearchEngine se = new ConfigureSearchEngine();
                se.groupId = resultSet.getInt(1);
                se.configUrl = resultSet.getString(2);
                se.isActive = resultSet.getBoolean(3);
                configureSearchEngineList.add(se);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return configureSearchEngineList;
    }
}
