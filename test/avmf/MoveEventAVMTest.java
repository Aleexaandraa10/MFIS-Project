package avmf;

import avmf.testable.MoveEventTestable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * MoveEventAVMTest — Teste JUnit pe inputurile generate de AVMf
 *
 * FLUX:
 *   1. Rulezi MoveEventAVM.main() → se generează generated_move_event_inputs.csv
 *   2. Rulezi această clasă → JUnit verifică fiecare input prin MoveEventTestable
 *
 * Structura testelor:
 *   BLOC 1 — Test principal: toate inputurile din CSV
 *   BLOC 2 — Teste individuale per branch (din CSV, generate de AVMf)
 *   BLOC 3 — Boundary value tests (scrise manual, ambele părți ale ||)
 *   BLOC 4 — Teste pentru branch-ul 5 complex (&&  și ||)
 *
 * Coduri de rezultat:
 *   0 → MOVE_SUCCESS
 *   1 → INVALID_DAY
 *   2 → INVALID_INDEX
 *   3 → INVALID_NEW_DAY
 *   4 → SAME_DAY
 *   5 → RESTRICTED_MOVE
 */
class MoveEventAVMTest {

    private static final String CSV_PATH = "generated_move_event_inputs.csv";

    private static final List<int[]> testInputs        = new ArrayList<>();
    private static final List<Integer> expectedResults = new ArrayList<>();

    @BeforeAll
    static void loadGeneratedInputs() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length != 4) continue;
                testInputs.add(new int[]{
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim())
                });
                expectedResults.add(Integer.parseInt(parts[3].trim()));
            }
        }
    }

    // =============================================================
    //  BLOC 1 — Test principal
    //  Verifică toate inputurile din CSV dintr-o singură rulare
    // =============================================================

    @Test
    @DisplayName("Toate inputurile din CSV produc rezultatul asteptat")
    void testAllGeneratedInputsMatchExpectedResult() {
        assertNotNull(testInputs, "CSV-ul nu a fost citit corect");
        for (int i = 0; i < testInputs.size(); i++) {
            int[] input = testInputs.get(i);
            int result = MoveEventTestable.moveEventTestable(input[0], input[1], input[2]);
            assertEquals(expectedResults.get(i), result,
                    "Test case " + (i + 1) + " esuat: dayEvent=" + input[0] +
                            ", eventIndex=" + input[1] + ", newDay=" + input[2]);
        }
    }

    // =============================================================
    //  BLOC 2 — Teste individuale per branch
    //  Inputurile sunt cele generate de AVMf și salvate în CSV
    // =============================================================

    @Test
    @DisplayName("return 1 — INVALID_DAY: dayEvent < 1 || dayEvent > 3")
    void testInvalidDay() { verifyResultExists(1); }

    @Test
    @DisplayName("return 2 — INVALID_INDEX: eventIndex < 0")
    void testInvalidIndex() { verifyResultExists(2); }

    @Test
    @DisplayName("return 3 — INVALID_NEW_DAY: newDay < 1 || newDay > 3")
    void testInvalidNewDay() { verifyResultExists(3); }

    @Test
    @DisplayName("return 4 — SAME_DAY: newDay == dayEvent")
    void testSameDay() { verifyResultExists(4); }

    @Test
    @DisplayName("return 5 — RESTRICTED_MOVE: newDay==1 && eventIndex>=4 && (dayEvent==3 || eventIndex>5)")
    void testRestrictedMove() { verifyResultExists(5); }

    @Test
    @DisplayName("return 0 — MOVE_SUCCESS: toate conditiile valide")
    void testMoveSuccess() { verifyResultExists(0); }

    // =============================================================
    //  BLOC 3 — Boundary value tests pentru branch-urile 1 si 3
    //  Testeaza ambele parti ale conditiilor ||
    // =============================================================
