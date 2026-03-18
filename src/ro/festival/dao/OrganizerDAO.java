package ro.festival.dao;

import ro.festival.database.DBConnection;
import ro.festival.model.Organizer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizerDAO extends BaseDAO<Organizer, Integer> {
    private static OrganizerDAO instance;

    public static OrganizerDAO getInstance() {
        if (instance == null) {
            instance = new OrganizerDAO();
        }
        return instance;
    }

    @Override
    public void create(Organizer organizer) {
        String sql = "INSERT INTO Organizer (companyName, organizerName) VALUES (?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, organizer.getCompanyName());
            stmt.setString(2, organizer.getOrganizerName());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                organizer.setId(keys.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error inserting organizer:");
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Organizer> read(Integer id) {
        String sql = "SELECT * FROM Organizer WHERE id_organizer = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Organizer o = new Organizer(
                        rs.getInt("id_organizer"),
                        rs.getString("companyName"),
                        rs.getString("organizerName"),
                        new ArrayList<>()
                );
                return Optional.of(o);
            }

        } catch (SQLException e) {
            System.err.println("Error reading organizer:");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void update(Organizer organizer) {
        String sql = "UPDATE Organizer SET companyName = ?, organizerName = ? WHERE id_organizer = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, organizer.getCompanyName());
            stmt.setString(2, organizer.getOrganizerName());
            stmt.setInt(3, organizer.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating organizer:");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM Organizer WHERE id_organizer = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting organizer:");
            e.printStackTrace();
        }
    }

    public List<Organizer> readAll() {
        List<Organizer> list = new ArrayList<>();
        String sql = "SELECT * FROM Organizer";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Organizer o = new Organizer(
                        rs.getInt("id_organizer"),
                        rs.getString("companyName"),
                        rs.getString("organizerName"),
                        new ArrayList<>()
                );
                list.add(o);
            }

        } catch (SQLException e) {
            System.err.println("Error reading all organizers:");
            e.printStackTrace();
        }

        return list;
    }
}
