package app.android.audiophile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


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

        etUsername = findViewById(R.id.login_username);
        etPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login);
        btnRegister = findViewById(R.id.login_register_instead);
        btnForgot = findViewById(R.id.forgot_pass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();

                loginUser(username, password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegisterActivity();
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goForgotActivity();
            }
        });
    }

    private void loginUser(String username, String password) {

    }

    private void wrongPasswordNotification() {
        SimpleDialog simpleDialog = new SimpleDialog("Login Error",
                "Invalid username or password. Please try again.");
        simpleDialog.show(getSupportFragmentManager(), "login error dialog");
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goRegisterActivity() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

    private void goForgotActivity() {
        Intent i = new Intent(this, ForgotActivity.class);
        startActivity(i);
        finish();
    }

}