package app.android.audiophile;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VoiceMessageActivity extends AppCompatActivity {

    private AudioRecorder audioRecorder;
    private File recordFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_message);

        audioRecorder = new AudioRecorder();

        RecordView recordView = findViewById(R.id.record_view);
        final RecordButton recordButton = findViewById(R.id.record_button);
        Button btnChangeOnclick = findViewById(R.id.btn_change_onclick);

        // To Enable Record Lock
//        recordView.setLockEnabled(true);
//        recordView.setRecordLockImageView(findViewById(R.id.record_lock));
        //IMPORTANT
        recordButton.setRecordView(recordView);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".3gp");
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
                Toast.makeText(VoiceMessageActivity.this, "onFinishRecord - Recorded Time is: " + time + " File saved at " + recordFile.getPath(), Toast.LENGTH_SHORT).show();
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

                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(VoiceMessageActivity.this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }


                ActivityCompat.
                        requestPermissions(VoiceMessageActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                0);

                return false;

            }
        });

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