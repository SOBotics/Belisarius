package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextRemovedFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextRemovedFilter.class);

    private final int roomId;
    private final Post post;
    private final int reasonId;
    private final double percentage = 0.8;
    private double score;

    public TextRemovedFilter(int chatroomId, Post post, int reasonId) {
        this.roomId = chatroomId;
        this.post = post;
        this.reasonId = reasonId;
    }

    @Override
    public boolean isHit() {
        String original = "";
        String target = "";

        if (this.post.getBody() != null && this.post.getLastBody() != null) {
            original = this.post.getLastBody();
            target = this.post.getBody();
        }

        this.score = CheckUtils.getJaroWinklerScore(original, target, percentage);

        return this.score < 0.6;
    }

    @Override
    public double getScore() {
        return 1.0;
    }

    @Override
    public double getTotalScore() {
        return getScore();
    }

    @Override
    public String getFormattedReasonMessage() {
        return "**" + percentage * 100 + "% or more text removed with a JW score of " + Math.round(this.score * 100.0) / 100.0 + "**";
    }

    @Override
    public List<String> getReasonName() {
        return new ArrayList<>(Collections.singletonList("Lots of text removed with a high JW score"));
    }

    @Override
    public Severity getSeverity() {
        return Severity.LOW;
    }

    @Override
    public void storeHit() {
        long postId = this.post.getPostId();
        int revisionNumber = this.post.getRevisionNumber();
        if (!DatabaseUtils.checkReasonCaughtExists(postId, revisionNumber, this.roomId, this.reasonId)) {
            DatabaseUtils.storeReasonCaught(postId, revisionNumber, this.roomId, this.reasonId, this.getScore());
            LOGGER.info("Successfully stored reason TextRemovedFilter for post " + postId + " to database.");
        }
    }
}
