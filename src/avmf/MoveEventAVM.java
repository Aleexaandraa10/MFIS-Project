//package avmf;
//
//import avmf.testable.MoveEventTestable;
//import org.apache.commons.math3.random.Well19937c;
//import org.avmframework.AlternatingVariableMethod;
//import org.avmframework.Monitor;
//import org.avmframework.TerminationPolicy;
//import org.avmframework.Vector;
//import org.avmframework.initialization.Initializer;
//import org.avmframework.initialization.RandomInitializer;
//import org.avmframework.localsearch.GeometricSearch;
//import org.avmframework.objective.NumericObjectiveValue;
//import org.avmframework.objective.ObjectiveFunction;
//import org.avmframework.objective.ObjectiveValue;
//import org.avmframework.variable.IntegerVariable;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * MoveEventAVM — Generare automată de date de test cu AVMf
// * pentru metoda MoveEventTestable.moveEventTestable()
// *
// * TARGET 1 — return 1 INVALID_DAY     (branch 1T)
// * TARGET 2 — return 2 INVALID_INDEX   (branch 2T)
// * TARGET 3 — return 3 INVALID_NEW_DAY (branch 3T)
// * TARGET 4 — return 4 SAME_DAY        (branch 4T)
// * TARGET 5 — return 0 MOVE_SUCCESS    (branch 5T)
// */
//public class MoveEventAVM {
//
//    private static final String CSV_PATH = "generated_move_event_inputs.csv";
//
//    private static NumericObjectiveValue dist(double distance) {
//        return NumericObjectiveValue.lowerIsBetterObjectiveValue(distance, 0.0);
//    }
//
//    private static Vector buildVector() {
//        Vector v = new Vector();
//        v.addVariable(new IntegerVariable(2,  -1,  5));  // dayEvent
//        v.addVariable(new IntegerVariable(0,  -3, 10));  // eventIndex
//        v.addVariable(new IntegerVariable(1,  -1,  5));  // newDay
//        return v;
//    }
//
//    private static int[] extract(Vector v) {
//        return new int[]{
//                ((IntegerVariable) v.getVariable(0)).asInt(),
//                ((IntegerVariable) v.getVariable(1)).asInt(),
//                ((IntegerVariable) v.getVariable(2)).asInt()
//        };
//    }
//
//    private static double distB1Pass(int dayEvent) {
//        if (dayEvent >= 1 && dayEvent <= 3) return 0;
//        if (dayEvent < 1) return (1 - dayEvent);
//        return (dayEvent - 3);
//    }
//
//    private static double distB2Pass(int eventIndex) {
//        if (eventIndex >= 0) return 0;
//        return -eventIndex;
//    }
//
//    private static double distB3Pass(int newDay) {
//        if (newDay >= 1 && newDay <= 3) return 0;
//        if (newDay < 1) return (1 - newDay);
//        return (newDay - 3);
//    }
//
//    public static ObjectiveFunction invalidDayTarget() {
//        return new ObjectiveFunction() {
//            @Override
//            protected ObjectiveValue computeObjectiveValue(Vector vector) {
//                int[] p = extract(vector);
//                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);
//                if (result == 1) return dist(0);
//                int day = p[0];
//                return dist(Math.min(day - 0, 4 - day) + 1);
//            }
//        };
//    }
//
//    public static ObjectiveFunction invalidIndexTarget() {
//        return new ObjectiveFunction() {
//            @Override
//            protected ObjectiveValue computeObjectiveValue(Vector vector) {
//                int[] p = extract(vector);
//                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);
//                if (result == 2) return dist(0);
//                double d = distB1Pass(p[0]);
//                if (p[1] >= 0) d += p[1] + 1;
//                return dist(d);
//            }
//        };
//    }
//
//    public static ObjectiveFunction invalidNewDayTarget() {
//        return new ObjectiveFunction() {
//            @Override
//            protected ObjectiveValue computeObjectiveValue(Vector vector) {
//                int[] p = extract(vector);
//                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);
//                if (result == 3) return dist(0);
//                double d = distB1Pass(p[0]) + distB2Pass(p[1]);
//                int newDay = p[2];
//                if (newDay >= 1 && newDay <= 3) d += Math.min(newDay - 0, 4 - newDay) + 1;
//                return dist(d);
//            }
//        };
//    }
//
//    public static ObjectiveFunction sameDayTarget() {
//        return new ObjectiveFunction() {
//            @Override
//            protected ObjectiveValue computeObjectiveValue(Vector vector) {
//                int[] p = extract(vector);
//                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);
//                if (result == 4) return dist(0);
//                double d = distB1Pass(p[0]) + distB2Pass(p[1]) + distB3Pass(p[2]);
//                if (p[2] != p[0]) d += Math.abs(p[2] - p[0]);
//                return dist(d + 1);
//            }
//        };
//    }
//
//    public static ObjectiveFunction moveSuccessTarget() {
//        return new ObjectiveFunction() {
//            @Override
//            protected ObjectiveValue computeObjectiveValue(Vector vector) {
//                int[] p = extract(vector);
//                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);
//
//                if (result == 0) return dist(0);
//
//                double d = 0;
//
//                d += distB1Pass(p[0]);
//                d += distB2Pass(p[1]);
//                d += distB3Pass(p[2]);
//
//                // B4 trebuie să treacă: newDay != dayEvent
//                if (p[2] == p[0]) d += 1;
//
//                // B5 trebuie să TREACĂ (nu să iasă)
//                // deci NU vrem: newDay==1 && eventIndex>=4 && (dayEvent==3 || eventIndex>5)
//                // cel mai simplu: newDay != 1 sau eventIndex < 4
//                if (p[2] == 1 && p[1] >= 4) {
//                    // suntem pe calea care ar putea intra pe B5
//                    // împingem newDay spre 2 sau 3
//                    d += Math.abs(p[2] - 2);
//                }
//
//                return dist(d + 1);
//            }
//        };
//    }
//
//    // =============================================================
//    //  TARGET 5 — return 5 (RESTRICTED_MOVE)
//    //  Branch: newDay == 1 && eventIndex >= 4 && (dayEvent == 3 || eventIndex > 5)
//    //
//    //  B1, B2, B3, B4 trebuie să treacă:
//    //    dayEvent  ∈ [1,3]
//    //    eventIndex >= 0
//    //    newDay    ∈ [1,3]
//    //    newDay   != dayEvent
//    //
//    //  La B5 vrem să IEȘIM — două căi posibile:
//    //    Calea A: newDay=1, eventIndex∈[4,5], dayEvent=3
//    //    Calea B: newDay=1, eventIndex>5,     dayEvent∈{1,2}
//    // =============================================================
//    public static ObjectiveFunction restrictedMoveTarget() {
//        return new ObjectiveFunction() {
//            @Override
//            protected ObjectiveValue computeObjectiveValue(Vector vector) {
//                int[] p = extract(vector);
//                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);
//
//                if (result == 5) return dist(0);
//
//                double d = 0;
//
//                // B1 trebuie să treacă: dayEvent în [1,3]
//                d += distB1Pass(p[0]);
//
//                // B2 trebuie să treacă: eventIndex >= 0
//                d += distB2Pass(p[1]);
//
//                // B3 trebuie să treacă: newDay în [1,3]
//                d += distB3Pass(p[2]);
//
//                // B4 trebuie să treacă: newDay != dayEvent
//                if (p[2] == p[0]) d += 1;
//
//                // B5 trebuie să fie TRUE
//                // newDay trebuie să fie exact 1
//                if (p[2] != 1) d += Math.abs(p[2] - 1);
//
//                // eventIndex trebuie >= 4
//                if (p[1] < 4) d += (4 - p[1]);
//
//                // dayEvent == 3 SAU eventIndex > 5
//                // alegem calea cu distanța minimă
//                double pathA = (p[0] != 3) ? Math.abs(p[0] - 3) : 0; // spre dayEvent=3
//                double pathB = (p[1] <= 5) ? (6 - p[1]) : 0;          // spre eventIndex>5
//                d += Math.min(pathA, pathB);
//
//                return dist(d + 1);
//            }
//        };
//    }
//
//    public static void main(String[] args) {
//
//        String[] branchIds = { "1T", "2T", "3T", "4T", "5T" , "6T"};
//        String[] targets = {
//                "dayEvent < 1 || dayEvent > 3 = true    ->  zi invalida",
//                "eventIndex < 0 = true                  ->  index invalid",
//                "newDay < 1 || newDay > 3 = true         ->  zi noua invalida",
//                "newDay == dayEvent = true               ->  aceeasi zi",
//                "newDay==1 && eventIndex>=4 && (dayEvent==3 || eventIndex>5) = true  ->  mutare restrictionata",
//                "toate conditiile valide                 ->  MOVE_SUCCESS"
//        };
//
//        ObjectiveFunction[] objFunctions = {
//                invalidDayTarget(), invalidIndexTarget(),
//                invalidNewDayTarget(), sameDayTarget(), restrictedMoveTarget(),  moveSuccessTarget()
//        };
//
//        int targetsHit = 0;
//        int[][] csvInputs   = new int[objFunctions.length][3];
//        String[] csvResults = new String[objFunctions.length];
//        boolean[] csvHits   = new boolean[objFunctions.length];
//
//        for (int t = 0; t < objFunctions.length; t++) {
//            System.out.println("--- Branch " + branchIds[t] + " ---");
//            System.out.println("Target: " + targets[t]);
//
//            Vector vector = buildVector();
//            TerminationPolicy tp = TerminationPolicy.createMaxEvaluationsTerminationPolicy(1000);
//            Initializer initializer = new RandomInitializer(new Well19937c());
//            AlternatingVariableMethod avm =
//                    new AlternatingVariableMethod(new GeometricSearch(), tp, initializer);
//            Monitor monitor = avm.search(vector, objFunctions[t]);
//
//            int[] found = extract(vector);
//            int result  = MoveEventTestable.moveEventTestable(found[0], found[1], found[2]);
//            boolean hit = monitor.getBestObjVal().isOptimal();
//            if (hit) targetsHit++;
//
//            csvInputs[t]  = found;
//            csvResults[t] = String.valueOf(result);
//            csvHits[t]    = hit;
//
//            System.out.printf("Best solution: [%d, %d, %d]%n", found[0], found[1], found[2]);
//            System.out.printf("Best objective value: %s%n", hit ? "0.0" : "1.0");
//            System.out.printf("Number of objective function evaluations: %d (unique: %d)%n",
//                    monitor.getNumEvaluations(), monitor.getNumUniqueEvaluations());
//            System.out.printf("Running time: %dms%n", monitor.getRunningTime());
//            System.out.println("---");
//            System.out.println();
//        }
//
//        System.out.println("==========================================================");
//        System.out.println("  RAPORT FINAL -- BRANCH COVERAGE");
//        System.out.println("  Branches acoperite : " + targetsHit + " / " + objFunctions.length);
//        System.out.printf("  Branch coverage   : %.1f%%%n",
//                (double) targetsHit / objFunctions.length * 100.0);
//        System.out.println("==========================================================");
//
//        saveToCSV(csvInputs, csvResults, csvHits);
//    }
//
//    private static void saveToCSV(int[][] inputs, String[] results, boolean[] hits) {
//        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
//            pw.println("dayEvent,eventIndex,newDay,expectedResult");
//            for (int i = 0; i < inputs.length; i++) {
//                if (hits[i]) {
//                    pw.printf("%d,%d,%d,%s%n",
//                            inputs[i][0], inputs[i][1], inputs[i][2], results[i]);
//                }
//            }
//            System.out.println("Inputurile generate au fost salvate in: " + CSV_PATH);
//        } catch (IOException e) {
//            System.err.println("Eroare la salvarea CSV: " + e.getMessage());
//        }
//    }
//}


