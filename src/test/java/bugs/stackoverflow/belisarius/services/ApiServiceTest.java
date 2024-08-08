package bugs.stackoverflow.belisarius.services;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiServiceTest {
    private final ApiService apiService = new ApiService("stackoverflow");

    @Test
    public void quotaTest() {
        apiService.setQuota(9483);
        assertEquals(ApiService.getQuota(), 9483);

        apiService.setQuota(9480);
        apiService.setQuota(9479);
        assertEquals(ApiService.getQuota(), 9479);
    }

    @Test
    public void backoffTest() {
        JsonObject sample1 = JsonParser
            .parseString("{\"items\": [],\"quota_remaining\": 9789,\"backoff\": 10}")
            .getAsJsonObject();

        apiService.setBackOffUntil(sample1);
        assertEquals(ApiService.getBackoffField(), 10);

        JsonObject sample2 = JsonParser
            .parseString("{\"items\": [],\"quota_remaining\": 9789}")
            .getAsJsonObject();
        apiService.setBackOffUntil(sample2);
        assertEquals(ApiService.getBackoffField(), 0);
    }

    @Test
    public void apiMethodsTest() throws IOException {
        apiService.getPostIdsByActivityDesc(1, 1);
        assertTrue(ApiService.getQuota() > 0);

        apiService.getLatestRevisions("4;78751187", 1);

        JsonObject response = apiService.getMorePostInformation("4");
        JsonObject item = response
            .get("items").getAsJsonArray()
            .get(0).getAsJsonObject();

        assertEquals(item.get("post_id").getAsInt(), 4);
    }
}