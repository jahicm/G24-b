package ch.g24.api.enums;

public enum Medication {

    MEDICATION_NO(1, "No medications"),
    MEDICATION_INSULIN(2, "Insulin"),
    MEDICATION_TABLETS(3, "Tablets");

    private final long medicationId;
    private final String medicationName;

    Medication(long medicationId, String medicationName) {
        this.medicationId = medicationId;
        this.medicationName = medicationName;
    }

    public long getMedicationId() {
        return medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }
    public static long getMedicationIdByName(String medicationName) {

        return switch (medicationName) {
            case "No medications" -> Medication.MEDICATION_NO.getMedicationId();
            case "Insulin" -> Medication.MEDICATION_INSULIN.getMedicationId();
            case "Tablets" -> Medication.MEDICATION_TABLETS.getMedicationId();
            default -> throw new IllegalArgumentException("Unknown medication: " + medicationName);
        };

    }

}
