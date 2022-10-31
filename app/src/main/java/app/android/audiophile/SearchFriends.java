package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

public class SearchFriends extends AppCompatActivity{

    ActivitySearchFriendsBinding binding;
//    ArrayAdapter<String> adapter;
    SearchFriendsAdapter adapter;
    ArrayList<UsernameAndUId> names = new ArrayList<>();
    RecyclerView rec;
    private Bundle bundle;
    private final String TAG = "SearchFriendsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();

        binding = ActivitySearchFriendsBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_search_friends);
        rec = findViewById(R.id.topBarLstView);

        Log.d(TAG, (String) bundle.getString("username"));
        buildRecyclerView();
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
        ArrayList<UsernameAndUId> filteredlist = new ArrayList<UsernameAndUId>();
        Log.d("Names size", new Integer(names.size()).toString());
        for (UsernameAndUId item : names) {
            if (item.getUsername().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            adapter.filterList(filteredlist);
        } else {
            adapter.filterList(filteredlist);
        }
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
                        String uId = "uId";
                        UsernameAndUId usernameAndUId= new UsernameAndUId( (String) mp.get(uId), (String) mp.get(username));
                        names.add(usernameAndUId);
                    }
                }else{
                    Log.d("SearchFriends", task.getException().getMessage());
                }
            }
        });
    }

    public void buildRecyclerView(){
        populateUserList();
        Log.d("SearchFriends", new Integer(names.size()).toString());
        adapter = new SearchFriendsAdapter(names, this, bundle);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rec.setHasFixedSize(true);
        rec.setLayoutManager(manager);

        rec.setAdapter(adapter);
    }
}