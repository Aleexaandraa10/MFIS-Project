package ro.festival.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseInfo {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword())) {

            DatabaseMetaData metaData = conn.getMetaData();

            String dbProductName = metaData.getDatabaseProductName();
            String dbProductVersion = metaData.getDatabaseProductVersion();

            System.out.println("Database Type: " + dbProductName);
            System.out.println("Database Version: " + dbProductVersion);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
