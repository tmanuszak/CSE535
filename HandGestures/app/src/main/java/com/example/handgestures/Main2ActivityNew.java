package com.example.handgestures;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Main2ActivityNew extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_new);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String selection = extras.getString("spinner");
        Toast.makeText(Main2ActivityNew.this, selection, Toast.LENGTH_LONG).show();

        // Making the video have a play and rewind button
        MediaController mediaController = new MediaController(this);

        VideoView vv = (VideoView) findViewById(R.id.videoView);
        vv.setVideoPath(Environment.getExternalStorageDirectory()+"/my_folder/"+selection+".mp4");
        vv.setMediaController(mediaController);
        vv.start();

        Button practice = (Button) findViewById(R.id.practice);
        practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Main2ActivityNew.this, screen3.class);
                startActivity(intent);
            }
        });

        Button backButton = (Button)findViewById(R.id.backToMainActivityButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2ActivityNew.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
