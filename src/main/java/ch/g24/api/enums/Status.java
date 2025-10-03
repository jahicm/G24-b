package ch.g24.api.enums;

public enum Status {

    LOW("low"),
    NORMAL("normal"),
    ELEVATED("elevated"),
    HIGH("high"),
    UNKNOWN("unknown");

    private final String status;

    Status(String status)
    {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
