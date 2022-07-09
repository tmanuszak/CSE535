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
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

// FTP imports

// ok http imports
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;

import java.io.File;
import java.util.Objects;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class MainActivity extends AppCompatActivity {

    private EditText hikename;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hikename = findViewById(R.id.hikename);
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(hikename.getText().toString(), "")) {
                    Intent intent = new Intent(MainActivity.this, Screen2.class);
                    intent.putExtra("name", hikename.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}

