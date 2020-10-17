package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OffensiveWordFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OffensiveWordFilter.class);

    private int roomId;
    private Post post;
    private int reasonId;

    private Map<Integer, String> offensiveWords = new HashMap<>();

    public OffensiveWordFilter(int chatroomId, Post post, int reasonId) {
        this.roomId = chatroomId;
        this.post = post;
        this.reasonId = reasonId;
    }

    @Override
    public boolean isHit() {

        if (this.post.getComment() != null) {
            offensiveWords = CheckUtils.checkForOffensiveWords(this.post.getComment());
        }

        return getTotalScore() > 0;
    }

    @Override
    public double getScore() {
        return 1.0;
    }

    @Override
    public double getTotalScore() {
        return this.offensiveWords.size();
    }

    @Override
    public String getFormattedReasonMessage() {
        return "**Edit summary contains offensive " + (this.offensiveWords.size() > 1 ? "words" : "word") + ":** " + getOffensiveWords();
    }

    @Override
    public List<String> getReasonName() {
        String name = "Contains offensive word: ";
        List<String> words = new ArrayList<>();

        // add name + word to the words list
        offensiveWords.values().forEach(word -> words.add(name + word));
        return words;
    }

    private String getOffensiveWords() {
        StringBuilder words = new StringBuilder();

        for (String word : offensiveWords.values()) {
            words.append(word).append(", ");
        }

        return words.toString().substring(0, words.length() - 2);
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
        if (!DatabaseUtils.checkReasonCaughtExists(postId, revisionNumber, this.roomId, this.reasonId)) {
            DatabaseUtils.storeReasonCaught(postId, revisionNumber, this.roomId, this.reasonId, this.getScore());
        }

        this.getCaughtOffensiveWordIds().forEach(id -> {
            if (!DatabaseUtils.checkOffensiveWordCaughtExists(postId, revisionNumber, this.roomId, id)) {
                DatabaseUtils.storeCaughtOffensiveWord(postId, revisionNumber, this.roomId, id);
                LOGGER.info("Successfully stored offensive word id for post " + postId + " to database.");
            }
        });
    }
}
