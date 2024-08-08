package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

public class VeryLongWordFilter implements Filter {
    private final int roomId;
    private final Post post;
    private final int reasonId = 6;
    private String listedWord;

    public VeryLongWordFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
    }

    @Override
    public boolean isHit() {
        this.listedWord = "";

        if (post.getBody() != null) {
            this.listedWord = CheckUtils.checkForLongWords(post.getBody());
            String oldListedWord = CheckUtils.checkForLongWords(post.getLastBody());

            return this.listedWord != null && oldListedWord == null;
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
        return "**Contains very long word:** " + this.listedWord.substring(0, 40) + "...";
    }

    @Override
    public List<String> getReasonName() {
        return new ArrayList<>(Collections.singletonList("Contains very long word"));
    }

    @Override
    public Severity getSeverity() {
        return Severity.MEDIUM;
    }

    @Override
    public void storeHit() {
        long postId = post.getPostId();
        int revisionNumber = post.getRevisionNumber();
        double score = getScore();

        DatabaseUtils.storeReason(postId, revisionNumber, roomId, reasonId, score);
    }

}
