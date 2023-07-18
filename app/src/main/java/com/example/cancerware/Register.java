package com.example.cancerware;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

public class Register extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        errorTextView = findViewById(R.id.errorTextView);

        // Set the click listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the username and password from the edit text fields
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if the username and password are valid
                if (username.isEmpty() || password.isEmpty()) {
                    // Show an error message
                    errorTextView.setText("Please enter a username and password");
                } else {
                    // Register the user
                    // TODO: Save the username and password in the database

                    // Go to the login page
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                }
            }
        });
    }
}