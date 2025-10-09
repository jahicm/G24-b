package ch.g24.api.utility;

import ch.g24.api.enums.SugarUnit;


public class Utility {

    public static final String UNIT_MGDL = "1";
    public static final String UNIT_MMOLL = "2";

    public static double convertSugar(double val, String unitId, String unit) {

        if (unit.equals((SugarUnit.getLabelById(unitId)))) {
            return val; // no conversion needed
        }
        if (!unit.equals(SugarUnit.getLabelById(unitId))) {
            if (unitId.equals(UNIT_MGDL)) { // convert to mg/dL
                val = val * 18.0;
                return Math.round(val);
            } else if (unitId.equals(UNIT_MMOLL)) { // convert to mmol/L
                val = val / 18.0;
                return Math.round(val);
            }
        }
        throw new IllegalArgumentException("Unknown unit conversion: " + unit + " -> " + unitId);
    }
}