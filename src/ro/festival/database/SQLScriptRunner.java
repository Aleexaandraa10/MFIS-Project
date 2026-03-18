package ro.festival.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class SQLScriptRunner {

    public static void runScript(String filePath) {
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] commands = content.split(";");

            for (String command : commands) {
                command = command.trim();
                if (!command.isEmpty() && !command.startsWith("--")) {
                    try {
                        stmt.execute(command);
                        System.out.println("Executed: " + command);
                    } catch (SQLException e) {
                        System.err.println("Error executing: " + command);
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Finished executing: " + filePath);

        } catch (IOException | SQLException e) {
            System.err.println("Error running SQL script: " + filePath);
            e.printStackTrace();
        }
    }
}
