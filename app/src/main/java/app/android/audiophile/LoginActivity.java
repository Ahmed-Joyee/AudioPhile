package app.android.audiophile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;

import app.android.audiophile.functions.SimpleDialog;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    TextInputLayout etUsername;
    TextInputLayout etPassword;
    Button btnLogin;
    Button btnRegister;
    Button btnForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hide action bar
        getSupportActionBar().hide();



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
                if(check(username,password)==true){
                    Intent intent = new Intent(LoginActivity.this,AccEmailActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"username or password incorrect.",Toast.LENGTH_SHORT).show();
                    SimpleDialog simpleDialog = new SimpleDialog("Login Error",
                            "Invalid username or password. Please try again.");
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
    private boolean check(String username, String password){
        return true;
    }

}