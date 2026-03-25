package ro.festival.model;

public class TicketUnder25 extends Ticket {
    private final double discountPercentage;

    public TicketUnder25(String code, double price, double discountPercentage) {
        super(code, price);
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public String toString() {
        return super.toString()+ "\n   This participant is under 25, so the discount percentage is " + discountPercentage + "%";
    }
}
