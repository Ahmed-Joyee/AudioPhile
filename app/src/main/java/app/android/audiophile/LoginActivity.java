package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
                if (check(username, password) == true) {

                    fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Login Successful
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            } else {

                                //Error Occurred
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Wrong Credential. Please try again.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });


                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "username or password incorrect.", Toast.LENGTH_SHORT).show();
                    SimpleDialog simpleDialog = new SimpleDialog("Login Error", "Invalid username or password. Please try again.");
                    simpleDialog.show(getSupportFragmentManager(), "login error dialog");
                    return;
                }
            }
        });
    }

//    private void wrongPasswordNotification() {
//
//    }

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
                finish();
            }
        });

    }

    private void goForgotActivity() {
        btnForgot = findViewById(R.id.forgot_pass);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private boolean check(String username, String password) {
        return true;
    }

}