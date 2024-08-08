package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.FilterUtils;

public class OffensiveWordFilter implements Filter {
    private final int roomId;
    private final Post post;
    private final int reasonId = 4;

    private Map<Integer, String> offensiveWords = new HashMap<>();

    public OffensiveWordFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
    }

    @Override
    public boolean isHit() {
        if (post.getComment() != null) {
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
        return offensiveWords.size();
    }

    @Override
    public String getFormattedReasonMessage() {
        return "**Edit summary contains offensive "
            + FilterUtils.pluralise("word", offensiveWords.size()) + ":** "
            + getOffensiveWords();
    }

    @Override
    public List<String> getReasonName() {
        String name = "Contains offensive word: ";
        List<String> words = new ArrayList<>();

        // add name + word to the words list
        offensiveWords
            .values()
            .forEach(word -> words.add(name + word));

        return words;
    }

    private String getOffensiveWords() {
        StringBuilder words = new StringBuilder();

        for (String word : offensiveWords.values()) {
            words.append(word).append(", ");
        }

        return words.substring(0, words.length() - 2);
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
        long postId = post.getPostId();
        int revisionNumber = post.getRevisionNumber();
        double score = getScore();

        DatabaseUtils.storeReason(postId, revisionNumber, roomId, reasonId, score);

        this.getCaughtOffensiveWordIds().forEach(id -> {
            DatabaseUtils.storeBlacklistedWord(postId, revisionNumber, this.roomId, id);
        });
    }
}
