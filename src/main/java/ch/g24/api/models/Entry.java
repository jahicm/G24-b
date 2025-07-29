package ch.g24.api.models;

import java.time.LocalDateTime;

public record Entry(String userId, LocalDateTime dataEntryTime, LocalDateTime measurementTime,
                    String value, double sugarValue, String referenceValue, String unit, String status) {
}
