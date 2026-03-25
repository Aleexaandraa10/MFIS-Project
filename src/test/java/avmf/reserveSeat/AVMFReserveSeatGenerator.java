package avmf.reserveSeat;

import org.avmframework.Vector;
import org.avmframework.variable.IntegerVariable;
import org.avmframework.objective.ObjectiveFunction;
import org.avmframework.objective.NumericObjectiveValue;
import org.avmframework.localsearch.PatternSearch;
import org.avmframework.TerminationPolicy;
import org.avmframework.Monitor;
import org.avmframework.TerminationException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class AVMFReserveSeatGenerator {

    static Random rand = new Random();

    public static void main(String[] args) throws Exception {

        new FileWriter(
                "src/test/java/avmf/reserveSeat/generated_tests.txt", false
        ).close();

        generate(1);
        generate(21);
        generate(22);
        generate(3);
        generate(4);
        generate(0);
    }

    // ================= GENERATE =================

    public static void generate(int targetBranch) {

        boolean found = false;

        for (int attempt = 0; attempt < 100; attempt++) {

            IntegerVariable talksSize =
                    new IntegerVariable(rand.nextInt(6), 0, 5);

            IntegerVariable index =
                    new IntegerVariable(rand.nextInt(11) - 5, -5, 5);

            IntegerVariable reserved =
                    new IntegerVariable(rand.nextInt(41), 0, 40);

            IntegerVariable seats =
                    new IntegerVariable(rand.nextInt(21), 0, 20);

            IntegerVariable exists =
                    new IntegerVariable(rand.nextInt(2), 0, 1);

            Vector vector = new Vector();
            vector.addVariable(talksSize);
            vector.addVariable(index);
            vector.addVariable(reserved);
            vector.addVariable(seats);
            vector.addVariable(exists);

            ObjectiveFunction objective = createObjective(targetBranch);

            TerminationPolicy tp =
                    TerminationPolicy.createMaxEvaluationsTerminationPolicy(20000);

            Monitor monitor = new Monitor(tp);
            objective.setMonitor(monitor);

            PatternSearch search = new PatternSearch();

            try {
                search.search(talksSize, vector, objective);
            } catch (TerminationException ignored) {}

            int t = talksSize.asInt();
            int i = index.asInt();
            int r = reserved.asInt();
            int s = seats.asInt();
            int e = exists.asInt();

            int result = ReserveSeatTestable.reserveSeatTestable(
                    t, i, r, s, e == 1
            );

            boolean ok = false;

            // branch normal
            if (result == targetBranch) {
                ok = true;
            }

            // pseudo-branch: index < 0
            if (targetBranch == 21 && result == 2 && i < 0) {
                ok = true;
            }

            // pseudo-branch: index > talksSize - 1
            if (targetBranch == 22 && result == 2 && i > t - 1) {
                ok = true;
            }

            if (ok) {
                found = true;
                System.out.println("\n✅ FOUND input for branch " + targetBranch);

                System.out.println(
                        "talks=" + t +
                                ", index=" + i +
                                ", reserved=" + r +
                                ", seats=" + s +
                                ", exists=" + (e == 1)
                );

                try (FileWriter fw = new FileWriter(
                        "src/test/java/avmf/reserveSeat/generated_tests.txt", true)) {

                    fw.write(result + "," + t + "," + i + "," + r + "," + s + "," + e + "\n");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                break;
            }
        }

        if (!found) {
            System.out.println("\n❌ FAILED for branch " + targetBranch);
        }
    }

    // ================= FITNESS =================

    static ObjectiveFunction createObjective(int targetBranch) {

        return new ObjectiveFunction() {

            @Override
            public NumericObjectiveValue computeObjectiveValue(Vector v) {

                int talksSize = ((IntegerVariable) v.getVariables().get(0)).asInt();
                int index = ((IntegerVariable) v.getVariables().get(1)).asInt();
                int reserved = ((IntegerVariable) v.getVariables().get(2)).asInt();
                int seats = ((IntegerVariable) v.getVariables().get(3)).asInt();
                int exists = ((IntegerVariable) v.getVariables().get(4)).asInt();

                double fitness = 0;

                // -------- BRANCH 1: talksSize == 0 --------
                if (targetBranch == 1) {
                    fitness += Math.abs(talksSize);
                }

                // -------- BRANCH 2: invalid index --------
                if (targetBranch == 21) {

                    fitness += Math.max(0, 1 - talksSize);

                    // vrem index < 0
                    fitness += Math.max(0, index);
                }

                if (targetBranch == 22) {

                    fitness += Math.max(0, 1 - talksSize);

                    // vrem index > talksSize - 1
                    fitness += Math.max(0, (talksSize - 1) - index + 1);
                }

                // -------- BRANCH 3: reserved >= seats --------
                if (targetBranch == 3) {

                    fitness += Math.max(0, 1 - talksSize);

                    fitness += Math.max(0, -index);
                    fitness += Math.max(0, index - (talksSize - 1));

                    fitness += Math.max(0, seats - reserved);
                }

                // -------- BRANCH 4: exists == false --------
                if (targetBranch == 4) {

                    fitness += Math.max(0, 1 - talksSize);

                    fitness += Math.max(0, -index);
                    fitness += Math.max(0, index - (talksSize - 1));

                    fitness += Math.max(0, reserved - seats);

                    fitness += exists;
                }

                // -------- BRANCH 0: succes --------
                if (targetBranch == 0) {

                    fitness += Math.max(0, 1 - talksSize);

                    fitness += Math.max(0, -index);
                    fitness += Math.max(0, index - (talksSize - 1));

                    fitness += Math.max(0, reserved - seats);

                    fitness += Math.max(0, 1 - exists);
                }

                return NumericObjectiveValue.higherIsBetterObjectiveValue(-fitness);
            }
        };
    }
}