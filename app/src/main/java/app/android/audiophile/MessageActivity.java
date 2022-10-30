package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import app.android.audiophile.databinding.ActivityMessageBinding;
import app.android.audiophile.databinding.LeftItemLayoutBinding;
import app.android.audiophile.databinding.RightItemLayoutBinding;

public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding binding;
    private String myUId, hisUId, chatUId, hisUsername;
    private Util util;
    private FirebaseRecyclerAdapter<MessageModel, ViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_message, null, false);
        setContentView(binding.getRoot());

        util = new Util();
        myUId = util.getUId();
        hisUId = getIntent().getStringExtra("hisUId");
        hisUsername = getIntent().getStringExtra("hisUsername");
        Log.d("MessageActivity", hisUsername);
        set();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.msgText.getText().toString().trim();

                if(message.isEmpty()){
                    Toast.makeText(MessageActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
                util.hideKeyboard(MessageActivity.this);
            }
        });

        if(chatUId==null){
            checkchat(hisUId);
        }
    }

    private void set(){
        binding.toolbar.recipientUsername.setText(hisUsername);
        Log.d("MessageActivity", binding.toolbar.recipientUsername.getText().toString());
    }

    private void checkchat(final String hisUId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ChatList").child(myUId);
        Query query = databaseReference.orderByChild("member").equalTo(hisUId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.child("member").getValue().toString();
                        if(id.equals(hisUId)){
                            chatUId = ds.getKey();
                            readMessages(chatUId);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createChat(String msg){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ChatList").child(myUId);
        chatUId = databaseReference.push().getKey();
        ChatListModel chatListModel = new ChatListModel(chatUId, util.currentData(), msg, hisUId);
        databaseReference.child(chatUId).setValue(chatListModel);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ChatList").child(hisUId);
//        chatUId = databaseReference.push().getKey();
        ChatListModel chatListModel1 = new ChatListModel(chatUId, util.currentData(), msg, myUId);
        databaseReference.child(chatUId).setValue(chatListModel1);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("chat").child(chatUId);
        MessageModel messageModel = new MessageModel(myUId, hisUId, msg, util.currentData(), "text", hisUsername);
        databaseReference.push().setValue(messageModel);

        readMessages(chatUId);
    }

    private void sendMessage(String msg){
        if(chatUId==null){
            createChat(msg);
        }else{
            MessageModel messageModel = new MessageModel(myUId, hisUId, msg, util.currentData(), "text", hisUsername);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("chat").child(chatUId);
            databaseReference.push().setValue(messageModel);
        }
    }

    private void readMessages(String chatUId){
        Query query = FirebaseDatabase.getInstance().getReference("Users").child("chat").child(chatUId);
        FirebaseRecyclerOptions<MessageModel> options = new FirebaseRecyclerOptions.Builder<MessageModel>().setQuery(query, MessageModel.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MessageModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MessageModel model) {
                holder.viewDataBinding.setVariable(BR.message,model);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if(viewType==0){
                    ViewDataBinding viewDataBinding = RightItemLayoutBinding.inflate(LayoutInflater.from(getBaseContext()), parent, false);
                    return new ViewHolder(viewDataBinding);
                }else{
                    ViewDataBinding viewDataBinding = LeftItemLayoutBinding.inflate(LayoutInflater.from(getBaseContext()), parent, false);
                    return  new ViewHolder(viewDataBinding);
                }
            }

            @Override
            public int getItemViewType(int position) {
                MessageModel messageModel = getItem(position);
                if(myUId.equals(messageModel.getSender())){
                    return  0;
                }else return  1;
            }
        };
        binding.recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMessage.setHasFixedSize(false);
        binding.recyclerViewMessage.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ViewDataBinding viewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }
    }
}