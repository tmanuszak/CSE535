package com.example.handgestures;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

// FTP imports
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

// ok http imports
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class screen3 extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int VIDEO_RECORD_CODE = 101;
    static String selection;
    private VideoView v;
    private EditText lastNameEditText;
    private EditText practiceNumberEditText;
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

        Button recordButton = (Button) findViewById(R.id.recordButton);
        if (!isCameraPresentInPhone()) {
            recordButton.setEnabled(false);
        } else {
            getCameraPermissions();
            recordButton.setEnabled(true);
        }

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(lastNameEditText.getText().toString(), "") && !Objects.equals(practiceNumberEditText.getText().toString(), "")) {
                    startRecording();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter your last name and practice number before recording.", Toast.LENGTH_LONG).show();
                }
            }
        });


        // Upload the mp4 to the webserver
        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        lastNameEditText = findViewById(R.id.lastNameText);
        practiceNumberEditText = findViewById(R.id.practiceNumberText);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert mediaFile != null;
                if (mediaFile.exists()
                        && mediaFile != null
                        && !Objects.equals(selection, "")
                        && !Objects.equals(lastNameEditText.getText().toString(), "")
                        && !Objects.equals(practiceNumberEditText.getText().toString(), "")) {
                    UploadTask up1 = new UploadTask();
                    Toast.makeText(getApplicationContext(), "Stating to Upload", Toast.LENGTH_SHORT).show();
                    up1.execute();
                } else if (!mediaFile.exists()) {
                    Toast.makeText(getApplicationContext(), "You must make a video to upload.", Toast.LENGTH_LONG).show();
                } else if (Objects.equals(lastNameEditText.getText().toString(), "")
                        || Objects.equals(practiceNumberEditText.getText().toString(), "")) {
                    Toast.makeText(getApplicationContext(), "You must input your last name and practice number.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void startRecording() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/PracticeVideos/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        mediaFile = new
                File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/PracticeVideos/" + selection + "_PRACTICE_" + practiceNumberEditText.getText().toString() + "_" + lastNameEditText.getText().toString() + ".mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        Uri fileUri = Uri.fromFile(mediaFile);

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            // SFTP CODE
//            String TAG = "UPLOAD_VIDEO";
//
//            FTPClient ftpClient = new FTPClient();
//            String server = getString(R.string.server);
//            String username = getString(R.string.username);
//            String password = getString(R.string.password);
//
//            try {
//
//                ftpClient.connect(server, 21);
//                ftpClient.login(username, password);
//                ftpClient.enterLocalPassiveMode();
//                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//
//                Log.e(TAG, "FTP Reply String:" + ftpClient.getReplyString());
//
//                ftpClient.changeWorkingDirectory("HandGesturesPracticeVideos");
//                Log.e(TAG, "FTP Reply String:" + ftpClient.getReplyString());
//
//                File videoFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                        + "/PracticeVideos/" + selection + "_PRACTICE_" + practiceNumberEditText.getText().toString() + "_" + lastNameEditText.getText().toString() + ".mp4");
//
//                InputStream inputStream = new FileInputStream(videoFile);
//                ftpClient.storeFile(selection + "_PRACTICE_" + practiceNumberEditText.getText().toString() + "_" + lastNameEditText.getText().toString() + ".mp4", inputStream);
//                Log.e(TAG, "FTP Reply String:" + ftpClient.getReplyString());
//                inputStream.close();
//
//                ftpClient.logout();
//                ftpClient.disconnect();
//
//
//
//
//
//                return null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            // ------------------------------------------------------------------------------------

            try {
                File vidfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/PracticeVideos/" + selection + "_PRACTICE_" + practiceNumberEditText.getText().toString()
                        + "_" + lastNameEditText.getText().toString() + ".mp4");

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();

                MediaType mediaType = MediaType.parse("text/plain");

                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("videofile",vidfile.getName(),
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        vidfile))
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.webServerURL))
                        .method("POST", body)
                        .addHeader("user", "CSE535Group") // so I know this request came from the app
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Upload successful. Wait 2-3 minutes before getting the classification.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(getApplicationContext(), "In Background Task " + text[0], Toast.LENGTH_LONG).show();
        }

    }
}