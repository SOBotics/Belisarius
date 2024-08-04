package bugs.stackoverflow.belisarius.models;

import java.util.ArrayList;
import java.util.Map;

import bugs.stackoverflow.belisarius.filters.Filter.Severity;

public class VandalisedPost {
    public enum Feedback {
        T("t"), TP("tp"), F("f"), FP("fp");

        private final String text;

        Feedback(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final Post post;
    private final Map<String, Double> reasons;
    private final Severity severity;
    private final Map<String, Double> reasonNames;

    public VandalisedPost(
        Post post,
        Map<String, Double> reasons,
        Severity severity,
        Map<String, Double> reasonNames
    ) {
        this.post = post;
        this.reasons = reasons;
        this.severity = severity;
        this.reasonNames = reasonNames;
    }

    public Post getPost() {
        return this.post;
    }

    public String getReasonMessage() {
        String reasons = null;
        ArrayList<String> reasonNames = new ArrayList<>();
        double score = 0;

        for (Map.Entry<String, Double> entry : this.reasons.entrySet()) {
            reasonNames.add(entry.getKey());
            score += entry.getValue();
        }

        if (!reasonNames.isEmpty() && score > 0) {
            reasons = String.join(", ", reasonNames) + "; **Score:** " + score;
        }

        return reasons;
    }

    public Map<String, Double> getReasonNames() {
        return this.reasonNames;
    }

    public double getScore() {
        return this.reasons
            .values()
            .stream()
            .mapToDouble(Number::doubleValue)
            .sum();
    }

    public String getSeverity() {
        if (this.severity == null) {
            return null;
        }

        switch (this.severity) {
            case LOW:
                return "low";
            case MEDIUM:
                return "medium";
            case HIGH:
                return "high";
            default:
                return null;
        }
    }
}
