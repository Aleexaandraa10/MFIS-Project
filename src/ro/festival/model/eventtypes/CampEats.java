package ro.festival.model.eventtypes;

import ro.festival.model.Event;
import ro.festival.model.FestivalDay;

import java.time.LocalTime;
import java.util.List;

public class CampEats extends Event {
    private final String vendorName;
    private final List<String> foodType;
    private final boolean openUntilLate;

    public CampEats(int id_event, FestivalDay day, String eventName,
                    LocalTime startTime, LocalTime endTime,
                    String vendorName, List<String> foodType, boolean openUntilLate) {
        super(id_event, day, 0, eventName, startTime, endTime);
        this.vendorName = vendorName;
        this.foodType = foodType;
        this.openUntilLate = openUntilLate;
    }

    public CampEats(String eventName, LocalTime startTime, LocalTime endTime, FestivalDay day,
                    String vendorName, List<String> foodType, boolean openUntilLate) {
        super(eventName, startTime, endTime, day);
        this.vendorName = vendorName;
        this.foodType = foodType;
        this.openUntilLate = openUntilLate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public List<String> getFoodType() {
        return foodType;
    }

    public boolean isOpenUntilLate() {
        return openUntilLate;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\n    Vendor: " + vendorName +
                "\n    Food options: " + String.join(", ", foodType) +
                "\n    Open until late: " + (openUntilLate ? "Yes" : "No");
    }
}
