package ro.festival.dao;

import ro.festival.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParticipantEventDAO {
    private static ParticipantEventDAO instance;

    public static ParticipantEventDAO getInstance() {
        if (instance == null)
            instance = new ParticipantEventDAO();
        return instance;
    }

    public void addParticipantToEvent(int participantId, int eventId) {
        String sql = "INSERT INTO ParticipantEvent (id_participant, id_event) VALUES (?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, participantId);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getEventIdsForParticipant(int participantId) {
        List<Integer> events = new ArrayList<>();
        String sql = "SELECT id_event FROM ParticipantEvent WHERE id_participant = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(rs.getInt("id_event"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public Map<Integer, Long> getParticipationCounts() {
        Map<Integer, Long> result = new HashMap<>();
        String sql = "SELECT id_event, COUNT(*) as cnt FROM ParticipantEvent GROUP BY id_event";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int eventId = rs.getInt("id_event");
                long count = rs.getLong("cnt");
                result.put(eventId, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("DEBUG: Participation counts");
        result.forEach((eventId, count) -> System.out.println("Event ID " + eventId + " → " + count + " participant(s)"));

        return result;
    }
}
