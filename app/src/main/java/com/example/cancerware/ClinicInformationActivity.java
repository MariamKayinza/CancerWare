package com.example.cancerware;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ClinicInformationActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_information);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        // Set the image for the clinic information page
        int imageResource = R.drawable.cancerware;
        imageView.setImageResource(imageResource);

        // Set the text for the clinic information page
        String text = "Clinic Information";
        textView.setText(text);

        // Set the button text for the clinic information page
        String buttonText = "Back";
        button.setText(buttonText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClinicInformationActivity.this, homeActivity.class);
                startActivity(intent);
            }
        });
    }
}