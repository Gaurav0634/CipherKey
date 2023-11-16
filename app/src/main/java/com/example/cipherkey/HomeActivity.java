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

import org.mindrot.jbcrypt.BCrypt;

public class HomeActivity extends AppCompatActivity {
    private TextView platformTextView, usernameTextView, passwordTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        platformTextView = findViewById(R.id.textView10);
        usernameTextView = findViewById(R.id.textView11);
        passwordTextView = findViewById(R.id.textView12);

        // Retrieve and display a saved password
        new DatabaseConnectionTask().execute();
    }

    private class DatabaseConnectionTask<Password> extends AsyncTask<Void, Void, Password> {
        @Override
        protected Password doInBackground(Void... params) {
            // Replace with your actual database connection details
            String url = DatabaseConfig.url;
            String username = DatabaseConfig.username;
            String password = DatabaseConfig.password;

            try {
                Connection connection = DriverManager.getConnection(url, username, password);

                // SQL query to retrieve a saved password (customize based on your schema)
                String sql = "SELECT website, username, password FROM user_passwords WHERE user_id = ? LIMIT 1";
                PreparedStatement statement = connection.prepareStatement(sql);

                // Set the user's ID; replace with the actual user's ID
                int userId = 1;
                statement.setInt(1, userId);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String website = resultSet.getString("website");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    return new Password(website, username, password);
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Handle any exceptions here
            }

            return null;
        }

        @Override
        protected void onPostExecute(Password savedPassword) {
            if (savedPassword != null) {
                platformTextView.setText("Platform: " + savedPassword.getWebsite());
                usernameTextView.setText("Username: " + savedPassword.getUsername());
                passwordTextView.setText("Password: " + savedPassword.getPassword());
            } else {
                Toast.makeText(HomeActivity.this, "No saved passwords found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
