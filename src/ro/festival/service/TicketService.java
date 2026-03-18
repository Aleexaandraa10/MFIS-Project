package ro.festival.service;
import ro.festival.dao.TicketDAO;
import ro.festival.model.Ticket;

import java.util.List;

public class TicketService {
    private static TicketService instance;
    private final TicketDAO ticketDAO;

    private TicketService() {
        this.ticketDAO = TicketDAO.getInstance();
    }

    public static TicketService getInstance() {
        if (instance == null) {
            instance = new TicketService();
        }
        return instance;
    }

    public void addTicket(Ticket ticket) {
        if (!ticketDAO.existsByCode(ticket.getCode())) {
            ticketDAO.create(ticket);
        }
    }

    public Ticket getTicketByCode(String code) {
        return ticketDAO.read(code).orElse(null);
    }

    public List<Ticket> getAllTickets() {
        return ticketDAO.readAll();
    }

    public void updateTicket(Ticket t) {
        ticketDAO.update(t);
    }

    public void deleteTicket(String code) {
        ticketDAO.delete(code);
    }
}
