package bugs.stackoverflow.belisarius;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
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
		List<Long> postIds = getPostIds();
		
		try {
			if (postIds.size() > 0) {
				List<Long> postIdsRemaining = new ArrayList<Long>();
				do {
					if (postIdsRemaining.size()>0) {
						postIds = postIdsRemaining;
						postIdsRemaining = new ArrayList<Long>();
					}
					String postIdInput = "";
					int count = 0;
					for (long id : postIds) {
						if (count != 50) {
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
	
	public Post getPost(String postId) {
		Post post = null;
		try {
			if (postId != null) {
				List<Post> postsWithLatestRevisions = getPostsWithLatestRevision(postId);
				if (postsWithLatestRevisions.size()==1) {
					post = postsWithLatestRevisions.get(0);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	
		post.setSite(this.site);
		post.setSiteUrl(this.siteUrl);
		
		return post;
	}
	
	public Post getPostLocal(long postId, int revisionId, int roomId) {
		Post post = null;
		try {
			post = DatabaseUtils.getPost(postId, revisionId, roomId);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		post.setSite(this.site);
		post.setSiteUrl(this.siteUrl);
		
		return post;
	}

	private List<Long> getPostIds() {
		List<Long> postIds = new ArrayList<Long>();
		
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
	            		long postId = post.getAsJsonObject().get("post_id").getAsLong();
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
			
		} while (hasMore && postIds.length>0);
		
		return revisions;
	}
	
}
