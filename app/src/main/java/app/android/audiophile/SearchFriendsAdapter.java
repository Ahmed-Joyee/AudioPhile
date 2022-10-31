package app.android.audiophile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchFriendsAdapter extends RecyclerView.Adapter<SearchFriendsAdapter.ViewHolder> {

    ArrayList<UsernameAndUId>names;
    Context context;
    Bundle bundle;


    public SearchFriendsAdapter(ArrayList<UsernameAndUId> names, Context context, Bundle _bundle) {
        this.names = names;
        this.context = context;
        this.bundle = _bundle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_friends_item,parent,false);
        return new SearchFriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTextView.setText(names.get(position).getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FriendProfileActivity.class);
                bundle.putString("hisUsername", names.get(position).getUsername());
                bundle.putString("hisUId",names.get(position).getuId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return names.size();
    }

    public void filterList(ArrayList<UsernameAndUId> filterlist) {
        names = filterlist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.nameOfUser);
        }
    }


}
