package bugs.stackoverflow.belisarius;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.sobotics.chatexchange.chat.Room;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.PostUtils;

public class Belisarius {

    public static final String README = "[ [Belisarius](https://stackapps.com/questions/7473) ";
    public static final String HIGGS_URL = "| [Hippo](https://higgs.sobotics.org/Hippo/report/";
    public static final String ALREADY_REPORTED = "The post has already been reported.";
    public static final String NO_ISSUES = "No issues have been found.";
    public static final String POTENTIAL_VANDALISM = "Potentially harmful edit found. Reason: ";

	private long lastPostTime;
	private ApiService apiService;
    private String site;

    public static String buildMessage(Room room, int higgsId, String postType, String severity, String status, String allRevs, int revNum, String revUrl) {
        String message = README;
        if (higgsId != 0) {
            message += HIGGS_URL + String.valueOf(higgsId) + ") ";
        }
        message += "] [tag:" + postType + "]";
        if (severity != null) {
            message += " [tag:severity-" + severity + "]";
        }
        message += " " + status + " [All revisions](" + allRevs + "). Revision: [" + String.valueOf(revNum) + "](" + revUrl + ")";
        room.send(message);
        return message;
    }
	
	public Belisarius(String site) {
		this.lastPostTime = System.currentTimeMillis()/1000-60;
		this.apiService = new ApiService(site);
		this.site = site;
	}
	
	public List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		List<Long> postIds = getPostIds();
		
		try {
			if (postIds.size() > 0) {
				List<Long> postIdsRemaining = new ArrayList<>();
				do {
					if (postIdsRemaining.size()>0) {
						postIds = postIdsRemaining;
						postIdsRemaining = new ArrayList<>();
					}
					int count = 0;
					StringBuilder postIdInput = new StringBuilder();
					for (long id : postIds) {
						if (count != 50) {
							postIdInput.append(id).append(";");
						} else {
							postIdsRemaining.add(id);
						}
						count++;
					}
					List<Post> postsWithLatestRevisions = getPostsWithLatestRevision(postIdInput.toString());
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
		
	    if(post != null) {
            post.setSite(this.site);
        }

		return post;
	}
	
	public Post getPostLocal(long postId, int revisionId, int roomId) {
		Post post = null;
		try {
			post = DatabaseUtils.getPost(postId, revisionId, roomId);
		} catch (Exception e){
			e.printStackTrace();
		}

        if(post != null) {
            post.setSite(this.site);
        }
		
		return post;
	}

	private List<Long> getPostIds() {
		List<Long> postIds = new ArrayList<>();
		
		JsonObject postsJSON ;
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
		List<Post> revisions = new ArrayList<>();
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
						    		revision = PostUtils.getPost(post.getAsJsonObject(), site);
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
