package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeAnalysis {
    public BestWorst best;
    public BestWorst worst;

}
