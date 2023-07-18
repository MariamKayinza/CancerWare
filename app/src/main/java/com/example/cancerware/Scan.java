package com.example.cancerware;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

public class Scan extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        // Set the image for the scan disease page
        int imageResource = R.drawable.cancerware;
        imageView.setImageResource(imageResource);

        // Set the text for the scan disease page
        String text = "Scan Disease";
        textView.setText(text);

        // Set the button text for the scan disease page
        String buttonText = "Start Scan";
        button.setText(buttonText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the scan process
                Intent intent = new Intent(Scan.this, Diagnosis.class);
                startActivity(intent);
            }
        });
    }
}