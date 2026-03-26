package avmf;

import avmf.model.BuyTicketResult;
import avmf.model.BuyTicketStatus;
import avmf.testable.BuyTicketTestable;
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
 * BuyTicketAVMTest — Teste JUnit pe inputurile generate de AVMf
 *
 * FLUX:
 *   1. Rulezi BuyTicketAVM.main() → se generează generated_test_inputs.csv
 *   2. Rulezi această clasă de test → JUnit citește CSV-ul și verifică
 *      că fiecare input produce statusul așteptat în BuyTicketTestable
 */
class BuyTicketAVMTest {

    private static final String CSV_PATH = "generated_test_inputs.csv";

    private static final List<int[]> testInputs              = new ArrayList<>();
    private static final List<BuyTicketStatus> expectedStatuses = new ArrayList<>();

    @BeforeAll
    static void loadGeneratedInputs() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length != 5) continue;
                testInputs.add(new int[]{
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim()),
                        Integer.parseInt(parts[3].trim())
                });
                expectedStatuses.add(BuyTicketStatus.valueOf(parts[4].trim()));
            }
        }
    }

    @Test
    @DisplayName("Toate inputurile din CSV produc statusul asteptat")
    void testAllGeneratedInputsMatchExpectedStatus() {
        assertNotNull(testInputs, "CSV-ul nu a fost citit corect");
        for (int i = 0; i < testInputs.size(); i++) {
            int[] input = testInputs.get(i);
            BuyTicketResult result = BuyTicketTestable.buyTicketTestable(
                    input[0], input[1], input[2], input[3]);
            assertEquals(expectedStatuses.get(i), result.getStatus(),
                    "Test case " + (i+1) + " esuat: nameIndex=" + input[0] +
                            ", age=" + input[1] + ", basePrice=" + input[2] +
                            ", discount=" + input[3]);
        }
    }

    @Test
    @DisplayName("INVALID_NAME_INDEX — nameIndex in afara [0,5]")
    void testInvalidNameIndex() { verifyStatusExists(BuyTicketStatus.INVALID_NAME_INDEX); }

    @Test
    @DisplayName("INVALID_AGE — age < 14 sau age > 60")
    void testInvalidAge() { verifyStatusExists(BuyTicketStatus.INVALID_AGE); }

    @Test
    @DisplayName("INVALID_NAME — numele nu trece regex-ul")
    void testInvalidName() { verifyStatusExists(BuyTicketStatus.INVALID_NAME); }

    @Test
    @DisplayName("INVALID_BASE_PRICE — basePrice in afara [200,400]")
    void testInvalidBasePrice() { verifyStatusExists(BuyTicketStatus.INVALID_BASE_PRICE); }

    @Test
    @DisplayName("INVALID_DISCOUNT_FOR_UNDER25 — discount invalid pentru under25")
    void testInvalidDiscountUnder25() { verifyStatusExists(BuyTicketStatus.INVALID_DISCOUNT_FOR_UNDER25); }

    @Test
    @DisplayName("INVALID_DISCOUNT_FOR_REGULAR — discount != 0 pentru regular")
    void testInvalidDiscountRegular() { verifyStatusExists(BuyTicketStatus.INVALID_DISCOUNT_FOR_REGULAR); }

    @Test
    @DisplayName("UNDER25_SUCCESS — bilet cumparat cu succes, age <= 25")
    void testUnder25Success() { verifyStatusExists(BuyTicketStatus.UNDER25_SUCCESS); }

    @Test
    @DisplayName("REGULAR_SUCCESS — bilet cumparat cu succes, age > 25")
    void testRegularSuccess() { verifyStatusExists(BuyTicketStatus.REGULAR_SUCCESS); }


    // ─────────────────────────────────────────────────────────────
    //  BOUNDARY VALUE TESTS — acoperă ambele părți ale condițiilor ||
    //  Aceste teste sunt scrise manual pentru 100% branch coverage
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("nameIndex < 0 — partea stanga a conditiei")
    void testNameIndexNegative() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(-1, 30, 300, 0);
        assertEquals(BuyTicketStatus.INVALID_NAME_INDEX, r.getStatus());
    }

    @Test
    @DisplayName("nameIndex >= 6 — partea dreapta a conditiei")
    void testNameIndexTooLarge() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(6, 30, 300, 0);
        assertEquals(BuyTicketStatus.INVALID_NAME_INDEX, r.getStatus());
    }

    @Test
    @DisplayName("age < 14 — partea stanga a conditiei")
    void testAgeTooYoung() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(0, 13, 300, 0);
        assertEquals(BuyTicketStatus.INVALID_AGE, r.getStatus());
    }

    @Test
    @DisplayName("age > 60 — partea dreapta a conditiei")
    void testAgeTooOld() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(0, 61, 300, 0);
        assertEquals(BuyTicketStatus.INVALID_AGE, r.getStatus());
    }

    @Test
    @DisplayName("basePrice < 200 — partea stanga a conditiei")
    void testBasePriceTooLow() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(0, 30, 199, 0);
        assertEquals(BuyTicketStatus.INVALID_BASE_PRICE, r.getStatus());
    }

    @Test
    @DisplayName("basePrice > 400 — partea dreapta a conditiei")
    void testBasePriceTooHigh() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(0, 30, 401, 0);
        assertEquals(BuyTicketStatus.INVALID_BASE_PRICE, r.getStatus());
    }

    @Test
    @DisplayName("discount < 5 pentru under25 — partea stanga a conditiei")
    void testDiscountTooLowUnder25() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(0, 20, 300, 4);
        assertEquals(BuyTicketStatus.INVALID_DISCOUNT_FOR_UNDER25, r.getStatus());
    }

    @Test
    @DisplayName("discount > 20 pentru under25 — partea dreapta a conditiei")
    void testDiscountTooHighUnder25() {
        BuyTicketResult r = BuyTicketTestable.buyTicketTestable(0, 20, 300, 21);
        assertEquals(BuyTicketStatus.INVALID_DISCOUNT_FOR_UNDER25, r.getStatus());
    }
    private void verifyStatusExists(BuyTicketStatus targetStatus) {
        for (int i = 0; i < expectedStatuses.size(); i++) {
            if (expectedStatuses.get(i) == targetStatus) {
                int[] input = testInputs.get(i);
                BuyTicketResult result = BuyTicketTestable.buyTicketTestable(
                        input[0], input[1], input[2], input[3]);
                assertEquals(targetStatus, result.getStatus(),
                        "Input generat pentru " + targetStatus + " nu produce statusul asteptat");
                return;
            }
        }
        org.junit.jupiter.api.Assertions.fail(
                "Nu exista input generat pentru: " + targetStatus +
                        ". Ruleaza mai intai BuyTicketAVM.main().");
    }
}