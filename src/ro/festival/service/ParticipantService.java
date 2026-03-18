package ro.festival.service;
import ro.festival.dao.ParticipantDAO;
import ro.festival.model.Participant;

import java.util.List;

public class ParticipantService {
    private static ParticipantService instance;
    private Participant lastTournamentWinner;
    private final ParticipantDAO participantDAO;

    private ParticipantService() {
        this.participantDAO = ParticipantDAO.getInstance();
    }

    public static ParticipantService getInstance() {
        if (instance == null) {
            instance = new ParticipantService();
        }
        return instance;
    }

    public Participant getLastTournamentWinner() {
        return lastTournamentWinner;
    }

    public void setLastTournamentWinner(Participant winner) {
        this.lastTournamentWinner = winner;
    }

    public void addParticipant(Participant p) {
        participantDAO.create(p);
    }

    public Participant getParticipantById(int id) {
        return participantDAO.read(id).orElse(null);
    }

    public List<Participant> getAllParticipants() {
        return participantDAO.readAll();
    }

    public void updateParticipant(Participant p) {
        participantDAO.update(p);
    }

    public void deleteParticipant(int id) {
        participantDAO.delete(id);
    }

    public Participant findByTicketCode(String code) {
        return participantDAO.findByTicketCode(code);
    }
}
