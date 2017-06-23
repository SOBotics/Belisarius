package bugs.stackoverflow.belisarius.models;

public class Post {
	
	public final static String postType_Answer = "answer";
	public final static String postType_Question = "question";
		
    private int postId;
    private int revisionNumber;
    private String title;
    private String lastTitle;
    private String body;
    private String lastBody;
    private SOUser user;
    private boolean isRollback;
    private String postType;
    
    public int getPostId() {
        return postId;
    }

    public void setPostID(int postId) {
        this.postId = postId;
    }
    
    public int getRevisionNumber() {
    	return revisionNumber;
    }
    
    public void setRevisionNumber(int revisionNumber) {
    	this.revisionNumber = revisionNumber;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getTitle() {
    	return title;
    }

    public void setLastTitle(String LastTitle) {
    	this.lastTitle = LastTitle;
    }
    
    public String getLastTitle() {
    	return lastTitle;
    }
    
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLastBody() {
        return lastBody;
    }

    public void setLastBody(String lastBody) {
        this.lastBody = lastBody;
    }

    public SOUser getUser() {
        return user;
    }

    public void setUser(SOUser user) {
        this.user = user;
    }
    
    public boolean getIsRollback() {
    	return isRollback;
    }
    
    public void setIsRollback(boolean isRollback) {
    	this.isRollback = isRollback;
    }
    
    public String getPostType() {
    	return postType;
    }
    
    public void setPostType(String postType) {
    	this.postType = postType;
    }
    
}