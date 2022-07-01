package com.example.HelpMeHike;

import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Screen3 extends AppCompatActivity {

    //THIS CODE IS ALL OUTDATED, WAS USED FOR GEOTAGGING
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen3);

        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("title", "replace this string with the title");
                intent.putExtra("photopath", "replace this string with the file path to .jpeg location");
                intent.putExtra("caption", "replace this string with the caption");
                intent.putExtra("type", false); //replace this value with the hazard type
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}