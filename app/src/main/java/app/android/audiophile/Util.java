package app.android.audiophile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Util {

    private String username;
    public String getUId(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return  firebaseAuth.getUid();
    }

    public String getUsername(String uId){
        findUsername(uId);
        return username;
    }

    public void findUsername(String uId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username =  user.username;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String currentData(){
        Calendar calendar = Calendar.getInstance();
        return sdf().format(calendar.getTimeInMillis());
    }

    private SimpleDateFormat sdf(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss a", Locale.US);
        return  simpleDateFormat;
    }

    public void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view==null){
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    public Bundle putInBundle(String username, String UId){
//
//    }
}
