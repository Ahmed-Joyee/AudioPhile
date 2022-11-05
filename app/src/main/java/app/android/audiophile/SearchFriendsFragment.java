package app.android.audiophile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import app.android.audiophile.databinding.ActivitySearchFriendsBinding;
import app.android.audiophile.databinding.FragmentChatBinding;
import app.android.audiophile.databinding.FragmentSearchFriendsBinding;

//import app.android.audiophile.databinding.FragmentSearchFriendsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFriendsFragment extends Fragment {



    FragmentSearchFriendsBinding binding;
    //    ArrayAdapter<String> adapter;
    SearchFriendsAdapter adapter;
    ArrayList<UsernameAndUId> names = new ArrayList<>();
    RecyclerView rec;
    private Bundle bundle;
    private String myUId;
    private final String TAG = "SearchFriendsFragment";


    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    private Bundle bundle;
    View view;

    public SearchFriendsFragment() {
        // Required empty public constructor
    }


    public static SearchFriendsFragment newInstance(String param1, String param2) {
        SearchFriendsFragment fragment = new SearchFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bundle = getArguments();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_search_friends, container, false);

        binding = FragmentSearchFriendsBinding.bind(view);

        Util util = new Util();
        myUId = util.getUId();
        rec = view.findViewById(R.id.recycler_user);
        binding.recyclerUser.setLayoutManager(new LinearLayoutManager(view.getContext()));
        buildRecyclerView();
        return binding.getRoot();
//        goToSearchFriends();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.topbar, menu);
        MenuItem search = menu.findItem(R.id.search_top);
        SearchView searchView = new SearchView(getContext());
        searchView = (SearchView)search.getActionView();
        searchView.setBackground(Drawable.createFromPath("@color/light_pink"));
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
        super.onCreateOptionsMenu(menu, inflater);
    }

//    public void goToSearchFriends(){
//        Log.wtf("asfdasd","sdfsadf");
//        Button button = view.findViewById(R.id.search_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.wtf("asfdasd","sdfsadf");
//                Intent intent = new Intent(getActivity(),SearchFriends.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
//    }

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
                    adapter.filterList(names);
                }else{
                    Log.d("SearchFriends", task.getException().getMessage());
                }
            }
        });
    }

    public void buildRecyclerView(){
        populateUserList();
        Log.d("SearchFriends", new Integer(names.size()).toString());
        adapter = new SearchFriendsAdapter(names, view.getContext(), bundle);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        rec.setHasFixedSize(true);
        rec.setLayoutManager(manager);

        rec.setAdapter(adapter);
    }

}