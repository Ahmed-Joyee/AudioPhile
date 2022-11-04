package app.android.audiophile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private ArrayList<String>nameOfSongs;
    Context context;

    public SuggestionAdapter(ArrayList<String> nameOfSongs, Context context) {
        this.nameOfSongs = nameOfSongs;
        this.context = context;
    }

    @NonNull
    @Override
    public SuggestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_friends_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionAdapter.ViewHolder holder, int position) {
        holder.titleTextView.setText(nameOfSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return nameOfSongs.size();
    }

    public void filterList(ArrayList<String> filterlist) {
        nameOfSongs = filterlist;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.nameOfUser);
        }
    }
}
