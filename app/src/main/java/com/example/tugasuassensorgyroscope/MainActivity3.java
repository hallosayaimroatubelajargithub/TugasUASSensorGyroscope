package com.example.tugasuassensorgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity3 extends AppCompatActivity {

    private Button buttonPrev;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    public void buttonPrev(View view) {
        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
        startActivity(intent);
    }

    public void buttonNext(View view) {
        Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
        startActivity(intent);
    }
}