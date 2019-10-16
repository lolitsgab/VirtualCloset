package com.example.virtualcloset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText password, email;
    private Button login_button;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progress = findViewById(R.id.log_in_progress);
        password = findViewById(R.id.password_login_field);
        email = findViewById(R.id.email_login_field);
        login_button = findViewById(R.id.log_in_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogIn();
                Toast.makeText(LoginActivity.this, "DEBUG MESSAGE\nEmail: " +
                        email.getText() + "\nPassword: " + password.getText() +
                        "\nLogging in now", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void attemptLogIn() {
        progress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT);
                            progress.setVisibility(View.GONE);

                            // Switch activities
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid credentials!",
                                    Toast.LENGTH_SHORT);
                            progress.setVisibility(View.GONE);
                        }
                    }
                });
    }


}
