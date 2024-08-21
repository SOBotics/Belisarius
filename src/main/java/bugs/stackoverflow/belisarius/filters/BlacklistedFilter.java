package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.FilterUtils;

public class BlacklistedFilter implements Filter {
    private final int roomId;
    private final Post post;
    private final int reasonId = 1;
    private final Map<String, Map<Integer, String>> blacklistedWords = new HashMap<>();

    public BlacklistedFilter(int chatroomId, Post post) {
        this.roomId = chatroomId;
        this.post = post;
    }

    @Override
    public boolean isHit() {
        if (post.getLastTitle() != null && "question".equals(post.getPostType())) {
            blacklistedWords.put(
                "title",
                CheckUtils.checkForBlackListedWords(
                    post.getTitle(),
                    post.getLastTitle(),
                    "question_title"
                )
            );
        }

        if (post.getBody() != null) {
            blacklistedWords.put(
                "body",
                CheckUtils.checkForBlackListedWords(
                    post.getBody(),
                    post.getLastBody(),
                    post.getPostType()
                )
            );
        }

        if (post.getComment() != null) {
            blacklistedWords.put(
                "comment",
                CheckUtils.checkForBlackListedWords(
                    post.getComment(),
                    null,
                    post.getPostType()
                )
            );
        }

        return getTotalScore() > 0;
    }

    @Override
    public double getScore() {
        return 1.0;
    }

    @Override
    public double getTotalScore() {
        int score = 0;

        for (Map<Integer, String> words : blacklistedWords.values()) {
            score += words.size();
        }

        return score;
    }

    @Override
    public String getFormattedReasonMessage() {
        String message = getFormattedMessage("title", "Title")
            + getFormattedMessage("body", "Body")
            + getFormattedMessage("comment", "Edit summary");

        return message.trim();
    }

    @Override
    public List<String> getReasonName() {
        String name = "Contains blacklisted word: ";
        List<String> words = new ArrayList<>();

        // add name + word to the words list
        blacklistedWords.values().forEach(wordsMap -> {
            // wordsMap is the Map<Integer, String> hashmap
            // its values are the blacklisted words
            wordsMap.values().forEach(word -> words.add(name + word));
        });
        return words;
    }

    private String getBlacklistedWords(String postType) {
        StringBuilder words = new StringBuilder();

        for (String word : this.blacklistedWords.get(postType).values()) {
            words.append(word).append(", ");
        }

        return words.substring(0, words.length() - 2);
    }

    @Override
    public Severity getSeverity() {
        return Severity.MEDIUM;
    }

    private List<Integer> getCaughtBlacklistedWordIds() {
        List<Integer> blacklistedWordIds = new ArrayList<>();

        blacklistedWords
            .values()
            .forEach(wordsMap -> blacklistedWordIds.addAll(wordsMap.keySet()));

        return blacklistedWordIds;
    }

    @Override
    public void storeHit() {
        long postId = post.getPostId();
        int revisionNumber = post.getRevisionNumber();
        double score = getScore();

        DatabaseUtils.storeReason(postId, revisionNumber, roomId, reasonId, score);

        this.getCaughtBlacklistedWordIds().forEach(id -> {
            DatabaseUtils.storeBlacklistedWord(postId, revisionNumber, this.roomId, id);
        });
    }

    private String getFormattedMessage(
        String field,
        String description
    ) {
        if (blacklistedWords.containsKey(field) && !blacklistedWords.get(field).isEmpty()) {
            return "**" + description + " contains blacklisted "
                + FilterUtils.pluralise("word", blacklistedWords.get(field).size())
                + ":** "
                + getBlacklistedWords(field) + " ";
        }

        return "";
    }
}
