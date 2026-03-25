package ro.festival.dao.eventtypes;

import ro.festival.dao.EventDAO;
import ro.festival.database.DBConnection;
import ro.festival.model.FestivalDay;
import ro.festival.model.eventtypes.CampEats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CampEatsDAO {

    private static CampEatsDAO instance;

    public static CampEatsDAO getInstance() {
        if (instance == null) {
            instance = new CampEatsDAO();
        }
        return instance;
    }

    public Optional<CampEats> read(int id) {
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               c.vendorName, c.openUntilLate
        FROM Event e
        JOIN CampEats c ON e.id_event = c.id_event
        WHERE e.id_event = ?
    """;

        String foodSql = "SELECT foodType FROM CampEatsFoodType WHERE id_event = ?";

        
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                List<String> foodTypes = new ArrayList<>();
                try (PreparedStatement foodStmt = conn.prepareStatement(foodSql)) {
                    foodStmt.setInt(1, id);
                    ResultSet frs = foodStmt.executeQuery();
                    while (frs.next()) {
                        foodTypes.add(frs.getString("foodType"));
                    }
                }

                CampEats ce = new CampEats(
                        id,
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        rs.getString("vendorName"),
                        foodTypes,
                        rs.getBoolean("openUntilLate")
                );
                ce.setId_organizer(rs.getInt("id_organizer"));
                return Optional.of(ce);
            }
        } catch (SQLException e) {
            System.err.println("Error reading CampEats:");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void create(CampEats campEats) {
        // 1. Inserăm în Event
        EventDAO.getInstance().create(campEats); // setează id_event

        // 2. Inserăm în CampEats
        String campSql = "INSERT INTO CampEats (id_event, vendorName, openUntilLate) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(campSql)) {

            stmt.setInt(1, campEats.getIdEvent());
            stmt.setString(2, campEats.getVendorName());
            stmt.setBoolean(3, campEats.isOpenUntilLate());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error inserting CampEats:");
            e.printStackTrace();
            return; // nu continuăm cu foodType dacă nu s-a inserat CampEats
        }

        // 3. Inserăm în CampEatsFoodType (lista de mâncăruri)
        String foodSql = "INSERT INTO CampEatsFoodType (id_event, foodType) VALUES (?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(foodSql)) {

            for (String food : campEats.getFoodType()) {
                stmt.setInt(1, campEats.getIdEvent());
                stmt.setString(2, food);
                stmt.addBatch();
            }
            stmt.executeBatch();

        } catch (SQLException e) {
            System.err.println("Error inserting CampEatsFoodType:");
            e.printStackTrace();
        }
    }

    public void delete(int idEvent) {
        String deleteFoodTypes = "DELETE FROM CampEatsFoodType WHERE id_event = ?";
        String deleteCampEats = "DELETE FROM CampEats WHERE id_event = ?";

        try (Connection conn = DBConnection.connect()) {
            try (PreparedStatement stmt1 = conn.prepareStatement(deleteFoodTypes)) {
                stmt1.setInt(1, idEvent);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(deleteCampEats)) {
                stmt2.setInt(1, idEvent);
                stmt2.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error deleting CampEats with ID: " + idEvent);
            e.printStackTrace();
        }
    }

    public List<CampEats> getAllCampEats() {
        List<CampEats> campEatsList = new ArrayList<>();
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               c.vendorName, c.openUntilLate
        FROM Event e
        JOIN CampEats c ON e.id_event = c.id_event
    """;

        String foodSql = "SELECT foodType FROM CampEatsFoodType WHERE id_event = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_event");

                // adunăm lista de foodType pentru fiecare CampEats
                List<String> foodTypes = new ArrayList<>();
                try (PreparedStatement foodStmt = conn.prepareStatement(foodSql)) {
                    foodStmt.setInt(1, id);
                    ResultSet frs = foodStmt.executeQuery();
                    while (frs.next()) {
                        foodTypes.add(frs.getString("foodType"));
                    }
                }

                CampEats campEats = new CampEats(
                        id,
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        rs.getString("vendorName"),
                        foodTypes,
                        rs.getBoolean("openUntilLate")
                );
                campEats.setId_organizer(rs.getInt("id_organizer"));
                campEatsList.add(campEats);
            }

        } catch (SQLException e) {
            System.err.println("Error reading CampEats:");
            e.printStackTrace();
        }

        return campEatsList;
    }
}