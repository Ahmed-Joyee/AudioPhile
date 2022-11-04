package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class ForgotPassOtpActivity extends AppCompatActivity {

    String verificationId;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_otp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        verificationId = (String) bundle.get("verificationId");
        email = (String) bundle.get("email");
        password = (String) bundle.get("password");
        goResetPassword();
    }

    public void goResetPassword(){
        Button goNewCred = findViewById(R.id.verifyForgotPassOtp);
        goNewCred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = ((EditText)findViewById(R.id.pinView)).getText().toString();
                PhoneAuthCredential credential1 = PhoneAuthProvider.getCredential(verificationId, otp);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signInWithCredential(credential1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(ForgotPassOtpActivity.this, NewCredActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("email",email);
                        bundle.putString("password",password);
                        intent.putExtras(bundle);
                        fAuth.signOut();
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassOtpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}