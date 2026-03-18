package ro.festival.dao;

import ro.festival.database.DBConnection;
import ro.festival.model.Participant;
import ro.festival.model.Ticket;
import ro.festival.service.TicketService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipantDAO extends BaseDAO<Participant, Integer> {

    private static ParticipantDAO instance;

    public static ParticipantDAO getInstance() {
        if (instance == null) {
            instance = new ParticipantDAO();
        }
        return instance;
    }

    @Override
    public void create(Participant participant) {
        String sql = "INSERT INTO Participant (participantName, age, code) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, participant.getParticipantName());
            stmt.setInt(2, participant.getAge());
            stmt.setString(3, participant.getTicket().getCode());
            stmt.executeUpdate();

            // Ia ID-ul generat de MySQL și setează-l în obiectul Java
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                participant.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error inserting participant:");
            e.printStackTrace();
        }
    }


    @Override
    public Optional<Participant> read(Integer id) {
        String sql = "SELECT id_participant, participantName, age, code FROM Participant WHERE id_participant = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("participantName");
                int age = rs.getInt("age");
                String ticketCode = rs.getString("code");
                Ticket ticket = TicketService.getInstance().getTicketByCode(ticketCode);
                Participant p = new Participant(name, age, ticket);
                return Optional.of(p);
            }

        } catch (SQLException e) {
            System.err.println("Error reading participant:");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void update(Participant participant) {
        String sql = "UPDATE Participant SET participantName = ?, age = ?, code = ? WHERE id_participant = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participant.getParticipantName());
            stmt.setInt(2, participant.getAge());
            stmt.setString(3, participant.getTicket().getCode());
            stmt.setInt(4, participant.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating participant:");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM Participant WHERE id_participant = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting participant:");
            e.printStackTrace();
        }
    }

    public List<Participant> readAll() {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT id_participant, participantName, age, code FROM Participant";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("participantName");
                int age = rs.getInt("age");
                String ticketCode = rs.getString("code");
                Ticket ticket = TicketService.getInstance().getTicketByCode(ticketCode);

                Participant p = new Participant(name, age, ticket);
                p.setId(rs.getInt("id_participant"));
                participants.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error reading all participants:");
            e.printStackTrace();
        }

        return participants;
    }

    public Participant findByNameAndAge(String name, int age) {
        String sql = "SELECT * FROM Participant WHERE participantName = ? AND age = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Participant p = new Participant(
                        rs.getString("participantName"),
                        rs.getInt("age"),
                        TicketService.getInstance().getTicketByCode(rs.getString("code"))
                );
                p.setId(rs.getInt("id_participant"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Participant findByTicketCode(String code) {
        String sql = "SELECT * FROM Participant WHERE code = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Participant(
                        rs.getInt("id_participant"),
                        rs.getString("participantName"),
                        rs.getInt("age"),
                        TicketDAO.getInstance().read(rs.getString("code")).orElse(null)
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding participant by ticket code:");
            e.printStackTrace();
        }

        return null;
    }


}
