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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewCredActivity extends AppCompatActivity {

    String password;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_cred);
        Bundle bundle = getIntent().getExtras();
        email = (String) bundle.get("email");
        password = (String) bundle.get("password");
        goSetNewPassword();
    }

    public void goSetNewPassword(){
        Button setNewPassword = findViewById(R.id.setNewPasswordBtn);
        setNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = ((EditText)findViewById(R.id.setNewPassword)).getText().toString();
                String confirmNewPassword = ((EditText)findViewById(R.id.confirmNewPassword)).getText().toString();

                if(newPassword.equals(confirmNewPassword)){
                    FirebaseAuth fAuth;
                    fAuth = FirebaseAuth.getInstance();
                    fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser fUser = fAuth.getCurrentUser();
                                fUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task2) {
                                        if (task2.isSuccessful()) {
                                            Log.d("Password Update", "Successful");
                                            fAuth.signOut();
                                            Intent intent = new Intent(NewCredActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            //error
                                            try {
                                                throw task2.getException();
                                            }catch (Exception e){
                                                Log.d("NewCredActivity", e.toString());
                                                Toast.makeText(NewCredActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }
                                });
                            }else{
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Log.d("Password Update", e.toString());
                                    Toast.makeText(NewCredActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(NewCredActivity.this,"Passwords don't match.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}