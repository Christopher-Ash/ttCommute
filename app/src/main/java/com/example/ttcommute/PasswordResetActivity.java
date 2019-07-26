package com.example.ttcommute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private Button ResetPassword;
    private EditText Email_Reset;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //Firebase Instance
        firebaseAuth = FirebaseAuth.getInstance();

        ResetPassword = (Button) findViewById(R.id.button_passwordreset);
        Email_Reset = (EditText) findViewById(R.id.email_passwordreset);

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_reset = Email_Reset.getText().toString().trim();

                if (email_reset.equals("")) {
                    Toast.makeText(PasswordResetActivity.this, "Please enter the email of your registered account", Toast.LENGTH_SHORT).show();

                } else {
                    firebaseAuth.sendPasswordResetEmail(email_reset).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordResetActivity.this, "Email sent! Please check your email to reset your password", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(PasswordResetActivity.this, "Email could not be sent to address provided", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

        });
    }
}

