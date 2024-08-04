package bugs.stackoverflow.belisarius.services;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import bugs.stackoverflow.belisarius.utils.ApiUtils;

import com.google.gson.JsonObject;

public class ApiService {

    private static final AtomicInteger REMAINING_QUOTA = new AtomicInteger(-1);
    private static final AtomicLong BACKOFF_FIELD = new AtomicLong(0);

    private final String apiKey = new PropertyService().getProperty("apikey");
    private final String site;

    public ApiService(String site) {
        this.site = site;
    }

    private void updateQuotaAndBackoff(JsonObject json) {
        setQuota(json.get("quota_remaining").getAsInt());
        setBackOffUntil(json);
    }

    public JsonObject getPostIdsByActivityDesc(int page, long minActivityDate) throws IOException {
        JsonObject postsJson = ApiUtils.getPostIdsByActivityDesc(page, site, apiKey, minActivityDate);
        this.updateQuotaAndBackoff(postsJson);

        return postsJson;
    }

    public JsonObject getLatestRevisions(String postIdInput, int page) throws IOException {
        JsonObject revisionJson = ApiUtils.getLastestRevisions(postIdInput, site, apiKey, page);
        this.updateQuotaAndBackoff(revisionJson);

        return revisionJson;
    }

    public JsonObject getMorePostInformation(String postId) throws IOException {
        JsonObject postJson = ApiUtils.getMorePostInformation(postId, site, apiKey);
        this.updateQuotaAndBackoff(postJson);

        return postJson;
    }

    public void setQuota(int quota) {
        REMAINING_QUOTA.set(quota);
    }

    public static int getQuota() {
        return REMAINING_QUOTA.get();
    }

    public void setBackOffUntil(JsonObject jsonObject) {
        if (jsonObject.has("backoff")) {
            BACKOFF_FIELD.set(jsonObject.get("backoff").getAsLong());
        } else {
            // Remember to reset to 0 if backoff doesn't exist!
            BACKOFF_FIELD.set(0L);
        }
    }

    public static long getBackoffField() {
        return BACKOFF_FIELD.get();
    }
}
