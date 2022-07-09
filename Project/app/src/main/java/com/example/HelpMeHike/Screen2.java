package com.example.HelpMeHike;

//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.text.PrecomputedText;
//import android.util.Log;
//import android.view.View;
//import android.webkit.CookieManager;
//import android.webkit.ValueCallback;
//import android.webkit.WebView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.MediaController;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//import android.widget.VideoView;
//
//import java.io.BufferedInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.file.Files;
//import java.security.KeyStore;
//import java.security.Security;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateFactory;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.List;
//import java.lang.reflect.Field;
//import android.support.annotation.Nullable;
//import android.app.Activity;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManagerFactory;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.MediaController;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//import android.widget.VideoView;
//
//// FTP imports
//
//// ok http imports
////import okhttp3.MediaType;
////import okhttp3.MultipartBody;
////import okhttp3.OkHttpClient;
////import okhttp3.Request;
////import okhttp3.RequestBody;
////import okhttp3.Response;
//
//import java.io.File;
//import java.util.Objects;
//
//import static android.location.LocationManager.GPS_PROVIDER;
//import static android.location.LocationManager.NETWORK_PROVIDER;















import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.ArrayList;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


// ok http imports
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.util.Objects;

import static android.location.LocationManager.GPS_PROVIDER;

public class Screen2 extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected ArrayList<ArrayList<Double>> coordinates;
    protected ArrayList<Heartrate> heartrate_list;
    protected double tempLongitude;
    protected double tempLatitude;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int VIDEO_RECORD_CODE = 101;
    private VideoView v;
    private static File mediaFile;
    private static String hikename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);

        coordinates = new ArrayList<ArrayList<Double>>();
        heartrate_list = new ArrayList<Heartrate>();

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        hikename = extras.getString("name");


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 15000, 0, this);

        MediaController mediaController = new MediaController(this);
        v = findViewById(R.id.videoscreen);
        v.setMediaController(mediaController);

        //System.out.println(Environment.getExternalStorageDirectory());

        if (isCameraPresentInPhone()) {
            Log.i("VIDEO_RECORD_TAG", "Camera is detected.");
            getCameraPermissions();
        } else {
            Log.i("VIDEO_RECORD_TAG", "No camera detected.");
        }

        Button video = (Button) findViewById(R.id.video);
        if (!isCameraPresentInPhone()) {
            video.setEnabled(false);
        } else {
            getCameraPermissions();
            video.setEnabled(true);
        }
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, Screen3.class); //old code for geo-tagging
                if (ActivityCompat.checkSelfPermission(Screen2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Screen2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                tempLongitude = location.getLongitude();
                tempLatitude = location.getLatitude();
                startRecording();
                //startActivityForResult(intent, 1); //old code for geo-tagging
                //ADD CODE HERE TO TAKE PICTURE
            }
        });

//        Button heartbeat = (Button) findViewById(R.id.heartbeat);
//        heartbeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Call script to analyze heartbeat from photo
//                //heartrate = script()
//                Heartrate point = new Heartrate(tempLatitude, tempLongitude, 80); //replace last number with actual heartrate
//                heartrate_list.add(point);
//                System.out.println(heartrate_list);
//            }
//        });

        Button heartbeat = (Button) findViewById(R.id.upload);
        heartbeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert mediaFile != null;
                if (mediaFile.exists() && mediaFile != null) {
                    UploadTask up1 = new UploadTask();
                    Toast.makeText(getApplicationContext(), "Stating to Upload", Toast.LENGTH_SHORT).show();
                    up1.execute();
                } else if (!mediaFile.exists()) {
                    Toast.makeText(getApplicationContext(), "You must make a video to upload.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void startRecording() {
        System.out.println("here1");
        File dir = new File(Environment.getExternalStorageDirectory() + "/HeartRateVideos/");
        System.out.println(dir.toString());
        if (!dir.exists()) {
            dir.mkdir();
        }
        mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/HeartRateVideos/" + hikename + ".mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        Uri fileUri = Uri.fromFile(mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_RECORD_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

//old code for geo-tagging
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == 1) {
//                if (data != null) {
//                    Heartrate point = new Heartrate(tempLatitude, tempLongitude, data.getDoubleExtra("heartrate"));
//                    heartrate_list.add(point);
//                }
//            }
//        }
//    }

    private boolean isCameraPresentInPhone() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void getCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        ArrayList<Double> point = new ArrayList<Double>();
//        point.add(location.getLatitude());
//        point.add(location.getLongitude());
//        coordinates.add(point);
//        System.out.println(coordinates);
//        System.out.println(heartrate_list);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                File vidfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HeartRateVideos/" + hikename + ".mp4");

                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.parse("text/plain");
                //add more information here?
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("videofile",vidfile.getName(),
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        vidfile))
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.webServerUpload))
                        .method("POST", body)
                        .addHeader("user", "CSE535Group") // so I know this request came from the app
                        .addHeader("time", String.valueOf(System.currentTimeMillis()))
                        .addHeader("coords", String.valueOf(tempLongitude) + ", " + String.valueOf(tempLatitude))
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    return "Upload successful. Wait 2-3 minutes before getting the data.";
                } else {
                    return Objects.requireNonNull(response.body()).string();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }



            return "";
        }


        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(getApplicationContext(), "In Background Task " + text[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute (String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }

    }




}

class Heartrate{
    public double latitude;
    public double longitude;
    public int heartrate;

    Heartrate(double latitude, double longitude, int heartrate){
        this.latitude = latitude;
        this.longitude = longitude;
        this.heartrate = heartrate;
    }

    int getHeartrate(){
        return this.heartrate;
    }
}