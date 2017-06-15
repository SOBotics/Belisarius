package bugs.stackoverflow.belisarius.models;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

public class Post {
    private int postID;
    private String title;
    private String lastTitle;
    private String body;
    private String lastBody;
    private SOUser editor;
    private boolean isRollback;
    private String postType;
    
    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
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

    public SOUser getEditor() {
        return editor;
    }

    public void setEditor(SOUser editor) {
        this.editor = editor;
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
    
    @Override
    public String toString() {

        JsonObject json = getJson();
        return json.toString();
    }

    @NotNull
    private JsonObject getJson() {
        JsonObject json = new JsonObject();

        json.addProperty("postId", postID);
        json.addProperty("body", body);
        json.addProperty("lastBody", lastBody);
        json.addProperty("isRollback", isRollback);
        json.addProperty("postType", postType);
        json.add("editor", editor.getJson());
        
        return json;

    }
    
}
