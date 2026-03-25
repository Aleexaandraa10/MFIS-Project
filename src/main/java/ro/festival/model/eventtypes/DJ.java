package ro.festival.model.eventtypes;

import ro.festival.model.Event;
import ro.festival.model.FestivalDay;

import java.time.LocalTime;

public class DJ extends Event {
    private final String djName;
    private final boolean isMainStage;

    public DJ(int id_event, FestivalDay day, String eventName,
              LocalTime startTime, LocalTime endTime,
              String djName, boolean isMainStage) {
        super(id_event, day, 0, eventName, startTime, endTime);
        this.djName = djName;
        this.isMainStage = isMainStage;
    }

    public DJ(String eventName, LocalTime startTime, LocalTime endTime, FestivalDay day,
              String djName, boolean isMainStage) {
        super(eventName, startTime, endTime, day);
        this.djName = djName;
        this.isMainStage = isMainStage;
    }

    public String getDjName() {
        return djName;
    }

    public boolean getIsMainStage() {
        return isMainStage;
    }

    @Override
    public String toString() {
        return super.toString() + "\n    The DJ features " + djName + ". You can find the DJ on " +
                (isMainStage ? "the main stage." : "the secondary stage.");
    }
}
