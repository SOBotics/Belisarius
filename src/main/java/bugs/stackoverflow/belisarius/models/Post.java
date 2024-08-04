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
    private User user;
    private boolean isRollback;
    private String postType;
    private String comment;
    private String site;
    private String revisionGuid;
    private String previousRevisionGuid;

    public Post(
        int postId,
        int revisionNumber,
        long creationDate,
        String title,
        String lastTitle,
        String body,
        String lastBody,
        User user,
        boolean isRollback,
        String postType,
        String comment,
        String site,
        String revisionGuid,
        String previousRevisionGuid
    ) {
        this.postId = postId;
        this.revisionNumber = revisionNumber;
        this.creationDate = creationDate;
        this.title = title;
        this.lastTitle = lastTitle;
        this.body = body;
        this.lastBody = lastBody;
        this.user = user;
        this.isRollback = isRollback;
        this.postType = postType;
        this.comment = comment;
        this.site = site;
        this.revisionGuid = revisionGuid;
        this.previousRevisionGuid = previousRevisionGuid;

        this.revisionUrl = "https://" + site + ".com/revisions/" + postId + "/" + revisionNumber;
        this.allRevisionsUrl = "https://" + site + ".com/posts/" + postId + "/revisions";
    }

    public int getPostId() {
        return postId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
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

    public String getAllRevisionsUrl() {
        return allRevisionsUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getBody() {
        return body;
    }

    public String getLastBody() {
        return lastBody;
    }

    public User getUser() {
        return user;
    }

    public boolean getIsRollback() {
        return isRollback;
    }

    public String getPostType() {
        return postType;
    }

    public String getComment() {
        return comment;
    }

    public String getSite() {
        return site;
    }

    public String getRevisionGuid() {
        return revisionGuid;
    }

    public String getPreviousRevisionGuid() {
        return previousRevisionGuid;
    }
}
