package app.android.audiophile;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_no_internet);
        checkInternet();
        goLocalMusic();
        getSupportActionBar().hide();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());

        return (capabilities != null && capabilities.hasCapability(NET_CAPABILITY_INTERNET));

    }

    private void checkInternet(){
        Button btnTryAgain = findViewById(R.id.try_again_button);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent intent = new Intent(NoInternetActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(NoInternetActivity.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goLocalMusic(){
        Button btnLocalMusic = findViewById(R.id.local_music_button);
        btnLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoInternetActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });
    }
}