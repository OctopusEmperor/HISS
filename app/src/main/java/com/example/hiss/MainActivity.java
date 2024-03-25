package com.example.hiss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.PasswordCredential;
import androidx.credentials.PublicKeyCredential;
import androidx.credentials.exceptions.GetCredentialException;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener, View.OnClickListener {

    androidx.credentials.CredentialManager credMan;
    CancellationSignal cs;

    private static final String TAG = "MySignInActivity";

    private FirebaseAuth firebaseAuth;
    CalendarView calendar;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        credMan = CredentialManager.create(this);
        firebaseAuth = FirebaseAuth.getInstance();

        calendar.setOnDateChangeListener(this);
        signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onClick(View v) {
        if (signInButton==v)
        {
            cs = new CancellationSignal();
            GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId("353278388473-0feqddjdm87hc4u20g56ns3b97p89g05.apps.googleusercontent.com")
                    .build();
            GetCredentialRequest request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build();
            credMan.getCredentialAsync(
                    this, request,
                    null,
                    Executors.newSingleThreadExecutor(),
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleSignIn(result);
                        }

                        @Override
                        public void onError(GetCredentialException e) {

                        }
                    });
        }
    }
    public void handleSignIn (GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential instanceof PublicKeyCredential) {
            String responseJson = ((PublicKeyCredential) credential).getAuthenticationResponseJson();
        }
        else if (credential instanceof PasswordCredential) {
            String username = ((PasswordCredential) credential).getId();
            String password = ((PasswordCredential) credential).getPassword();
        }
        else if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                } catch (GoogleIdTokenParsingException e) {
                    Log.e(TAG, "Received an invalid Google ID token response", e);
                }
            }
            else {
                Log.e(TAG, "Unexpected type of credential");
            }
        }
        else {
            Log.e(TAG, "Unexpected type of credential");
        }
    }
}