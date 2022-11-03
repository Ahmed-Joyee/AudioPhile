package app.android.audiophile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment = null;
    private String username, userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle bundle = getIntent().getExtras();
        chipNavigationBar = findViewById(R.id.chipNavigation);
        chipNavigationBar.setItemSelected(R.id.chat, true);
        fragment = new ChatFragment();
        fragment.setArguments(bundle);
        getSupportActionBar().hide();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i){
                case R.id.chat:
                    fragment = new ChatFragment();
                    if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                    break;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                    break;
                case R.id.local:
                    fragment = new LocalSongsFragment();
                    if(!MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().reset();
                    break;
                case R.id.friend:
                    fragment = new SearchFriendsFragment();
                    if(MyMediaPlayer.getInstance().isPlaying())MyMediaPlayer.getInstance().stop();
                    break;
            }

            if(fragment != null){
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
}