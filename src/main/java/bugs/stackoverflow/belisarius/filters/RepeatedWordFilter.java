package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.FilterUtils;

public class RepeatedWordFilter implements Filter {
    private final int roomId;
    private final Post post;
    private final int reasonId = 5;
    private Set<String> repeatedWords = new HashSet<>();

    public RepeatedWordFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
    }

    @Override
    public boolean isHit() {
        if (post.getBody() != null) {
            repeatedWords = CheckUtils.checkRepeatedWords(post.getBody());
        }

        double score = getScore();

        return score > 0 && score <= 5;
    }

    @Override
    public double getScore() {
        return repeatedWords.size();
    }

    @Override
    public double getTotalScore() {
        return 6 - getScore();
    }

    @Override
    public String getFormattedReasonMessage() {
        String message = "";

        if (!repeatedWords.isEmpty()) {
            message += "**Post contains repeated "
                + FilterUtils.pluralise("word", repeatedWords.size())
                + ":** " + getRepeatedWords() + " ";
        }

        return message.trim();
    }

    @Override
    public List<String> getReasonName() {
        return new ArrayList<>(Collections.singletonList("Contains repeated words"));
    }

    private String getRepeatedWords() {
        StringBuilder words = new StringBuilder();

        for (String word : repeatedWords) {
            words.append(word);
        }

        return words.toString().length() < 40
            ? words.toString()
            : words.toString().substring(0, 40) + "...";
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