package avmf;

import avmf.testable.MoveEventTestable;
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
 * MoveEventAVM — Generare automată de date de test cu AVMf
 * pentru metoda MoveEventTestable.moveEventTestable()
 *
 * Fiecare branch cu || este tratat cu DOUĂ target-uri separate:
 *   - xT-L (Left)  → partea stângă a condiției ||
 *   - xT-R (Right) → partea dreaptă a condiției ||
 *
 * Astfel AVMf generează automat inputuri pentru ambele părți
 * ale fiecărei condiții compuse, obținând 100% branch coverage
 * fără teste manuale suplimentare.
 *
 * TARGET 1L — dayEvent < 1               (stânga B1)
 * TARGET 1R — dayEvent > 3               (dreapta B1)
 * TARGET 2T — eventIndex < 0             (B2 — condiție simplă)
 * TARGET 3L — newDay < 1                 (stânga B3)
 * TARGET 3R — newDay > 3                 (dreapta B3)
 * TARGET 4T — newDay == dayEvent         (B4 — condiție simplă)
 * TARGET 5A — B5 calea A: dayEvent==3    (stânga || din B5)
 * TARGET 5B — B5 calea B: eventIndex>5   (dreapta || din B5)
 * TARGET 6T — MOVE_SUCCESS               (B6)
 */
