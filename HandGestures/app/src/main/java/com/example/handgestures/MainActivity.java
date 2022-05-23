package com.example.handgestures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner gestureSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureSpinner = findViewById(R.id.gestureSpinner);

        // Adding all of the gestures to the gestures list
        final ArrayList<String> gestures = new ArrayList<>();
        addGesturesToList(gestures);
        System.out.println("Total gestures: " + gestures.size());

        ArrayAdapter<String> gesturesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                gestures
        );

        gestureSpinner.setAdapter(gesturesAdapter);
    }

    private void addGesturesToList(ArrayList<String> gestures) {
        gestures.add("All Night");
        gestures.add("Appetite");
        gestures.add("Bull");
        gestures.add("Can");
        gestures.add("Carrot");
        gestures.add("Cat");
        gestures.add("Cheer");
        gestures.add("Chicken");
        gestures.add("Cow");
        gestures.add("Cucumber");
        gestures.add("Day");
        gestures.add("Deaf");
        gestures.add("Decide");
        gestures.add("Dolphin");
        gestures.add("Evening");
        gestures.add("Fish");
        gestures.add("Flower");
        gestures.add("Food");
        gestures.add("Full");
        gestures.add("Goat");
        gestures.add("Hearing");
        gestures.add("Hurt");
        gestures.add("Meat");
        gestures.add("Noodle");
        gestures.add("Noon");
        gestures.add("Pepper");
        gestures.add("Phone");
        gestures.add("Rice");
        gestures.add("Rooster");
        gestures.add("Rotten");
        gestures.add("Salt");
        gestures.add("Sandwich");
        gestures.add("Shark");
        gestures.add("Smell");
        gestures.add("Sorry");
        gestures.add("Sour");
        gestures.add("Taco");
        gestures.add("Task");
        gestures.add("Taste");
        gestures.add("Tasty");
        gestures.add("Taurus");
        gestures.add("Tiger");
        gestures.add("Tomato");
        gestures.add("Whale");
        gestures.add("Work Hard");
    }
}