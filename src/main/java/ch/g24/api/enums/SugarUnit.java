package ch.g24.api.enums;

public enum SugarUnit {
    MG_DL("1", "mg/dL"),
    MMOL_L("2", "mmol/L");

    private final String id;
    private final String label;

    SugarUnit(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static String getLabelById(String id) {
        for (SugarUnit unit : values()) {
            if (unit.getId().equals(id)) {
                return unit.getLabel();
            }
        }
        return null; // or throw exception
    }
    public static String getIdByLabel(String label) {
        if(label.equals(MG_DL.getLabel()))
            return MG_DL.id;
        else
            return MMOL_L.id; // or throw exception
    }
}

