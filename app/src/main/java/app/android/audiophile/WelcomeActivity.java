package app.android.audiophile;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;

import app.android.audiophile.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import app.android.audiophile.R;

public class WelcomeActivity extends AppCompatActivity {

    private TextView t1,t2,t3;
    private ImageView im1,im2;
    Animation top,bottom;
    private  static  int SPLASH_SCREEN =2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        t3=findViewById(R.id.name);
        im1=findViewById(R.id.imageView);
        top = AnimationUtils.loadAnimation(this, R.anim.top);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);

        im1.setAnimation(top);
        t3.setAnimation(bottom);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if(isNetworkAvailable()) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(WelcomeActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_SCREEN);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());

        return (capabilities != null && capabilities.hasCapability(NET_CAPABILITY_INTERNET));

    }
}