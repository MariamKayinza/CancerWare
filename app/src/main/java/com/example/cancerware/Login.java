package com.example.cancerware;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

public class Login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Set the click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the username and password from the edit text fields
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if the username and password are valid
                if (username.equals("admin") && password.equals("password")) {
                    // Login the user
                    Intent intent = new Intent(Login.this, home.class);
                    startActivity(intent);
                } else {
                    // Show an error message
                    TextView errorTextView = findViewById(R.id.errorTextView);
                    errorTextView.setText("Invalid username or password");
                }
            }
        });

        // Set the click listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the register page
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        //for cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }
        });
    }
}