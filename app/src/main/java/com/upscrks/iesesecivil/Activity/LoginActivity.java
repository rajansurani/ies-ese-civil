package com.upscrks.iesesecivil.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.Database.Model.User;
import com.upscrks.iesesecivil.R;
import com.upscrks.iesesecivil.Utils.OnCompleteSingleListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private static final int GOOGLE_SIGN_IN = 1002;
    private ProgressDialog dialog;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Connection Error", Toast.LENGTH_LONG);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @OnClick(R.id.btnGoogleLogin)
    public void googleSignin() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account != null ? account.getIdToken() : null, null);
                connectToFirebase(credential, account.getIdToken(), "google", false);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectToFirebase(AuthCredential credential, String token, String login, boolean isAnonymous) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging you in ...");
        dialog.show();

        OnCompleteListener completeListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(LoginActivity.this, "SignIn Success ", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    mDataAccess.getCurrentUser(false, new OnCompleteSingleListener<User>() {
                        @Override
                        public void OnComplete(@Nullable User user) {
                            Log.d("Login", "OnComplete: user=" + user);
                            Map<String, Object> map = new HashMap<>();
                            map.put("uid", firebaseUser.getUid());
                            map.put("name", firebaseUser.getDisplayName());
                            map.put("email", firebaseUser.getEmail());
                            if (firebaseUser.getPhotoUrl() != null)
                                map.put("pic", firebaseUser.getPhotoUrl().getPath());
                            if (login == "google") {
                                map.put("google_id", token);
                            }
                            if (user == null) {
                                map.put("created_on", Helper.currentDateTime());
                            }

                            mDataAccess.updateUser(map, object -> {
                            });

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    });

                } else {
                    Toast.makeText(LoginActivity.this, "SignIn Failed Please try again", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (!isAnonymous) {
            if (mAuth.getCurrentUser() != null)
                mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, completeListener);
            else
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, completeListener);
            Helper.setBooleanSharedPreference("anonymousLogin", false, this);
        } else {
            mAuth.signInAnonymously().addOnCompleteListener(this, completeListener);
            Helper.setBooleanSharedPreference("anonymousLogin", true, this);
        }
    }

    @OnClick(R.id.skipLogin)
    public void OnClickSkipLogin() {
        if (mAuth.getCurrentUser() != null)
            finish();
        else
            connectToFirebase(null, null, null, true);
    }
}