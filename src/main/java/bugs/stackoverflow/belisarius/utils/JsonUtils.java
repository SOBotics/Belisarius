package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;

import bugs.stackoverflow.belisarius.services.ApiService;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Created by bhargav.h on 22-Jan-17.
 * AKA, TunaLib - All code is courtesy of Lord Tunaki
 */
public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    public synchronized JsonObject get(String url, String... data) throws IOException {
        long backOffUntil = ApiService.getBackoffField();

        if (backOffUntil > 0) {
            LOGGER.info("BACKOFF received. Timeout for " + (backOffUntil + 2) + " seconds.");

            try {
                // sleep for 2 more seconds to avoid more BACKOFFs in the future
                Thread.sleep(1000 * backOffUntil + 2000);
            } catch (InterruptedException exception) {
                LOGGER.info("sleep interrupted.", exception);
            }
        }

        Connection.Response response = Jsoup
            .connect(url)
            .data(data)
            .method(Connection.Method.GET)
            .maxBodySize(20971520)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .execute();

        String json = response.body();
        if (response.statusCode() != 200) {
            throw new IOException(
                "HTTP " + response.statusCode()
                    + " fetching URL " + url + ". "
                    + "Body is: " + response.body()
            );
        }

        JsonObject root = null;
        try {
            root = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonSyntaxException exception) {
            LOGGER.error("Exception occurred while parsing the JSON API returned.", exception);
        }

        LOGGER.info("Received an API response for method " + url + ".");

        return root;
    }

    public static String escapeHtmlEncoding(String message) {
        String sanitised = JsonUtils.sanitizeChatMessage(message);

        return Parser
            .unescapeEntities(sanitised, false)
            .trim();
    }

    public static String sanitizeChatMessage(String message) {
        return message.replaceAll("([\\[\\]_*`])", "\\\\$1");
    }

    public static String getHtml(String url) throws IOException {
        Connection.Response response = Jsoup
            .connect(url)
            .method(Connection.Method.GET)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .execute();

        String body = response.body();

        if (response.statusCode() == 404) { // body was not edited
            return null;
        } else if (response.statusCode() != 200) {
            throw new IOException(
                "HTTP " + response.statusCode() + " error fetching " + url + "."
            );
        }

        return Jsoup.parse(body).getElementsByTag("pre").text();
    }

}
