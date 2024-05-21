package com.example.hiss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    ImageButton signInButton;
    private Intent intent;
    String TAG;
    private boolean credentialManagerStatus = false;
    CredentialManager credentialManager;
    GetCredentialRequest credentialRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder()
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);


        signInButton = (ImageButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null){
            navigateToMainMenu();
        }
    }


    @Override
    public void onClick(View v) {
        if (signInButton==v)
        {
            signIn();
        }
    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                signInAccountTask.getResult(ApiException.class);
                navigateToMainMenu();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void navigateToMainMenu(){
        finish();
        Intent intent1 = new Intent(MainActivity.this, MainMenuActivity.class);
        startActivity(intent1);
    }
}