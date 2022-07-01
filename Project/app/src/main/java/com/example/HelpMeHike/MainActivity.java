package com.example.HelpMeHike;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
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
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import android.support.annotation.Nullable;
import android.app.Activity;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class MainActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected ArrayList<ArrayList<Double>> coordinates;
    protected ArrayList<Heartrate> heartrate_list;
    protected double tempLongitude;
    protected double tempLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinates = new ArrayList<ArrayList<Double>>();
        heartrate_list = new ArrayList<Heartrate>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 15000, 0, this);

        Button photo = (Button) findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, Screen3.class); //old code for geo-tagging
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                tempLongitude = location.getLongitude();
                tempLatitude = location.getLatitude();
                //startActivityForResult(intent, 1); //old code for geo-tagging
                //ADD CODE HERE TO TAKE PICTURE
            }
        });

        Button heartbeat = (Button) findViewById(R.id.heartbeat);
        heartbeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call script to analyze heartbeat from photo
                //heartrate = script()
                //Heartrate point = new Heartrate(tempLatitude, tempLongitude, data.getDoubleExtra("heartrate"));
                //heartrate_list.add(point);
            }
        });
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

    @Override
    public void onLocationChanged(Location location) {
        ArrayList<Double> point = new ArrayList<Double>();
        point.add(location.getLatitude());
        point.add(location.getLongitude());
        coordinates.add(point);
        System.out.println(coordinates);
        System.out.println(heartrate_list);
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
}

class Heartrate{
    public double latitude;
    public double longitude;
    public double heartrate;

    Heartrate(double latitude, double longitude, double heartrate){
        this.latitude = latitude;
        this.longitude = longitude;
        this.heartrate = heartrate;
    }
}
