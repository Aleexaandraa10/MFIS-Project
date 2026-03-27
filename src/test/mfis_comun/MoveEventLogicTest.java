package mfis_comun;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoveEventLogicTest {

    @BeforeClass
    public static void generateData() throws Exception {
        GenerateMoveEventInputData.main(new String[0]);
    }

    @Test
    public void testInputsFromCsvMatchExpectedResults() throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("avmf_move_event_inputs.csv"));
        reader.readLine(); // sar header

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");

            int branchId       = Integer.parseInt(parts[0]);
            boolean targetTrue = Boolean.parseBoolean(parts[1]);
            int expectedResult = Integer.parseInt(parts[2]);
            int dayEvent       = Integer.parseInt(parts[3]);
            int eventIndex     = Integer.parseInt(parts[4]);
            int newDay         = Integer.parseInt(parts[5]);

            // Verificarea 1 — functia returneaza rezultatul corect
            int actualResult = MoveEventTestable.moveEventTestable(
                    dayEvent, eventIndex, newDay);

            assertEquals(
                    "Branch " + branchId + (targetTrue ? "T" : "F") +
                            " — rezultat greșit pentru input: " + line,
                    expectedResult,
                    actualResult
            );

            // Verificarea 2 — ramura corecta a fost acoperita
            switch (branchId) {
                case 1:
                    if (targetTrue)
                        assertEquals("Branch 1T trebuie să returneze 1",
                                1, actualResult);
                    else
                        assertTrue("Branch 1F trebuie să treacă de ramura 1",
                                actualResult != 1);
                    break;
                case 2:
                    if (targetTrue)
                        assertEquals("Branch 2T trebuie să returneze 2",
                                2, actualResult);
                    else
                        assertTrue("Branch 2F trebuie să treacă de ramura 2",
                                actualResult != 1 && actualResult != 2);
                    break;
                case 3:
                    if (targetTrue)
                        assertEquals("Branch 3T trebuie să returneze 3",
                                3, actualResult);
                    else
                        assertTrue("Branch 3F trebuie să treacă de ramura 3",
                                actualResult != 1 && actualResult != 2 && actualResult != 3);
                    break;
                case 4:
                    if (targetTrue)
                        assertEquals("Branch 4T trebuie să returneze 4",
                                4, actualResult);
                    else
                        assertEquals("Branch 4F trebuie să returneze 0",
                                0, actualResult);
                    break;
                case 5:
                    assertEquals("Branch 5 trebuie să returneze 0",
                            0, actualResult);
                    break;
            }
        }
        reader.close();
    }
}