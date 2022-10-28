package app.android.audiophile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.zip.Inflater;

import app.android.audiophile.databinding.ActivitySearchFriendsBinding;
import app.android.audiophile.databinding.FragmentSearchFriendsBinding;

public class SearchFriends extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ActivitySearchFriendsBinding binding;
    ArrayAdapter<String> adapter;
    List<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        binding = ActivitySearchFriendsBinding.inflate(getLayoutInflater());
        ListView rec = findViewById(R.id.topBarLstView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        rec.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.topbar,menu);
        MenuItem search = menu.findItem(R.id.search_top);
        SearchView searchView = (SearchView)search.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
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
}