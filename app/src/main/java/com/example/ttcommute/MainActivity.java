package com.example.ttcommute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText UserName, PassWord;
    private Button FBLogin, Login;
    private TextView ForgotPassword, SkipLogin, SignUp;
    private int counter = 10;
    private FirebaseAuth mAuth;
    private SignInButton GoogleLogin;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "Main";
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if(firebaseAuth.getCurrentUser() !=null){
                   startActivity(new Intent(MainActivity.this, HomePageActivity.class));
               }
            }
        };


        //Checking if user has logged in
        FirebaseUser user = mAuth.getCurrentUser();

        //if user is signed in go to homepage
        if (user != null) {
            finish();
            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
        }

        //mapping xml object id's to variables used here

        UserName = (EditText) findViewById(R.id.username_login);
        PassWord = (EditText) findViewById(R.id.password_login);
        GoogleLogin = (SignInButton) findViewById(R.id.button_googlelogin);
        FBLogin = (Button) findViewById(R.id.button_fblogin);
        Login = (Button) findViewById(R.id.button_login);
        ForgotPassword = (TextView) findViewById((R.id.password_forget));
        SkipLogin = (TextView) findViewById(R.id.skip_login);
        SignUp = (TextView) findViewById(R.id.text_signup);

        //Skip Login
        SkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HomePageActivity.class));
            }
        });

        //login for Email/Password login type on login page
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(UserName.getText().toString(), PassWord.getText().toString());
            }
        });

        //sign up for Email/Password option on login page
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        //forgot password button configuration
        ForgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this, PasswordResetActivity.class));


            }
        });




        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        //take this out if it doesn't work
        mGoogleSignInClient.connect();

        GoogleLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

     //Google sign in Activity
    //sign in method
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success" +task.isSuccessful());
                            Toast.makeText(MainActivity.this, "SUCCESS!!", Toast.LENGTH_SHORT ).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }



    private void validate(String Username, String Password) {
        if (Username.isEmpty() || Password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

        } else {
            mAuth.signInWithEmailAndPassword(Username, Password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkVerifyEmail();
                    } else {
                        Toast.makeText(MainActivity.this, "Username or Password does not match existing account. Please try again", Toast.LENGTH_LONG).show();
                        counter--;
                        if (counter == 0) {
                            Login.setEnabled(false);
                            Toast.makeText(MainActivity.this, "You can no longer sign in", Toast.LENGTH_SHORT).show();

                        }
                    }
                }


            });
        }
    }
    //Email Verification Method
    private void checkVerifyEmail(){
        FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();
        Boolean email_ver = firebaseUser.isEmailVerified();

        if(email_ver){
            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
        }
        else{
            Toast.makeText(MainActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
            //Sign out user
            mAuth.signOut();
        }
    }

}




