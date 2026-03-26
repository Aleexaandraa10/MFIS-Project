package avmf;

import avmf.model.SwapOrganizersResult;
import avmf.model.SwapOrganizersStatus;
import avmf.testable.SwapOrganizersTestable;
import org.apache.commons.math3.random.Well19937c;
import org.avmframework.AlternatingVariableMethod;
import org.avmframework.Monitor;
import org.avmframework.TerminationPolicy;
import org.avmframework.Vector;
import org.avmframework.initialization.Initializer;
import org.avmframework.initialization.RandomInitializer;
import org.avmframework.localsearch.GeometricSearch;
import org.avmframework.objective.NumericObjectiveValue;
import org.avmframework.objective.ObjectiveFunction;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.variable.IntegerVariable;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * SwapOrganizersAVM — Generare automată de date de test cu AVMf
 * pentru metoda SwapOrganizersTestable.swapOrganizersTestable()
 *
 * Fiecare branch cu || este tratat cu DOUĂ target-uri separate:
 *   - xT-L (Left)  → partea stângă a condiției ||
 *   - xT-R (Right) → partea dreaptă a condiției ||
 *
 * Astfel AVMf generează automat inputuri pentru ambele părți
 * ale fiecărei condiții compuse, obținând 100% branch coverage
 * fără teste manuale suplimentare.
 *
 * TARGET 1L — org1Index < 0               (stânga B1)
 * TARGET 1R — org1Index >= 3              (dreapta B1)
 * TARGET 2L — org2Index < 0               (stânga B2)
 * TARGET 2R — org2Index >= 3              (dreapta B2)
 * TARGET 3T — org1Index == org2Index      (B3)
 * TARGET 4L — ev1Index < 0                (stânga B4)
 * TARGET 4R — ev1Index >= events1.size()  (dreapta B4)
 * TARGET 5L — ev2Index < 0                (stânga B5)
 * TARGET 5R — ev2Index >= events2.size()  (dreapta B5)
 * TARGET 6T — SWAP_SUCCESS                (B6)
 */
public class SwapOrganizersAVM {

    private static final String CSV_PATH = "generated_swap_inputs.csv";

    private static final int MAX_EVENTS_ORG1 = 6;
    private static final int MAX_EVENTS_ORG2 = 4;
    private static final int MAX_EVENTS_ORG3 = 5;

    private static NumericObjectiveValue dist(double distance) {
        return NumericObjectiveValue.lowerIsBetterObjectiveValue(distance, 0.0);
    }

    // ─────────────────────────────────────────────────────────────
    //  UTILITAR: vector de start pentru fiecare target
    // ─────────────────────────────────────────────────────────────
    private static Vector buildVector() {
        Vector v = new Vector();
        v.addVariable(new IntegerVariable(0,  -2,  5));  // org1Index
        v.addVariable(new IntegerVariable(1,  -2,  5));  // org2Index
        v.addVariable(new IntegerVariable(0,  -2,  8));  // ev1Index
        v.addVariable(new IntegerVariable(0,  -2,  8));  // ev2Index
        return v;
    }

    private static int[] extract(Vector v) {
        return new int[]{
                ((IntegerVariable) v.getVariable(0)).asInt(),
                ((IntegerVariable) v.getVariable(1)).asInt(),
                ((IntegerVariable) v.getVariable(2)).asInt(),
                ((IntegerVariable) v.getVariable(3)).asInt()
        };
    }

    // ─────────────────────────────────────────────────────────────
    //  METODE UTILITARE — distanța pentru ca fiecare branch să TREACĂ
    // ─────────────────────────────────────────────────────────────

    // B1 trece: org1Index în [0, 2]
    private static double distB1Pass(int org1Index) {
        if (org1Index >= 0 && org1Index <= 2) return 0;
        if (org1Index < 0) return -org1Index;
        return org1Index - 2;
    }

    // B2 trece: org2Index în [0, 2]
    private static double distB2Pass(int org2Index) {
        if (org2Index >= 0 && org2Index <= 2) return 0;
        if (org2Index < 0) return -org2Index;
        return org2Index - 2;
    }

    // B3 trece: org1Index != org2Index
    private static double distB3Pass(int org1Index, int org2Index) {
        if (org1Index != org2Index) return 0;
        return 1;
    }

    // numărul de evenimente per organizator
    private static int getEventCount(int orgIndex) {
        if (orgIndex == 0) return MAX_EVENTS_ORG1;
        if (orgIndex == 1) return MAX_EVENTS_ORG2;
        return MAX_EVENTS_ORG3;
    }

    // B4 trece: ev1Index în [0, eventCount(org1)-1]
    private static double distB4Pass(int ev1Index, int org1Index) {
        int maxEv = getEventCount(org1Index) - 1;
        if (ev1Index >= 0 && ev1Index <= maxEv) return 0;
        if (ev1Index < 0) return -ev1Index;
        return ev1Index - maxEv;
    }

