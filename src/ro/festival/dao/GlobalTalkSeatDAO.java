package ro.festival.dao;

import ro.festival.database.DBConnection;
import ro.festival.model.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GlobalTalkSeatDAO {
    private static GlobalTalkSeatDAO instance;

    public static GlobalTalkSeatDAO getInstance() {
        if (instance == null)
            instance = new GlobalTalkSeatDAO();
        return instance;
    }

    public void reserveSeat(int participantId, int talkId) {
        String sql = "INSERT INTO GlobalTalkSeat (id_participant, id_event) VALUES (?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participantId);
            stmt.setInt(2, talkId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error reserving seat: " + e.getMessage());
        }
    }

    public List<Participant> getParticipantsForTalk(int talkId) {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT id_participant FROM GlobalTalkSeat WHERE id_event = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, talkId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int participantId = rs.getInt("id_participant");
                ParticipantDAO.getInstance().read(participantId).ifPresent(participants::add);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participants;
    }

}
