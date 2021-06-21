package com.upscrks.iesesecivil.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.upscrks.iesesecivil.Application.Constants;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.BuildConfig;
import com.upscrks.iesesecivil.Database.DataAccess;
import com.upscrks.iesesecivil.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected DataAccess mDataAccess;
    protected FirebaseDatabase firebaseDatabase;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected FirebaseStorage mStorage;
    protected FirebaseCrashlytics mFirebaseCrash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mDataAccess = DataAccess.getInstance(getApplicationContext());

        mStorage = FirebaseStorage.getInstance();
        firebaseDatabase = Helper.getDatabase();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = firebaseDatabase.getReference(Constants.FIREBASE_DB);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            mFirebaseAnalytics.setUserId(mAuth.getCurrentUser().getUid());
        }
        mFirebaseCrash = FirebaseCrashlytics.getInstance();
        mFirebaseCrash.setUserId(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "");
        if (BuildConfig.DEBUG) {
            mFirebaseCrash.setCrashlyticsCollectionEnabled(false);
        } else {
            mFirebaseCrash.setCrashlyticsCollectionEnabled(true);
        }
    }
}
