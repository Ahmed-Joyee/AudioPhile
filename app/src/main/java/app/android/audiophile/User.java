package app.android.audiophile;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public String email;
    public String username;
    public String password;
    public String uId;
    public String mobile;
    public List<Playlist> playlists;
    public List<String> friends;

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
        friends.add(user.uId);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(this.uId).child("Friends").setValue(friends);
    }
}
