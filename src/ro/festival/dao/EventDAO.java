package ro.festival.dao;

import ro.festival.database.DBConnection;
import ro.festival.model.Event;
import ro.festival.model.FestivalDay;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventDAO extends BaseDAO<Event, Integer> {

    private static EventDAO instance;

    public static EventDAO getInstance() {
        if (instance == null) {
            instance = new EventDAO();
        }
        return instance;
    }

    @Override
    public void create(Event event) {
        String sql = "INSERT INTO Event (day, id_organizer, eventName, startTime, endTime) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             // după ce rulez acest statement, vreau să am acces la valorile generate automat de DB, cum ar fi AUTO_INCREMENT-urile.
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getDay().name());
            if (event.getId_organizer() != 0) stmt.setInt(2, event.getId_organizer());
            else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, event.getEventName());
            stmt.setTime(4, Time.valueOf(event.getStartTime()));
            stmt.setTime(5, Time.valueOf(event.getEndTime()));
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                event.setIdEvent(keys.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error inserting event:");
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Event> read(Integer id) {
        String sql = "SELECT * FROM Event WHERE id_event = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Event event = new Event(
                        rs.getInt("id_event"),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getInt("id_organizer"),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime()
                );
                return Optional.of(event);
            }

        } catch (SQLException e) {
            System.err.println("Error reading event:");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Event event) {
        String sql = "UPDATE Event SET day = ?, id_organizer = ?, eventName = ?, startTime = ?, endTime = ? WHERE id_event = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getDay().name());
            stmt.setInt(2, event.getId_organizer());
            stmt.setString(3, event.getEventName());
            stmt.setTime(4, Time.valueOf(event.getStartTime()));
            stmt.setTime(5, Time.valueOf(event.getEndTime()));
            stmt.setInt(6, event.getIdEvent());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating event:");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM Event WHERE id_event = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting event:");
            e.printStackTrace();
        }
    }

    public List<Event> readAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id_event"),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getInt("id_organizer"),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime()
                );
                events.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error reading all base events:");
            e.printStackTrace();
        }

        return events;
    }


    public boolean existsByNameAndTime(String name, LocalTime start, LocalTime end) {
        String sql = "SELECT COUNT(*) FROM Event WHERE eventName = ? AND startTime = ? AND endTime = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setTime(2, Time.valueOf(start));
            stmt.setTime(3, Time.valueOf(end));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking event existence:");
            e.printStackTrace();
        }

        return false;
    }

    public List<Event> getEventsByDay(FestivalDay day) {
        List<Event> result = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE day = ? ORDER BY startTime";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, day.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id_event"),
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getInt("id_organizer"),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime()
                );
                result.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error reading events by day:");
            e.printStackTrace();
        }

        return result;
    }


}
