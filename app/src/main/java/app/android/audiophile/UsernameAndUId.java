package app.android.audiophile;

public class UsernameAndUId {
    private String uId;
    private  String username;

    public UsernameAndUId(){

    }
    public UsernameAndUId(String uId, String username) {
        this.uId = uId;
        this.username = username;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
