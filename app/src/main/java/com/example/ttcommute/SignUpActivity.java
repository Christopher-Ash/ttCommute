package com.example.ttcommute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {


    private EditText username, email, password;                          //fields to fill
    private Button btn_signup;                                          //sign up button
    private TextView existingaccount;                                  //sign in option for existing account
    private FirebaseAuth mAuth;                                             //Enable authentication with Firebase


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        username = (EditText) findViewById(R.id.username_signup);
        email = (EditText) findViewById(R.id.email_signup);
        password = (EditText) findViewById(R.id.password_signup);
        btn_signup = (Button) findViewById(R.id.button_signup);
        existingaccount = (TextView) findViewById(R.id.text_existingaccount);

        //what happens when sign up button is clicked
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    //uploading to Firebase database
                    String email_signupnew = email.getText().toString().trim();
                    String password_signupnew = password.getText().toString().trim();

                    mAuth.createUserWithEmailAndPassword(email_signupnew, password_signupnew).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                sendEmailVerify();;
                                //Toast.makeText(SignUpActivity.this, "Registration Complete! Verification Email has been sent to you", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                            } else {
                                // If sign in fails, display a message to the user
                                Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //redirects to login page
                    existingaccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        }
                    });
                }
            }

            //Ensuring all requested fields are entered
            private Boolean validate() {
                Boolean result = false;
                String Username = username.getText().toString();
                String Password = password.getText().toString();
                String Email = email.getText().toString();


                //Toast message to show that all fields were not entered for registration
                if (Username.isEmpty() || Password.isEmpty() || Email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    result = true;

                }

                return result;
            }
        });
    }
    private void sendEmailVerify(){
        FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "Registration Complete! Verification email sent",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Verification email can't be sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}