    // B5 trece: ev2Index în [0, eventCount(org2)-1]
    private static double distB5Pass(int ev2Index, int org2Index) {
        int maxEv = getEventCount(org2Index) - 1;
        if (ev2Index >= 0 && ev2Index <= maxEv) return 0;
        if (ev2Index < 0) return -ev2Index;
        return ev2Index - maxEv;
    }

    // =============================================================
    //  TARGET 1L — org1Index < 0  (partea STÂNGĂ a B1)
    //  Vrem ca org1Index să fie NEGATIV
    // =============================================================
    public static ObjectiveFunction invalidOrg1IndexLeftTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                // Target atins doar dacă statusul e INVALID_ORG1_INDEX
                // ȘI org1Index e negativ (partea stângă a ||)
                if (r.getStatus() == SwapOrganizersStatus.INVALID_ORG1_INDEX
                        && p[0] < 0) return dist(0);

                double d = 0;
                // org1Index trebuie să fie < 0
                if (p[0] >= 0) d += p[0] + 1;  // cât trebuie scăzut ca org1Index < 0
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 1R — org1Index >= 3  (partea DREAPTĂ a B1)
    //  Vrem ca org1Index să fie >= 3 (în afara [0,2] spre dreapta)
    // =============================================================
    public static ObjectiveFunction invalidOrg1IndexRightTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                // Target atins doar dacă statusul e INVALID_ORG1_INDEX
                // ȘI org1Index e >= 3 (partea dreaptă a ||)
                if (r.getStatus() == SwapOrganizersStatus.INVALID_ORG1_INDEX
                        && p[0] >= 3) return dist(0);

