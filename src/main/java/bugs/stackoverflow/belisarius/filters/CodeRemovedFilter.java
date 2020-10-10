package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

public class CodeRemovedFilter implements Filter {

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
    public String getFormattedReasonMessage() {
        return "**Code removed**";
    }

    @Override
    public String getReasonName() {
        return "Code removed";
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
        }
    }
}
