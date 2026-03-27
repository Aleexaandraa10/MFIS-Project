package mfis;

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

public class GenerateSanctionInputData {

    static final int MAX_EVALUATIONS = 100000;

    public static void main(String[] args) throws IOException {

        PrintWriter csvWriter = new PrintWriter(new FileWriter("avmf_inputs.csv"));
        csvWriter.println("branchId,targetTrue,participantExists,hasUnder25Ticket,correctAge,oldPrice,discountPercentage");

        // rulez AVMf pentru fiecare ramură și pentru true/false
        for (int branch = 1; branch <= 5; branch++) {
            for (boolean targetTrue : new boolean[]{true, false}) {
                runForBranch(branch, targetTrue, csvWriter);
            }
        }

        csvWriter.close();
        System.out.println("Inputuri salvate in avmf_inputs.csv");
    }

    private static void runForBranch(int branchID, boolean targetTrue,
                                     PrintWriter csvWriter) {

        // ==============================================================
        // Pasul 1 — funcția obiectiv
        // ==============================================================
        final SanctionBranchTargetObjectiveFunction branchFunc =
                new SanctionBranchTargetObjectiveFunction(branchID, targetTrue);

        ObjectiveFunction objFun = new ObjectiveFunction() {
            @Override
            protected ObjectiveValue computeObjectiveValue(Vector vector) {
                return branchFunc.evaluate(vector);
            }
        };

        // ==============================================================
        // Pasul 2 — vectorul de variabile
        // ==============================================================
        // [participantExists, hasUnder25Ticket, correctAge, oldPrice, discount]
        Vector vector = new Vector();
        vector.addVariable(new IntegerVariable(1, 0, 1));
        vector.addVariable(new IntegerVariable(1, 0, 1));
        vector.addVariable(new IntegerVariable(50, 1, 120));
        vector.addVariable(new IntegerVariable(500, 0, 9999));
        vector.addVariable(new IntegerVariable(50, 0, 200));

        // ==============================================================
        // Pasul 3 — configurare AVMf
        // ==============================================================
        LocalSearch localSearch = new IteratedPatternSearch();

        TerminationPolicy terminationPolicy =
                TerminationPolicy.createMaxEvaluationsTerminationPolicy(MAX_EVALUATIONS);

        RandomGenerator randomGenerator = new MersenneTwister();
        Initializer initializer = new RandomInitializer(randomGenerator);

        AlternatingVariableMethod avm = new AlternatingVariableMethod(
                localSearch, terminationPolicy, initializer);

        // ==============================================================
        // Pasul 4 — rulare
        // ==============================================================
        Monitor monitor = avm.search(vector, objFun);

        // extrag valorile găsite de AVMf
        int pE  = ((IntegerVariable) monitor.getBestVector().getVariable(0)).getValue();
        int hT  = ((IntegerVariable) monitor.getBestVector().getVariable(1)).getValue();
        int age = ((IntegerVariable) monitor.getBestVector().getVariable(2)).getValue();
        int op  = ((IntegerVariable) monitor.getBestVector().getVariable(3)).getValue();
        int dc  = ((IntegerVariable) monitor.getBestVector().getVariable(4)).getValue();

        boolean participantExists = (pE == 1);
        boolean hasUnder25Ticket = (hT == 1);

        // descrierea ramurilor — doar pentru consolă
        String[][] branchDescriptions = {
                {"", ""},
                {"!participantExists = true  → participant negăsit",
                        "!participantExists = false → participant găsit"},
                {"!hasUnder25Ticket = true  → bilet nu e Under25",
                        "!hasUnder25Ticket = false → bilet e Under25"},
                {"correctAge <= 25 = true  → vârstă invalidă",
                        "correctAge <= 25 = false → vârstă validă"},
                {"oldPrice <= 0 = true  → preț invalid",
                        "oldPrice <= 0 = false → preț valid"},
                {"discount <= 0 || discount >= 100 = true  → discount invalid",
                        "discount <= 0 || discount >= 100 = false → discount valid"}
        };

        String description = branchDescriptions[branchID][targetTrue ? 0 : 1];
        String branchLabel = "\nBranch " + branchID + (targetTrue ? "T" : "F");

        String result =
                "--- " + branchLabel + " ---\n" +
                        "Target: " + description + "\n" +
                        "Best solution: " + monitor.getBestVector() + "\n" +
                        "Best objective value: " + monitor.getBestObjVal() + "\n" +
                        "Number of objective function evaluations: " +
                        monitor.getNumEvaluations() +
                        " (unique: " + monitor.getNumUniqueEvaluations() + ")\n" +
                        "Running time: " + monitor.getRunningTime() + "ms\n";

        // afișare detaliată în consolă
        System.out.print(result);

        int expectedResult = ParticipantSanctionLogic.classifySanctionCase(
                participantExists, hasUnder25Ticket, age, op, dc);

        csvWriter.println(
                branchID + "," +
                        targetTrue + "," +
                        expectedResult + "," +
                        participantExists + "," +
                        hasUnder25Ticket + "," +
                        age + "," +
                        op + "," +
                        dc
        );
    }
}