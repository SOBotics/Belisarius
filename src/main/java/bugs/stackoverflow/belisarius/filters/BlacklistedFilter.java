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

public class BlacklistedFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlacklistedFilter.class);

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
            blacklistedWords.put("title", CheckUtils.checkForBlackListedWords(post.getTitle(), post.getLastTitle(), "question_title"));
        }

        if (post.getBody() != null) {
            blacklistedWords.put("body", CheckUtils.checkForBlackListedWords(post.getBody(), post.getLastBody(), post.getPostType()));
        }

        if (post.getComment() != null) {
            blacklistedWords.put("comment", CheckUtils.checkForBlackListedWords(post.getComment(), null, post.getPostType()));
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
        String message = "";

        if (this.blacklistedWords.containsKey("title") && !this.blacklistedWords.get("title").isEmpty()) {
            message += "**Title contain blacklisted " + (this.blacklistedWords.get("title").size() > 1 ? "words" : "word") + ":** ";
            message += getBlacklistedWords("title") + " ";
        }

        if (this.blacklistedWords.containsKey("body") && !this.blacklistedWords.get("body").isEmpty()) {
            message += "**Body contains blacklisted " + (this.blacklistedWords.get("body").size() > 1 ? "words" : "word") + ":** ";
            message += getBlacklistedWords("body") + " ";
        }

        if (this.blacklistedWords.containsKey("comment") && !this.blacklistedWords.get("comment").isEmpty()) {
            message += "**Edit summary contains blacklisted " + (this.blacklistedWords.get("comment").size() > 1 ? "words" : "word") + ":** ";
            message += getBlacklistedWords("comment") + " ";
        }

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
        // for each of the hashmap of the blacklistedWords hashmap, add the keys to blacklistedWordIds
        blacklistedWords.values().forEach(wordsMap -> blacklistedWordIds.addAll(wordsMap.keySet()));
        return blacklistedWordIds;
    }

    @Override
    public void storeHit() {
        long postId = this.post.getPostId();
        int revisionNumber = this.post.getRevisionNumber();
        if (!DatabaseUtils.checkReasonCaughtExists(postId, revisionNumber, this.roomId, this.reasonId)) {
            DatabaseUtils.storeReasonCaught(postId, revisionNumber, this.roomId, this.reasonId, this.getScore());
            LOGGER.info("Successfully stored reason BlacklistedFilter for post " + postId + " to database.");
        }

        this.getCaughtBlacklistedWordIds().forEach(id -> {
            if (!DatabaseUtils.checkBlacklistedWordCaughtExists(postId, revisionNumber, this.roomId, id)) {
                DatabaseUtils.storeCaughtBlacklistedWord(postId, revisionNumber, this.roomId, id);
                LOGGER.info("Successfully stored blacklisted word id for post " + postId + " to database.");
            }
        });
    }
}
