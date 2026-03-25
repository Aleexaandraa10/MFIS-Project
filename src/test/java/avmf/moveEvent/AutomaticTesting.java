package avmf.moveEvent;

import java.io.*;

public class AutomaticTesting {

    public static void main(String[] args) throws Exception {

        new FileWriter(
                "src/test/java/avmf/moveEvent/generated_tests.txt", false
        ).close();

        // 1. Ruleaza generatorul AVMF
        System.out.println("Running AVMF generator...");
        for (int i = 0; i <= 4; i++) {
            AVMFMoveEventGenerator.generate(i);
        }

        // 2. Genereaza testele JUnit
        System.out.println("Generating JUnit tests...");

        BufferedReader br = new BufferedReader(new FileReader(
                "src/test/java/avmf/moveEvent/generated_tests.txt"));

        StringBuilder sb = new StringBuilder();

        sb.append("package avmf.moveEvent;\n\n");
        sb.append("import avmf.moveEvent.MoveEventTestable;\n");
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");

        sb.append("public class MoveEventGeneratedTests {\n\n");

        String line;
        int testId = 0;

        while ((line = br.readLine()) != null) {

            String[] parts = line.split(",");

            int branch = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int index = Integer.parseInt(parts[2]);
            int newDay = Integer.parseInt(parts[3]);

            sb.append("    @Test\n");
            sb.append("    void test_" + testId + "() {\n");
            sb.append("        assertEquals(" + branch +
                    ", MoveEventTestable.moveEventTestable(" +
                    day + ", " + index + ", " + newDay + "));\n");
            sb.append("    }\n\n");

            testId++;
        }

        sb.append("}\n");

        br.close();

        FileWriter fw = new FileWriter(
                "src/test/java/avmf/moveEvent/MoveEventGeneratedTests.java");

        fw.write(sb.toString());
        fw.close();

        System.out.println("DONE: AVMF + Tests generated!");
    }
}