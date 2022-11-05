package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AccEmailActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    String resetByPhone;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String emailL;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_email);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        resetByPhone = (String) bundle.get("resetByPhone");
        fAuth = FirebaseAuth.getInstance();
        goNewCredActivity();
    }

    public void goNewCredActivity() {
        Button sendToThisEmail = findViewById(R.id.forget_password_next_btn);
        sendToThisEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText) findViewById(R.id.forgetPassEmail)).getText().toString();
                if (resetByPhone.equals("0")) {
                    checkEmailExistsOrNot(email);
                } else {
                    fAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.getResult().getSignInMethods().size() == 0) {
                                //not a valid email
                                Toast.makeText(AccEmailActivity.this, "No user found.", Toast.LENGTH_SHORT).show();
                            } else {
                                StringBuilder s = new StringBuilder(email);
                                for (int i = 0; i < email.length(); i++) {
                                    if (email.charAt(i) == '.') {
                                        s.setCharAt(i, ',');
                                    }
                                }
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("Users").child("uIdByEmail").child(s.toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            //error
                                        } else {
                                            Log.d("Uid", String.valueOf(task.getResult().getValue()));
                                            mDatabase.child("Users").child(String.valueOf(task.getResult().getValue())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task2) {
                                                    HashMap<String, Object> user = (HashMap<String, Object>) task2.getResult().getValue();

                                                    Log.d("Mobile num", (String) user.get("mobile"));
                                                    emailL = (String) user.get("email");
                                                    password = (String) user.get("password");
                                                    verifyPhoneNumber((String) user.get("mobile"));
                                                    Log.d("email and password", email + "  " + password);
                                                }
                                            });
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
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authinticateUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                try {
                    throw e;
                } catch (Exception ea) {
                    Toast.makeText(AccEmailActivity.this, ea.toString(), Toast.LENGTH_SHORT).show();
                }
                //handle errors
                //2-kinds
                //1. massage limit exceeded
                //
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(AccEmailActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccEmailActivity.this, ForgotPassOtpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("email", emailL);
                bundle.putString("password", password);
                bundle.putString("verificationId", s);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }


    public void verifyPhoneNumber(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth).setActivity(this).setPhoneNumber(number).setTimeout(60L, TimeUnit.SECONDS).setCallbacks(callbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void authinticateUser(PhoneAuthCredential credential) {
        Intent intent = new Intent(AccEmailActivity.this, NewCredActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", emailL);
        bundle.putString("password", password);
        intent.putExtras(bundle);
        startActivity(intent);
        //new activity te jabo
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