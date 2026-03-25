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

            if (result == targetBranch) {

                System.out.println("✅ FOUND input for branch " + targetBranch);
                System.out.println("day=" + day +
                        ", index=" + index +
                        ", newDay=" + newDay);

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
            System.out.println("❌ FAILED for branch " + targetBranch);
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

                // ---- BRANCH 1 ----
                if (targetBranch == 1) {
                    if (day >= 1 && day <= 3)
                        fitness += 10;
                }

                // ---- BRANCH 2 ----
                if (targetBranch == 2) {

                    // evit 1
                    if (day < 1 || day > 3) fitness += 1000;


                    // target
                    if (index >= 0)
                        fitness += index + 1;
                }

                // ---- BRANCH 3 ----
                if (targetBranch == 3) {

                    // evit 1
                    if (day < 1 || day > 3) fitness += 1000;

                    // evit 2
                    if (index < 0) fitness += 1000;

                    // target
                    if (newDay >= 1 && newDay <= 3)
                        fitness += 10;
                }

                // ---- BRANCH 4 ----
                if (targetBranch == 4) {

                    // evit 1
                    if (day < 1 || day > 3) fitness += 1000;

                    // evit 2
                    if (index < 0) fitness += 1000;

                    // evit 3
                    if (newDay < 1 || newDay > 3) fitness += 1000;

                    // target
                    fitness += Math.abs(newDay - day);
                }

                // ---- BRANCH 0 ----
                if (targetBranch == 0) {

                    // evit toate
                    if (day < 1 || day > 3) fitness += 1000;
                    if (index < 0) fitness += 1000;
                    if (newDay < 1 || newDay > 3) fitness += 1000;
                    if (newDay == day) fitness += 1000;
                }

                return NumericObjectiveValue.higherIsBetterObjectiveValue(-fitness);
            }
        };
    }
}