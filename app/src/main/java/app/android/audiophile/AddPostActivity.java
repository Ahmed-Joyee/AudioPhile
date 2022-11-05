package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddPostActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
//        getActionBar().hide();
        fAuth = FirebaseAuth.getInstance();
        ProgressDialog dialog=new ProgressDialog(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        

        Button addPostBtn=findViewById(R.id.addpost_btn);
        TextView puniv=findViewById(R.id.puniv);
        TextView username=findViewById(R.id.usName);
        TextView posttext=findViewById(R.id.posttext);
        ImageView imgselect=findViewById(R.id.imgselect);
        TextView postedMessage = findViewById(R.id.postedMessage);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fAuth.getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        posttext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description=posttext.getText().toString();
                if(!description.isEmpty())
                {
                    addPostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getApplication(),R.drawable.right_side_background));
                    addPostBtn.setTextColor(getApplication().getResources().getColor(R.color.light_pink));
                    addPostBtn.setEnabled(true);
//                    imgselect.setVisibility(View.VISIBLE);
                    postedMessage.setVisibility(View.INVISIBLE);
                }
                else
                {
                    addPostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getApplication(),R.drawable.left_side_background));
//                    addPostBtn.setTextColor(getApplication().getResources().getColor(R.color.);
//                    imgselect.setVisibility(View.GONE);
                    addPostBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeInMs = System.currentTimeMillis();
                String postId = String.valueOf(timeInMs);
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("posts").child(postId);
                databaseReference1.setValue(new Post(postId, "sdf", posttext.getText().toString(), fAuth.getCurrentUser().getUid(),timeInMs, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("AddPostActivity", "post added");
                        posttext.setText("");
                        postedMessage.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        imgselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeInMs = System.currentTimeMillis();
                String postId = String.valueOf(timeInMs);
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("posts").child(postId);
                databaseReference1.setValue(new Post(postId, "sdf", posttext.getText().toString(), fAuth.getCurrentUser().getUid(),timeInMs, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("AddPostActivity", "post added");
                    }
                });
            }
        });

    }
}