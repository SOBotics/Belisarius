package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.utils.PostUtils;

import com.google.gson.JsonObject;

public class FilterTestUtils {
    public static final ApiService apiService = new ApiService("stackoverflow");
    public static JsonObject json;

    public static Post getSamplePost(
        String body,
        String lastBody,
        String title,
        String lastTitle,
        String summary,
        String postType
    ) throws IOException {
        setJson();

        JsonObject clone = json.deepCopy();

        if (body == null) {
            clone.remove("body");
        } else  {
            clone.addProperty("body", body);
        }

        if (lastBody == null) {
            clone.remove("last_body");
        } else  {
            clone.addProperty("last_body", lastBody);
        }

        if (title == null) {
            clone.remove("title");
        } else  {
            clone.addProperty("title", title);
        }

        if (lastTitle == null) {
            clone.remove("last_title");
        } else  {
            clone.addProperty("last_title", lastTitle);
        }

        if (summary == null) {
            clone.remove("comment");
        } else  {
            clone.addProperty("comment", summary);
        }

        if (postType == null) {
            clone.remove("post_type");
        } else  {
            clone.addProperty("post_type", postType);
        }

        return PostUtils.getPost(clone, "stackoverflow", "title");
    }

    private static void setJson() throws IOException {
        if (json != null) {
            return;
        }

        json = apiService.getLatestRevisions("2276572", 1)
            .get("items").getAsJsonArray()
            .get(2).getAsJsonObject();
    }
}