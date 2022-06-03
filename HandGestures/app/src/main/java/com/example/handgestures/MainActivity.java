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

public class MainActivity extends AppCompatActivity implements LocationListener {

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
                if (!selection.equals("Select a gesture!")) {
                    Intent intent = new Intent(MainActivity.this, Main2ActivityNew.class);
                    intent.putExtra("spinner", selection);
                    startActivity(intent);
                } else { // No gesture was selected (i.e. The hint is still selected)
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
                } else {
                    Toast.makeText(MainActivity.this, "You must select a valid gesture.", Toast.LENGTH_LONG).show();
                }

            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, this);
        Button bt6 = (Button) findViewById(R.id.button5);
        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(GPS_PROVIDER);
                Toast.makeText(getApplicationContext(), "Current Longitute: " + location.getLongitude() + " Current Latitude: " + location.getLatitude(), Toast.LENGTH_LONG).show();
            }
        });

        Button bt1 = (Button) findViewById(R.id.button);

        if (!hasCamera()) {
            bt1.setEnabled(false);
        }

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "Current Longitute: " + location.getLongitude() + " Current Latitude: " + location.getLatitude(), Toast.LENGTH_LONG).show();
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
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setReadTimeout(95 * 1000);
                        urlConnection.setConnectTimeout(95 * 1000);
                        urlConnection.setDoInput(true);
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
                        //input = url.openStream();
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

