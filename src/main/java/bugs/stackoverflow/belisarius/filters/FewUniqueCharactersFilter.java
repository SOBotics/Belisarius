package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.JsonUtils;

public class FewUniqueCharactersFilter implements Filter {
    private final int roomId;
    private final Post post;
    private final int reasonId = 3;
    private String listedWord;

    public FewUniqueCharactersFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
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
    public double getTotalScore() {
        return getScore();
    }

    @Override
    public String getFormattedReasonMessage() {
        // make sure to escape characters like [], see
        // https://chat.stackoverflow.com/messages/51803561/history
        return "**Few unique characters detected:** " + JsonUtils.sanitizeChatMessage(this.listedWord);
    }

    @Override
    public List<String> getReasonName() {
        return new ArrayList<>(Collections.singletonList("Few unique characters in body"));
    }

    @Override
    public Severity getSeverity() {
        return Severity.HIGH;
    }

    @Override
    public void storeHit() {
        long postId = post.getPostId();
        int revisionNumber = post.getRevisionNumber();
        double score = getScore();

        DatabaseUtils.storeReason(postId, revisionNumber, roomId, reasonId, score);
    }
}
