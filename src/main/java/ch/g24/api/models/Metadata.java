package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

class Metadata {
    @JsonProperty("generated_at")
    private String generatedAt;

    @JsonProperty("api_version")
    private String apiVersion;

    // getters & setters
    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }

    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
}
