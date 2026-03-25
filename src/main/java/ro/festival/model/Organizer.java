package ro.festival.model;

import java.util.List;

public class Organizer {
    private int id_organizer;
    private final String companyName;
    private String organizerName;
    private final List<Event> events;

    public Organizer(int id_organizer, String companyName, String organizerName, List<Event> events) {
        this.id_organizer = id_organizer;
        this.companyName = companyName;
        this.organizerName = organizerName;
        this.events = events;
    }

    public Organizer(String companyName, String organizerName, List<Event> events) {
        this(0, companyName, organizerName, events);
    }

    public int getId() {
        return id_organizer;
    }

    public void setId(int id_organizer) {
        this.id_organizer = id_organizer;
    }


    public String getCompanyName() {
        return companyName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String s) {
        this.organizerName = s;
    }

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Organizer company: ").append(companyName).append("\n");
        sb.append("Representative: ").append(organizerName).append("\n");
        sb.append("Events organized in this festival:\n");

        if (events == null || events.isEmpty()) {
            sb.append("No events assigned yet.");
        } else {
            for (Event e : events) {
                sb.append("\n   - ").append(e.getEventName()).append(" (").append(e.getDay()).append(")");
            }
        }
        return sb.toString();
    }
}
