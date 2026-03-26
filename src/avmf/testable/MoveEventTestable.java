package avmf.testable;

/**
 * MoveEventTestable — funcție pură pentru testare cu AVMf
 * (funcție preluată de la colegă, adaptată în pachetul avmf.testable)
 *
 * Coduri de rezultat:
 *   0 → MOVE_SUCCESS    — mutare reușită
 *   1 → INVALID_DAY     — dayEvent invalid (< 1 sau > 3)
 *   2 → INVALID_INDEX   — eventIndex invalid (< 0)
 *   3 → INVALID_NEW_DAY — newDay invalid (< 1 sau > 3)
 *   4 → SAME_DAY        — newDay == dayEvent
 */
public class MoveEventTestable {

    /**
     * @param dayEvent    ziua curentă a evenimentului (1–3)
     * @param eventIndex  indexul evenimentului în lista zilei (>= 0)
     * @param newDay      ziua nouă la care vrem să mutăm (1–3)
     * @return cod de rezultat (0–4)
     */
    public static int moveEventTestable(int dayEvent, int eventIndex, int newDay) {

        // BRANCH 1 — ziua curentă invalidă
        if (dayEvent < 1 || dayEvent > 3) return 1;

        // BRANCH 2 — indexul evenimentului invalid
        if (eventIndex < 0) return 2;

        // BRANCH 3 — ziua nouă invalidă
        if (newDay < 1 || newDay > 3) return 3;

        // BRANCH 4 — ziua nouă e aceeași cu cea curentă
        if (newDay == dayEvent) return 4;

        // BRANCH 5 — mutare restricționată
        // Condiție complexă: nu poți muta un eveniment de seară târzie (eventIndex >= 4)
        // în Ziua 1 (newDay == 1) dacă evenimentul vine din Ziua 3 (dayEvent == 3)
        // SAU dacă indexul e prea mare pentru relocare (eventIndex > 5)
        if (newDay == 1 && eventIndex >= 4 && (dayEvent == 3 || eventIndex > 5)) return 5;

        // BRANCH 6 — totul valid, mutare reușită
        return 0;
    }
}