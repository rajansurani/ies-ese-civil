package com.upscrks.iesesecivil.Fragment;

import android.os.Bundle;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    protected DataAccess mDataAccess;
    protected FirebaseDatabase firebaseDatabase;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected FirebaseStorage mStorage;
    protected FirebaseCrashlytics mFirebaseCrash;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataAccess = DataAccess.getInstance(getContext());

        mStorage = FirebaseStorage.getInstance();
        firebaseDatabase = Helper.getDatabase();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = firebaseDatabase.getReference(Constants.FIREBASE_DB);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
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
