package bugs.stackoverflow.belisarius.models;

public class Post {

    private final int postId;
    private final int revisionNumber;
    private final long creationDate;
    private final String revisionUrl;
    private final String allRevisionsUrl;
    private final String title;
    private final String lastTitle;
    private final String body;
    private final String lastBody;
    private final User user;
    private final boolean isRollback;
    private final String postType;
    private final String comment;
    private final String site;
    private final String revisionGuid;
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
        String revisionGuid
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

    public void setPreviousRevisionGuid(String guid) {
        this.previousRevisionGuid = guid;
    }
}
