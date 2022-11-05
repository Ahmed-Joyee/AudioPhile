package app.android.audiophile;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextInputLayout username;
    Button btn;
    String em, us, ph, pass;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");
        progressDialog.setTitle("Fetching Data...");
        progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.show();

        btn = getActivity().findViewById(R.id.up_btn);
        username = getActivity().findViewById(R.id.up_username);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query check = FirebaseDatabase.getInstance().getReference("Users");

        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    em = snapshot.child(uid).child("email").getValue(String.class);
                    us = snapshot.child(uid).child("username").getValue(String.class);

                    username.getEditText().setText(us);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setCanceledOnTouchOutside(false);
                //progressDialog.show();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String user = username.getEditText().getText().toString().trim();

                Log.i(TAG, "onClick: " + " " + us + " " + em);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                String noWhiteSpace = "\\A\\w{4,15}\\z";

                username.setErrorEnabled(false);

                if(TextUtils.isEmpty(user))
                {
                    username.setError("Username field cannot be empty");
                }
                else if (!user.matches(noWhiteSpace)) {
                    username.setError("Remove white spaces, length (4-15)");
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("username").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                Map<String, Object> mp = documentSnapshot.getData();
                                                Log.d("SettingsFragment", mp.get("uId").toString());
                                                if(mp.get("uId").toString().equals(uid)){
                                                    Map<String,Object>mp2 = new HashMap<>();
                                                    mp2.put("uId", uid);
                                                    mp2.put("username", user);
                                                    Log.d("SettingsFragment", documentSnapshot.getId());
                                                    db.collection("users").document(documentSnapshot.getId()).update(mp2);
                                                }
                                            }
                                        }
                                        else{
                                            Log.d("SettingsFragment", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                                Toast.makeText(getContext(), "Successfully username changed, you can go back now", Toast.LENGTH_SHORT).show();
                                //successful
                            }
                            else{
                                //mara
                            }
                        }
                    });

                }
            }
        });

    }

    private void changeUser(String user) {
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        UserModel helperClass = new UserModel(user, em, pass,currentuser);
        FirebaseDatabase.getInstance().getReference("Users").child(currentuser).setValue(helperClass);

        us = user;

        UserModel helperClass2 = new UserModel(em, pass, user);
        FirebaseDatabase.getInstance().getReference("emails").child(user).setValue(helperClass2);

        Toast.makeText(getActivity(), "Changed Profile", Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(getActivity(),UserProfileActivity.class));
        NavigationView navigationView = getActivity().findViewById(R.id.prof_navigation_view);

        View headerView = navigationView.getHeaderView(0);

        TextView nav_user_name = headerView.findViewById(R.id.nav_header_user_name);
        TextView nav_name = headerView.findViewById(R.id.nav_header_name);
        nav_user_name.setText(user );
        nav_name.setText(em);
    }
}