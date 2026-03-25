package avmf.moveEvent;

public class MoveEventTestable {

    public static int moveEventTestable(int dayEvent, int eventIndex, int newDay) {

        if (dayEvent < 1 || dayEvent > 3) return 1;

        if (eventIndex < 0) return 2;

        if (newDay < 1 || newDay > 3) return 3;

        if (newDay == dayEvent) return 4;

        return 0;
    }
}