public class MoveEventAVM {

    private static final String CSV_PATH = "generated_move_event_inputs.csv";

    private static NumericObjectiveValue dist(double distance) {
        return NumericObjectiveValue.lowerIsBetterObjectiveValue(distance, 0.0);
    }

    private static Vector buildVector() {
        Vector v = new Vector();
        v.addVariable(new IntegerVariable(2,  -1,  5));  // dayEvent
        v.addVariable(new IntegerVariable(0,  -3, 10));  // eventIndex
        v.addVariable(new IntegerVariable(1,  -1,  5));  // newDay
        return v;
    }

    private static int[] extract(Vector v) {
        return new int[]{
                ((IntegerVariable) v.getVariable(0)).asInt(),
                ((IntegerVariable) v.getVariable(1)).asInt(),
                ((IntegerVariable) v.getVariable(2)).asInt()
        };
    }

    // ─────────────────────────────────────────────────────────────
    //  METODE UTILITARE — distanța pentru ca fiecare branch să TREACĂ
    // ─────────────────────────────────────────────────────────────

    // B1 trece: dayEvent în [1, 3]
    private static double distB1Pass(int dayEvent) {
        if (dayEvent >= 1 && dayEvent <= 3) return 0;
        if (dayEvent < 1) return (1 - dayEvent);
        return (dayEvent - 3);
    }

