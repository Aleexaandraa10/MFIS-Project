package ro.festival.dao.eventtypes;

import ro.festival.dao.EventDAO;
import ro.festival.database.DBConnection;
import ro.festival.model.FestivalDay;
import ro.festival.model.eventtypes.DJ;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DJDAO {
    private static DJDAO instance;

    public static DJDAO getInstance() {
        if (instance == null) {
            instance = new DJDAO();
        }
        return instance;
    }

    public Optional<DJ> read(int id) {
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               d.djName, d.isMainStage
        FROM Event e
        JOIN DJ d ON e.id_event = d.id_event
        WHERE e.id_event = ?
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                DJ dj = new DJ(
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("djName"),
                        rs.getBoolean("isMainStage")
                );
                dj.setIdEvent(rs.getInt("id_event"));
                dj.setId_organizer(rs.getInt("id_organizer"));
                return Optional.of(dj);
            }
        } catch (SQLException e) {
            System.err.println("Error reading DJ:");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void create(DJ dj) {
        // 1. Inserăm în Event
        EventDAO.getInstance().create(dj); // setează id_event

        // 2. Inserăm în DJ
        String sql = "INSERT INTO DJ (id_event, djName, isMainStage) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dj.getIdEvent());
            stmt.setString(2, dj.getDjName());
            stmt.setBoolean(3, dj.getIsMainStage());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error inserting DJ:");
            e.printStackTrace();
        }
    }

    public void delete(int idEvent) {
        String sql = "DELETE FROM DJ WHERE id_event = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting DJ with ID: " + idEvent);
            e.printStackTrace();
        }
    }

    public List<DJ> getAllDJs() {
        List<DJ> djs = new ArrayList<>();
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               d.djName, d.isMainStage
        FROM Event e
        JOIN DJ d ON e.id_event = d.id_event
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DJ dj = new DJ(
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("djName"),
                        rs.getBoolean("isMainStage")
                );
                dj.setIdEvent(rs.getInt("id_event"));
                dj.setId_organizer(rs.getInt("id_organizer"));
                djs.add(dj);
            }

        } catch (SQLException e) {
            System.err.println("Error reading DJs:");
            e.printStackTrace();
        }

        return djs;
    }

}
