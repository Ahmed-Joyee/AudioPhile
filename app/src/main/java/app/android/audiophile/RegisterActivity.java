package app.android.audiophile;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {


    FirebaseAuth fAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;


    String email;
    String username;
    String fullName;
    String password;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        fAuth = FirebaseAuth.getInstance();
        goLoginActivity();
        sendOtp();
        sendLink();
    }

    public void goLoginActivity() {
        Button btnLogin = findViewById(R.id.reg_login_btn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void sendLink(){
        Button btnLink = findViewById(R.id.reg_btn_email);
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = ((EditText) findViewById(R.id.regEmail)).getText().toString();
                username = ((EditText) findViewById(R.id.regUserName)).getText().toString();
                fullName = ((EditText) findViewById(R.id.regFullName)).getText().toString();
                password = ((EditText) findViewById(R.id.regPassword)).getText().toString();
                mobile = ((EditText) findViewById(R.id.regPhoneNumber)).getText().toString();
                mobile = "+1" + mobile;
                Log.wtf("mobile", mobile);
                if (email == "" || password == "" || mobile == "" || fullName == "" || username == "") {
                    Toast.makeText(RegisterActivity.this, "Information is incorrect.", Toast.LENGTH_LONG).show();
                }

                //username or email or phone unique na hoile
                else {

                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

                                if (auth == null) {
                                    //unknown error
                                } else {
                                    FirebaseUser uu = FirebaseAuth.getInstance().getCurrentUser();
                                    User user = new User(email, username, password, uu.getUid(), mobile, false, false);
                                    user.InsertIntoDb();
                                    user.uIdByEmail();
                                    user.usernameByEmail();
                                    Map<String, Object> xx = new HashMap<>();
                                    xx.put("username",username);
                                    xx.put("uId", uu.getUid());
                                    putInFirebaseStore(xx);
                                    auth.sendEmailVerification();
                                    Toast.makeText(RegisterActivity.this, "Verification Link Sent To Email", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Log.d("problem", e.toString());
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                }
            }
        });
    }

    public void sendOtp() {
        Button btnOtp = findViewById(R.id.reg_btn_otp);
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = ((EditText) findViewById(R.id.regEmail)).getText().toString();
                username = ((EditText) findViewById(R.id.regUserName)).getText().toString();
                fullName = ((EditText) findViewById(R.id.regFullName)).getText().toString();
                password = ((EditText) findViewById(R.id.regPassword)).getText().toString();
                mobile = ((EditText) findViewById(R.id.regPhoneNumber)).getText().toString();
                mobile = "+1" + mobile;
                Log.wtf("mobile", mobile);
                if (email == "" || password == "" || mobile == "" || fullName == "" || username == "") {
                    Toast.makeText(RegisterActivity.this, "Information is incorrect.", Toast.LENGTH_LONG).show();
                }

                //username or email or phone unique na hoile
                else {

                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                                if (auth == null) {
                                    //unknown error
                                } else {
                                    auth.delete();
                                    verifyPhoneNumber(mobile);
                                }
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Log.d("problem", e.toString());
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
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
                    Toast.makeText(RegisterActivity.this, ea.toString(), Toast.LENGTH_SHORT).show();
                }
                //handle errors
                //2-kinds
                //1. massage limit exceeded
                //
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
                Bundle bundle = new Bundle();
                bundle.putString("verificationId", verificationId);
                bundle.putString("email", email);
                bundle.putString("password", password);
                bundle.putString("username", username);
                bundle.putString("mobile", mobile);
                Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
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

        fAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(RegisterActivity.this, "success", Toast.LENGTH_SHORT).show();

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //Database e user er info save kortasi
                            //uId hoilo key
                            FirebaseUser uu = FirebaseAuth.getInstance().getCurrentUser();
                            User user = new User(email, username, password, uu.getUid(), mobile, true, true);
                            user.InsertIntoDb();
                            user.uIdByEmail();
                            user.usernameByEmail();
                            Map<String, Object> xx = new HashMap<>();
                            xx.put("username",username);
                            xx.put("uId", uu.getUid());
                            putInFirebaseStore(xx);
                            Intent intent = new Intent(RegisterActivity.this, AccEmailActivity.class);
                            startActivity(intent);
                        } else {
                            // Error occurred
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void putInFirebaseStore(Map<String,Object> xx){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").add(xx).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //success
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failure
            }
        });
    }
}