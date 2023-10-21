package com.example.cipherkey;
import android.annotation.SuppressLint;
import android.os.AsyncTask;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import android.util.Log;


public class RegisterActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, nameEditText;
    Button signupEditButton;
    TextView navigateToLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailInput);
        passwordEditText = findViewById(R.id.passwordInput);
        nameEditText = findViewById(R.id.nameInput);
        signupEditButton = findViewById(R.id.signupButton);
        navigateToLogin = findViewById(R.id.textView2);

        signupEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(v);
            }
        });


        navigateToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login activity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void signup(View view) {
        String enteredEmail = emailEditText.getText().toString();
        String enteredPassword = passwordEditText.getText().toString();
        String enteredName = nameEditText.getText().toString();
        new DatabaseConnectionTask().execute(enteredEmail, enteredPassword, enteredName);
    }

    private class DatabaseConnectionTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String enteredEmail = params[0];
            String enteredPassword = params[1];
            String enteredName = params[2];

            try {
                Connection connection = DriverManager.getConnection(DatabaseConfig.url, DatabaseConfig.username, DatabaseConfig.password);
                String sql = "INSERT INTO login (name, email, password_hash) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, enteredName);
                statement.setString(2, enteredEmail);
                statement.setString(3, enteredPassword);

                int rowsInserted = statement.executeUpdate(); // Use executeUpdate for INSERT

                return rowsInserted > 0; // true if one or more rows were inserted

            } catch (Exception e) {
                e.printStackTrace();
                return false; // Handle the error appropriately in onPostExecute
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(RegisterActivity.this, "Failed to register the user", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
