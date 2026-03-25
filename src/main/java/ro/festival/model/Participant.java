package ro.festival.model;

public class Participant {
    private int id;
    private final String participantName;
    private int age;
    private final Ticket ticket;

    public Participant(int id, String participantName, int age, Ticket ticket) {
        this.id = id;
        this.participantName = participantName;
        this.age = age;
        this.ticket = ticket;
    }

    public Participant(String participantName, int age, Ticket ticket) {
        this(0, participantName, age, ticket);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParticipantName() {
        return participantName;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public String toString() {
        return "Participant: " + participantName +
                "\nAge: " + age +
                (ticket != null ? "\n" + ticket : "\nNo ticket assigned yet.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return id == that.id; // sau getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
