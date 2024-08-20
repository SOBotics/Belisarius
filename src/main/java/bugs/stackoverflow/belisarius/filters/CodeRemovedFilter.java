package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

public class CodeRemovedFilter implements Filter {
    private final int roomId;
    private final Post post;
    private final int reasonId = 2;

    public CodeRemovedFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
    }

    @Override
    public boolean isHit() {
        // Check if code has been removed when the post is a question
        // (https://chat.stackoverflow.com/transcript/message/50208463)
        if (post.getLastBody() != null
            && post.getBody() != null
            && "question".equals(post.getPostType())
        ) {
            return CheckUtils.containsCode(post.getLastBody())
                && !CheckUtils.containsCode(post.getBody());
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
        return new ArrayList<>(Collections.singletonList("Code removed"));
    }

    @Override
    public Severity getSeverity() {
        return Severity.LOW;
    }

    @Override
    public void storeHit() {
        long postId = post.getPostId();
        int revisionNumber = post.getRevisionNumber();
        double score = getScore();

        DatabaseUtils.storeReason(postId, revisionNumber, roomId, reasonId, score);
    }
}
