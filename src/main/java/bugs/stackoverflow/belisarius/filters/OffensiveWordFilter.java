package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.sobotics.chatexchange.chat.Room;

public class OffensiveWordFilter implements Filter {

    private Room room;
    private Post post;
    private int reasonId;

    private Map<Integer, String> offensiveWords = new HashMap<>();

    public OffensiveWordFilter(Room room, Post post, int reasonId) {
        this.room = room;
        this.post = post;
        this.reasonId = reasonId;
    }

    @Override
    public boolean isHit() {

        if (this.post.getComment() != null) {
            offensiveWords = CheckUtils.checkForOffensiveWords(this.post.getComment());
        }

        return getScore() > 0;
    }

    @Override
    public double getScore() {
        return offensiveWords.size();
    }

    @Override
    public String getFormattedReasonMessage() {
        return "**Edit summary contains offensive " + (this.offensiveWords.size() > 1 ? "words" : "word") + ":** " + getOffensiveWords();
    }

    @Override
    public String getReasonName() {
        return "Edit summary contains offensive " + (this.offensiveWords.size() > 1 ? "words: " : "word: ") + getOffensiveWords();
    }

    private String getOffensiveWords() {
        StringBuilder words = new StringBuilder();

        for (String word : offensiveWords.values()) {
            words.append(word);
        }

        return words.toString();
    }

    @Override
    public Severity getSeverity() {
        return Severity.HIGH;
    }

    private List<Integer> getCaughtOffensiveWordIds() {
        return new ArrayList<>(offensiveWords.keySet());
    }

    @Override
    public void storeHit() {
        long postId = this.post.getPostId();
        int revisionNumber = this.post.getRevisionNumber();
        int roomId = this.room.getRoomId();
        if (!DatabaseUtils.checkReasonCaughtExists(postId, revisionNumber, roomId, this.reasonId)) {
            DatabaseUtils.storeReasonCaught(postId, revisionNumber, roomId, this.reasonId, this.getScore());
        }

        this.getCaughtOffensiveWordIds().forEach(id -> {
            DatabaseUtils.storeCaughtOffensiveWord(postId, revisionNumber, roomId, id);
        });
    }
}
