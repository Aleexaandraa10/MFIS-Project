package avmf;

import avmf.model.SwapOrganizersResult;
import avmf.model.SwapOrganizersStatus;
import avmf.testable.SwapOrganizersTestable;
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
 * SwapOrganizersAVMTest — Teste JUnit pe inputurile generate de AVMf
 *
 * FLUX:
 *   1. Rulezi SwapOrganizersAVM.main() → se generează generated_swap_inputs.csv
 *   2. Rulezi această clasă → JUnit verifică fiecare input prin SwapOrganizersTestable
 */
class SwapOrganizersAVMTest {

    private static final String CSV_PATH = "generated_swap_inputs.csv";

    private static final List<int[]> testInputs                  = new ArrayList<>();
    private static final List<SwapOrganizersStatus> expectedStatuses = new ArrayList<>();

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
                expectedStatuses.add(SwapOrganizersStatus.valueOf(parts[4].trim()));
            }
        }
    }

    @Test
    @DisplayName("Toate inputurile din CSV produc statusul asteptat")
    void testAllGeneratedInputsMatchExpectedStatus() {
        assertNotNull(testInputs, "CSV-ul nu a fost citit corect");
        for (int i = 0; i < testInputs.size(); i++) {
            int[] input = testInputs.get(i);
            SwapOrganizersResult result = SwapOrganizersTestable.swapOrganizersTestable(
                    input[0], input[1], input[2], input[3]);
            assertEquals(expectedStatuses.get(i), result.getStatus(),
                    "Test case " + (i+1) + " esuat: org1=" + input[0] +
                            ", org2=" + input[1] + ", ev1=" + input[2] + ", ev2=" + input[3]);
        }
    }

    @Test
    @DisplayName("INVALID_ORG1_INDEX — org1Index in afara [0,2]")
    void testInvalidOrg1Index() { verifyStatusExists(SwapOrganizersStatus.INVALID_ORG1_INDEX); }

    @Test
    @DisplayName("INVALID_ORG2_INDEX — org2Index in afara [0,2]")
    void testInvalidOrg2Index() { verifyStatusExists(SwapOrganizersStatus.INVALID_ORG2_INDEX); }

    @Test
    @DisplayName("SAME_ORGANIZER — org1Index == org2Index")
    void testSameOrganizer() { verifyStatusExists(SwapOrganizersStatus.SAME_ORGANIZER); }

    @Test
    @DisplayName("INVALID_EVENT1_INDEX — ev1Index invalid pentru org1")
    void testInvalidEvent1Index() { verifyStatusExists(SwapOrganizersStatus.INVALID_EVENT1_INDEX); }

    @Test
    @DisplayName("INVALID_EVENT2_INDEX — ev2Index invalid pentru org2")
    void testInvalidEvent2Index() { verifyStatusExists(SwapOrganizersStatus.INVALID_EVENT2_INDEX); }

    @Test
    @DisplayName("SWAP_SUCCESS — swap simulat cu succes")
    void testSwapSuccess() { verifyStatusExists(SwapOrganizersStatus.SWAP_SUCCESS); }

    private void verifyStatusExists(SwapOrganizersStatus targetStatus) {
        for (int i = 0; i < expectedStatuses.size(); i++) {
            if (expectedStatuses.get(i) == targetStatus) {
                int[] input = testInputs.get(i);
                SwapOrganizersResult result = SwapOrganizersTestable.swapOrganizersTestable(
                        input[0], input[1], input[2], input[3]);
                assertEquals(targetStatus, result.getStatus(),
                        "Input generat pentru " + targetStatus + " nu produce statusul asteptat");
                return;
            }
        }
        org.junit.jupiter.api.Assertions.fail(
                "Nu exista input generat pentru: " + targetStatus +
                        ". Ruleaza mai intai SwapOrganizersAVM.main().");
    }
}