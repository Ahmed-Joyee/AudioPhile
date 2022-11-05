package app.android.audiophile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedFragment() {
        // Required empty public constructor
    }


    RecyclerView rv;
    ArrayList<Post> dashboardList;
    ImageView img;
    ImageView addpost;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
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
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addpost=getView().findViewById(R.id.addpost);
        rv=getView().findViewById(R.id.rv);
        img=getView().findViewById(R.id.feed);

        dashboardList=new ArrayList<>();
        PostAdapter pp=new PostAdapter(dashboardList,getContext());
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),DividerItemDecoration.VERTICAL));
        rv.setNestedScrollingEnabled(true);
        rv.setAdapter(pp);
        dashboardList.clear();
        Map<String, Boolean> map=new HashMap<String,Boolean>();
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    UsernameAndUId usernameAndUId = data.getValue(UsernameAndUId.class);
                    map.put(usernameAndUId.getuId(), true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child("posts").orderByChild("postedAt").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                dashboardList.clear();
                for(DataSnapshot dataSnapshot: snapshot1.getChildren())
                {
                    Post post=dataSnapshot.getValue(Post.class);
                    if(map.containsKey(post.getPostedBy())==true){
                        dashboardList.add(post);
                    }
                }
                Collections.reverse(dashboardList);
                            pp.notifyDataSetChanged();
//                pp.filter(dashboardList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AddPostActivity.class);
                startActivity(intent);
            }
        });


    }
}