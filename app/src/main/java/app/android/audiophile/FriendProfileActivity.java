package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.android.audiophile.databinding.ActivityFriendProfileBinding;

public class FriendProfileActivity extends AppCompatActivity {

    private ActivityFriendProfileBinding binding;
    private final String TAG = "FriendProfileActivity";
    String hisUsername, hisUId, username, userUId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        binding = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hisUsername = (String)bundle.get("hisUsername");
        hisUId = (String) bundle.get("hisUId");
        username = (String) bundle.get("username");
        Log.d(TAG, username);
        userUId = (String) bundle.get("userUid");
        Log.d(TAG, userUId);
        binding.friendName.setText(hisUsername);
        binding.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                databaseReference.child(new Util().getUId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().getValue(User.class);
                            Log.d(TAG, user.getUsername());
//                            Log.d(TAG, user.getFriends().toString());
                            user.addFriends(new User(hisUsername, hisUId));
                        }
                    }
                });
                databaseReference.child(hisUId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            User user1 = task.getResult().getValue(User.class);
                            Log.d(TAG, user1.getUsername());
//                            Toast.makeText(FriendProfileActivity.this, "Friend Added",Toast.LENGTH_SHORT).show();;
                            user1.addFriends(new User(username, userUId));

                            Toast.makeText(FriendProfileActivity.this, "Friend Added, You can go back now",Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
            }
        });
    }
}