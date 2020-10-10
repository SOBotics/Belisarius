package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

public class FewUniqueCharactersFilter implements Filter {

    private int roomId;
    private Post post;
    private int reasonId;
    private String listedWord;

    public FewUniqueCharactersFilter(int chatroomId, Post post, int reasonId) {
        this.roomId = chatroomId;
        this.post = post;
        this.reasonId = reasonId;
    }

    @Override
    public boolean isHit() {
        this.listedWord = "";
        if (post.getBody() != null) {
            this.listedWord = CheckUtils.checkForFewUniqueCharacters(post.getBody());
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
        return "**Few unique characters detected:** " + this.listedWord;
    }

    @Override
    public String getReasonName() {
        return "Few unique characters in body";
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
        }
    }

}
