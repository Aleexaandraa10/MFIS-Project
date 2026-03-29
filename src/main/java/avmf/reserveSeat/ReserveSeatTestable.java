package avmf.reserveSeat;

public class ReserveSeatTestable {

    public static int reserveSeatTestable(
            int talksSize,
            int index,
            int reservedSize,
            int seats,
            boolean participantExists
    ) {

        // no talks
        if (talksSize == 0) return 1;

        // invalid index
        if (index < 0 || index >= talksSize) return 2;

        // full
        if (reservedSize >= seats) return 3;

        // participant not found
        if (!participantExists) return 4;

        // success
        return 0;
    }
}