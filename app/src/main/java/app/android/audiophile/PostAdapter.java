package app.android.audiophile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.android.audiophile.databinding.PostSampleBinding;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewholder>{

    ArrayList<Post> list;
    Context context;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_sample, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Post model = list.get(position);

        holder.binding.like.setText(model.getPostLike() + "");
        String pst = model.getPostDescription();

        if (pst.equals("")) {
            holder.binding.postdesc.setVisibility(View.GONE);
        } else {
            holder.binding.postdesc.setText(model.getPostDescription());
            holder.binding.postdesc.setVisibility(View.VISIBLE);
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.binding.usName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //line 89 from Qmark



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {


        PostSampleBinding binding;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            binding=PostSampleBinding.bind(itemView);
        }
    }

}
