package ro.festival.database;

public class DBInitializer {

    public static void setupDatabase() {
        System.out.println("Running setup.sql...");
        SQLScriptRunner.runScript("src/main.java.resources/setup.sql");
    }

    public static void dropDatabase() {
        System.out.println("Running drop_tables.sql...");
        SQLScriptRunner.runScript("src/main.java.resources/drop_tables.sql");
    }

    public static void resetDatabase() {
        dropDatabase();
        setupDatabase();
    }
}
