package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepeatedWordFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatedWordFilter.class);

    private final int roomId;
    private final Post post;
    private final int reasonId = 5;
    private Set<String> repeatedWords = new HashSet<>();

    public RepeatedWordFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
    }

    @Override
    public boolean isHit() {

        if (this.post.getBody() != null) {
            repeatedWords = CheckUtils.checkRepeatedWords(this.post.getBody());
        }

        double score = getScore();
        return score > 0 && score <= 5;
    }

    @Override
    public double getScore() {
        return repeatedWords.size();
    }

    @Override
    public double getTotalScore() {
        return getScore();
    }

    @Override
    public String getFormattedReasonMessage() {
        String message = "";

        if (this.repeatedWords.size() > 0) {
            message += "**Post contains repeated " + (this.repeatedWords.size() > 1 ? "words" : "word") + ":** " + getRepeatedWords() + " ";
        }

        return message.trim();
    }

    @Override
    public List<String> getReasonName() {
        return new ArrayList<>(Collections.singletonList("Contains repeated words"));
    }

    private String getRepeatedWords() {
        StringBuilder words = new StringBuilder();

        for (String word : repeatedWords) {
            words.append(word);
        }

        return words.toString().substring(0, 40) + "...";
    }

    @Override
    public Severity getSeverity() {
        return Severity.HIGH;
    }

    @Override
    public void storeHit() {
        long postId = this.post.getPostId();
        int revisionNumber = this.post.getRevisionNumber();
        if (!DatabaseUtils.checkReasonCaughtExists(postId, revisionNumber, this.roomId, this.reasonId)) {
            DatabaseUtils.storeReasonCaught(postId, revisionNumber, this.roomId, this.reasonId, this.getScore());
            LOGGER.info("Successfully stored reason RepeatedWordFilter for post " + postId + " to database.");
        }
    }
}
