package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ApiResponse {
    private String status;
    private Data data;
    private Metadata metadata;

    // getters & setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }
}

