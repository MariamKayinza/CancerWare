package com.example.cancerware;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;

public class Splashscreen extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        imageView = findViewById(R.id.imageView);

        // Set the image for the splash screen
        int imageResource = R.drawable.cancerware;
        imageView.setImageResource(imageResource);

        // Set a timer to delay the start of the main activity
        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity
                Intent intent = new Intent(Splashscreen.this, home.class);
                startActivity(intent);

                // Finish the splash activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}