    // B2 trece: eventIndex >= 0
    private static double distB2Pass(int eventIndex) {
        if (eventIndex >= 0) return 0;
        return -eventIndex;
    }

    // B3 trece: newDay în [1, 3]
    private static double distB3Pass(int newDay) {
        if (newDay >= 1 && newDay <= 3) return 0;
        if (newDay < 1) return (1 - newDay);
        return (newDay - 3);
    }

    // =============================================================
    //  TARGET 1L — dayEvent < 1  (partea STÂNGĂ a B1)
    //  Vrem ca dayEvent să fie < 1 (negativ sau 0)
    // =============================================================
    public static ObjectiveFunction invalidDayLeftTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                if (result == 1 && p[0] < 1) return dist(0);

                double d = 0;
                if (p[0] >= 1) d += p[0];  // cât trebuie scăzut ca dayEvent < 1
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 1R — dayEvent > 3  (partea DREAPTĂ a B1)
    //  Vrem ca dayEvent să fie > 3
    // =============================================================
    public static ObjectiveFunction invalidDayRightTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                if (result == 1 && p[0] > 3) return dist(0);

                double d = 0;
                if (p[0] <= 3) d += (4 - p[0]);  // cât trebuie crescut ca dayEvent > 3
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 2T — eventIndex < 0  (B2 — condiție simplă, un singur target)
    //  B1 trebuie să treacă: dayEvent în [1, 3]
    // =============================================================
    public static ObjectiveFunction invalidIndexTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                if (result == 2) return dist(0);

