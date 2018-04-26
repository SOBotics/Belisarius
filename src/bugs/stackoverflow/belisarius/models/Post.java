package bugs.stackoverflow.belisarius.models;

public class Post {
	
	public final static String postTypeAnswer = "answer";
	public final static String postTypeQuestion = "question";
		
    private int postId;
    private int revisionNumber;
    private String title;
    private String lastTitle;
    private String body;
    private String lastBody;
    private SOUser user;
    private boolean isRollback;
    private String postType;
    private String comment;
    private String site;
    
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

    public void setLastTitle(String lastTitle) {
    	this.lastTitle = lastTitle;
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
    
    public String getComment() {
    	return comment;
    }
    
    public void setComment(String comment) {
    	this.comment = comment;
    }
    
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
    
}