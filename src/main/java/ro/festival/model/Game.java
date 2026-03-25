package ro.festival.model;

public class Game {
    private final int id_game;
    private final String gameName;
    private final boolean isOpenAllNight;
    private final boolean hasPrize;
    private final int maxCapacity;

    public Game(int id_game, String gameName, boolean isOpenAllNight, boolean hasPrize, int maxCapacity) {
        this.id_game = id_game;
        this.gameName = gameName;
        this.isOpenAllNight = isOpenAllNight;
        this.hasPrize = hasPrize;
        this.maxCapacity = maxCapacity;
    }

    public Game(String gameName, boolean isOpenAllNight, boolean hasPrize, int maxCapacity) {
        this(0, gameName, isOpenAllNight, hasPrize, maxCapacity);
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isOpenAllNight() {
        return isOpenAllNight;
    }

    public boolean hasPrize() {
        return hasPrize;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        return gameName +
                (isOpenAllNight ? " is open all night" : " isn't open all night") +
                (hasPrize ? ", has prize available " : ", has no prize available ") +
                "and has " + maxCapacity + " available seats.";
    }
}
