package mfis;

public class ParticipantSanctionLogic {

    public static int classifySanctionCase(
            boolean participantExists,
            boolean hasUnder25Ticket,
            int correctAge,
            double oldPrice,
            double discountPercentage
    ) {
        if (!participantExists) {
            return 0;
        }
        if (!hasUnder25Ticket) {
            return 1;
        }
        if (correctAge <= 25) {
            return 2;
        }
        if (oldPrice <= 0) {
            return 3;
        }
        if (discountPercentage <= 0 || discountPercentage >= 100) {
            return 4;
        }
        return 5;
    }
}
