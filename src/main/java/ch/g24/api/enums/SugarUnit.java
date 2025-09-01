package ch.g24.api.enums;

public enum SugarUnit {
    MG_DL(1, "mg/dL"),
    MMOL_L(2, "mmol/L");

    private final int id;
    private final String label;

    SugarUnit(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static String getLabelById(int id) {
        for (SugarUnit unit : values()) {
            if (unit.getId() == id) {
                return unit.getLabel();
            }
        }
        return null; // or throw exception
    }
}

