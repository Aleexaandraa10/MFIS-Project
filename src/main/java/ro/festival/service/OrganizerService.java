package ro.festival.service;
import ro.festival.dao.OrganizerDAO;
import ro.festival.model.Organizer;
import java.util.List;


public class OrganizerService {
    private static OrganizerService instance;
    private final OrganizerDAO organizerDAO;

    private OrganizerService() {
        this.organizerDAO = OrganizerDAO.getInstance();
    }

    public static OrganizerService getInstance() {
        if (instance == null) {
            instance = new OrganizerService();
        }
        return instance;
    }

    public void addOrganizer(Organizer organizer) {
        organizerDAO.create(organizer);
    }

    public Organizer getOrganizerById(int id) {
        return organizerDAO.read(id).orElse(null);
    }


    public List<Organizer> getAllOrganizers() {
        return organizerDAO.readAll();
    }

    public void updateOrganizer(Organizer organizer) {
        organizerDAO.update(organizer);
    }

    public void deleteOrganizer(int id) {
        organizerDAO.delete(id);
    }
}
