package mfis;

import org.avmframework.objective.NumericObjectiveValue;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.Vector;
import org.avmframework.variable.IntegerVariable;

public class SanctionBranchTargetObjectiveFunction {

    private int branchID;           //ce ramura vrem
    private boolean targetTrue;     // vrem true sau false pe acea ramura

    public SanctionBranchTargetObjectiveFunction(int branchID, boolean targetTrue) {
        this.branchID = branchID;
        this.targetTrue = targetTrue;
    }

    public ObjectiveValue evaluate(Vector vector) {
        int pE  = ((IntegerVariable) vector.getVariable(0)).getValue();
        int hT  = ((IntegerVariable) vector.getVariable(1)).getValue();
        int age = ((IntegerVariable) vector.getVariable(2)).getValue();
        int op  = ((IntegerVariable) vector.getVariable(3)).getValue();
        int dc  = ((IntegerVariable) vector.getVariable(4)).getValue();


        double distance = 0.0;

        // Ordinea: numeric întâi (AVMf poate ghida), boolean la final

        if (branchID == 1) {
            // if (!participantExists)
            if (targetTrue) {
                distance = pE;        // vrem pE=0
            } else {
                distance = 1 - pE;    // vrem pE=1
            }

        } else if (branchID == 2) {
            // trebuie participantExists=true ca să ajungem la branch 2
            distance += (1 - pE);     // penalizare dacă pE=0
            // if (!hasUnder25Ticket)
            if (targetTrue) {
                distance += hT;       // vrem hT=0
            } else {
                distance += (1 - hT); // vrem hT=1
            }

        } else if (branchID == 3) {
            // numeric — if (correctAge <= 25)
            if (targetTrue) {
                distance += Math.max(0, age - 25);  // vrem age<=25
            } else {
                distance += Math.max(0, 26 - age);  // vrem age>25
            }
            // boolean — trebuie să fi trecut de branch 1 și 2
            distance += (1 - pE);       // participantExists=true
            distance += hT == 0 ? 1 : 0; // hasUnder25Ticket=true → hT=1

        } else if (branchID == 4) {
            // numeric — if (oldPrice <= 0)
            if (targetTrue) {
                distance += Math.max(0, op);      // vrem op<=0
            } else {
                distance += Math.max(0, 1 - op);  // vrem op>0
            }
            // numeric — trebuie correctAge > 25
            distance += Math.max(0, 26 - age);
            // boolean — trebuie participantExists=true și hasUnder25Ticket=true
            distance += (1 - pE);
            distance += hT == 0 ? 1 : 0;

        } else if (branchID == 5) {
            // numeric — if (discount <= 0 || discount >= 100)
            if (targetTrue) {
                double distTo0   = Math.max(0, dc);
                double distTo100 = Math.max(0, 100 - dc);
                distance += Math.min(distTo0, distTo100);
            } else {
                distance += Math.max(0, -dc) + Math.max(0, dc - 99);
            }
            // numeric — trebuie correctAge > 25 și oldPrice > 0
            distance += Math.max(0, 26 - age);
            distance += Math.max(0, 1 - op);
            // boolean — trebuie participantExists=true și hasUnder25Ticket=true
            distance += (1 - pE);
            distance += hT == 0 ? 1 : 0;
        }

        return NumericObjectiveValue.lowerIsBetterObjectiveValue(distance, 0.0);
    }
}