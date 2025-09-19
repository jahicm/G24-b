package ch.g24.api.models;

import java.time.LocalDate;

public record User(String name, String lastName, LocalDate dob, String diabetesType, String postCode,
                   String city, String country, int unitId,String email,String password) {
}