//
//    @Test
//    @DisplayName("B1 stanga: dayEvent=0 < 1 → INVALID_DAY")
//    void testDayEventTooLow() {
//        assertEquals(1, MoveEventTestable.moveEventTestable(0, 0, 2));
//    }
//
//    @Test
//    @DisplayName("B1 dreapta: dayEvent=4 > 3 → INVALID_DAY")
//    void testDayEventTooHigh() {
//        assertEquals(1, MoveEventTestable.moveEventTestable(4, 0, 2));
//    }
//
//    @Test
//    @DisplayName("B3 stanga: newDay=0 < 1 → INVALID_NEW_DAY")
//    void testNewDayTooLow() {
//        assertEquals(3, MoveEventTestable.moveEventTestable(2, 0, 0));
//    }
//
//    @Test
//    @DisplayName("B3 dreapta: newDay=4 > 3 → INVALID_NEW_DAY")
//    void testNewDayTooHigh() {
//        assertEquals(3, MoveEventTestable.moveEventTestable(2, 0, 4));
//    }

    // =============================================================
    //  BLOC 4 — Teste pentru branch-ul 5 complex
    //  newDay==1 && eventIndex>=4 && (dayEvent==3 || eventIndex>5)
    //
    //  Calea A: dayEvent==3 TRUE,  eventIndex>5 FALSE (eventIndex in [4,5])
    //  Calea B: dayEvent==3 FALSE, eventIndex>5 TRUE  (eventIndex >= 6)
    //  FALSE:   conditia intreaga e FALSE (nu intra pe branch)
    // =============================================================
//
//    @Test
//    @DisplayName("B5 calea A: dayEvent=3 TRUE, eventIndex=4 (>=4 dar <=5) → RESTRICTED_MOVE")
//    void testRestrictedMovePathA() {
//        assertEquals(5, MoveEventTestable.moveEventTestable(3, 4, 1));
//    }
//
//    @Test
//    @DisplayName("B5 calea B: eventIndex=6 TRUE (>5), dayEvent=2 (!=3) → RESTRICTED_MOVE")
//    void testRestrictedMovePathB() {
//        assertEquals(5, MoveEventTestable.moveEventTestable(2, 6, 1));
//    }
//
//    @Test
//    @DisplayName("B5 limita exacta: eventIndex=4 exact la limita cu dayEvent=3 → RESTRICTED_MOVE")
//    void testEventIndexExactBoundary() {
//        assertEquals(5, MoveEventTestable.moveEventTestable(3, 4, 1));
//    }
//
//    @Test
//    @DisplayName("B5 sub limita: eventIndex=3 < 4 cu dayEvent=3 → MOVE_SUCCESS")
//    void testEventIndexBelowBoundary() {
//        assertEquals(0, MoveEventTestable.moveEventTestable(3, 3, 1));
//    }
//
//    @Test
//    @DisplayName("B5 FALSE: newDay=1, eventIndex=4 dar dayEvent=2 si eventIndex<=5 → MOVE_SUCCESS")
//    void testRestrictedMoveFalse() {
//        // newDay==1 ✓, eventIndex>=4 ✓, dar dayEvent!=3 ✗ si eventIndex<=5 ✗
//        // deci conditia intreaga e FALSE → return 0
//        assertEquals(0, MoveEventTestable.moveEventTestable(2, 4, 1));
//    }

    // ─────────────────────────────────────────────────────────────
    //  UTILITAR: găsește în CSV inputul pentru un rezultat dat
    //  și verifică că moveEventTestable() produce acel rezultat
    // ─────────────────────────────────────────────────────────────
    private void verifyResultExists(int targetResult) {
        for (int i = 0; i < expectedResults.size(); i++) {
            if (expectedResults.get(i) == targetResult) {
                int[] input = testInputs.get(i);
                int result = MoveEventTestable.moveEventTestable(
                        input[0], input[1], input[2]);
                assertEquals(targetResult, result,
                        "Input generat pentru rezultatul " + targetResult +
                                " nu produce rezultatul asteptat: " +
                                "dayEvent=" + input[0] +
                                ", eventIndex=" + input[1] +
                                ", newDay=" + input[2]);
                return;
            }
        }
        org.junit.jupiter.api.Assertions.fail(
                "Nu exista input generat pentru rezultatul: " + targetResult +
                        ". Ruleaza mai intai MoveEventAVM.main().");
    }
}