//        @Override
//        protected void onPostExecute(String text){
//            VideoView vv = (VideoView) findViewById(R.id.videoView);
//            vv.setVideoPath(Environment.getExternalStorageDirectory()+"/my_folder/Patch.mp4");
//            vv.start();
//            Button bt4 = (Button)findViewById(R.id.button3);
//            bt4.setEnabled(true);
//        }
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
        urls.put("AC Power", "https://drive.google.com/uc?id=1nBgSQdH7ZuvUTfJac4DHdgPaPIVFSMjM&authuser=0&export=download");
        urls.put("Algorithm", "https://drive.google.com/uc?id=1eT5g1JPXwxOIIFwVLqjO2N5YGBqYVTO5&authuser=0&export=download");
        urls.put("Antenna", "https://drive.google.com/uc?id=1X0ZQF-oriF9eWgL3SoU64sdxM_pVudCU&authuser=0&export=download");
        urls.put("Authentication", "https://drive.google.com/uc?id=1mg5i66f9zbh413Gyj1WsnWqfHKBuYQ2d&authuser=0&export=download");
        urls.put("Authorization", "https://drive.google.com/uc?id=1bwwTx10PnO57WM_3g3JYVDsap-t-0dMD&authuser=0&export=download");
        urls.put("Bandwidth", "https://drive.google.com/uc?id=1B95x7o9JQ6C5tVYN38DMHnytwzWfUDop&authuser=0&export=download");
        urls.put("Bluetooth", "https://drive.google.com/uc?id=1_K2fxNT5fdI028x8-8cke97wXaCg18Nt&authuser=0&export=download");
        urls.put("Browser", "https://drive.google.com/uc?id=1DUV0RHK39_yD_Xp0KUAr4rPd5hLxf9FB&authuser=0&export=download");
        urls.put("Cloud Computing", "https://drive.google.com/uc?id=1PKgSDLS_suYYRYse2zGQwTe2qRHQ_gYW&authuser=0&export=download");
        urls.put("Data Compression", "https://drive.google.com/uc?id=1sCLxdSjyAG9BuhBmC4IyESXq308wnBjQ&authuser=0&export=download");
        urls.put("Data Link Layer", "https://drive.google.com/uc?id=15khsyj5TBnaxDucTgtZ8TblA7HY0MpB5&authuser=0&export=download");
        urls.put("Data Mining", "https://www.dropbox.com/s/0byl3u9eftn08i6/DataMining.mp4?dl=1");
        urls.put("Decryption", "https://drive.google.com/uc?id=11jFSQLfHCdRG6zuvE8VYqaZBpUDrYImF&authuser=0&export=download");
        urls.put("Domain", "https://drive.google.com/uc?id=1S2NdpjzbBTM8cRG4njgRbW_CNRrjQrOs&authuser=0&export=download");
        urls.put("Email", "https://drive.google.com/uc?id=1EoWpEvjX50xhTrVZPR0Xfu5L_0QlEutc&authuser=0&export=download");
        urls.put("Exposure", "https://drive.google.com/uc?id=1aCTozu4aiYzV7FU7PxWXrNkUFJp3F9td&authuser=0&export=download");
        urls.put("Filter", "https://drive.google.com/uc?id=1bppYkF6pBUpjgCmd5dwyIiPK8025jLSD&authuser=0&export=download");
        urls.put("Firewall", "https://drive.google.com/uc?id=1D9T_LsTIq7QcDRydFTlTYXqJodey3wOG&authuser=0&export=download");
        urls.put("Flooding", "https://drive.google.com/uc?id=193ErtT1InqxnbLvfVw2n1mKIUS6eCK4r&authuser=0&export=download");
        urls.put("Gateway", "https://drive.google.com/uc?id=1l7OUpB2gDRWbzsfD0wKYGcXN0nE8TpMa&authuser=0&export=download");
        urls.put("Hacker", "https://drive.google.com/uc?id=1N9MvRqFGCsH3pjoh4uAXTvpbwaejfuA7&authuser=0&export=download");
        urls.put("Header", "https://drive.google.com/uc?id=1sI8AKiswVhogwr7pvMAed_QbL-bO-xDj&authuser=0&export=download");
        urls.put("Hot Swap", "https://drive.google.com/uc?id=19Q4v_dqbm3YUfbNHsQQWoApWsp5Lhkbl&authuser=0&export=download");
        urls.put("Hyperlink", "https://drive.google.com/uc?id=1WDGAyl_Yi1rZZxbaV_BzhMw-WUrYtmGU&authuser=0&export=download");
        urls.put("Infrastructure", "https://drive.google.com/uc?id=1SMQVMwWviFYSNDHytPpOvd0VHpAFPYvW&authuser=0&export=download");
        urls.put("Integrity", "https://drive.google.com/uc?id=1QBr366DmZgqlQGtvAkMvRL8pQrRwVWF7&authuser=0&export=download");
        urls.put("Internet", "https://drive.google.com/uc?id=1M2XrAlMJjQFzlqSRxDRW3aM7uaqpaWnX&authuser=0&export=download");
        urls.put("Intranet", "https://drive.google.com/uc?id=15qrkeHjW9vlmv_fyN2NylWEswCnlIxJC&authuser=0&export=download");
        urls.put("Latency", "https://drive.google.com/uc?id=1Vcku5y_xS_M6FzPa2uI8UVxdpFZXL2Sh&authuser=0&export=download");
        urls.put("Loopback", "https://drive.google.com/uc?id=1Tp0XQKty6Il5EtIshTyA-vQ3tlNvs5yl&authuser=0&export=download");
        urls.put("Motherboard", "https://drive.google.com/uc?id=18c-Ej-rOy1qdHsK_-HCmzf27FDwB2bKs&authuser=0&export=download");
        urls.put("Network", "https://drive.google.com/uc?id=1skCOl05yXKh4vBh1ilKl7klS3-N-c0IQ&authuser=0&export=download");
        urls.put("Networking", "https://drive.google.com/uc?id=1ZKoHq3R1yygsZnPlhRHEmwmQ2u6O3oap&authuser=0&export=download");
        urls.put("Network Layer", "https://drive.google.com/uc?id=1zcVPM7ysshSRk92C5gT95OqBp0NZAWqY&authuser=0&export=download");
        urls.put("Node", "https://drive.google.com/uc?id=1HHtaTqamqnLeyeJY7a4JyU8TzXWJm2Wl&authuser=0&export=download");
        urls.put("Packet", "https://drive.google.com/uc?id=1DI-a3Ht859Npgq1hb1MbLDDzFEnIDs7i&authuser=0&export=download");
        urls.put("Partition", "https://drive.google.com/uc?id=1sWa2FwOSCeM7qwn2kgwHY3OIyPSuuddc&authuser=0&export=download");
        urls.put("Password Sniffing", "https://drive.google.com/uc?id=1HAUutbhlCOQe2qxmJWlAyoYfXXCI9Tn0&authuser=0&export=download");
        urls.put("Patch", "https://drive.google.com/uc?id=1DigAND-uw7mf-nKSCYnntdrTpX9s_5vu&authuser=0&export=download");
        urls.put("Phishing", "https://drive.google.com/uc?id=1E0Hml1W4L8dD_tmGAjlhwBmkDbHNVCng&authuser=0&export=download");
        urls.put("Physical Layer", "https://drive.google.com/uc?id=12GzFpJ1Tst1hmkaSkOTjDaD4bd3vd0ju&authuser=0&export=download");
        urls.put("Ping", "https://drive.google.com/uc?id=1jXbjggX8jsbICXIINX4DIYjuQVCvmKU2&authuser=0&export=download");
        urls.put("Port Scan", "https://drive.google.com/uc?id=1nlJ2EuHYXjnaEI7R5dj0ud19kASg3mqZ&authuser=0&export=download");
        urls.put("Presentation Layer", "https://drive.google.com/uc?id=1nWyRkTRggGH4o7wwxfI_TgbXIuCRRHx_&authuser=0&export=download");
        urls.put("Protocol", "https://drive.google.com/uc?id=1NsgrKuktsLu81UXYVygSGXBQrlUFvqU8&authuser=0&export=download");
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
