package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeRemovedFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeRemovedFilter.class);

    private int roomId;
    private Post post;
    private int reasonId;

    public CodeRemovedFilter(int chatroomId, Post post, int reasonId) {
        this.roomId = chatroomId;
        this.post = post;
        this.reasonId = reasonId;
    }

    @Override
    public boolean isHit() {
        // Check if code has been removed when the post is a question (https://chat.stackoverflow.com/transcript/message/50208463)
        if (post.getLastBody() != null && post.getBody() != null && "question".equals(post.getPostType())) {
            return !CheckUtils.checkIfNoCodeBlock(post.getLastBody()) && CheckUtils.checkIfNoCodeBlock(post.getBody());
        }
        return false;
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
        return "**Code removed**";
    }

    @Override
    public List<String> getReasonName() {
        return new ArrayList<>(Arrays.asList("Code removed"));
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
            LOGGER.info("Successfully stored reason CodeRemovedFilter for post " + postId + " to database.");
        }
    }
}
