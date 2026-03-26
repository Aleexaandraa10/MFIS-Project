package avmf.testable;

import avmf.model.SwapOrganizersResult;
import avmf.model.SwapOrganizersStatus;

import java.util.List;

/**
 * SwapOrganizersTestable — funcție pură pentru testare cu AVMf
 *
 * Conține logica completă a swapEventsBetweenOrganizers() fără:
 *   - Scanner (nu citește input de la utilizator)
 *   - DB (nu scrie în baza de date)
 *
 * ORGANIZER_POOL — date fixe, fără dependență de FestivalService
 *   Index 0 → "Stage Masters"    → 6 evenimente (indecși 0–5)
 *   Index 1 → "Taste & Joy"      → 4 evenimente (indecși 0–3)
 *   Index 2 → "Experience Lab"   → 5 evenimente (indecși 0–4)
 */
public class SwapOrganizersTestable {

    // Date fixe despre organizatori — fără DB
    private static final String[] ORG_NAMES = {
            "Stage Masters",    // 0 — 6 evenimente
            "Taste & Joy",      // 1 — 4 evenimente
            "Experience Lab"    // 2 — 5 evenimente
    };

    private static final String[][] ORG_EVENTS = {
            // Stage Masters — 6 evenimente
            {"Imagine Dragons Live", "Coldplay Vibes", "EDM Arena",
                    "Night Vibes", "Electro Pulse", "Chill Grooves"},
            // Taste & Joy — 4 evenimente
            {"Salty Delights", "Savoury Stop", "Sweet Corner", "Fast & Yummy"},
            // Experience Lab — 5 evenimente
            {"FunZone Madness", "GameZone Fiesta",
                    "Future of Music", "Festival Sustainability", "Marketing 4 Festivals"}
    };

    /**
     * Simulează swap-ul de evenimente între doi organizatori
     * fără efecte secundare (fără DB, fără Scanner).
     *
     * @param org1Index  indexul primului organizator  (0–2)
     * @param org2Index  indexul celui de-al doilea    (0–2)
     * @param ev1Index   indexul evenimentului din org1
     * @param ev2Index   indexul evenimentului din org2
     * @return SwapOrganizersResult cu statusul și detaliile swap-ului
     */
    public static SwapOrganizersResult swapOrganizersTestable(int org1Index, int org2Index,
                                                              int ev1Index,  int ev2Index) {

        // BRANCH 1 — org1Index în afara intervalului [0, 2]
        if (org1Index < 0 || org1Index >= ORG_NAMES.length) {
            return new SwapOrganizersResult(
                    SwapOrganizersStatus.INVALID_ORG1_INDEX,
                    null, null, null, null);
        }

        String org1Name = ORG_NAMES[org1Index];

        // BRANCH 2 — org2Index în afara intervalului [0, 2]
        if (org2Index < 0 || org2Index >= ORG_NAMES.length) {
            return new SwapOrganizersResult(
                    SwapOrganizersStatus.INVALID_ORG2_INDEX,
                    org1Name, null, null, null);
        }

        String org2Name = ORG_NAMES[org2Index];

        // BRANCH 3 — același organizator selectat de două ori
        if (org1Index == org2Index) {
            return new SwapOrganizersResult(
                    SwapOrganizersStatus.SAME_ORGANIZER,
                    org1Name, org2Name, null, null);
        }

        String[] events1 = ORG_EVENTS[org1Index];
        String[] events2 = ORG_EVENTS[org2Index];

        // BRANCH 4 — ev1Index în afara listei de evenimente a org1
        if (ev1Index < 0 || ev1Index >= events1.length) {
            return new SwapOrganizersResult(
                    SwapOrganizersStatus.INVALID_EVENT1_INDEX,
                    org1Name, org2Name, null, null);
        }

        // BRANCH 5 — ev2Index în afara listei de evenimente a org2
        if (ev2Index < 0 || ev2Index >= events2.length) {
            return new SwapOrganizersResult(
                    SwapOrganizersStatus.INVALID_EVENT2_INDEX,
                    org1Name, org2Name, events1[ev1Index], null);
        }

        // ── Totul valid → swap simulat (fără scriere în DB) ──────────
        return new SwapOrganizersResult(
                SwapOrganizersStatus.SWAP_SUCCESS,
                org1Name, org2Name,
                events1[ev1Index], events2[ev2Index]);
    }
}