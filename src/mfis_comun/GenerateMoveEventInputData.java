package mfis_comun;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.avmframework.AlternatingVariableMethod;
import org.avmframework.Monitor;
import org.avmframework.TerminationPolicy;
import org.avmframework.Vector;
import org.avmframework.initialization.Initializer;
import org.avmframework.initialization.RandomInitializer;
import org.avmframework.localsearch.IteratedPatternSearch;
import org.avmframework.localsearch.LocalSearch;
import org.avmframework.objective.ObjectiveFunction;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.variable.IntegerVariable;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateMoveEventInputData {

    static final int MAX_EVALUATIONS = 100000;

    public static void main(String[] args) throws IOException {

        PrintWriter csvWriter = new PrintWriter(new FileWriter("avmf_move_event_inputs.csv"));
        csvWriter.println("branchId,targetTrue,expectedResult,dayEvent,eventIndex,newDay");

        // rulez AVMf pentru fiecare ramura si pentru true/false
        int[] branches = {11, 12, 2, 31, 32, 4, 5};
        for (int branch : branches) {
            for (boolean targetTrue : new boolean[]{true, false}) {
                if (branch == 5 && !targetTrue) continue;
                runForBranch(branch, targetTrue, csvWriter);
            }
        }

        csvWriter.close();
        System.out.println("Inputuri salvate in avmf_move_event_inputs.csv");
    }

    private static void runForBranch(int branchID, boolean targetTrue,
                                     PrintWriter csvWriter) {

        // Pasul 1 — functia obiectiv
        final MoveEventBranchTargetObjectiveFunction branchFunc =
                new MoveEventBranchTargetObjectiveFunction(branchID, targetTrue);

        ObjectiveFunction objFun = new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                return branchFunc.evaluate(vector);
            }
        };

        // Pasul 2 — vectorul de variabile
        // [dayEvent, eventIndex, newDay]
        Vector vector = new Vector();
        vector.addVariable(new IntegerVariable(2, -5, 10));   // dayEvent: start=2, min=-5, max=10
        vector.addVariable(new IntegerVariable(1, -10, 20));  // eventIndex: start=1, min=-10, max=20
        vector.addVariable(new IntegerVariable(2, -5, 10));   // newDay: start=2, min=-5, max=10

        // Pasul 3 — configurare AVMf
        LocalSearch localSearch = new IteratedPatternSearch();

        TerminationPolicy terminationPolicy =
                TerminationPolicy.createMaxEvaluationsTerminationPolicy(MAX_EVALUATIONS);

        RandomGenerator randomGenerator = new MersenneTwister();
        Initializer initializer = new RandomInitializer(randomGenerator);

        AlternatingVariableMethod avm = new AlternatingVariableMethod(
                localSearch, terminationPolicy, initializer);

        // Pasul 4 — rulare
        Monitor monitor = avm.search(vector, objFun);

        int dayEvent   = ((IntegerVariable) monitor.getBestVector().getVariable(0)).getValue();
        int eventIndex = ((IntegerVariable) monitor.getBestVector().getVariable(1)).getValue();
        int newDay     = ((IntegerVariable) monitor.getBestVector().getVariable(2)).getValue();

        // descrierea ramurilor
        String description;
        switch (branchID) {
            case 11: description = targetTrue ?
                    "dayEvent < 1 = true → zi eveniment prea mică" :
                    "dayEvent < 1 = false → dayEvent >= 1";
                break;
            case 12: description = targetTrue ?
                    "dayEvent > 3 = true → zi eveniment prea mare" :
                    "dayEvent > 3 = false → dayEvent <= 3";
                break;
            case 2: description = targetTrue ?
                    "eventIndex < 0 = true → index invalid" :
                    "eventIndex < 0 = false → index valid";
                break;
            case 31: description = targetTrue ?
                    "newDay < 1 = true → zi nouă prea mică" :
                    "newDay < 1 = false → newDay >= 1";
                break;
            case 32: description = targetTrue ?
                    "newDay > 3 = true → zi nouă prea mare" :
                    "newDay > 3 = false → newDay <= 3";
                break;
            case 4: description = targetTrue ?
                    "newDay == dayEvent = true → aceeași zi" :
                    "newDay == dayEvent = false → zi diferită";
                break;
            case 5: description = "toate condițiile false → mutare reușită";
                break;
            default: description = "";
        }


        String branchLabel = "Branch " + branchID + (branchID == 5 ? "" : (targetTrue ? "T" : "F"));

        String consolePrint =
                "--- " + branchLabel + " ---\n" +
                        "Target: " + description + "\n" +
                        "Best solution: " + monitor.getBestVector() + "\n" +
                        "Best objective value: " + monitor.getBestObjVal() + "\n" +
                        "Number of objective function evaluations: " +
                        monitor.getNumEvaluations() +
                        " (unique: " + monitor.getNumUniqueEvaluations() + ")\n" +
                        "Running time: " + monitor.getRunningTime() + "ms\n";

        System.out.print(consolePrint);

        int expectedResult = MoveEventTestable.moveEventTestable(dayEvent, eventIndex, newDay);

        csvWriter.println(
                branchID + "," +
                        targetTrue + "," +
                        expectedResult + "," +
                        dayEvent + "," +
                        eventIndex + "," +
                        newDay
        );
    }
}