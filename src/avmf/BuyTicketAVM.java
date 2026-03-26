package avmf;

import avmf.model.BuyTicketResult;
import avmf.model.BuyTicketStatus;
import avmf.testable.BuyTicketTestable;
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
 * BuyTicketAVM — Generare automată de date de test cu AVMf
 * pentru metoda BuyTicketTestable.buyTicketTestable()
 *
 * TARGET 1 — INVALID_NAME_INDEX  (branch 1T)
 * TARGET 2 — INVALID_AGE         (branch 2T)
 * TARGET 3 — INVALID_NAME        (branch 3T)
 * TARGET 4 — INVALID_BASE_PRICE  (branch 4T)
 * TARGET 5 — INVALID_DISCOUNT_FOR_UNDER25 (branch 5T)
 * TARGET 6 — INVALID_DISCOUNT_FOR_REGULAR (branch 6T)
 * TARGET 7 — UNDER25_SUCCESS     (branch 7T)
 * TARGET 8 — REGULAR_SUCCESS     (branch 8T)
 */
public class BuyTicketAVM {

    private static final String CSV_PATH = "generated_test_inputs.csv";

    private static NumericObjectiveValue dist(double distance) {
        return NumericObjectiveValue.lowerIsBetterObjectiveValue(distance, 0.0);
    }

