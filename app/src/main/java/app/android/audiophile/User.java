package app.android.audiophile;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    public String email;
    public String username;
    public String password;
    public String uId;
    public String mobile;
    public List<Playlist> playlists = new ArrayList<>();
    public List<UsernameAndUId> friends = new ArrayList<>();
    public Boolean isPhoneVerified, isEmailVerified;
//    public Map<String, String>friends;

    public String getEmail() {
        return email;
    }

    public Boolean isPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public Boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UsernameAndUId> getFriends() {
        return friends;
    }

    public void setFriends(List<UsernameAndUId> friends) {
        this.friends = friends;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public User(String _username, String _uId){
        this.username = _username;
        this.uId = _uId;
    }

    public User(String _email, String _username, String _password, String _uId, String _mobile, Boolean _isEmailVerified, Boolean _isPhoneVerified) {
        this.email = _email;
        this.username = _username;
        this.password = _password;
        this.uId = _uId;
        this.mobile = _mobile;
        this.playlists = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.isPhoneVerified = _isPhoneVerified;
        this.isEmailVerified = _isEmailVerified;
    }

    public User(){

    }

    public User(String _email, String _username, String _password, String _uId, String _mobile, ArrayList<Playlist>_playlists, Boolean _isEmailVerified, Boolean _isPhoneVerified) {
        this.email = _email;
        this.username = _username;
        this.password = _password;
        this.uId = _uId;
        this.mobile = _mobile;
        this.playlists = _playlists;
        this.friends = new ArrayList<>();
        this.isPhoneVerified = _isPhoneVerified;
        this.isEmailVerified = _isEmailVerified;
    }

    public User(String _email, String _username, String _password, String _uId, String _mobile, ArrayList<Playlist>_playlists, List<UsernameAndUId> _friends,  Boolean _isEmailVerified, Boolean _isPhoneVerified) {
        this.email = _email;
        this.username = _username;
        this.password = _password;
        this.uId = _uId;
        this.mobile = _mobile;
        this.playlists = _playlists;
        this.friends = _friends;
        this.isPhoneVerified = _isPhoneVerified;
        this.isEmailVerified = _isEmailVerified;
    }

    public User(String _email, String _username, String _password, String _uId, String _mobile, List<UsernameAndUId> _friends,  Boolean _isEmailVerified, Boolean _isPhoneVerified) {
        this.email = _email;
        this.username = _username;
        this.password = _password;
        this.uId = _uId;
        this.mobile = _mobile;
        this.playlists = new ArrayList<>();
        this.friends = _friends;
        this.isPhoneVerified = _isPhoneVerified;
        this.isEmailVerified = _isEmailVerified;
    }

    public void InsertIntoDb() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(this.uId).setValue(this).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("firebase", e.toString());
            }
        });
    }

    public void uIdByEmail(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String, Object>mp = new HashMap<>();
        mp.put(formatEmail(),this.uId);
        mDatabase.child("Users").child("uIdByEmail").updateChildren(mp);
    }
    public void usernameByEmail(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String, Object>mp = new HashMap<>();
        mp.put(formatEmail(),this.username);
        mDatabase.child("Users").child("username").updateChildren(mp);
    }

    private String formatEmail(){
        StringBuilder s = new StringBuilder(email);
        for(int i = 0 ; i < email.length(); i++){
            if(email.charAt(i)=='.'){
                s.setCharAt(i,',');
            }
        }
        return s.toString();
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(this.uId).child("playlists").setValue(playlists);
    }

    public void addSongsToPlaylist(String songName, String playlistNumber) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Playlist p = playlists.get(new Integer(playlistNumber));
        p.playlist.add(songName);
        mDatabase.child("Users").child(this.uId).setValue(this);
    }

    public void deletePlaylist(String id) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        playlists.remove(new Integer(id));
        mDatabase.child("Users").child(this.uId).setValue(this);
    }

    public boolean isAlreadyFriend(User user){
        boolean no = false;
        for(UsernameAndUId item : this.friends){
            if(item.getuId()==user.getuId()){
                no = true;
                return no;
            }
        }
        return false;
    }
    public void addFriends(User user) {


//            friends.add(new UsernameAndUId(user.uId, user.username));
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Users").child(this.uId).child("friends").orderByChild("uId").equalTo(user.getuId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount() > 0){
                        Log.d("User", "works");
                    }else{
                        friends.add(new UsernameAndUId(user.uId, user.username));
                        mDatabase.child("Users").child(uId).child("friends").setValue(friends);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }
}
