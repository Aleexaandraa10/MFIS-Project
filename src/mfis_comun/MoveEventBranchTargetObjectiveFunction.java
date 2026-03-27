package mfis_comun;

import org.avmframework.objective.NumericObjectiveValue;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.Vector;
import org.avmframework.variable.IntegerVariable;

public class MoveEventBranchTargetObjectiveFunction {

    private int branchID;
    private boolean targetTrue;

    public MoveEventBranchTargetObjectiveFunction(int branchID, boolean targetTrue) {
        this.branchID = branchID;
        this.targetTrue = targetTrue;
    }

    public ObjectiveValue evaluate(Vector vector) {
        int dayEvent   = ((IntegerVariable) vector.getVariable(0)).getValue();
        int eventIndex = ((IntegerVariable) vector.getVariable(1)).getValue();
        int newDay     = ((IntegerVariable) vector.getVariable(2)).getValue();

        double distance = 0.0;

        if (branchID == 11) {
            // vrem dayEvent < 1
            if (targetTrue) {
                distance += Math.max(0, dayEvent);        // dayEvent=0 → 0, dayEvent=2 → 2
            } else {
                distance += Math.max(0, 1 - dayEvent);    // dayEvent=1 → 0, dayEvent=0 → 1
            }

        } else if (branchID == 12) {
            // vrem dayEvent > 3
            if (targetTrue) {
                distance += Math.max(0, 4 - dayEvent);    // dayEvent=4 → 0, dayEvent=2 → 2
            } else {
                distance += Math.max(0, dayEvent - 3);    // dayEvent=3 → 0, dayEvent=4 → 1
            }

        } else if (branchID == 2) {
            // trebuie dayEvent valid: 1 <= dayEvent <= 3
            distance += Math.max(0, 1 - dayEvent);
            distance += Math.max(0, dayEvent - 3);
            // if (eventIndex < 0)
            if (targetTrue) {
                distance += Math.max(0, eventIndex + 1);
            } else {
                distance += Math.max(0, -eventIndex);
            }

        } else if (branchID == 31) {
            // trebuie dayEvent valid, eventIndex >= 0
            distance += Math.max(0, 1 - dayEvent);
            distance += Math.max(0, dayEvent - 3);
            distance += Math.max(0, -eventIndex);
            // vrem newDay < 1
            if (targetTrue) {
                distance += Math.max(0, newDay);
            } else {
                distance += Math.max(0, 1 - newDay);
            }

        } else if (branchID == 32) {
            // trebuie dayEvent valid, eventIndex >= 0
            distance += Math.max(0, 1 - dayEvent);
            distance += Math.max(0, dayEvent - 3);
            distance += Math.max(0, -eventIndex);
            // vrem newDay > 3
            if (targetTrue) {
                distance += Math.max(0, 4 - newDay);
            } else {
                distance += Math.max(0, newDay - 3);
            }

        } else if (branchID == 4) {
            // trebuie toate condițiile anterioare false
            distance += Math.max(0, 1 - dayEvent);
            distance += Math.max(0, dayEvent - 3);
            distance += Math.max(0, -eventIndex);
            distance += Math.max(0, 1 - newDay);
            distance += Math.max(0, newDay - 3);
            // if (newDay == dayEvent)
            if (targetTrue) {
                distance += Math.abs(newDay - dayEvent);
            } else {
                distance += (newDay == dayEvent ? 1 : 0);
            }

        } else if (branchID == 5) {
            // succes — toate false
            distance += Math.max(0, 1 - dayEvent);
            distance += Math.max(0, dayEvent - 3);
            distance += Math.max(0, -eventIndex);
            distance += Math.max(0, 1 - newDay);
            distance += Math.max(0, newDay - 3);
            distance += (newDay == dayEvent ? 1 : 0);
        }

        return NumericObjectiveValue.lowerIsBetterObjectiveValue(distance, 0.0);
    }
}