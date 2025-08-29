package ch.g24.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeepSeekResult {

    @JsonProperty("ai_analysis")
    private AiAnalysis ai_analysis;

    @JsonProperty("smart_insight")
    private SmartInsight smart_insight;

    public AiAnalysis getAi_analysis() {
        return ai_analysis;
    }

    public void setAi_analysis(AiAnalysis ai_analysis) {
        this.ai_analysis = ai_analysis;
    }

    public SmartInsight getSmart_insight() {
        return smart_insight;
    }

    public void setSmart_insight(SmartInsight smart_insight) {
        this.smart_insight = smart_insight;
    }
}
