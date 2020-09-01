package bugs.stackoverflow.belisarius.utils;

import com.google.gson.*;

import bugs.stackoverflow.belisarius.services.ApiService;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bhargav.h on 22-Jan-17.
 * AKA, TunaLib - All code is courtesy of Lord Tunaki
 */
public class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    public synchronized JsonObject get(String url, String... data) throws IOException {
        long backOffUntil = ApiService.getBackOffUntil();

        if (backOffUntil > 0) {
            try {
                LOGGER.info("BACKOFF received. Timeout for " + String.valueOf(backOffUntil) + " seconds.");
                Thread.sleep(1000 * backOffUntil);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            backOffUntil = 0L;
        }

        try {
            Thread.sleep(100); // timeout for 100ms anyway
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Connection.Response response = Jsoup.connect(url).data(data).method(Connection.Method.GET).ignoreContentType(true).ignoreHttpErrors(true).execute();
        String json = response.body();
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " fetching URL " + (url) + ". Body is: " + response.body());
       }
        JsonObject root = null;

        try {
            root = new JsonParser().parse(json).getAsJsonObject();
        } catch (Exception e) {
            DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            org.apache.commons.io.FileUtils.writeStringToFile(new java.io.File("jsondump" + timeStampPattern.format(java.time.LocalDateTime.now()) + ".txt"), json);
            org.apache.commons.io.FileUtils.writeStringToFile(new java.io.File("url" + timeStampPattern.format(java.time.LocalDateTime.now()) + ".txt"), response.url().getQuery());
        }


        if (root.has("quota_remaining")) {
            StatusUtils.remainingQuota = new AtomicInteger(root.get("quota_remaining").getAsInt());
        }

        LOGGER.info("Received an API response.");

        return root;
    }

    public static String escapeHtmlEncoding(String message) {
        return Parser.unescapeEntities(JsonUtils.sanitizeChatMessage(message), false).trim();
    }
    public static String sanitizeChatMessage(String message) {
        return message.replaceAll("(\\[|\\]|_|\\*|`)", "\\\\$1");
    }

    public static String getHtml(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        String bodyMarkdown = document.body().getElementsByTag("pre").text();
        return bodyMarkdown;
    }

}