    private static Vector buildVector() {
        Vector v = new Vector();
        v.addVariable(new IntegerVariable(0,   -3,  8));  // nameIndex
        v.addVariable(new IntegerVariable(30,   0, 80));  // age
        v.addVariable(new IntegerVariable(300,  0, 600)); // basePrice
        v.addVariable(new IntegerVariable(0,   -5, 30));  // discount
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

    private static double distB1Pass(int nameIndex) {
        if (nameIndex >= 0 && nameIndex <= 5) return 0;
        return Math.abs(nameIndex - 2) + 1;
    }

    private static double distB2Pass(int age) {
        if (age >= 14 && age <= 60) return 0;
        if (age < 14) return (14 - age);
        return (age - 60);
    }

    private static double distB3Pass(int nameIndex) {
        if (nameIndex >= 0 && nameIndex <= 2) return 0;
        return Math.abs(nameIndex - 1) + 1;
    }

    private static double distB4Pass(int basePrice) {
        if (basePrice >= 200 && basePrice <= 400) return 0;
        if (basePrice < 200) return (200 - basePrice);
        return (basePrice - 400);
    }

    // =============================================================
    //  TARGET 1 — INVALID_NAME_INDEX
    //  Branch: nameIndex < 0 || nameIndex >= 6 = true
    // =============================================================
    public static ObjectiveFunction invalidNameIndexTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.INVALID_NAME_INDEX) return dist(0);
                int ni = p[0];
                double toUnder = ni + 1;
                double toOver  = 6 - ni;
                return dist(Math.min(toUnder, toOver) + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 2 — INVALID_AGE
    //  Branch: age < 14 || age > 60 = true
    // =============================================================
    public static ObjectiveFunction invalidAgeTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.INVALID_AGE) return dist(0);
                double d = 0;
                d += distB1Pass(p[0]);
                int age = p[1];
                if (age >= 14 && age <= 60) {
                    double toLow  = age - 13;
                    double toHigh = 61 - age;
                    d += Math.min(toLow, toHigh) + 1;
                }
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 3 — INVALID_NAME
    //  Branch: name.matches(regex) = false
    // =============================================================
    public static ObjectiveFunction invalidNameTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.INVALID_NAME) return dist(0);
                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                if (p[0] >= 0 && p[0] <= 2) d += (3 - p[0]) + 1;
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 4 — INVALID_BASE_PRICE
    //  Branch: basePrice < 200 || basePrice > 400 = true
    // =============================================================
    public static ObjectiveFunction invalidBasePriceTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.INVALID_BASE_PRICE) return dist(0);
                double d = 0;
                d += distB1Pass(p[0]);
                d += distB2Pass(p[1]);
                d += distB3Pass(p[0]);
                int bp = p[2];
                if (bp >= 200 && bp <= 400) {
                    double toLow  = bp - 199;
                    double toHigh = 401 - bp;
                    d += Math.min(toLow, toHigh) + 1;
                }
                return dist(d);
            }
        };
    }

    // =============================================================
    //  TARGET 5 — INVALID_DISCOUNT_FOR_UNDER25
    //  Branch: isUnder25 && (discount < 5 || discount > 20) = true
    // =============================================================
    public static ObjectiveFunction invalidDiscountUnder25Target() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.INVALID_DISCOUNT_FOR_UNDER25) return dist(0);
                double d = 0;
                d += distB1Pass(p[0]);
                if      (p[1] < 14) d += (14 - p[1]);
                else if (p[1] > 25) d += (p[1] - 25);
                d += distB3Pass(p[0]);
                d += distB4Pass(p[2]);
                if (p[3] >= 5 && p[3] <= 20) {
                    double toUnder = p[3] - 4;
                    double toOver  = 21 - p[3];
                    d += Math.min(toUnder, toOver) + 1;
                }
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 6 — INVALID_DISCOUNT_FOR_REGULAR
    //  Branch: !isUnder25 && discount != 0 = true
    // =============================================================
    public static ObjectiveFunction invalidDiscountRegularTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.INVALID_DISCOUNT_FOR_REGULAR) return dist(0);
                double d = 0;
                d += distB1Pass(p[0]);
                if      (p[1] <= 25) d += (26 - p[1]);
                else if (p[1] >  60) d += (p[1] - 60);
                d += distB3Pass(p[0]);
                d += distB4Pass(p[2]);
                if (p[3] == 0) d += 1;
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 7 — UNDER25_SUCCESS
    //  Branch: toate conditiile valide, age <= 25
    // =============================================================
    public static ObjectiveFunction under25SuccessTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.UNDER25_SUCCESS) return dist(0);
                double d = 0;
                d += distB3Pass(p[0]);
                if      (p[1] < 14) d += (14 - p[1]);
                else if (p[1] > 25) d += (p[1] - 25);
                d += distB4Pass(p[2]);
                if      (p[3] < 5)  d += (5  - p[3]);
                else if (p[3] > 20) d += (p[3] - 20);
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  TARGET 8 — REGULAR_SUCCESS
    //  Branch: toate conditiile valide, age > 25
    // =============================================================
    public static ObjectiveFunction regularSuccessTarget() {
        return new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                int[] p = extract(vector);
                BuyTicketResult r = BuyTicketTestable.buyTicketTestable(p[0], p[1], p[2], p[3]);
                if (r.getStatus() == BuyTicketStatus.REGULAR_SUCCESS) return dist(0);
                double d = 0;
                d += distB3Pass(p[0]);
                if      (p[1] <= 25) d += (26 - p[1]);
                else if (p[1] >  60) d += (p[1] - 60);
                d += distB4Pass(p[2]);
                if (p[3] != 0) d += Math.abs(p[3]);
                return dist(d + 1);
            }
        };
    }

    // =============================================================
    //  MAIN — rulează toate cele 8 target-uri
    // =============================================================
    public static void main(String[] args) {

        String[] branchIds = {
                "1T", "2T", "3T", "4T", "5T", "6T", "7T", "8T"
        };

        String[] targets = {
                "nameIndex < 0 || nameIndex >= 6 = true  ->  nameIndex invalid",
                "age < 14 || age > 60 = true             ->  varsta invalida",
                "name.matches(regex) = false             ->  nume invalid",
                "basePrice < 200 || basePrice > 400 = true ->  pret invalid",
                "isUnder25 && discount not in [5,20] = true ->  discount invalid under25",
                "!isUnder25 && discount != 0 = true      ->  discount invalid regular",
                "toate conditiile valide, age <= 25      ->  UNDER25_SUCCESS",
                "toate conditiile valide, age > 25       ->  REGULAR_SUCCESS"
        };

        ObjectiveFunction[] objFunctions = {
                invalidNameIndexTarget(), invalidAgeTarget(),
                invalidNameTarget(),      invalidBasePriceTarget(),
                invalidDiscountUnder25Target(), invalidDiscountRegularTarget(),
                under25SuccessTarget(),   regularSuccessTarget()
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
            GeometricSearch localSearch = new GeometricSearch();
            AlternatingVariableMethod avm =
                    new AlternatingVariableMethod(localSearch, tp, initializer);
            Monitor monitor = avm.search(vector, objFunctions[t]);

            int[] found   = extract(vector);
            BuyTicketResult result = BuyTicketTestable.buyTicketTestable(
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
            pw.println("nameIndex,age,basePrice,discount,expectedStatus");
            for (int i = 0; i < inputs.length; i++) {
                if (hits[i]) {
                    pw.printf("%d,%d,%d,%d,%s%n",
                            inputs[i][0], inputs[i][1],
                            inputs[i][2], inputs[i][3], statuses[i]);
                }
            }
            System.out.println("Inputurile generate au fost salvate in: " + CSV_PATH);
        } catch (IOException e) {
            System.err.println("Eroare la salvarea CSV: " + e.getMessage());
        }
    }
}