                double d = distB1Pass(p[0]);
                if (p[1] >= 0) d += p[1] + 1;  // cât trebuie scăzut ca eventIndex < 0
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 3L — newDay < 1  (partea STÂNGĂ a B3)
    //  B1, B2 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction invalidNewDayLeftTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                if (result == 3 && p[2] < 1) return dist(0);

                double d = distB1Pass(p[0]) + distB2Pass(p[1]);
                if (p[2] >= 1) d += p[2];  // cât trebuie scăzut ca newDay < 1
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 3R — newDay > 3  (partea DREAPTĂ a B3)
    //  B1, B2 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction invalidNewDayRightTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                if (result == 3 && p[2] > 3) return dist(0);

                double d = distB1Pass(p[0]) + distB2Pass(p[1]);
                if (p[2] <= 3) d += (4 - p[2]);  // cât trebuie crescut ca newDay > 3
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 4T — newDay == dayEvent  (B4 — condiție simplă)
    //  B1, B2, B3 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction sameDayTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                if (result == 4) return dist(0);

                double d = distB1Pass(p[0]) + distB2Pass(p[1]) + distB3Pass(p[2]);
                if (p[2] != p[0]) d += Math.abs(p[2] - p[0]);
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 5A — B5 calea A: dayEvent == 3  (stânga || din B5)
    //  Vrem: newDay==1, eventIndex>=4, dayEvent==3
    //  B1, B2, B3, B4 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction restrictedMovePathATarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                // Target atins dacă result==5 ȘI dayEvent==3 (calea A activată)
                if (result == 5 && p[0] == 3) return dist(0);

                double d = 0;

                // B1, B2, B3 trebuie să treacă
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[2]);

                // B4 trebuie să treacă: newDay != dayEvent
                // newDay=1 și dayEvent=3 → deja diferite, B4 trece
                if (p[2] == p[0]) d += 1;

                // Condiția B5: newDay==1
                if (p[2] != 1) d += Math.abs(p[2] - 1);

                // eventIndex >= 4
                if (p[1] < 4) d += (4 - p[1]);

                // dayEvent == 3 (calea A)
                if (p[0] != 3) d += Math.abs(p[0] - 3);

                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 5B — B5 calea B: eventIndex > 5  (dreapta || din B5)
    //  Vrem: newDay==1, eventIndex>5, dayEvent != 3
    //  B1, B2, B3, B4 trebuie să treacă
    // =============================================================
    public static ObjectiveFunction restrictedMovePathBTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                // Target atins dacă result==5 ȘI eventIndex>5 (calea B activată)
                if (result == 5 && p[1] > 5) return dist(0);

                double d = 0;

                // B1, B2, B3 trebuie să treacă
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[2]);

                // B4 trebuie să treacă: newDay != dayEvent
                if (p[2] == p[0]) d += 1;

                // Condiția B5: newDay==1
                if (p[2] != 1) d += Math.abs(p[2] - 1);

                // eventIndex > 5 (calea B)
                if (p[1] <= 5) d += (6 - p[1]);

                // dayEvent != 3 (ca să nu activăm calea A)
                // nu adăugăm penalizare — AVMf poate alege liber

                return dist(d + 1);
            }
        };
    }


    // =============================================================
