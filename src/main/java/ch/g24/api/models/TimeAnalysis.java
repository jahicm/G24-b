package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class TimeAnalysis {
    public BestWorst best;
    public BestWorst worst;

    public BestWorst getBest() {
        return best;
    }

    public void setBest(BestWorst best) {
        this.best = best;
    }

    public BestWorst getWorst() {
        return worst;
    }

    public void setWorst(BestWorst worst) {
        this.worst = worst;
    }
}
