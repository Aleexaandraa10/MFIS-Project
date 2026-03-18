package ro.festival.dao.eventtypes;

import ro.festival.dao.EventDAO;
import ro.festival.database.DBConnection;
import ro.festival.model.FestivalDay;
import ro.festival.model.Game;
import ro.festival.model.eventtypes.FunZone;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunZoneDAO {
    private static FunZoneDAO instance;

    public static FunZoneDAO getInstance() {
        if (instance == null) {
            instance = new FunZoneDAO();
        }
        return instance;
    }

    public Optional<FunZone> read(int id) {
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime
        FROM Event e
        JOIN FunZone fz ON e.id_event = fz.id_event
        WHERE e.id_event = ?
    """;

        String gameSql = """
        SELECT g.id_game, g.gameName, g.isOpenAllNight, g.hasPrize, g.maxCapacity
        FROM FunZoneGames fz JOIN Game g ON fz.id_game = g.id_game
        WHERE fz.id_event = ?
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                List<Game> games = new ArrayList<>();
                try (PreparedStatement gameStmt = conn.prepareStatement(gameSql)) {
                    gameStmt.setInt(1, id);
                    ResultSet grs = gameStmt.executeQuery();
                    while (grs.next()) {
                        games.add(new Game(
                                grs.getInt("id_game"),
                                grs.getString("gameName"),
                                grs.getBoolean("isOpenAllNight"),
                                grs.getBoolean("hasPrize"),
                                grs.getInt("maxCapacity")
                        ));
                    }
                }

                FunZone fz = new FunZone(
                        id,
                        FestivalDay.valueOf(rs.getString("day")),
                        rs.getString("eventName"),
                        rs.getTime("startTime").toLocalTime(),
                        rs.getTime("endTime").toLocalTime(),
                        games
                );
                fz.setId_organizer(rs.getInt("id_organizer"));
                return Optional.of(fz);
            }
        } catch (SQLException e) {
            System.err.println("Error reading FunZone:");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void create(FunZone funZone) {
        // 1. Inserare în Event
        EventDAO.getInstance().create(funZone);

        // 2. Inserare în FunZone
        String sqlFunZone = "INSERT INTO FunZone (id_event) VALUES (?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sqlFunZone)) {

            stmt.setInt(1, funZone.getIdEvent());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error inserting into FunZone:");
            e.printStackTrace();
            return;
        }

        // 3. Inserare Game-uri și asociere în FunZoneGames
        for (Game game : funZone.getGames()) {
            int gameId = insertOrGetGame(game);
            if (gameId != -1) {
                insertFunZoneGameMapping(funZone.getIdEvent(), gameId);
            }
        }
    }

    public void delete(int idEvent) {
        String sql = "DELETE FROM FunZone WHERE id_event = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting FunZone with ID: " + idEvent);
            e.printStackTrace();
        }
    }

    private int insertOrGetGame(Game game) {
        // Caută dacă jocul există deja (după nume și capacitate)
        String selectSQL = "SELECT id_game FROM Game WHERE gameName = ? AND maxCapacity = ?";
        String insertSQL = "INSERT INTO Game (gameName, isOpenAllNight, hasPrize, maxCapacity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

            selectStmt.setString(1, game.getGameName());
            selectStmt.setInt(2, game.getMaxCapacity());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_game"); // deja există
            }

            // nu există → îl inserăm
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, game.getGameName());
                insertStmt.setBoolean(2, game.isOpenAllNight());
                insertStmt.setBoolean(3, game.hasPrize());
                insertStmt.setInt(4, game.getMaxCapacity());
                insertStmt.executeUpdate();

                ResultSet keys = insertStmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting or fetching Game:");
            e.printStackTrace();
        }
        return -1;
    }

    private void insertFunZoneGameMapping(int idEvent, int idGame) {
        String sql = "INSERT INTO FunZoneGames (id_event, id_game) VALUES (?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEvent);
            stmt.setInt(2, idGame);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error inserting FunZoneGames mapping:");
            e.printStackTrace();
        }
    }

    public List<FunZone> getAllFunZones() {
        List<FunZone> funZones = new ArrayList<>();
        String sql = """
        SELECT e.id_event, e.day, e.id_organizer, e.eventName, e.startTime, e.endTime
        FROM Event e
        JOIN FunZone fz ON e.id_event = fz.id_event
    """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int eventId = rs.getInt("id_event");
                FestivalDay day = FestivalDay.valueOf(rs.getString("day"));
                int organizerId = rs.getInt("id_organizer");
                String name = rs.getString("eventName");
                LocalTime start = rs.getTime("startTime").toLocalTime();
                LocalTime end = rs.getTime("endTime").toLocalTime();

                // Adaugă jocurile asociate
                List<Game> games = new ArrayList<>();
                String gameSql = """
                SELECT g.id_game, g.gameName, g.isOpenAllNight, g.hasPrize, g.maxCapacity
                FROM FunZoneGames fzg
                JOIN Game g ON fzg.id_game = g.id_game
                WHERE fzg.id_event = ?
            """;
                try (PreparedStatement gameStmt = conn.prepareStatement(gameSql)) {
                    gameStmt.setInt(1, eventId);
                    ResultSet gameRs = gameStmt.executeQuery();
                    while (gameRs.next()) {
                        Game game = new Game(
                                gameRs.getInt("id_game"),
                                gameRs.getString("gameName"),
                                gameRs.getBoolean("isOpenAllNight"),
                                gameRs.getBoolean("hasPrize"),
                                gameRs.getInt("maxCapacity")
                        );
                        games.add(game);
                    }
                }

                FunZone fz = new FunZone(eventId, day, name, start, end, games);
                fz.setId_organizer(organizerId);
                funZones.add(fz);
            }

        } catch (SQLException e) {
            System.err.println("Error reading FunZones:");
            e.printStackTrace();
        }

        return funZones;
    }

}