//  TARGET 5F — B5 FALSE
//  Execuția AJUNGE la B5 (newDay==1 && eventIndex in [4,5])
//  dar iese pe FALSE pentru că dayEvent!=3 și eventIndex<=5
//  Rezultat: return 0 (MOVE_SUCCESS)
// =============================================================
    public static ObjectiveFunction branch5FalseTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                // Target atins: ajunge la B5 dar iese pe FALSE → return 0
                if (result == 0 && p[2] == 1 && p[1] >= 4
                        && p[0] != 3 && p[1] <= 5) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[2]);

                // B4 trebuie să treacă: newDay != dayEvent
                if (p[2] == p[0]) d += 1;

                // newDay == 1
                if (p[2] != 1) d += Math.abs(p[2] - 1);

                // eventIndex în [4,5]
                if (p[1] < 4) d += (4 - p[1]);
                if (p[1] > 5) d += (p[1] - 5);

                // dayEvent != 3
                if (p[0] == 3) d += 1;

                return dist(d + 1);
            }
        };
    }

    // =============================================================
//  TARGET 5C — B5 FALSE prin newDay != 1
//  Execuția ajunge aproape de B5 dar newDay != 1
//  Rezultat: return 0
// =============================================================
    public static ObjectiveFunction branch5FalseNewDayTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                // newDay != 1, eventIndex >= 4, totul valid → B5 FALSE prin newDay
                if (result == 0 && p[2] != 1 && p[1] >= 4) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[2]);

                // B4 trebuie să treacă
                if (p[2] == p[0]) d += 1;

                // newDay != 1 — împingem spre 2 sau 3
                if (p[2] == 1) d += 1;

                // eventIndex >= 4
                if (p[1] < 4) d += (4 - p[1]);

                return dist(d + 1);
            }
        };
    }

    // =============================================================
//  TARGET 5D — B5 TRUE prin eventIndex > 5 explicit
//  Forțăm calea B: newDay==1, eventIndex>=6, dayEvent!=3
// =============================================================
    public static ObjectiveFunction branch5TrueEventIndexTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                // result==5 ȘI eventIndex > 5 ȘI dayEvent != 3
                if (result == 5 && p[1] > 5 && p[0] != 3) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[2]);

                if (p[2] == p[0]) d += 1;

                // newDay == 1
                if (p[2] != 1) d += Math.abs(p[2] - 1);

                // eventIndex > 5 strict
                if (p[1] <= 5) d += (6 - p[1]);

                // dayEvent != 3
                if (p[0] == 3) d += 1;

                return dist(d + 1);
            }
        };

    }

    // =============================================================
