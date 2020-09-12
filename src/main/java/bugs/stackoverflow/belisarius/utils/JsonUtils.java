package bugs.stackoverflow.belisarius.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import bugs.stackoverflow.belisarius.services.ApiService;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                LOGGER.info("BACKOFF received. Timeout for " + (backOffUntil + 2) + " seconds.");
                Thread.sleep(1000 * backOffUntil + 2000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        Connection.Response response = Jsoup.connect(url).data(data).method(Connection.Method.GET)
                                            .ignoreContentType(true).ignoreHttpErrors(true).execute();
        String json = response.body();
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " fetching URL " + url + ". Body is: " + response.body());
        }

        JsonObject root = null;
        try {
            root = new JsonParser().parse(json).getAsJsonObject();
        } catch (Exception exception) {
            LOGGER.info("Exception occurred while parsing the JSON API returned.", exception);
            DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String jsonDumpFilename = "jsondump" + timeStampPattern.format(java.time.LocalDateTime.now()) + ".txt";
            String urlDumpFilename = "url" + timeStampPattern.format(java.time.LocalDateTime.now()) + ".txt";
            FileUtils.writeStringToFile(new File(jsonDumpFilename), json, StandardCharsets.UTF_8);
            FileUtils.writeStringToFile(new File(urlDumpFilename), response.url().getQuery(), StandardCharsets.UTF_8);
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
        Connection.Response response = Jsoup.connect(url).method(Connection.Method.GET)
                                            .ignoreContentType(true).ignoreHttpErrors(true).execute();
        String body = response.body();
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " error fetching " + url + ". Body is: " + body);
        }
        return Jsoup.parse(body).getElementsByTag("pre").text();
    }

}
