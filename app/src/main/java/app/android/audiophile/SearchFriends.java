package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import app.android.audiophile.databinding.ActivitySearchFriendsBinding;
import app.android.audiophile.databinding.FragmentSearchFriendsBinding;

public class SearchFriends extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ActivitySearchFriendsBinding binding;
    ArrayAdapter<String> adapter;
    List<String> names = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_friends);
        binding = ActivitySearchFriendsBinding.inflate(getLayoutInflater());
        ListView rec = findViewById(R.id.topBarLstView);
        populateUserList();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        rec.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
//        menuInflater.inflate(R.menu.topbar,menu);
        getMenuInflater().inflate(R.menu.topbar, menu);
        MenuItem search = menu.findItem(R.id.search_top);
        SearchView searchView = new SearchView(this);
        searchView = (SearchView)search.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query != null){
            adapter.getFilter().filter(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText != null){
            adapter.getFilter().filter(newText);
        }
        return true;
    }

    public void populateUserList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Map<String, Object> mp = documentSnapshot.getData();
                        String username = "username";
                        names.add((String) mp.get(username));
                    }
                }else{
                    Log.wtf("SearchFriends", task.getException());
                }
            }
        });
    }
}