                double d = 0;
                // org1Index trebuie să fie >= 3
                if (p[0] < 3) d += (3 - p[0]);  // cât trebuie crescut ca org1Index >= 3
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 2L — org2Index < 0  (partea STÂNGĂ a B2)
    //  B1 trebuie să treacă: org1Index în [0, 2]
    // =============================================================
    public static ObjectiveFunction invalidOrg2IndexLeftTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.INVALID_ORG2_INDEX
                        && p[1] < 0) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);           // B1 trebuie să treacă
                if (p[1] >= 0) d += p[1] + 1;   // org2Index trebuie < 0
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 2R — org2Index >= 3  (partea DREAPTĂ a B2)
    //  B1 trebuie să treacă: org1Index în [0, 2]
    // =============================================================
    public static ObjectiveFunction invalidOrg2IndexRightTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.INVALID_ORG2_INDEX
                        && p[1] >= 3) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);           // B1 trebuie să treacă
                if (p[1] < 3) d += (3 - p[1]);  // org2Index trebuie >= 3
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 3T — SAME_ORGANIZER
    //  Branch: org1Index == org2Index = true
    //  B1 și B2 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction sameOrganizerTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.SAME_ORGANIZER) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                if (p[0] != p[1]) d += Math.abs(p[0] - p[1]);
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 4L — ev1Index < 0  (partea STÂNGĂ a B4)
    //  B1, B2, B3 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction invalidEvent1IndexLeftTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.INVALID_EVENT1_INDEX
                        && p[2] < 0) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[0], p[1]);
                if (p[2] >= 0) d += p[2] + 1;   // ev1Index trebuie < 0
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 4R — ev1Index >= events1.size()  (partea DREAPTĂ a B4)
    //  B1, B2, B3 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction invalidEvent1IndexRightTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.INVALID_EVENT1_INDEX
                        && p[2] >= getEventCount(p[0])) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[0], p[1]);
                int maxEv = getEventCount(p[0]);
                if (p[2] < maxEv) d += (maxEv - p[2]);  // ev1Index trebuie >= maxEv
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 5L — ev2Index < 0  (partea STÂNGĂ a B5)
    //  B1, B2, B3, B4 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction invalidEvent2IndexLeftTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.INVALID_EVENT2_INDEX
                        && p[3] < 0) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[0], p[1]);
                d += distB4Pass(p[2], p[0]);
                if (p[3] >= 0) d += p[3] + 1;   // ev2Index trebuie < 0
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 5R — ev2Index >= events2.size()  (partea DREAPTĂ a B5)
    //  B1, B2, B3, B4 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction invalidEvent2IndexRightTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.INVALID_EVENT2_INDEX
                        && p[3] >= getEventCount(p[1])) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[0], p[1]);
                d += distB4Pass(p[2], p[0]);
                int maxEv = getEventCount(p[1]);
                if (p[3] < maxEv) d += (maxEv - p[3]);  // ev2Index trebuie >= maxEv
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 6T — SWAP_SUCCESS
    //  Toate branch-urile trebuie să TREACĂ
    // =============================================================
    public static ObjectiveFunction swapSuccessTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                SwapOrganizersResult r =
                        SwapOrganizersTestable.swapOrganizersTestable(p[0], p[1], p[2], p[3]);

                if (r.getStatus() == SwapOrganizersStatus.SWAP_SUCCESS) return dist(0);

                double d = distB1Pass(p[0]) + distB2Pass(p[1]) + distB3Pass(p[0], p[1])
                        + distB4Pass(p[2], p[0]) + distB5Pass(p[3], p[1]);
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  MAIN — rulează toate cele 10 target-uri
    // =============================================================
    public static void main(String[] args) {

        System.out.println("ORGANIZER_POOL:");
        System.out.println("  0 -> Stage Masters    (6 evenimente, indecsi 0-5)");
        System.out.println("  1 -> Taste & Joy      (4 evenimente, indecsi 0-3)");
        System.out.println("  2 -> Experience Lab   (5 evenimente, indecsi 0-4)");
        System.out.println();

        String[] branchIds = {
                "1T-L", "1T-R",
                "2T-L", "2T-R",
                "3T",
                "4T-L", "4T-R",
                "5T-L", "5T-R",
                "6T"
        };

        String[] targets = {
                "org1Index < 0 = true               ->  org1 negativ",
                "org1Index >= 3 = true              ->  org1 prea mare",
                "org2Index < 0 = true               ->  org2 negativ",
                "org2Index >= 3 = true              ->  org2 prea mare",
                "org1Index == org2Index = true      ->  acelasi organizator",
                "ev1Index < 0 = true                ->  ev1 negativ",
                "ev1Index >= events1.size() = true  ->  ev1 prea mare",
                "ev2Index < 0 = true                ->  ev2 negativ",
                "ev2Index >= events2.size() = true  ->  ev2 prea mare",
                "toate conditiile valide            ->  SWAP_SUCCESS"
        };

        ObjectiveFunction[] objFunctions = {
                invalidOrg1IndexLeftTarget(),    invalidOrg1IndexRightTarget(),
                invalidOrg2IndexLeftTarget(),    invalidOrg2IndexRightTarget(),
                sameOrganizerTarget(),
                invalidEvent1IndexLeftTarget(),  invalidEvent1IndexRightTarget(),
                invalidEvent2IndexLeftTarget(),  invalidEvent2IndexRightTarget(),
                swapSuccessTarget()
        };

        int targetsHit = 0;
        int[][] csvInputs    = new int[objFunctions.length][4];
        String[] csvStatuses = new String[objFunctions.length];
        boolean[] csvHits    = new boolean[objFunctions.length];

        for (int t = 0; t < objFunctions.length; t++) {
            System.out.println("--- Branch " + branchIds[t] + " ---");
            System.out.println("Target: " + targets[t]);

            Vector vector = buildVector();
            TerminationPolicy tp =
                    TerminationPolicy.createMaxEvaluationsTerminationPolicy(1000);
            Initializer initializer = new RandomInitializer(new Well19937c());
            AlternatingVariableMethod avm =
                    new AlternatingVariableMethod(new GeometricSearch(), tp, initializer);
            Monitor monitor = avm.search(vector, objFunctions[t]);

            int[] found = extract(vector);
            SwapOrganizersResult result = SwapOrganizersTestable.swapOrganizersTestable(
                    found[0], found[1], found[2], found[3]);
            boolean hit = monitor.getBestObjVal().isOptimal();
            if (hit) targetsHit++;

            csvInputs[t]   = found;
            csvStatuses[t] = result.getStatus().name();
            csvHits[t]     = hit;

            System.out.printf("Best solution: [%d, %d, %d, %d]%n",
                    found[0], found[1], found[2], found[3]);
            System.out.printf("Best objective value: %s%n", hit ? "0.0" : "1.0");
            System.out.printf("Number of objective function evaluations: %d (unique: %d)%n",
                    monitor.getNumEvaluations(), monitor.getNumUniqueEvaluations());
            System.out.printf("Running time: %dms%n", monitor.getRunningTime());
            System.out.println("---");
            System.out.println();
        }

        System.out.println("==========================================================");
        System.out.println("  RAPORT FINAL -- BRANCH COVERAGE");
        System.out.println("  Branches acoperite : " + targetsHit + " / " + objFunctions.length);
        System.out.printf("  Branch coverage   : %.1f%%%n",
                (double) targetsHit / objFunctions.length * 100.0);
        System.out.println("==========================================================");

        saveToCSV(csvInputs, csvStatuses, csvHits);
    }

    private static void saveToCSV(int[][] inputs, String[] statuses, boolean[] hits) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
            pw.println("org1Index,org2Index,ev1Index,ev2Index,expectedStatus");
            for (int i = 0; i < inputs.length; i++) {
                if (hits[i]) {
                    pw.printf("%d,%d,%d,%d,%s%n",
                            inputs[i][0], inputs[i][1],
                            inputs[i][2], inputs[i][3],
                            statuses[i]);
                }
            }
            System.out.println("Inputurile generate au fost salvate in: " + CSV_PATH);
        } catch (IOException e) {
            System.err.println("Eroare la salvarea CSV: " + e.getMessage());
        }
    }
}