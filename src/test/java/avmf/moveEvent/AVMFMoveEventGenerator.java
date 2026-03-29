package avmf.moveEvent;

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

public class AVMFMoveEventGenerator {

    public static void main(String[] args) {

        for (int targetBranch = 0; targetBranch <= 4; targetBranch++) {
            System.out.println("\nGenerating test for branch: " + targetBranch);
            generate(targetBranch);
        }
    }

    static int random(int min, int max) {
        return min + (int)(Math.random() * (max - min + 1));
    }

    public static void generate(int targetBranch) {

        boolean found = false;

        for (int attempt = 0; attempt < 100; attempt++) {

            IntegerVariable dayVar = new IntegerVariable(
                    random(-2, 5), -2, 5);

            IntegerVariable indexVar = new IntegerVariable(
                    random(-200, 200), -200, 200);

            IntegerVariable newDayVar = new IntegerVariable(
                    random(-2, 5), -2, 5);

            Vector vector = new Vector();
            vector.addVariable(dayVar);
            vector.addVariable(indexVar);
            vector.addVariable(newDayVar);

            ObjectiveFunction objective = createObjective(targetBranch);

            TerminationPolicy tp =
                    TerminationPolicy.createMaxEvaluationsTerminationPolicy(20000);

            Monitor monitor = new Monitor(tp);
            objective.setMonitor(monitor);

            PatternSearch search = new PatternSearch();

            try {
                search.search(dayVar, vector, objective);
            } catch (TerminationException e) {}

            int day = dayVar.asInt();
            int index = indexVar.asInt();
            int newDay = newDayVar.asInt();

            int result = MoveEventTestable.moveEventTestable(day, index, newDay);

            int expected;

            if (targetBranch == 11 || targetBranch == 12) expected = 1;
            else if (targetBranch == 2) expected = 2;
            else if (targetBranch == 31 || targetBranch == 32) expected = 3;
            else if (targetBranch == 4) expected = 4;
            else expected = 0;

            if (result == expected) {

                System.out.println("\n--- Branch " + targetBranch + " ---");

                if (targetBranch == 11)
                    System.out.println("Target: dayEvent < 1 = true → zi eveniment prea mica");
                else if (targetBranch == 12)
                    System.out.println("Target: dayEvent > 3 = true → zi eveniment prea mare");
                else if (targetBranch == 2)
                    System.out.println("Target: eventIndex < 0 = true → index invalid");
                else if (targetBranch == 31)
                    System.out.println("Target: newDay < 1 = true → zi noua prea mica");
                else if (targetBranch == 32)
                    System.out.println("Target: newDay > 3 = true → zi noua prea mare");
                else if (targetBranch == 4)
                    System.out.println("Target: newDay == dayEvent = true → aceeasi zi");
                else if (targetBranch == 0)
                    System.out.println("Target: toate conditiile false → caz valid");

                System.out.println("Best solution: [" +
                        day + ", " + index + ", " + newDay + "]");

                System.out.println("Number of objective function evaluations: "
                        + monitor.getNumEvaluations());

                // scriere in fisier
                try (FileWriter fw = new FileWriter(
                        "src/test/java/avmf/moveEvent/generated_tests.txt", true)) {
                    fw.write(targetBranch + "," + day + "," + index + "," + newDay + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println(" FAILED for branch " + targetBranch);
        }
    }

    static ObjectiveFunction createObjective(int targetBranch) {

        return new ObjectiveFunction() {
            @Override
            public NumericObjectiveValue computeObjectiveValue(Vector v) {

                int day = ((IntegerVariable) v.getVariables().get(0)).asInt();
                int index = ((IntegerVariable) v.getVariables().get(1)).asInt();
                int newDay = ((IntegerVariable) v.getVariables().get(2)).asInt();

                double fitness = 0;

                // ---- BRANCH 11: day < 1 ----
                if (targetBranch == 11) {
                    fitness += Math.max(0, day);
                }

                // ---- BRANCH 12: day > 3 ----
                if (targetBranch == 12) {
                    fitness += Math.max(0, 3 - day);
                }

                // ---- BRANCH 2: index < 0 ----
                if (targetBranch == 2) {

                    // evit branch 1 (day valid)
                    fitness += Math.max(0, 1 - day);
                    fitness += Math.max(0, day - 3);

                    // target
                    fitness += Math.max(0, index);
                }

                // ---- BRANCH 31: newDay < 1 ----
                if (targetBranch == 31) {

                    fitness += Math.max(0, 1 - day);
                    fitness += Math.max(0, day - 3);

                    fitness += Math.max(0, -index);

                    // target: newDay < 1
                    fitness += Math.max(0, newDay);

                    // evit cealalta ramura
                    fitness += Math.max(0, newDay - 3);
                }

                // ---- BRANCH 32: newDay > 3 ----
                if (targetBranch == 32) {

                    fitness += Math.max(0, 1 - day);
                    fitness += Math.max(0, day - 3);

                    fitness += Math.max(0, -index);

                    // target: newDay > 3
                    fitness += Math.max(0, 4 - newDay);

                    // evit cealalta ramura
                    fitness += Math.max(0, 1 - newDay);
                }

                // ---- BRANCH 4: newDay == day ----
                if (targetBranch == 4) {

                    // evit 1
                    fitness += Math.max(0, 1 - day);
                    fitness += Math.max(0, day - 3);

                    // evit 2
                    fitness += Math.max(0, -index);

                    // evit 3
                    fitness += Math.max(0, 1 - newDay);
                    fitness += Math.max(0, newDay - 3);

                    // target
                    fitness += Math.abs(newDay - day);
                }

                // ---- BRANCH 0: toate false ----
                if (targetBranch == 0) {

                    // day valid
                    fitness += Math.max(0, 1 - day);
                    fitness += Math.max(0, day - 3);

                    // index >= 0
                    fitness += Math.max(0, -index);

                    // newDay valid
                    fitness += Math.max(0, 1 - newDay);
                    fitness += Math.max(0, newDay - 3);

                    // newDay != day
                    fitness += (newDay == day) ? 1 : 0;
                }

                return NumericObjectiveValue.higherIsBetterObjectiveValue(-fitness);
            }
        };
    }
}