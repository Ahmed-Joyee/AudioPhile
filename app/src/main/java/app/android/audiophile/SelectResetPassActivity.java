package app.android.audiophile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectResetPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_reset_pass);

        goResetUsingEmail();
        goResetUsingMobile();
    }

    public void goResetUsingEmail(){
        Button resetUsingEmail = findViewById(R.id.resetUsingEmail);
        resetUsingEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectResetPassActivity.this, AccEmailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("resetByPhone","0");
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    public void goResetUsingMobile(){
        Button resetUsingMobile = findViewById(R.id.resetUsingMobile);
        resetUsingMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectResetPassActivity.this, AccEmailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("resetByPhone","1");
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }
}