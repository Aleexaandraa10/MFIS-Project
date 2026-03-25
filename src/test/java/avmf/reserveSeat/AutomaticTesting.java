package avmf.reserveSeat;


import java.io.*;

public class AutomaticTesting {

    public static void main(String[] args) throws Exception {


        new FileWriter(
                "src/test/java/avmf/reserveSeat/generated_tests.txt", false
        ).close();

        // 1. Ruleaza generatorul AVMF
        System.out.println("Running AVMF generator...");
        int[] branches = {1, 21, 22, 3, 4, 0};

        for (int b : branches) {
            AVMFReserveSeatGenerator.generate(b);
        }

        // 2. Genereaza testele JUnit
        System.out.println("Generating JUnit tests...");

        BufferedReader br = new BufferedReader(new FileReader(
                "src/test/java/avmf/reserveSeat/generated_tests.txt"));

        StringBuilder sb = new StringBuilder();

        sb.append("package avmf.reserveSeat;\n\n");
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");

        sb.append("public class ReserveSeatGeneratedTests {\n\n");

        String line;
        int testId = 0;

        while ((line = br.readLine()) != null) {

            String[] parts = line.split(",");

            int branch = Integer.parseInt(parts[0]);
            int talks = Integer.parseInt(parts[1]);
            int index = Integer.parseInt(parts[2]);
            int reserved = Integer.parseInt(parts[3]);
            int seats = Integer.parseInt(parts[4]);
            int exists = Integer.parseInt(parts[5]);

            sb.append("    @Test\n");
            sb.append("    void test_" + testId + "() {\n");
            sb.append("        assertEquals(" + branch +
                    ", ReserveSeatTestable.reserveSeatTestable(" +
                    talks + ", " + index + ", " + reserved + ", " +
                    seats + ", " + (exists == 1 ? "true" : "false") + "));\n");
            sb.append("    }\n\n");

            testId++;
        }

        sb.append("}\n");

        br.close();

        // 4. Scrie fisierul JUnit
        FileWriter fw = new FileWriter(
                "src/test/java/avmf/reserveSeat/ReserveSeatGeneratedTests.java");

        fw.write(sb.toString());
        fw.close();

        System.out.println("DONE: AVMF + Tests generated!");
    }
}
