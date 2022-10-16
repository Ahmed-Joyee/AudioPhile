package app.android.audiophile;

import app.android.audiophile.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        t1=findViewById(R.id.name_profile);
        t2=findViewById(R.id.to);
        t3=findViewById(R.id.name);
        im1=findViewById(R.id.imageView);
        top = AnimationUtils.loadAnimation(this, R.anim.top);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);

        im1.setAnimation(top);
        t1.setAnimation(bottom);
        t2.setAnimation(top);
        t3.setAnimation(bottom);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}