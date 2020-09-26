package bugs.stackoverflow.belisarius.utils;

// import java.nio.file.StringWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class CheckUtils {

    public static Map<Integer, String> checkForBlackListedWords(String target, String postType) {
        Map<Integer, String> blacklistedWords = DatabaseUtils.getBlacklistedWords(postType);
        return getCaughtByRegex(blacklistedWords, removeHtml(stripTags(target)));
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

        long uniquesCount = body.chars().distinct().count();
        // There are two cases: body's length is 30+ and unique chars are at least 5
        //                      body's length is 100+ and unique characters are at least 15
        if ((body.length() >= 30 && uniquesCount <= 6) || body.length() >= 100 && uniquesCount <= 15) {
            return body.chars().distinct().collect(StringWriter::new, StringWriter::write, (swl, swr) -> swl.write(swr.toString())).toString();
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
        Set<String> repeatedWords = new HashSet<>();

        String[] words = target.split("\\W");
        for (String word : words) {
            if (!repeatedWords.contains(word)) {
                repeatedWords.add(word);
            }
        }

        return repeatedWords;
    }

}
