package avmf.model;

/**
 * BuyTicketResult — obiectul returnat de buyTicketTestable()
 * Conține statusul obținut și detaliile biletului (dacă inputul e valid).
 */
public class BuyTicketResult {

    private final BuyTicketStatus status;
    private final String name;
    private final int age;
    private final String ticketCode;
    private final double finalPrice;

    public BuyTicketResult(BuyTicketStatus status, String name,
                           int age, String ticketCode, double finalPrice) {
        this.status     = status;
        this.name       = name;
        this.age        = age;
        this.ticketCode = ticketCode;
        this.finalPrice = finalPrice;
    }

    public BuyTicketStatus getStatus()   { return status; }
    public String getName()              { return name; }
    public int getAge()                  { return age; }
    public String getTicketCode()        { return ticketCode; }
    public double getFinalPrice()        { return finalPrice; }

    @Override
    public String toString() {
        return "BuyTicketResult{" +
                "status="     + status     +
                ", name='"    + name       + '\'' +
                ", age="      + age        +
                ", code='"    + ticketCode + '\'' +
                ", price="    + finalPrice +
                '}';
    }
}