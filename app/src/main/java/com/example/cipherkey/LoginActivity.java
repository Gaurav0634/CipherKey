package com.example.cipherkey;
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


public class LoginActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    Button loginEditButton;
    TextView navigateToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailInput);
        passwordEditText = findViewById(R.id.passwordInput);
        loginEditButton = findViewById(R.id.loginButton);
        navigateToLogin = findViewById(R.id.textView2);

        loginEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });


    }
    public void login(View view) {
        String enteredEmail = emailEditText.getText().toString();
        String enteredPassword = passwordEditText.getText().toString();

        new DatabaseConnectionTask().execute(enteredEmail, enteredPassword);
    }

    private class DatabaseConnectionTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String enteredEmail = params[0];
            String enteredPassword = params[1];

            try {
                Connection connection = DriverManager.getConnection(DatabaseConfig.url, DatabaseConfig.username, DatabaseConfig.password);
                String sql = "SELECT password_hash, salt FROM login WHERE email = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, enteredEmail);

                ResultSet resultSet = statement.executeQuery();

                if(resultSet.next()){
                    // Retrieve the stored password_hash and salt
                    String storedHashedPassword = resultSet.getString("password_hash");
                    String salt = resultSet.getString("salt");

                    //Combine the stored salt and Inputed Password
                    String combinedPassword = BCrypt.hashpw(enteredPassword, salt);

                    //Compare combinePassword and storeHashedPassword
                    return  BCrypt.checkpw(enteredPassword, storedHashedPassword);
                    } else{
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false; // Handle the error appropriately in onPostExecute
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}