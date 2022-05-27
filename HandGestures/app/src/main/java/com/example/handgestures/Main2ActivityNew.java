package com.example.androidhelloworldthursday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main2ActivityNew extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_new);

        Bundle extras = getIntent().getExtras();
        String str = extras.getString("spinner");
        Toast.makeText(Main2ActivityNew.this, str, Toast.LENGTH_LONG).show();

        Button practice = (Button) findViewById(R.id.practice);
        practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Main2ActivityNew.this, screen3.class);
                startActivity(intent);
            }
        });

        Button bt2 = (Button)findViewById(R.id.button2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int2 = new Intent(Main2ActivityNew.this, MainActivity.class);
                startActivity(int2);
            }
        });

    }
}
