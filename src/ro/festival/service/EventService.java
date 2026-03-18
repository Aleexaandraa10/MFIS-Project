package ro.festival.service;

import ro.festival.dao.EventDAO;
import ro.festival.model.Event;
import ro.festival.model.FestivalDay;

import java.util.List;
import java.util.Optional;

public class EventService {
    private static EventService instance;
    private final EventDAO eventDAO;

    private EventService() {
        this.eventDAO = EventDAO.getInstance();
    }

    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    public void addEvent(Event event) {
        // verifică dacă există deja un event identic (după nume și interval orar)
        boolean exists = eventDAO.existsByNameAndTime(event.getEventName(), event.getStartTime(), event.getEndTime());
        if (!exists) {
            eventDAO.create(event);
        }
    }
    public List<Event> getAllEvents() {
        return eventDAO.readAll();
    }

    public Optional<Event> getEventById(int id) {
        return eventDAO.read(id);
    }

    public void updateEvent(Event event) {
        eventDAO.update(event);
    }

    public void deleteEvent(Integer id) {
        eventDAO.delete(id);
    }
    public List<Event> getEventsByDay(FestivalDay day) {
        return eventDAO.getEventsByDay(day);
    }

}
