package bugs.stackoverflow.belisarius.services;

import java.io.IOException;

import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.api.ApiMethods;

public class ApiService {
	
    private String apiKey;
    private String site;
    
    private static int quota=0;

    public ApiService(String site) {
    	PropertyService ps = new PropertyService();
    	
    	this.site = site;
    	this.apiKey = ps.getApiKey();
    }
    
    public JsonObject getPostIdsByActivity(long fromTimestamp, int page) throws IOException {
    	JsonObject postsJSON = ApiMethods.getPostIdsByActivity(fromTimestamp, page, site, apiKey);
    	quota = postsJSON.get("quota_remaining").getAsInt();
    	return postsJSON;
    }
    
    public JsonObject getLastRevisionByPostId(int postId) throws IOException {
    	JsonObject revisionJSON = ApiMethods.getLastRevisionByPostId(postId, site, apiKey);
    	quota = revisionJSON.get("quota_remaining").getAsInt();
    	return revisionJSON;
    }
 
    public static int getQuota() {
    	return quota;
    }
    
}