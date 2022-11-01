package app.android.audiophile;

public class Post {

    private String postID,postedDate,postDescription,postedBy;
    private long postedAt,postLike;

    public Post() {
    }

    public Post(String postID, String postedDate, String postDescription, String postedBy, long postedAt, long postLike) {
        this.postID = postID;
        this.postedDate = postedDate;
        this.postDescription = postDescription;
        this.postedBy = postedBy;
        this.postedAt = postedAt;
        this.postLike = postLike;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public long getPostLike() {
        return postLike;
    }

    public void setPostLike(long postLike) {
        this.postLike = postLike;
    }
}
