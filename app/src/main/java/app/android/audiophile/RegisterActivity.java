package app.android.audiophile;

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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {


    FirebaseAuth fAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Button resendOtp;


    String email;
    String username;
    String fullName;
    String password;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fAuth = FirebaseAuth.getInstance();
        goLoginActivity();
        sendOtp();
    }

    public void goLoginActivity(){
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


    public void sendOtp(){
        Button btnOtp = findViewById(R.id.reg_btn);
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email =  ((EditText)findViewById(R.id.regEmail)).getText().toString();
                username = ((EditText)findViewById(R.id.regUserName)).getText().toString();
                fullName = ((EditText)findViewById(R.id.regFullName)).getText().toString();
                password = ((EditText)findViewById(R.id.regPassword)).getText().toString();
                mobile = ((EditText)findViewById(R.id.regPhoneNumber)).getText().toString();
                mobile = "+88"+mobile;
                Log.wtf("mobile", mobile);
                if(email=="" || password=="" || mobile == "" || fullName=="" || username==""){
                    Toast.makeText(RegisterActivity.this, "Information is incorrect.",Toast.LENGTH_LONG).show();
                }

                //username or email or phone unique na hoile
                else if(email=="1"){
                    Toast.makeText(RegisterActivity.this, "This email is already in use.",Toast.LENGTH_LONG).show();
                }
                else if(username=="1"){
                    Toast.makeText(RegisterActivity.this, "This username is taken.",Toast.LENGTH_LONG).show();
                }
                else if(mobile=="1"){
                    Toast.makeText(RegisterActivity.this, "This phone number is already in use.",Toast.LENGTH_LONG).show();
                }
                else{
                    verifyPhoneNumber(mobile);
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
                try{
                    throw e;
                }catch (Exception ea){
                    Toast.makeText(RegisterActivity.this,ea.toString(),Toast.LENGTH_SHORT).show();
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
                bundle.putString("verificationId",verificationId);
                bundle.putString("email", email);
                bundle.putString("password", password);
                Intent intent = new Intent(RegisterActivity.this,OTPActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }


    public void verifyPhoneNumber(String number){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth)
                .setActivity(this)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void authinticateUser(PhoneAuthCredential credential){
        fAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(RegisterActivity.this,"success",Toast.LENGTH_SHORT).show();

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(RegisterActivity.this,AccEmailActivity.class);
                                    startActivity(intent);
                                }
                                else {

// Error occurred
                                    try {
                                        throw task.getException();
                                    }
                                    catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });

                startActivity(new Intent(RegisterActivity.this, AccEmailActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}