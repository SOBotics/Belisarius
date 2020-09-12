package bugs.stackoverflow.belisarius.services;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import bugs.stackoverflow.belisarius.utils.ApiUtils;

import com.google.gson.JsonObject;

public class ApiService {

    private static AtomicInteger remainingQuota = new AtomicInteger(-1);
    private static AtomicLong backOffUntil = new AtomicLong(0);

    private String apiKey;
    private String site;

    public ApiService(String site) {
        PropertyService propertyService = new PropertyService();

        this.site = site;
        this.apiKey = propertyService.getApiKey();
    }

    public JsonObject getPostIdsByActivityDesc(int page, long minActivityDate) throws IOException {
        JsonObject postsJson = ApiUtils.getPostIdsByActivityDesc(page, site, apiKey, minActivityDate);
        setQuota(postsJson.get("quota_remaining").getAsInt());
        setBackOffUntil(postsJson);
        return postsJson;
    }

    public JsonObject getLatestRevisions(String postIdInput, int page) throws IOException {
        JsonObject revisionJson = ApiUtils.getLastestRevisions(postIdInput, site, apiKey, page);
        setQuota(revisionJson.get("quota_remaining").getAsInt());
        setBackOffUntil(revisionJson);
        return revisionJson;
    }

    public JsonObject getMorePostInformation(String postId) throws IOException {
        JsonObject postJson = ApiUtils.getMorePostInformation(postId, site, apiKey);
        setQuota(postJson.get("quota_remaining").getAsInt());
        setBackOffUntil(postJson);
        return postJson;
    }

    public void setQuota(int quota) {
        remainingQuota.set(quota);
    }

    public static int getQuota() {
        return remainingQuota.get();
    }

    public void setBackOffUntil(JsonObject jsonObject) {
        if (jsonObject.has("backoff")) {
            backOffUntil.set(jsonObject.get("backoff").getAsLong());
        } else {
            // Remember to reset to 0 if backoff doesn't exist!
            backOffUntil.set(0L);
        }
    }

    public static long getBackOffUntil() {
        return backOffUntil.get();
    }
}
