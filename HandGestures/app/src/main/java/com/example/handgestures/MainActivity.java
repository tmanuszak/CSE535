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
                Log.e("ERROR", "doInBackground: gesture is" + gesture);
                Log.e("ERROR", "doInBackground: url is " + urls.get(gesture));
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
        urls.put("AC Power", "https://www.dropbox.com/s/kmx9qbsh7wfxsoj/ACPower.mp4");
        urls.put("Algorithm", "https://www.signingsavvy.com/media/mp4-ld/23/23188.mp4");
        urls.put("Antenna", "https://www.dropbox.com/s/c3jtr1e0dy06cqi/Antenna.mp4?dl=1");
        urls.put("Authentication", "https://www.dropbox.com/s/pkh6ip27bn4wkao/Authentication.mp4?dl=1");
        urls.put("Authorization", "https://www.dropbox.com/s/ernc0nxzmiao783/Authorization.mp4?dl=1");
        urls.put("Bandwidth", "https://www.dropbox.com/s/c4aub9ovbha0nax/Bandwidth.mp4?dl=1");
        urls.put("Bluetooth", "https://www.dropbox.com/s/ecqnhfdbrpjwyxb/Bluetooth.mp4?dl=1");
        urls.put("Browser", "https://www.dropbox.com/s/dhiyakt71tjek2n/Browser.mp4?dl=1");
        urls.put("Cloud Computing", "https://www.dropbox.com/s/w82vrwwxjc2epq6/Cloudcomputing.mp4?dl=1");
        urls.put("Data Compression", "https://www.dropbox.com/s/0dipxauabc2b04e/DataCompression.mp4?dl=1");
        urls.put("Data Link Layer", "https://www.dropbox.com/s/wi41ahd382tqsgt/DataLinkLayer.mp4?dl=1");
        urls.put("Data Mining", "https://www.dropbox.com/s/0byl3u9eftn08i6/DataMining.mp4?dl=1");
        urls.put("Decryption", "https://www.dropbox.com/s/uajulxph25o2z8r/Decryption.mp4");
        urls.put("Domain", "https://www.dropbox.com/s/mkwqft2syc2lq90/Domain.mp4?dl=1");
        urls.put("Email", "https://www.dropbox.com/s/zol8kk1x1w6zuol/Email.mp4?dl=1");
        urls.put("Exposure", "https://www.dropbox.com/s/uzurkb6eff5iu50/Exposure.mp4?dl=1");
        urls.put("Filter", "https://www.dropbox.com/s/49zwnuejs2jcrcr/Filter.mp4?dl=1");
        urls.put("Firewall", "https://www.dropbox.com/s/hyena09jk3pniod/Firewall.mp4?dl=1");
        urls.put("Flooding", "https://www.dropbox.com/s/05szr1hp2gk7irh/Flooding.mp4?dl=1");
        urls.put("Gateway", "https://www.dropbox.com/s/jumwniudjpx6dw1/Gateway.mp4?dl=1");
        urls.put("Hacker", "https://www.dropbox.com/s/lge3d1zsj3pruy1/Hacker.mp4?dl=1");
        urls.put("Header", "https://www.dropbox.com/s/3m41cfyfem23ile/Header.mp4?dl=1");
        urls.put("Hot Swap", "https://www.dropbox.com/s/n19x27f2yf934i2/HotSwap.mp4?dl=1");
        urls.put("Hyperlink", "https://www.dropbox.com/s/716b5xsm6vq1630/Hyperlink.mp4?dl=1");
        urls.put("Infrastructure", "https://www.dropbox.com/s/tqnxwf88xeokvyw/Infrastructure.mp4?dl=1");
        urls.put("Integrity", "https://www.dropbox.com/s/qc3flqrjl3z7gqg/Integrity.mp4?dl=1");
        urls.put("Internet", "https://www.dropbox.com/s/aott64kec48s5qo/Internet.mp4?dl=1");
        urls.put("Intranet", "https://www.dropbox.com/s/3k0fmobhs2wxdai/Intranet.mp4?dl=1");
        urls.put("Latency", "https://www.dropbox.com/s/ykr85w9iki7pwu0/Latency.mp4?dl=1");
        urls.put("Loopback", "https://www.dropbox.com/s/su3myi19amjl3ov/Loopback.mp4?dl=1");
        urls.put("Motherboard", "https://www.dropbox.com/s/9eap2m27drhk0is/Motherboard.mp4?dl=1");
        urls.put("Network", "https://www.dropbox.com/s/h4002ouwos0k4sp/Network.mp4?dl=1");
        urls.put("Networking", "https://www.dropbox.com/s/z80yaw0bd31gom6/Networking.mp4?dl=1");
        urls.put("Network Layer", "https://www.dropbox.com/s/70xyg7rnh32eyyr/Networklayer.mp4?dl=1");
        urls.put("Node", "https://www.dropbox.com/s/thnzx6x2vka28k7/Node.mp4?dl=1");
        urls.put("Packet", "https://www.dropbox.com/s/pubdbe1g9yr2n5j/Packet.mp4?dl=1");
        urls.put("Partition", "https://www.dropbox.com/s/yvjkq77ehzy16jw/Partition.mp4?dl=1");
        urls.put("Password Sniffing", "https://www.dropbox.com/s/2w6fv527uap0vdh/PasswordSniffing.mp4?dl=1");
        urls.put("Patch", "https://www.dropbox.com/s/gtrc1e1r3nfii1k/Patch.mp4?dl=1");
        urls.put("Phishing", "https://www.dropbox.com/s/9ubwe1bsnkdgk5e/Phishing.mp4?dl=1");
        urls.put("Physical Layer", "https://www.dropbox.com/s/1my6ldydsh1f54z/PhysicalLayer.mp4?dl=1");
        urls.put("Ping", "https://www.dropbox.com/s/6m0t955zppk4dhq/Ping.mp4?dl=1");
        urls.put("Port Scan", "https://www.dropbox.com/s/730jj9vsmegib1c/Portscan.mp4?dl=1");
        urls.put("Presentation Layer", "https://www.dropbox.com/s/fjv3tgc3lpl0xa0/PresentationLayer.mp4?dl=1");
        urls.put("Protocol", "https://www.dropbox.com/s/283h18e6yl0ffx7/Protocol.mp4?dl=1");
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
