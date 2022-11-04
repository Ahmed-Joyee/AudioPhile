package app.android.audiophile;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {


    private String videoId = "";
    private final  String apiKey = "AIzaSyAgP9YnHS0pWlD3Vl6FIHAULCEEeIdH-gU";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        EditText text = view.findViewById(R.id.searchForSongs);
        Button btn = view.findViewById(R.id.searchForSongsButton);
        LinearLayout linearLayout = view.findViewById(R.id.searchLinearLayout);
        TextView textView = view.findViewById(R.id.searchForSongsResult);
        RecyclerView recyclerView = view.findViewById(R.id.suggestionRV);


        ArrayList<String>nameOfSongs = new ArrayList<>();
        SuggestionAdapter adapter = new SuggestionAdapter(nameOfSongs, view.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!text.getText().toString().equals("")){
                    btn.setVisibility(View.VISIBLE);
//                    linearLayout.setVisibility(View.INVISIBLE);
                }
                else {
                    btn.setVisibility(View.INVISIBLE);
//                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn.getVisibility() == View.VISIBLE) {
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
                    String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q=" + text.getText().toString() + "&key=AIzaSyAgP9YnHS0pWlD3Vl6FIHAULCEEeIdH-gU";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray items = response.getJSONArray("items");
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject item = items.getJSONObject(i);
                                    JSONObject snippet = item.getJSONObject("snippet");
                                    String title = snippet.getString("title");
                                    Log.d("ExplorerFragment", title);
                                    linearLayout.setVisibility(View.VISIBLE);
                                    textView.setText("Are you searching for :" + title);
                                    JSONObject id = item.getJSONObject("id");
                                    videoId = id.getString("videoId");
//                                    linearLayout.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(view.getContext(), "Fail to get data..", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(jsonObjectRequest);
                }
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameOfSongs.clear();
                if(linearLayout.getVisibility()==View.VISIBLE){
                    String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=5&relatedToVideoId="+ videoId + "&type=video&key=AIzaSyAgP9YnHS0pWlD3Vl6FIHAULCEEeIdH-gU";
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
//                    String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q=" + text.getText().toString() + "&key=AIzaSyAgP9YnHS0pWlD3Vl6FIHAULCEEeIdH-gU";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray items = response.getJSONArray("items");
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject item = items.getJSONObject(i);
                                    JSONObject snippet = item.getJSONObject("snippet");
                                    String title = snippet.getString("title");
                                    Log.d("ExplorerFragment", title);
                                    nameOfSongs.add(title);
                                    JSONObject id = item.getJSONObject("id");
                                    videoId = id.getString("videoId");
                                }
                                adapter.filterList(nameOfSongs);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(view.getContext(), "Fail to get data..", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(jsonObjectRequest);

                }
            }
        });
        return view;
    }
}