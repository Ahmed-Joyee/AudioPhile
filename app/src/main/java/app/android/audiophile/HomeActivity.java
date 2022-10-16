package app.android.audiophile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goOnlocal();
        goOnVoice();
    }

    public void goOnlocal(){
        Button goToLocal = findViewById(R.id.local_music);
        goToLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,PlayerActivity.class);
                startActivity(intent);
            }
        });
    }
    public void goOnVoice(){
        Button goToVoice = findViewById(R.id.Voice);
        goToVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,VoiceMessageActivity.class);
                startActivity(intent);
            }
        });
    }
}