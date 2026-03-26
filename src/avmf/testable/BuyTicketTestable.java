package avmf.testable;

import avmf.model.BuyTicketResult;
import avmf.model.BuyTicketStatus;

/**
 * BuyTicketTestable — funcție pură pentru testare cu AVMf
 *
 * Conține logica completă a buyTicket() fără:
 *   - Scanner (nu citește input de la utilizator)
 *   - Random (nu generează valori aleatorii)
 *   - DB (nu scrie în baza de date)
 *
 * NAME_POOL — înlocuiește String-ul liber cu un index numeric
 * Indecși 0,1,2 → nume VALIDE  (trec regex-ul)
 * Indecși 3,4,5 → nume INVALIDE (nu trec regex-ul)
 */
public class BuyTicketTestable {

    private static final String[] NAME_POOL = {
            "Ana",           // 0 — valid:   un cuvânt, majusculă + litere mici
            "Ioana",         // 1 — valid:   un cuvânt
            "Maria Popescu", // 2 — valid:   două cuvinte
            "a",             // 3 — INVALID: prea scurt, minusculă
            "123",           // 4 — INVALID: cifre, nu litere
            ""               // 5 — INVALID: șir gol
    };

    /**
     * Simulează cumpărarea unui bilet fără efecte secundare.
     *
     * @param nameIndex  index în NAME_POOL (0–5)
     * @param age        vârsta participantului
     * @param basePrice  prețul de bază al biletului (RON)
     * @param discount   reducere în procente (0 dacă nu e under25)
     * @return BuyTicketResult cu statusul și detaliile biletului
     */
    public static BuyTicketResult buyTicketTestable(int nameIndex, int age,
                                                    int basePrice, int discount) {

        // BRANCH 1 — nameIndex în afara intervalului [0, 5]
        if (nameIndex < 0 || nameIndex >= NAME_POOL.length) {
            return new BuyTicketResult(
                    BuyTicketStatus.INVALID_NAME_INDEX, null, age, null, 0);
        }

        String name = NAME_POOL[nameIndex];

        // BRANCH 2 — vârstă invalidă: nu e în [14, 60]
        if (age < 14 || age > 60) {
            return new BuyTicketResult(
                    BuyTicketStatus.INVALID_AGE, name, age, null, 0);
        }

        // BRANCH 3 — nume invalid (nu trece regex-ul)
        if (!name.matches("^([A-Z][a-z]+)( [A-Z][a-z]+)*$")) {
            return new BuyTicketResult(
                    BuyTicketStatus.INVALID_NAME, name, age, null, 0);
        }

        // BRANCH 4 — prețul de bază în afara intervalului [200, 400]
        if (basePrice < 200 || basePrice > 400) {
            return new BuyTicketResult(
                    BuyTicketStatus.INVALID_BASE_PRICE, name, age, null, 0);
        }

        boolean isUnder25 = age <= 25;

        // BRANCH 5 — discount invalid pentru under25 (trebuie în [5, 20])
        if (isUnder25 && (discount < 5 || discount > 20)) {
            return new BuyTicketResult(
                    BuyTicketStatus.INVALID_DISCOUNT_FOR_UNDER25, name, age, null, 0);
        }

        // BRANCH 6 — discount invalid pentru regular (trebuie exact 0)
        if (!isUnder25 && discount != 0) {
            return new BuyTicketResult(
                    BuyTicketStatus.INVALID_DISCOUNT_FOR_REGULAR, name, age, null, 0);
        }

        // ── Totul valid → calculăm prețul final (fără scriere în DB) ──
        double finalPrice = isUnder25
                ? Math.round(basePrice * (1 - discount / 100.0) * 100.0) / 100.0
                : (double) basePrice;

        return new BuyTicketResult(
                isUnder25 ? BuyTicketStatus.UNDER25_SUCCESS
                        : BuyTicketStatus.REGULAR_SUCCESS,
                name, age, "T-SIM", finalPrice);
    }
}