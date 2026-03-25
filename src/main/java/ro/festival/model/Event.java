package ro.festival.model;

import java.time.LocalTime;


// final pt un atribut = nu se mai poate schimba referinta listei
// dar pot modifica continutul listei --> .add(), .clear()

public class Event {
    private int id_event;
    private FestivalDay day;
    private int id_organizer;
    private final String eventName;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Event(int id_event, FestivalDay day, int id_organizer, String eventName, LocalTime startTime, LocalTime endTime) {
        this.id_event = id_event;
        this.day = day;
        this.id_organizer = id_organizer;
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Event(String eventName, LocalTime startTime, LocalTime endTime, FestivalDay day) {
        this(0, day, 0, eventName, startTime, endTime);
    }
    public int getIdEvent() {
        return id_event;
    }

    public void setIdEvent(int id_event) {
        this.id_event = id_event;
    }
    public int getId_organizer() { return id_organizer; }

    public void setId_organizer(int id) {
        this.id_organizer = id;
    }
    public String getEventName() { return eventName; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public FestivalDay getDay() { return day; }

    public void setDay(FestivalDay day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return eventName + " starts at " + startTime.toString() + " and ends at " + endTime.toString() + " on " + day + ".";
    }


}
