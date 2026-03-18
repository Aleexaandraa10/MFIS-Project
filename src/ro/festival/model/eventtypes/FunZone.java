package ro.festival.model.eventtypes;

import ro.festival.model.Event;
import ro.festival.model.FestivalDay;
import ro.festival.model.Game;

import java.time.LocalTime;
import java.util.List;

public class FunZone extends Event {
    private final List<Game> games;

    public FunZone(int id_event, FestivalDay day, String eventName,
                   LocalTime startTime, LocalTime endTime,
                   List<Game> games) {
        super(id_event, day, 0, eventName, startTime, endTime);
        this.games = games;
    }

    public FunZone(String eventName, LocalTime startTime, LocalTime endTime, FestivalDay day,
                   List<Game> games) {
        super(eventName, startTime, endTime, day);
        this.games = games;
    }

    public List<Game> getGames() {
        return games;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\n   All the games available:");
        for (Game game : games) {
            sb.append("\n   - ").append(game.toString());
        }
        return sb.toString();
    }
}
