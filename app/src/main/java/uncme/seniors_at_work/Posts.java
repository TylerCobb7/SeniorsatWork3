package uncme.seniors_at_work;

public class Posts {
    public String uid, time, date, postImage, description, profileImage, username;

    public Posts(){

    }

    public Posts(String uid, String time, String date, String postImage, String description, String profileImage, String username) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postImage = postImage;
        this.description = description;
        this.profileImage = profileImage;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
