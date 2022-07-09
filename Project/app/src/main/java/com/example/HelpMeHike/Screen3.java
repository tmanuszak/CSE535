package com.example.HelpMeHike;

import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Screen3 extends AppCompatActivity {
    private static String hikename;

    //THIS CODE IS ALL OUTDATED, WAS USED FOR GEOTAGGING
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen3);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        hikename = extras.getString("name");
        File img = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HeartRateVideos/" + hikename + ".png");
        if (img.exists()) {
            ImageView MyImageView = (ImageView)findViewById(R.id.chart);
            MyImageView.setVisibility(View.VISIBLE);
            TextView t = (TextView) findViewById(R.id.textView2);
            t.setVisibility(View.GONE);
            Drawable d = Drawable.createFromPath( Environment.getExternalStorageDirectory().getAbsolutePath() + "/HeartRateVideos/" + hikename + ".png" );
            MyImageView.setImageDrawable(d);
        } else {
            ImageView MyImageView = (ImageView)findViewById(R.id.chart);
            MyImageView.setVisibility(View.GONE);
            TextView t = (TextView) findViewById(R.id.textView2);
            t.setVisibility(View.VISIBLE);
        }

        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Screen3.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}