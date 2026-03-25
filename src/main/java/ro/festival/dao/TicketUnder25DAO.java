package ro.festival.dao;
import ro.festival.database.DBConnection;
import java.sql.*;

public class TicketUnder25DAO {
    private static TicketUnder25DAO instance;

    private TicketUnder25DAO() {}

    public static TicketUnder25DAO getInstance() {
        if (instance == null) {
            instance = new TicketUnder25DAO();
        }
        return instance;
    }

    public void delete(String code) {
        String sql = "DELETE FROM TicketUnder25 WHERE code = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting TicketUnder25 with code " + code);
            e.printStackTrace();
        }
    }
}
