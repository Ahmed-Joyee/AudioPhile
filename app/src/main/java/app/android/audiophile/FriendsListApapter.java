package app.android.audiophile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendsListApapter extends RecyclerView.Adapter<FriendsListApapter.ViewHolder> {

    ArrayList<UsernameAndUId> names;
    Context context;


    public FriendsListApapter(ArrayList<UsernameAndUId> names, Context context) {
        this.names = names;
        this.context = context;
    }
    @NonNull
    @Override
    public FriendsListApapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_friends_item,parent,false);
        return new FriendsListApapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsListApapter.ViewHolder holder, int position) {
        holder.titleTextView.setText(names.get(position).getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                Bundle bundle = new Bundle();
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
