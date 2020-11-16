package bugs.stackoverflow.belisarius.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class CheckUtils {

    // Map with the threshold for the FewUniqueCharacters reason.
    // Stolen from https://github.com/Charcoal-SE/SmokeDetector/blob/84099eecffbd85b15de90a1ea41a7c8776ac0903/findspam.py#L592-L595
    // Key is the number of unique characters, value is a list with lengths which correspond to the key
    private static final Map<Integer, List<Integer>> FEW_UNIQUES_THRESHOLD = new HashMap<Integer, List<Integer>>() {
        {
            put(6, Arrays.asList(30, 36));
            put(7, Arrays.asList(36, 42));
            put(8, Arrays.asList(42, 48));
            put(9, Arrays.asList(48, 54));
            put(10, Arrays.asList(54, 60));
            put(11, Arrays.asList(60, 70));
            put(12, Arrays.asList(70, 80));
            put(13, Arrays.asList(80, 90));
            put(14, Arrays.asList(90, 100));
        }
    };

    public static Map<Integer, String> checkForBlackListedWords(String target, String lastTarget, String postType) {
        Map<Integer, String> blacklistedWords = DatabaseUtils.getBlacklistedWords(postType);
        Map<Integer, String> blacklistedWordsCaught = new HashMap<>();
        Map<Integer, String> lastBodyBlacklistedWords = getCaughtByRegex(blacklistedWords, removeHtml(stripTags(lastTarget)));
        Map<Integer, String> bodyBlacklistedWords = getCaughtByRegex(blacklistedWords, removeHtml(stripTags(target)));

        // Instead of finding the difference between the most recent revisions, then checking that piece for vandalism,
        // we check if the blacklisted part was added with the latest revision (i.e. old blacklisted !== new blacklisted).
        // However, there are cases where a blacklisted word is added to a post which already contains one.
        // The hashmaps won't be the same (the bodyBlacklistedWords will have more words), but we still want to report the post.
        // Thus, we also check if both of them are NOT empty.
        if (!(lastBodyBlacklistedWords.equals(bodyBlacklistedWords) || (lastBodyBlacklistedWords.isEmpty()
                                                                     && bodyBlacklistedWords.isEmpty())) || lastTarget == null) {
            blacklistedWordsCaught.putAll(bodyBlacklistedWords);
        }
        return blacklistedWordsCaught;
    }

    private static String stripTags(String target) {
        // strips links, code, images and blockquotes, as we don't want to catch the text inside those
        Document doc = Jsoup.parse("<body>" + target + "</body>");
        doc.getElementsByTag("a").remove();
        doc.getElementsByTag("code").remove();
        doc.getElementsByTag("img").remove();
        doc.getElementsByTag("pre").remove();
        doc.getElementsByTag("blockquote").remove();
        return doc.outerHtml();
    }

    private static String removeHtml(String target) {
        return Jsoup.parse(target).text();
    }

    public static String checkForLongWords(String target) {
        String[] bodyParts = removeHtml(stripTags(target)).replaceAll("[^a-zA-Z ]", " ").split(" ");
        for (String part : bodyParts) {
            // check for words which are longer than 50 characters
            if (part.length() > 50) {
                return part;
            }
        }
        return null;
    }

    public static boolean checkIfNoCodeBlock(String target) {
        return !target.contains("<code>");
    }

    public static double getJaroWinklerScore(String original, String target, double percentage) {
        String originalBody = stripTags(original);
        String targetBody = stripTags(target);
        double score = 1.0;

        if (targetBody.length() < originalBody.length() * percentage) {
            JaroWinkler jaroWinklerScore = new JaroWinkler();
            score = jaroWinklerScore.similarity(targetBody, originalBody);
        }

        return score;
    }

    public static String checkForFewUniqueCharacters(String target) {
        String body = removeHtml(stripTags(target));

        long uniquesCount = body.codePoints().distinct().count();
        long length = body.codePoints().count();

        for (Map.Entry<Integer, List<Integer>> threshold : FEW_UNIQUES_THRESHOLD.entrySet()) {
            List<Integer> lengths = threshold.getValue();
            if (length >= lengths.get(0) && length < lengths.get(1) && uniquesCount <= threshold.getKey()) {
                return body.codePoints()         // Intstream of codePoints
                    .distinct()
                    .collect(StringBuilder::new, // collect to a StringBuilder
                             StringBuilder::appendCodePoint, (swl, swr) -> swl.append(swr.toString()))
                    .toString();
            }
        }

        return null;
    }

    public static Map<Integer, String> checkForOffensiveWords(String target) {
        Map<Integer, String> offensiveWords = DatabaseUtils.getOffensiveWords();
        return getCaughtByRegex(offensiveWords, target);
    }

    private static Map<Integer, String> getCaughtByRegex(Map<Integer, String> words, String target) {
        Map<Integer, String> caught = new HashMap<>();

        for (Map.Entry<Integer, String> word : words.entrySet()) {
            Pattern pattern = Pattern.compile(word.getValue());

            if (checkIfBodyContainsWord(pattern, target)) {
                caught.put(word.getKey(), word.getValue());
            }
        }

        return caught;
    }

    public static boolean checkIfBodyContainsWord(Pattern pattern, String target) {
        String body = removeHtml(target);
        return pattern.matcher(body).find();
    }

    public static Set<String> checkRepeatedWords(String target) {
        String[] words = target.split("\\W");
        return new HashSet<>(Arrays.asList(words));
    }

}
