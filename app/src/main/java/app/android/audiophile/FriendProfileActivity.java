package app.android.audiophile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import app.android.audiophile.databinding.ActivityFriendProfileBinding;

public class FriendProfileActivity extends AppCompatActivity {

    private ActivityFriendProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        binding = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.friendName.setText((String)bundle.get("hisUsername"));
    }
}