package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.apache.commons.lang3.StringUtils;

public class VeryLongWordFilter implements Filter {

    private int roomId;
    private Post post;
    private int reasonId;
    private String listedWord;

    public VeryLongWordFilter(int chatroomId, Post post, int reasonId) {
        this.roomId = chatroomId;
        this.post = post;
        this.reasonId = reasonId;
    }

    @Override
    public boolean isHit() {
        this.listedWord = "";
        if (post.getBody() != null) {
            this.listedWord = CheckUtils.checkForLongWords(StringUtils.difference(post.getLastBody(), post.getBody()));
            return this.listedWord != null;
        }
        return false;
    }

    @Override
    public double getScore() {
        return 1.0;
    }

    @Override
    public String getFormattedReasonMessage() {
        return "**Contains very long word:** " + this.listedWord.substring(0, 40) + "...";
    }

    @Override
    public String getReasonName() {
        return "Contains very long word";
    }

    @Override
    public Severity getSeverity() {
        return Severity.MEDIUM;
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
