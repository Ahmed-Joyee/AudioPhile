package app.android.audiophile;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AudioRecorder audioRecorder;
    private File recordFile;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        audioRecorder = new AudioRecorder();

        RecordView recordView = view.findViewById(R.id.record1_view);
        final RecordButton recordButton = view.findViewById(R.id.record1_button);
        Button btnChangeOnclick = view.findViewById(R.id.btn1_change_onclick);

        // To Enable Record Lock
//        recordView.setLockEnabled(true);
//        recordView.setRecordLockImageView(findViewById(R.id.record_lock));
        //IMPORTANT
        recordButton.setRecordView(recordView);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                recordFile = new File(getActivity().getFilesDir(), UUID.randomUUID().toString() + ".3gp");
                try {
                    audioRecorder.start(recordFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                stopRecording(true);
                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                //limitReached to determine if the Record was finished when time limit reached.
                stopRecording(false);
                String time = getHumanTimeText(recordTime);
                Toast.makeText(getActivity(), "onFinishRecord - Recorded Time is: " + time + " File saved at " + recordFile.getPath(), Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onFinish" + " Limit Reached? " + limitReached);
                Log.d("RecordView", "onFinish");

                Log.d("RecordTime", time);
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                stopRecording(true);
                Log.d("RecordView", "onLessThanSecond");
            }
        });
        recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }


                ActivityCompat.
                        requestPermissions(getActivity(),
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                0);

                return false;

            }
        });
        return view;
    }

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }


    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }
}