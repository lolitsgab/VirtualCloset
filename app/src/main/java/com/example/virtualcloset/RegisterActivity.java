package com.example.virtualcloset;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import com.google.firebase.auth.UserProfileChangeRequest;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;



public class RegisterActivity extends AppCompatActivity {

    EditText userEmail, userName, userPassword, userConfirm;
    ImageView registerBtn;
    TextView alreadyAcct;
    private FirebaseAuth mAuth;

    public static String theirEmail, theirPassword, theirUserName, theirConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.email);
        userName = findViewById(R.id.user);
        userPassword = findViewById(R.id.password);
        userConfirm = findViewById(R.id.confirmPassword);
        registerBtn = findViewById(R.id.rectangleRegister);
        alreadyAcct = findViewById(R.id.alreadyAccount);


        theirEmail = userEmail.getText().toString();
        theirUserName = userName.getText().toString();
        theirPassword = userPassword.getText().toString();
        theirConfirm = userConfirm.getText().toString();

        // if user clicks TextView link, transfer them to traditional log in page
        alreadyAcct.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent arg1) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            }
        });

        // registering user
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(readyToRegister()) {
                    sendToFireBase();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Database error", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    // verifying that user information is acceptable
    private void sendToFireBase() {
        mAuth.createUserWithEmailAndPassword(theirEmail, theirPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(theirUserName)
                                    .build();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
    private boolean readyToRegister() {

        if(theirEmail.isEmpty()) {
            Toast.makeText(this, "Please enter a email", Toast.LENGTH_LONG).show();
            return false;
        }

        if(theirUserName.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show();
            return false;
        }

        if(theirUserName.length() < 5) {
            Toast.makeText(this, "Username must be more than 5 characters",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(theirPassword.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
            return false;
        }
        if(theirPassword.length() < 8) {
            Toast.makeText(this, "Password must be more than 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        if(theirConfirm.isEmpty()) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_LONG);
            return false;
        }
        if(!theirConfirm.equals(theirPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
