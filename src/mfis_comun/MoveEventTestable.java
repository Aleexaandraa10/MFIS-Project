package mfis_comun;

public class MoveEventTestable {

    public static int moveEventTestable(int dayEvent, int eventIndex, int newDay) {

        if (dayEvent < 1 || dayEvent > 3) return 1;

        if (eventIndex < 0) return 2;

        if (newDay < 1 || newDay > 3) return 3;

        if (newDay == dayEvent) return 4;

        return 0;
    }
}

// Branch 11 — dayEvent < 1
// Branch 12 — dayEvent > 3
// Branch 2  — eventIndex < 0  (rămâne la fel)
// Branch 31 — newDay < 1
// Branch 32 — newDay > 3
// Branch 4  — newDay == dayEvent
// Branch 5  — succes