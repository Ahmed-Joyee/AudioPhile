package app.android.audiophile;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public List<Playlist> playlists;
    public List<UsernameAndUId> friends;
//    public Map<String, String>friends;

    public String getEmail() {
        return email;
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





    public User() {

    }

    public User(String _email, String _username, String _password, String _uId, String _mobile) {
        email = _email;
        username = _username;
        password = _password;
        uId = _uId;
        mobile = _mobile;
        playlists = new ArrayList<>();
        friends = new ArrayList<>();
    }

    public User(String _email, String _username, String _password, String _uId, String _mobile, ArrayList<Playlist>_playlists) {
        email = _email;
        username = _username;
        password = _password;
        uId = _uId;
        mobile = _mobile;
        playlists = _playlists;
        friends = new ArrayList<>();
    }

    public User(String _email, String _username, String _password, String _uId, String _mobile, ArrayList<Playlist>_playlists, List<UsernameAndUId> _friends) {
        email = _email;
        username = _username;
        password = _password;
        uId = _uId;
        mobile = _mobile;
        playlists = _playlists;
        friends = _friends;
    }

    public User(String _email, String _username, String _password, String _uId, String _mobile, List<UsernameAndUId> _friends) {
        email = _email;
        username = _username;
        password = _password;
        uId = _uId;
        mobile = _mobile;
        playlists = new ArrayList<>();
        friends = _friends;
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
        mDatabase.child("Users").child(this.uId).child("Playlists").setValue(playlists);
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

    public void addFriends(User user) {
        friends.add(new UsernameAndUId(user.uId, user.username));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(this.uId).child("Friends").setValue(friends);
    }
}
