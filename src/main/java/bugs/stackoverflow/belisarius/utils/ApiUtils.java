package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;

import com.google.gson.JsonObject;

public class ApiUtils {

    private static final String POSTS_FILTER = "!b0OfN5SJ6GH.YG";
    private static final String REVISIONS_FILTER = "!--Ozln_OPoiF";
    private static final String POST_FILTER = "!T.nr)x29OFqqjA8axH";

    private static JsonUtils jsonUtils = new JsonUtils();

    public static JsonObject getPostIdsByActivityDesc(int page, String site, String apiKey, long min) throws IOException {
        String postsUrl = "https://api.stackexchange.com/2.2/posts";
        return jsonUtils.get(postsUrl, "page", String.valueOf(page), "pagesize", "100", "order", "desc",
                             "sort", "activity", "site", site, "filter", POSTS_FILTER, "key", apiKey,
                             "min", String.valueOf(min));
    }

    public static JsonObject getLastestRevisions(String postIdInput, String site, String apiKey) throws IOException {
        String postsUrl = "https://api.stackexchange.com/2.2/posts/" + postIdInput + "/revisions";
        return jsonUtils.get(postsUrl, "page", "1", "pagesize", "100", "site", site, "filter", REVISIONS_FILTER, "key", apiKey);
    }

    public static JsonObject getMorePostInformation(String postId, String site, String apiKey) throws IOException {
        String postUrl = "https://api.stackexchange.com/2.2/posts/" + postId;
        return jsonUtils.get(postUrl, "page", "1", "pagesize", "100", "site", site, "filter", POST_FILTER, "key", apiKey);
    }
}
