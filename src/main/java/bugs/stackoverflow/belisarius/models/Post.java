package bugs.stackoverflow.belisarius.models;

public class Post {

    private int postId;
    private int revisionNumber;
    private long creationDate;
    private String revisionUrl;
    private String allRevisionsUrl;
    private String title;
    private String lastTitle;
    private String body;
    private String lastBody;
    private StackOverflowUser user;
    private boolean isRollback;
    private String postType;
    private String comment;
    private String site;
    private String revisionGuid;
    private String previousRevisionGuid;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getRevisionUrl() {
        return revisionUrl;
    }

    public void setRevisionUrl(String revisionUrl) {
        this.revisionUrl = revisionUrl;
    }

    public String getAllRevisionsUrl() {
        return allRevisionsUrl;
    }

    public void setAllRevisionsUrl(String allRevisionsUrl) {
        this.allRevisionsUrl = allRevisionsUrl;
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

    public StackOverflowUser getUser() {
        return user;
    }

    public void setUser(StackOverflowUser user) {
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

    public String getRevisionGuid() {
        return revisionGuid;
    }

    public void setRevisionGuid(String revisionGuid) {
        this.revisionGuid = revisionGuid;
    }

    public String getPreviousRevisionGuid() {
        return previousRevisionGuid;
    }

    public void setPreviousRevisionGuid(String previousRevisionGuid) {
        this.previousRevisionGuid = previousRevisionGuid;
    }

}
