package bugs.stackoverflow.belisarius;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.utils.PostUtils;

public class Belisarius {
	
	private long lastPostTime;
	private ApiService apiService;
    private String site;
    private String siteUrl;
    
	public static String readMe = "https://stackapps.com/questions/7473";
	
	public Belisarius(String site, String siteUrl) {
		this.lastPostTime = System.currentTimeMillis()/1000-1*60;
		this.apiService = new ApiService(site);
		this.site = site;
		this.siteUrl = siteUrl;
	}
	
	public List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		List<Integer> postIds = getPostIds();
		
		try {
			if (postIds.size() > 0) {
				List<Integer> postIdsRemaining = new ArrayList<Integer>();
				do {
					if (postIdsRemaining.size()>0) {
						postIds = postIdsRemaining;
					}
					String postIdInput = "";
					int count = 0;
					for (int id : postIds) {
						if (count != 100) {
							postIdInput += id + ";";
						} else {
							postIdsRemaining.add(id);
						}
						count++;
					}
					postIdInput = postIdInput.substring(0, postIdInput.length()-1);
					List<Post> postsWithLatestRevisions = getPostsWithLatestRevision(postIdInput);
					if (postsWithLatestRevisions.size()>0) {
						posts.addAll(postsWithLatestRevisions);
					}
				} while (postIdsRemaining.size()>0);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		for (Post post : posts) {
			post.setSite(this.site);
			post.setSiteUrl(this.siteUrl);
		}
		
		return posts;
	}

	private List<Integer> getPostIds() {
		List<Integer> postIds = new ArrayList<Integer>();
		
		JsonObject postsJSON = null;
		try {
			long lastActivityDate = 0;
			long maxActivityDate = this.lastPostTime;
			int page = 1;
			do {
				postsJSON = apiService.getPostIdsByActivityDesc(page);
	            for (JsonElement post : postsJSON.get("items").getAsJsonArray()) {
            		lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();
	            	
            		if (PostUtils.postBeenEdited(post) && PostUtils.editorAlsoOwner(post) && lastPostTime < lastActivityDate) {
	            		int postId = post.getAsJsonObject().get("post_id").getAsInt();
	            		if (!postIds.contains(postId)) {
	            			postIds.add(postId);
	            		}
	            	}
            		
	                if (maxActivityDate < lastActivityDate) {
	            		maxActivityDate = lastActivityDate;
	            	}
	            }
	            page++;
			} while (lastPostTime < lastActivityDate & page<10);
		
           	this.lastPostTime = maxActivityDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return postIds;
	}
	
	private List<Post> getPostsWithLatestRevision(String postIdFilter) {
		List<Post> revisions = new ArrayList<Post>();
		String[] postIds = postIdFilter.split(";");
		boolean hasMore = false;
		do {
			
			try {
				if (postIds.length>0) {
					JsonObject postsJSON = apiService.getLastestRevisions(String.join(";", postIds));
					hasMore = postsJSON.get("has_more").getAsBoolean();
					for (String id : postIds) {
						int revisionNo = 0;
						Post revision = null;
						for (JsonElement post : postsJSON.get("items").getAsJsonArray()) {
						    if (post.getAsJsonObject().get("post_id").getAsInt() == Integer.parseInt(id) && post.getAsJsonObject().has("revision_number")) {
						    	if (revisionNo < post.getAsJsonObject().get("revision_number").getAsInt()) {
						    		revisionNo = post.getAsJsonObject().get("revision_number").getAsInt();
						    		revision = PostUtils.getPost(post.getAsJsonObject());
						    	}
						    }
						}
					    if (revision != null) {
					    	revisions.add(revision);
					    }
						postIds = ArrayUtils.removeElement(postIds, id);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} while (hasMore || postIds.length>0);
		
		return revisions;
	}
	
}
