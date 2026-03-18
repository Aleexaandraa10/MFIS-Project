package ro.festival.model;

public class Ticket {
    private final String code;
    private double price;

    public Ticket(String code, double price) {
        this.code = code;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket: " + code + " | Price: " + price + " RON.";
    }
}
