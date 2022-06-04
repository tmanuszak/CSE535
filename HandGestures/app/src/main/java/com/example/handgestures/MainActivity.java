package com.example.handgestures;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_CAPTURE = 101;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private Uri fileUri;
    private Spinner gestureSpinner;
    private static Hashtable<String, String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the dropdown of gestures and urls
        gestureSpinner = findViewById(R.id.gestureSpinner);
        final ArrayList<String> gestures = new ArrayList<>(); // This is the list of gesture dropdown items
        urls = new Hashtable<String, String>(); // This is the list of urls with gestures as keys
        addGesturesToList(gestures);
        setupURLHashtable();
        ArrayAdapter<String> gesturesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                gestures
        );
        gestureSpinner.setAdapter(gesturesAdapter);


        // The learn button takes us to the "Learn" screen for the selected gesture.
        Button learn = (Button) findViewById(R.id.LearnButton);
        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
                String selection = gestureSpinner.getSelectedItem().toString();
                File gestureFile = new File(Environment.getExternalStorageDirectory() + "/my_folder/" + selection + ".mp4");
                if (!selection.equals("Select a gesture!") && gestureFile.exists()) {
                    Intent intent = new Intent(MainActivity.this, Main2ActivityNew.class);
                    intent.putExtra("spinner", selection);
                    startActivity(intent);
                } else if (!gestureFile.exists()) {
                    Toast.makeText(MainActivity.this, "Gesture video is not downloaded.", Toast.LENGTH_LONG).show();
                } else {// No gesture was selected (i.e. The hint is still selected)
                    Toast.makeText(MainActivity.this, "You must select a valid gesture.", Toast.LENGTH_LONG).show();
                }
            }
        });


        // The download button downloads the selected gesture.
        Button download = (Button) findViewById(R.id.downloadButton);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //UploadTask up1 = new UploadTask();
                //Toast.makeText(getApplicationContext(),"Starting to Upload",Toast.LENGTH_LONG).show();
                //up1.execute();

                String selection = gestureSpinner.getSelectedItem().toString();
                if (!selection.equals("Select a gesture!")) {
                    DownloadTask dw1 = new DownloadTask();
                    dw1.execute(selection);
                } else { // No gesture was selected.
                    Toast.makeText(MainActivity.this, "You must select a valid gesture.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                //Toast.makeText(getApplicationContext(),"Starting to Upload",Toast.LENGTH_LONG).show();
                String url = "http://10.218.107.121/cse535/upload_video.php";
                String charset = "UTF-8";
                String group_id = "40";
                String ASUid = "1200072576";
                String accept = "1";


                File videoFile = new File(Environment.getExternalStorageDirectory() + "/my_folder/Action1.mp4");
                String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
                String CRLF = "\r\n"; // Line separator required by multipart/form-data.

                URLConnection connection;

                connection = new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                try (
                        OutputStream output = connection.getOutputStream();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                ) {
                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"accept\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(accept).append(CRLF).flush();

                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"id\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(ASUid).append(CRLF).flush();

                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"group_id\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(group_id).append(CRLF).flush();


                    // Send video file.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + videoFile.getName() + "\"").append(CRLF);
                    writer.append("Content-Type: video/mp4; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
                    writer.append(CRLF).flush();
                    FileInputStream vf = new FileInputStream(videoFile);
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = vf.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, bytesRead);

                        }
                        //   output.close();
                        //Toast.makeText(getApplicationContext(),"Read Done", Toast.LENGTH_LONG).show();
                    } catch (Exception exception) {


                        //Toast.makeText(getApplicationContext(),"output exception in catch....."+ exception + "", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(exception));
                        publishProgress(String.valueOf(exception));
                        // output.close();

                    }

                    output.flush(); // Important before continuing with writer!
                    writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.


                    // End of multipart/form-data.
                    writer.append("--" + boundary + "--").append(CRLF).flush();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Request is lazily fired whenever you need to obtain information about response.
                int responseCode = ((HttpURLConnection) connection).getResponseCode();
                System.out.println(responseCode); // Should be 200

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(getApplicationContext(), "In Background Task " + text[0], Toast.LENGTH_LONG).show();
        }

    }

    public class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Starting to download", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... selectedGestures) {

            File SDCardRoot = Environment.getExternalStorageDirectory(); // location where you want to store
            File directory = new File(SDCardRoot, "/my_folder/"); //create directory to keep your downloaded file
            if (!directory.exists()) {
                directory.mkdir();
            }

            for (String gesture : selectedGestures) {
                String fileName = gesture + ".mp4";
                try {
                    InputStream input = null;
                    try {

                        URL url = new URL(urls.get(gesture)); // link of the song which you want to download like (http://...)
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setReadTimeout(95 * 1000);
                        urlConnection.setConnectTimeout(95 * 1000);
                        // urlConnection.setDoInput(true);
                        urlConnection.setRequestProperty("Accept", "application/json");
                        urlConnection.setRequestProperty("X-Environment", "android");


                        urlConnection.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                /** if it necessarry get url verfication */
                                //return HttpsURLConnection.getDefaultHostnameVerifier().verify("your_domain.com", session);
                                return true;
                            }
                        });
                        urlConnection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());


                        urlConnection.connect();
                        input = urlConnection.getInputStream();
                        OutputStream output = new FileOutputStream(new File(directory, fileName));

                        try {
                            byte[] buffer = new byte[1024];
                            int bytesRead = 0;
                            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                                output.write(buffer, 0, bytesRead);

                            }
                            output.close();
                            //Toast.makeText(getApplicationContext(),"Read Done", Toast.LENGTH_LONG).show();
                        } catch (Exception exception) {


                            //Toast.makeText(getApplicationContext(),"output exception in catch....."+ exception + "", Toast.LENGTH_LONG).show();
                            Log.d("Error", String.valueOf(exception));
                            publishProgress(String.valueOf(exception));
                            output.close();

                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "input exception in catch....."+ exception + "", Toast.LENGTH_LONG).show();
                        publishProgress(String.valueOf(exception));

                    } finally {
                        assert input != null;
                        input.close();
                    }
                } catch (Exception exception) {
                    publishProgress(String.valueOf(exception));
                }
            }

            return "true";
        }


        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(getApplicationContext(), "In Background Task" + text[0], Toast.LENGTH_LONG).show();
        }
    }


    public void startRecording() {
        File mediaFile = new
                File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/myvideo.mp4");


        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        fileUri = Uri.fromFile(mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private void addGesturesToList(ArrayList<String> gestures) {
        gestures.add("Select a gesture!");
        gestures.add("AC Power");
        gestures.add("Algorithm");
        gestures.add("Antenna");
        gestures.add("Authentication");
        gestures.add("Authorization");
        gestures.add("Bandwidth");
        gestures.add("Bluetooth");
        gestures.add("Browser");
        gestures.add("Cloud Computing");
        gestures.add("Data Compression");
        gestures.add("Data Link Layer");
        gestures.add("Data Mining");
        gestures.add("Decryption");
        gestures.add("Domain");
        gestures.add("Email");
        gestures.add("Exposure");
        gestures.add("Filter");
        gestures.add("Firewall");
        gestures.add("Flooding");
        gestures.add("Gateway");
        gestures.add("Hacker");
        gestures.add("Header");
        gestures.add("Hot Swap");
        gestures.add("Hyperlink");
        gestures.add("Infrastructure");
        gestures.add("Integrity");
        gestures.add("Internet");
        gestures.add("Intranet");
        gestures.add("Latency");
        gestures.add("Loopback");
        gestures.add("Motherboard");
        gestures.add("Network");
        gestures.add("Networking");
        gestures.add("Network Layer");
        gestures.add("Node");
        gestures.add("Packet");
        gestures.add("Partition");
        gestures.add("Password Sniffing");
        gestures.add("Patch");
        gestures.add("Phishing");
        gestures.add("Physical Layer");
        gestures.add("Ping");
        gestures.add("Port Scan");
        gestures.add("Presentation Layer");
        gestures.add("Protocol");
    }

    private void setupURLHashtable() {
        urls.put("AC Power", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/5D-2.mp4");
        urls.put("Algorithm", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/9D-2.mp4");
        urls.put("Antenna", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/11D-2.mp4");
        urls.put("Authentication", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/15D-2.mp4");
        urls.put("Authorization", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/16D-2.mp4");
        urls.put("Bandwidth", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/19D-2.mp4");
        urls.put("Bluetooth", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/26D-2.mp4");
        urls.put("Browser", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/30D-2.mp4");
        urls.put("Cloud Computing", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/39D-2.mp4");
        urls.put("Data Compression", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/46D-2.mp4");
        urls.put("Data Link Layer", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/47D-2.mp4");
        urls.put("Data Mining", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/48D-2.mp4");
        urls.put("Decryption", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/51D-2.mp4");
        urls.put("Domain", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/55D-2.mp4");
        urls.put("Email", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/60D-2.mp4");
        urls.put("Exposure", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/63D-2.mp4");
        urls.put("Filter", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/67D-2.mp4");
        urls.put("Firewall", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/68D-2.mp4");
        urls.put("Flooding", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/70D-2.mp4");
        urls.put("Gateway", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/74D-2.mp4");
        urls.put("Hacker", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/76D-2.mp4");
        urls.put("Header", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/80D-2.mp4");
        urls.put("Hot Swap", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/81D-2.mp4");
        urls.put("Hyperlink", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/84D-2.mp4");
        urls.put("Infrastructure", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/87D-2.mp4");
        urls.put("Integrity", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/88D-2.mp4");
        urls.put("Internet", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/90D-2.mp4");
        urls.put("Intranet", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/92D-2.mp4");
        urls.put("Latency", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/101D-2.mp4");
        urls.put("Loopback", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/104D-2.mp4");
        urls.put("Motherboard", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/108D-2.mp4");
        urls.put("Network", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/110D-2.mp4");
        urls.put("Networking", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/113D-2.mp4");
        urls.put("Network Layer", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/112D-2.mp4");
        urls.put("Node", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/114D-1.mp4");
        urls.put("Packet", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/118D-2.mp4");
        urls.put("Partition", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/119D-1.mp4");
        urls.put("Password Sniffing", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/120D-1.mp4");
        urls.put("Patch", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/121D-1.mp4");
        urls.put("Phishing", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/123D-1.mp4");
        urls.put("Physical Layer", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/124D-1.mp4");
        urls.put("Ping", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/125D-1.mp4");
        urls.put("Port Scan", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/130D-1.mp4");
        urls.put("Presentation Layer", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/131D-1.mp4");
        urls.put("Protocol", "https://deaftec.org/stem-dictionary/wp-content/uploads/sites/7/2019/08/132D-1.mp4");
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video has been saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
