package com.example.cancerware;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class homeActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        // Set the image for the homeActivity page
        int imageResource = R.drawable.cancerware;
        imageView.setImageResource(imageResource);

        // Set the text for the homeActivity page
        String text = "Welcome to CANCERWARE";
        textView.setText(text);

        // Set the button text for the homeActivity page
        String buttonText = "Start DiagnosisActivity";
        button.setText(buttonText);

        // Set the button listener for the homeActivity page
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the diagnosis process
                Intent intent = new Intent(homeActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }
}