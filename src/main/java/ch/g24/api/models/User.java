package ch.g24.api.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record User(Long userId, String name, String lastName, LocalDate dob, String diabetesType, String postCode,
                   String city, String country, String unit, String email, String medication, String password, LocalDateTime dataEntryTime) {
}