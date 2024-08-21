package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

public class TextRemovedFilter implements Filter {
    private final int roomId;
    private final Post post;
    private final int reasonId = 6;
    private final double percentage = 0.8;
    private double score;

    public TextRemovedFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
    }

    @Override
    public boolean isHit() {
        String original = "";
        String target = "";

        if (post.getBody() != null && post.getLastBody() != null) {
            original = post.getLastBody();
            target = post.getBody();
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
        return "**" + percentage * 100 + "% or more text removed "
            + "with a JW score of " + Math.round(this.score * 100.0) / 100.0 + "**";
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
        long postId = post.getPostId();
        int revisionNumber = post.getRevisionNumber();

        DatabaseUtils.storeReason(postId, revisionNumber, roomId, reasonId, getScore());
    }
}
