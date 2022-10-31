package app.android.audiophile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import app.android.audiophile.databinding.ActivityMessageBinding;
import app.android.audiophile.databinding.LeftAudioItemLayoutBinding;
import app.android.audiophile.databinding.LeftItemLayoutBinding;
import app.android.audiophile.databinding.RightAudioItemLayoutBinding;
import app.android.audiophile.databinding.RightItemLayoutBinding;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding binding;
    private String myUId, hisUId, chatUId, hisUsername, audioPath;
    private Util util;
    private FirebaseRecyclerAdapter<MessageModel, ViewHolder> firebaseRecyclerAdapter;
    private AudioRecorder audioRecorder;
    private File recordFile;

    private MediaRecorder mediaRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_message, null, false);
        setContentView(binding.getRoot());
        audioRecorder = new AudioRecorder();
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
                    binding.msgText.setText("");
                }
                util.hideKeyboard(MessageActivity.this);
            }
        });

        if(chatUId==null){
            checkchat(hisUId);
        }

        binding.msgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0){
                    binding.btnSend.setVisibility(View.GONE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                }else{
                    binding.btnSend.setVisibility(View.VISIBLE);
                    binding.recordButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inItView();
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
//                holder.viewDataBinding.setVariable(BR.message,model);
                int x = getItemViewType(position);
                if(x==0 || x==1) {
                    holder.viewDataBinding.setVariable(BR.message, model);
                }
                else{
                    holder.voicePlayerView.setAudio(model.message);
                }
            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewDataBinding viewDataBinding = null;
                switch (viewType){
                    case 0:
                        viewDataBinding = RightItemLayoutBinding.inflate(LayoutInflater.from(MessageActivity.this), parent, false);
                        break;
                    case 100:
                        viewDataBinding = RightAudioItemLayoutBinding.inflate(LayoutInflater.from(MessageActivity.this), parent, false);
                        break;
                    case 1:
                        viewDataBinding = LeftItemLayoutBinding.inflate(LayoutInflater.from(MessageActivity.this), parent, false);
                        break;
                    case 200:
                        viewDataBinding = LeftAudioItemLayoutBinding.inflate(LayoutInflater.from(MessageActivity.this), parent, false);
                        break;
                }
                return new ViewHolder(viewDataBinding);
            }

            @Override
            public int getItemViewType(int position) {
                MessageModel messageModel = getItem(position);
                if(myUId.equals(messageModel.getSender())){
                    if(messageModel.getType().equals("recording"))return 100;
                    else return  0;
                }else{
                    if(messageModel.getType().equals("recording"))return 200;
                    else return 1;
                }
            }
        };
        binding.recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMessage.setHasFixedSize(false);
        binding.recyclerViewMessage.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ViewDataBinding viewDataBinding;
        private VoicePlayerView voicePlayerView;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
            if(viewDataBinding instanceof RightAudioItemLayoutBinding){
                voicePlayerView = ((RightAudioItemLayoutBinding) viewDataBinding).voicePlayerView;
            }
            if(viewDataBinding instanceof LeftAudioItemLayoutBinding){
                voicePlayerView = ((LeftAudioItemLayoutBinding) viewDataBinding).voicePlayerView;
            }
        }
    }

    private void inItView(){
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordButton.setListenForRecord(false);

        binding.recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecordingOk(MessageActivity.this)){
                    binding.recordButton.setListenForRecord(true);
                }else{
                    requestRecording(MessageActivity.this);
                }
            }
        });

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");

                recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".3gp");
                try {
                    audioRecorder.start(recordFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.recordView.setVisibility(View.VISIBLE);
                binding.messageLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");

                stopRecording(true);
                binding.recordButton.setVisibility(View.GONE);
                binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
                    @Override
                    public void onAnimationEnd() {
                        binding.recordButton.setVisibility(View.VISIBLE);
                        binding.recordView.setVisibility(View.GONE);
                        binding.messageLayout.setVisibility(View.VISIBLE);
                    }
                });


            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                //limitReached to determine if the Record was finished when time limit reached.
                stopRecording(false);
                String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");

                binding.recordView.setVisibility(View.GONE);
                binding.messageLayout.setVisibility(View.VISIBLE);
                sendRecordingMessage();

                Log.d("RecordTime", time);
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                stopRecording(true);
                binding.recordView.setVisibility(View.GONE);
                binding.messageLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public boolean isRecordingOk(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED;
    }
    public void requestRecording(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void sendRecordingMessage(){
        // Create a Cloud Storage reference from the app
        if(chatUId==null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ChatList").child(myUId);
            chatUId = databaseReference.push().getKey();
            ChatListModel chatListModel = new ChatListModel(chatUId, util.currentData(), "A", hisUId);
            databaseReference.child(chatUId).setValue(chatListModel);

            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("ChatList").child(hisUId);
//        chatUId = databaseReference.push().getKey();
            ChatListModel chatListModel1 = new ChatListModel(chatUId, util.currentData(), "A", myUId);
            databaseReference.child(chatUId).setValue(chatListModel1);
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(chatUId + "Media/Recording"+util.getUId() + "/" + System.currentTimeMillis());
        Uri audioFile = Uri.fromFile(recordFile);
        storageRef.putFile(audioFile).addOnSuccessListener(success ->{
            Task<Uri> audioUrl = success.getStorage().getDownloadUrl();
            audioUrl.addOnCompleteListener(path ->{
               if(path.isSuccessful()){
                   String url = path.getResult().toString();
                   DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("chat").child(chatUId);
                   MessageModel messageModel = new MessageModel(myUId,hisUId,url, util.currentData(),"recording", hisUsername);
                   databaseReference.push().setValue(messageModel);
               }
            });
        });
    }

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }
}