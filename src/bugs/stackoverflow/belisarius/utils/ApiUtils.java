package bugs.stackoverflow.belisarius.utils;

import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.utils.JsonUtils;

import java.io.IOException;

public class ApiUtils {

    private static final String postsFilter = "!b0OfN5SJ6GH.YG";
	private static final String revisionsFilter = "!)Q7oQ)W6KSnSJdHfy6XPjec6";
	
	static JsonUtils jsonUtils = new JsonUtils();

    public static JsonObject getPostIdsByActivityDesc(int page, String site, String apiKey) throws IOException{
    	String postsUrl = "https://api.stackexchange.com/2.2/posts";
    	return jsonUtils.get(postsUrl,"page",String.valueOf(page),"pagesize","100","order","desc","sort","activity","site",site,"filter",postsFilter,"key",apiKey);
    }
    
    public static JsonObject getLastestRevisionByPostId(int postId, String site, String apiKey) throws IOException{
    	String postsUrl = "https://api.stackexchange.com/2.2/posts/" + String.valueOf(postId) + "/revisions";
    	return jsonUtils.get(postsUrl,"page","1","pagesize","1","site",site,"filter",revisionsFilter,"key",apiKey);
    }
	
}
