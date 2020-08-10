package bugs.stackoverflow.belisarius.services;

import java.io.IOException;

import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.utils.ApiUtils;

public class ApiService {
	
    private String apiKey;
    private String site;
    
    private static int quota=0;
	private static long backOffUntil = 0L;

    public ApiService(String site) {
    	PropertyService ps = new PropertyService();
    	
    	this.site = site;
    	this.apiKey = ps.getApiKey();
    }
    
    public JsonObject getPostIdsByActivityDesc(int page) throws IOException {
    	JsonObject postsJSON = ApiUtils.getPostIdsByActivityDesc(page, site, apiKey);
    	quota = postsJSON.get("quota_remaining").getAsInt();
    	setBackOffUntil(postsJSON);
    	return postsJSON;
    }
    
    public JsonObject getLastestRevisions(String postIdInput) throws IOException {
    	JsonObject revisionJSON = ApiUtils.getLastestRevisions(postIdInput, site, apiKey);
    	quota = revisionJSON.get("quota_remaining").getAsInt();
    	setBackOffUntil(revisionJSON);
    	return revisionJSON;
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