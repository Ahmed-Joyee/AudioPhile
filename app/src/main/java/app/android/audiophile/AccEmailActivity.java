package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class AccEmailActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_email);

        fAuth = FirebaseAuth.getInstance();
        goNewCredActivity();
    }

    public void goNewCredActivity() {
        Button sendToThisEmail = findViewById(R.id.forget_password_next_btn);
        sendToThisEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText) findViewById(R.id.forgetPassEmail)).getText().toString();
                checkEmailExistsOrNot(email);
            }
        });
    }

    public void checkEmailExistsOrNot(String eml) {

        fAuth.fetchSignInMethodsForEmail(eml).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0) {
                    //not a valid email
                    Toast.makeText(AccEmailActivity.this, "No user has this email", Toast.LENGTH_SHORT).show();
                } else {
                    fAuth.sendPasswordResetEmail(eml).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccEmailActivity.this, "Reset email instructions sent to " + eml, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AccEmailActivity.this, eml + " does not exist", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccEmailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}