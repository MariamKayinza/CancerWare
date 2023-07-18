package com.example.cancerware;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;

public class StartScreen extends AppCompatActivity {

    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);

        // Set the image for the splash screen
        int imageResource = R.drawable.cancerware;
        imageView.setImageResource(imageResource);

        // Set the button text for the home page
        String buttonText = "GET STARTED";
        button.setText(buttonText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // home
                Intent intent = new Intent(StartScreen.this, homeActivity.class);
                startActivity(intent);
            }
        });

        // Set a timer to delay the start of the main activity
        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity
                Intent intent = new Intent(StartScreen.this, homeActivity.class);
                startActivity(intent);

                // Finish the splash activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}