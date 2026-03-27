package mfis;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParticipantSanctionLogicTest {

    @BeforeClass
    public static void generateData() throws Exception {
        // rulează AVMf înainte de teste
        GenerateSanctionInputData.main(new String[0]);
    }

    @Test
    public void testInputsFromCsvMatchExpectedResults() throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("avmf_inputs.csv"));
        reader.readLine(); // sar header

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");

            int branchId            = Integer.parseInt(parts[0]);
            boolean targetTrue      = Boolean.parseBoolean(parts[1]);
            int expectedResult      = Integer.parseInt(parts[2]);
            boolean participantExists  = Boolean.parseBoolean(parts[3]);
            boolean hasUnder25Ticket   = Boolean.parseBoolean(parts[4]);
            int correctAge          = Integer.parseInt(parts[5]);
            double oldPrice         = Double.parseDouble(parts[6]);
            double discountPercentage = Double.parseDouble(parts[7]);

            // Verificarea 1 — funcția returnează rezultatul corect
            int actualResult = ParticipantSanctionLogic.classifySanctionCase(
                    participantExists, hasUnder25Ticket,
                    correctAge, oldPrice, discountPercentage);

            assertEquals(
                    "Branch " + branchId + (targetTrue ? "T" : "F") +
                            " — rezultat greșit pentru input: " + line,
                    expectedResult,
                    actualResult
            );

            // Verificarea 2 — ramura corectă a fost acoperită
            // Branch 1T înseamnă că am intrat pe ramura 1 true → result trebuie să fie 0
            // Branch 1F înseamnă că am trecut de ramura 1 false → result >= 1
            // etc.
            switch (branchId) {
                case 1:
                    if (targetTrue)
                        assertEquals("Branch 1T trebuie să returneze 0",
                                0, actualResult);
                    else
                        assertTrue("Branch 1F trebuie să treacă de ramura 1",
                                actualResult >= 1);
                    break;
                case 2:
                    if (targetTrue)
                        assertEquals("Branch 2T trebuie să returneze 1",
                                1, actualResult);
                    else
                        assertTrue("Branch 2F trebuie să treacă de ramura 2",
                                actualResult >= 2);
                    break;
                case 3:
                    if (targetTrue)
                        assertEquals("Branch 3T trebuie să returneze 2",
                                2, actualResult);
                    else
                        assertTrue("Branch 3F trebuie să treacă de ramura 3",
                                actualResult >= 3);
                    break;
                case 4:
                    if (targetTrue)
                        assertEquals("Branch 4T trebuie să returneze 3",
                                3, actualResult);
                    else
                        assertTrue("Branch 4F trebuie să treacă de ramura 4",
                                actualResult >= 4);
                    break;
                case 5:
                    if (targetTrue)
                        assertEquals("Branch 5T trebuie să returneze 4",
                                4, actualResult);
                    else
                        assertEquals("Branch 5F trebuie să returneze 5",
                                5, actualResult);
                    break;
            }
        }
        reader.close();
    }
}