package com.example.handgestures;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class screen3 extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int VIDEO_RECORD_CODE = 101;
    private Uri fileUri;
    static String selection;
    private VideoView v;
    private static File mediaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);
        MediaController mediaController = new MediaController(this);
        v = findViewById(R.id.videoViewScreen3);
        v.setMediaController(mediaController);

        // Getting the selected gesture from Screen 1
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        selection = extras.getString("gesture");

        // Check if phone has a camera
        if (isCameraPresentInPhone()) {
            Log.i("VIDEO_RECORD_TAG", "Camera is detected.");
            getCameraPermissions();
        } else {
            Log.i("VIDEO_RECORD_TAG", "No camera detected.");
        }


        // Upload the video to the webserver
        Button upload = (Button) findViewById(R.id.uploadButton);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(screen3.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button recordButton = (Button) findViewById(R.id.recordButton);
        if(!isCameraPresentInPhone()){
            recordButton.setEnabled(false);
        } else {
            getCameraPermissions();
            recordButton.setEnabled(true);
        }

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });
    }

    public void startRecording()
    {
        mediaFile = new
                File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/myvideo.mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);
        fileUri = Uri.fromFile(mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_RECORD_CODE);
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == VIDEO_RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                Log.e("RECORD_VIDEO_TAG", "Video is recorded and available at path" + mediaFile.getPath());
                v.setVideoPath(mediaFile.getPath());
                v.start();
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("RECORD_VIDEO_TAG", "Video is recording is cancelled.");
            } else {
                Log.e("RECORD_VIDEO_TAG", "Video is recording failed.");
            }
        }
    }

    private boolean isCameraPresentInPhone() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void getCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }
}