package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;

import com.google.gson.JsonObject;

public class ApiUtils {

    private static final String POSTS_FILTER = "!b0OfN5SJ6GH.YG";
    private static final String REVISIONS_FILTER = "!--Ozln_OPoiF";
    private static final String POST_FILTER = "!T.nr)x29OFqqjA8axH";
    private static final String API_VERSION = "2.3";
    private static final String BASE_URL = "https://api.stackexchange.com/" + API_VERSION;

    private static final JsonUtils JSON_UTILS = new JsonUtils();

    public static JsonObject getPostIdsByActivityDesc(
        int page,
        String site,
        String apiKey,
        long min
    ) throws IOException {
        String postsUrl = BASE_URL + "/posts";

        return JSON_UTILS.get(
            postsUrl,
            "page", String.valueOf(page),
            "pagesize", "100",
            "order", "desc",
            "sort", "activity",
            "site", site,
            "filter", POSTS_FILTER,
            "key", apiKey,
            "min", String.valueOf(min)
        );
    }

    public static JsonObject getLastestRevisions(
        String postIds,
        String site,
        String apiKey,
        int page
    ) throws IOException {
        String postsUrl = BASE_URL + "/posts/" + postIds + "/revisions";

        return JSON_UTILS.get(
            postsUrl,
            "page", String.valueOf(page),
            "pagesize", "100",
            "site", site,
            "filter", REVISIONS_FILTER,
            "key", apiKey
        );
    }

    public static JsonObject getMorePostInformation(String postId, String site, String apiKey) throws IOException {
        String postUrl = BASE_URL + "/posts/" + postId;

        return JSON_UTILS.get(
            postUrl,
            "page", "1",
            "pagesize", "100",
            "site", site,
            "filter", POST_FILTER,
            "key", apiKey
        );
    }
}
