package bugs.stackoverflow.belisarius.models;

import java.util.Map;

import bugs.stackoverflow.belisarius.filters.Filter.Severity;

import java.util.HashMap;

public class VandalisedPost {

    public enum Feedback{

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

    private Post post;
    private Map<String, Double> reasons;
    private Severity severity;
    private Map<String, Double> reasonNames;


    public VandalisedPost(Post post, Map<String, Double> reasons, Severity severity, Map<String, Double> reasonNames) {
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
            reasons = String.join(", ", reasonNames) + "; **Score:** " + String.valueOf(score);
        }

        return reasons;
    }

    public Map<String, Double> getReasons() {
        return this.reasons;
    }

    public Map<String, Double> getReasonNames() {
        return this.reasonNames;
    }

    public double getScore() {
        return this.reasons.values().stream().mapToDouble(Number::doubleValue).sum();
    }

    public String getSeverity() {
        if (this.severity == Severity.LOW) {
            return "low";
        } else if (this.severity == Severity.MEDIUM) {
            return "medium";
        } else if (this.severity == Severity.HIGH) {
            return "high";
        }
        return null;
    }

}