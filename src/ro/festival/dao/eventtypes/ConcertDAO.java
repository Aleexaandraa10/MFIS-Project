package ro.festival.dao.eventtypes;

import ro.festival.dao.EventDAO;
import ro.festival.database.DBConnection;
import ro.festival.model.FestivalDay;
import ro.festival.model.eventtypes.Concert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConcertDAO {

    private static ConcertDAO instance;

    public static ConcertDAO getInstance() {
        if (instance == null) {
            instance = new ConcertDAO();
        }
        return instance;
    }

    public Optional<Concert> read(int id) {
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               c.artist, c.genre
        FROM Event e
        JOIN Concert c ON e.id_event = c.id_event
        WHERE e.id_event = ?
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Concert concert = new Concert(
                        rs.getInt("id_event"),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        rs.getString("artist"),
                        rs.getString("genre")
                );
                concert.setId_organizer(rs.getInt("id_organizer"));
                return Optional.of(concert);
            }
        } catch (SQLException e) {
            System.err.println("Error reading concert:");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void create(Concert concert) {
        // 1. Inserează în Event și setează id_event
        EventDAO.getInstance().create(concert); // face și setIdEvent()

        // 2. Inserează în Concert
        String sql = "INSERT INTO Concert (id_event, artist, genre) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, concert.getIdEvent());
            stmt.setString(2, concert.getArtist());
            stmt.setString(3, concert.getGenre());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error inserting Concert:");
            e.printStackTrace();
        }
    }

    public void delete(int idEvent) {
        String sql = "DELETE FROM Concert WHERE id_event = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting Concert with ID: " + idEvent);
            e.printStackTrace();
        }
    }

    public List<Concert> getAllConcerts() {
        List<Concert> concerts = new ArrayList<>();
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime,
               c.artist, c.genre
        FROM Event e
        JOIN Concert c ON e.id_event = c.id_event
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Concert concert = new Concert(
                        rs.getInt("id_event"),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        rs.getString("artist"),
                        rs.getString("genre")
                );
                concert.setId_organizer(rs.getInt("id_organizer"));
                concerts.add(concert);
            }

        } catch (SQLException e) {
            System.err.println("Error reading concerts:");
            e.printStackTrace();
        }

        return concerts;
    }

}
