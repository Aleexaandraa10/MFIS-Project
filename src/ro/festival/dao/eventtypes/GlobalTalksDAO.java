package ro.festival.dao.eventtypes;

import ro.festival.dao.EventDAO;
import ro.festival.database.DBConnection;
import ro.festival.model.FestivalDay;
import ro.festival.model.eventtypes.GlobalTalks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GlobalTalksDAO {

    private static GlobalTalksDAO instance;

    public static GlobalTalksDAO getInstance() {
        if (instance == null) {
            instance = new GlobalTalksDAO();
        }
        return instance;
    }

    public Optional<GlobalTalks> read(int id) {
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               g.speakerName, g.topic, g.seats
        FROM Event e
        JOIN GlobalTalks g ON e.id_event = g.id_event
        WHERE e.id_event = ?
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                GlobalTalks gt = new GlobalTalks(
                        id,
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        rs.getString("speakerName"),
                        rs.getString("topic"),
                        rs.getInt("seats")
                );
                gt.setId_organizer(rs.getInt("id_organizer"));
                return Optional.of(gt);
            }
        } catch (SQLException e) {
            System.err.println("Error reading GlobalTalks:");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void create(GlobalTalks talk) {
        // 1. Inserăm în Event
        EventDAO.getInstance().create(talk); // setează id_event

        // 2. Inserăm în GlobalTalks
        String sql = "INSERT INTO GlobalTalks (id_event, speakerName, topic, seats) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, talk.getIdEvent());
            stmt.setString(2, talk.getSpeakerName());
            stmt.setString(3, talk.getTopic());
            stmt.setInt(4, talk.getSeats());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error inserting GlobalTalks:");
            e.printStackTrace();
        }
    }

    public void delete(int idEvent) {
        String sql = "DELETE FROM GlobalTalks WHERE id_event = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting GlobalTalks with ID: " + idEvent);
            e.printStackTrace();
        }
    }

    public List<GlobalTalks> getAllGlobalTalks() {
        List<GlobalTalks> talks = new ArrayList<>();
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               g.speakerName, g.topic, g.seats
        FROM Event e
        JOIN GlobalTalks g ON e.id_event = g.id_event
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                GlobalTalks talk = new GlobalTalks(
                        rs.getInt("id_event"),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        rs.getString("speakerName"),
                        rs.getString("topic"),
                        rs.getInt("seats")
                );
                talk.setId_organizer(rs.getInt("id_organizer"));
                talks.add(talk);
            }

        } catch (SQLException e) {
            System.err.println("Error reading GlobalTalks:");
            e.printStackTrace();
        }

        return talks;
    }

}
