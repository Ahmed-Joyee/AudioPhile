package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OTPActivity extends AppCompatActivity {


    String username;
    String password;
    String email;
    String mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Bundle bundle = getIntent().getExtras();
        String verificationId = (String) bundle.get("verificationId");
        email = (String) bundle.get("email");
        password = (String) bundle.get("password");
        username = (String) bundle.get("username");
        mobile = (String) bundle.get("mobile");
        goSomewhere(verificationId);
    }

    public void goSomewhere(String verificationId) {
        Button verify = findViewById(R.id.verifyOtp);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = ((PinView) findViewById(R.id.pin_view)).getText().toString();
                if (otp.isEmpty()) {
                    //show error
                    return;
                }
                PhoneAuthCredential credential1 = PhoneAuthProvider.getCredential(verificationId, otp);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signInWithCredential(credential1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(OTPActivity.this, "success", Toast.LENGTH_SHORT).show();

                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {


                                    FirebaseUser uu = FirebaseAuth.getInstance().getCurrentUser();
                                    User user = new User(email, username, password, uu.getUid(), mobile);
                                    user.InsertIntoDb();


                                    Intent intent = new Intent(OTPActivity.this, AccEmailActivity.class);
                                    startActivity(intent);
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}