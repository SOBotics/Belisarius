package bugs.stackoverflow.belisarius.api;

import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.utils.JsonUtils;

import java.io.IOException;

public class ApiMethods {
	
	private static final String postsFilter = "!b0OfN5SJ6GH.YG";
	private static final String revisionsFilter = "!9YdnS7lAA";

    public static JsonObject getPostIdsByActivity(long fromTimestamp, int page, String site, String apiKey) throws IOException{
    	String postsUrl = "https://api.stackexchange.com/2.2/posts";
    	return JsonUtils.get(postsUrl,"page",String.valueOf(page),"pagesize","100","fromdate",String.valueOf(fromTimestamp),"order","desc","sort","activity","site",site,"filter",postsFilter,"key",apiKey);
    }
    
    public static JsonObject getLastRevisionByPostId(int postId, String site, String apiKey) throws IOException{
    	String postsUrl = "https://api.stackexchange.com/2.2/posts/" + String.valueOf(postId) + "/revisions";
    	return JsonUtils.get(postsUrl,"page","1","pagesize","1","site",site,"filter",revisionsFilter,"key",apiKey);
    }
	
}
