package com.example.ttcommute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class HomePageActivity extends AppCompatActivity {
    private Button logout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() ==null){
                    startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
                }
            }
        };

        logout = (Button)findViewById(R.id.button_logout);

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mAuth.signOut();
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }
}
