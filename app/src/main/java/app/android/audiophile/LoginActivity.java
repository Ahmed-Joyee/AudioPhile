package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import app.android.audiophile.functions.SimpleDialog;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    TextInputLayout etUsername;
    TextInputLayout etPassword;
    Button btnLogin;
    Button btnRegister;
    Button btnForgot;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        // hide action bar
        getSupportActionBar().hide();
        fAuth = FirebaseAuth.getInstance();


        goForgotActivity();
        loginUser();
        goRegisterActivity();
    }

    private void loginUser() {
        btnLogin = findViewById(R.id.login);
        etUsername = findViewById(R.id.login_username);
        etPassword = findViewById(R.id.login_password);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();
                Log.d(TAG, username+" " + password);
                if(!username.equals("") && !password.equals("")){
                    Log.d(TAG, "comes here");
                    fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Login Successful
                                if (fAuth.getCurrentUser().isEmailVerified()) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fAuth.getCurrentUser().getUid());
                                    Map<String, Object> mp = new HashMap<>();
                                    mp.put("isEmailVerified",new Boolean(true));
                                    mp.put("isPhoneVerified", new Boolean(true));
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);
                                            databaseReference.updateChildren(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    user.uIdByEmail();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ref.child("Users").child(fuser.getUid()).child("password").setValue(password);


                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    Bundle bundle = new Bundle();
                                    ref.child("Users").child(fuser.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String userUid = (String) task.getResult().getValue();
                                                bundle.putString("userUid", fAuth.getCurrentUser().getUid());
                                                bundle.putString("username", userUid);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                } else {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fAuth.getCurrentUser().getUid());
                                    databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(task.isSuccessful()){
                                                User user = task.getResult().getValue(User.class);
                                                if (user.isEmailVerified || user.isPhoneVerified) {
                                                    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                    ref.child("Users").child(fuser.getUid()).child("password").setValue(password);


                                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    ref.child("Users").child(fuser.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                String userUid = (String) task.getResult().getValue();
                                                                bundle.putString("userUid", fAuth.getCurrentUser().getUid());
                                                                bundle.putString("username", userUid);
                                                                intent.putExtras(bundle);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(LoginActivity.this, "Please Verify Your Email", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Please Verify Your Email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                } else{
                                    try {
                                        throw task.getException();
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "Wrong Credential. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Type Email and Passsword", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void goRegisterActivity() {
        btnRegister = findViewById(R.id.login_register_instead);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

    }

    private void goForgotActivity() {
        btnForgot = findViewById(R.id.forgot_pass);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SelectResetPassActivity.class);
                startActivity(i);
            }
        });

    }

    private boolean check(String username, String password) {
        return true;
    }

}