//  TARGET 5E — B5 FALSE prin eventIndex < 4
//  Execuția ajunge la B5 dar eventIndex < 4 → B5 FALSE imediat
//  Rezultat: return 0
// =============================================================
    public static ObjectiveFunction branch5FalseEventIndexTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                // newDay==1, eventIndex < 4, totul valid → B5 FALSE → return 0
                if (result == 0 && p[2] == 1 && p[1] >= 0 && p[1] < 4) return dist(0);

                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[2]);

                // B4 trebuie să treacă: newDay != dayEvent
                if (p[2] == p[0]) d += 1;

                // newDay == 1
                if (p[2] != 1) d += Math.abs(p[2] - 1);

                // eventIndex în [0, 3] — valid dar < 4
                if (p[1] < 0) d += -p[1];
                if (p[1] >= 4) d += (p[1] - 3);

                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 6T — MOVE_SUCCESS  (B6)
    //  Toate branch-urile trebuie să TREACĂ
    // =============================================================
    public static ObjectiveFunction moveSuccessTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                int result = MoveEventTestable.moveEventTestable(p[0], p[1], p[2]);

                if (result == 0) return dist(0);

                double d = 0;

                // B1, B2, B3 trebuie să treacă
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[2]);

                // B4 trebuie să treacă: newDay != dayEvent
                if (p[2] == p[0]) d += 1;

                // B5 trebuie să TREACĂ (nu să iasă)
                // NU vrem: newDay==1 && eventIndex>=4 && (dayEvent==3 || eventIndex>5)
                // cel mai simplu: împingem newDay spre 2 sau 3
                if (p[2] == 1 && p[1] >= 4) {
                    d += Math.abs(p[2] - 2);
                }

                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  MAIN — rulează toate cele 9 target-uri
    // =============================================================
    public static void main(String[] args) {

        String[] branchIds = {
                "1T-L", "1T-R",
                "2T",
                "3T-L", "3T-R",
                "4T",
                "5T-A", "5T-B","5T-F","5T-C", "5T-D", "5T-E",
                "6T"
        };

        String[] targets = {
                "dayEvent < 1 = true                                          ->  zi invalida (stanga)",
                "dayEvent > 3 = true                                          ->  zi invalida (dreapta)",
                "eventIndex < 0 = true                                        ->  index invalid",
                "newDay < 1 = true                                            ->  zi noua invalida (stanga)",
                "newDay > 3 = true                                            ->  zi noua invalida (dreapta)",
                "newDay == dayEvent = true                                    ->  aceeasi zi",
                "newDay==1 && eventIndex>=4 && dayEvent==3 = true            ->  mutare restrictionata calea A",
                "newDay==1 && eventIndex>5 && dayEvent!=3 = true             ->  mutare restrictionata calea B",
                "newDay==1 && eventIndex in [4,5] && dayEvent!=3 = false     ->  B5 FALSE, MOVE_SUCCESS",
                "newDay!=1 && eventIndex>=4 = false  ->  B5 FALSE prin newDay",
                "newDay==1 && eventIndex>5 && dayEvent!=3 = true  ->  B5 TRUE calea B explicita",
                "newDay==1 && eventIndex<4 = false  ->  B5 FALSE prin eventIndex",
                "toate conditiile valide                                      ->  MOVE_SUCCESS"
        };

        ObjectiveFunction[] objFunctions = {
                invalidDayLeftTarget(),         invalidDayRightTarget(),
                invalidIndexTarget(),
                invalidNewDayLeftTarget(),      invalidNewDayRightTarget(),
                sameDayTarget(),
                restrictedMovePathATarget(),    restrictedMovePathBTarget(),
                branch5FalseTarget(), branch5FalseNewDayTarget(),
                branch5TrueEventIndexTarget(), branch5FalseEventIndexTarget(),
                moveSuccessTarget()
        };

        int targetsHit = 0;
        int[][] csvInputs   = new int[objFunctions.length][3];
        String[] csvResults = new String[objFunctions.length];
        boolean[] csvHits   = new boolean[objFunctions.length];

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
            int result  = MoveEventTestable.moveEventTestable(found[0], found[1], found[2]);
            boolean hit = monitor.getBestObjVal().isOptimal();
            if (hit) targetsHit++;

            csvInputs[t]  = found;
            csvResults[t] = String.valueOf(result);
            csvHits[t]    = hit;

            System.out.printf("Best solution: [%d, %d, %d]%n", found[0], found[1], found[2]);
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

        saveToCSV(csvInputs, csvResults, csvHits);
    }

    private static void saveToCSV(int[][] inputs, String[] results, boolean[] hits) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
            pw.println("dayEvent,eventIndex,newDay,expectedResult");
            for (int i = 0; i < inputs.length; i++) {
                if (hits[i]) {
                    pw.printf("%d,%d,%d,%s%n",
                            inputs[i][0], inputs[i][1], inputs[i][2], results[i]);
                }
            }
            System.out.println("Inputurile generate au fost salvate in: " + CSV_PATH);
        } catch (IOException e) {
            System.err.println("Eroare la salvarea CSV: " + e.getMessage());
        }
    }
}