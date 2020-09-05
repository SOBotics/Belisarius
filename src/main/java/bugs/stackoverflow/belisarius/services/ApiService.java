package bugs.stackoverflow.belisarius.services;

import java.io.IOException;

import bugs.stackoverflow.belisarius.utils.ApiUtils;

import com.google.gson.JsonObject;

public class ApiService {

    private static int quota;
    private static long backOffUntil;

    private String apiKey;
    private String site;

    public ApiService(String site) {
        PropertyService propertyService = new PropertyService();

        this.site = site;
        this.apiKey = propertyService.getApiKey();
    }

    public JsonObject getPostIdsByActivityDesc(int page) throws IOException {
        JsonObject postsJson = ApiUtils.getPostIdsByActivityDesc(page, site, apiKey);
        quota = postsJson.get("quota_remaining").getAsInt();
        setBackOffUntil(postsJson);
        return postsJson;
    }

    public JsonObject getLastestRevisions(String postIdInput) throws IOException {
        JsonObject revisionJson = ApiUtils.getLastestRevisions(postIdInput, site, apiKey);
        quota = revisionJson.get("quota_remaining").getAsInt();
        setBackOffUntil(revisionJson);
        return revisionJson;
    }

    public JsonObject getMorePostInformation(String postId) throws IOException {
        JsonObject postJson = ApiUtils.getMorePostInformation(postId, site, apiKey);
        quota = postJson.get("quota_remaining").getAsInt();
        setBackOffUntil(postJson);
        return postJson;
    }

    public static int getQuota() {
        return quota;
    }

    public void setBackOffUntil(JsonObject jsonObject) {
        if (jsonObject.has("backoff")) {
            backOffUntil = jsonObject.get("backoff").getAsLong();
        }
    }

    public static long getBackOffUntil() {
        return backOffUntil;
    }
}
