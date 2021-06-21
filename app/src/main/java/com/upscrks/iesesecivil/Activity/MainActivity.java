package com.upscrks.iesesecivil.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.upscrks.iesesecivil.Adapter.HomeViewPagerAdapter;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.BuildConfig;
import com.upscrks.iesesecivil.Database.Model.MCQ;
import com.upscrks.iesesecivil.R;
import com.upscrks.iesesecivil.Utils.CSVUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tvSubHeading)
    TextView tvSubHeading;

    @BindView(R.id.tvHeading)
    TextView tvHeading;

    @BindView(R.id.viewpager)
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sendRegistrationIdToServer();
        setupViewpager();
        Glide.with(MainActivity.this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.default_avatar)
                .apply(new RequestOptions().circleCrop())
                .into((ImageView) findViewById(R.id.avatar));

        OnClickButtonHome();

        initializeAdMob();
        //CSVUtils.addMCQs(this);

        /*Map<String, Object> filters = new HashMap<>();
        filters.put("subject", "Geotech and Foundation Engineering");
        filters.put("prevYear", false);
        mDataAccess.getMCQ(filters, "createdOn", 200,
                0,
                Query.Direction.ASCENDING,
                null, mcqs -> {
                    for(MCQ mcq: mcqs){
                        mDataAccess.deleteMcq(mcq.getQuestionId());
                    }
                });*/
    }

    private void activeIcon(String active) {
        ((ImageView) findViewById(R.id.ic_home)).setImageTintList(getResources().getColorStateList(R.color.grey));
        ((ImageView) findViewById(R.id.ic_notes)).setImageTintList(getResources().getColorStateList(R.color.grey));
        ((ImageView) findViewById(R.id.ic_books)).setImageTintList(getResources().getColorStateList(R.color.grey));

        ((TextView) findViewById(R.id.text_home)).setTextColor(getResources().getColorStateList(R.color.grey));
        ((TextView) findViewById(R.id.text_notes)).setTextColor(getResources().getColorStateList(R.color.grey));
        ((TextView) findViewById(R.id.text_books)).setTextColor(getResources().getColorStateList(R.color.grey));

        ((LinearLayout) findViewById(R.id.btnHome)).setBackground(getResources().getDrawable(R.drawable.custom_background_white_75r));
        ((LinearLayout) findViewById(R.id.btnNotes)).setBackground(getResources().getDrawable(R.drawable.custom_background_white_75r));
        ((LinearLayout) findViewById(R.id.btnBooks)).setBackground(getResources().getDrawable(R.drawable.custom_background_white_75r));

        switch (active) {
            case "home":
                tvHeading.setText("IES Civil Engineering");
                tvSubHeading.setText("WELCOME");
                ((ImageView) findViewById(R.id.ic_home)).setImageTintList(getResources().getColorStateList(R.color.white));
                ((TextView) findViewById(R.id.text_home)).setTextColor(getResources().getColorStateList(R.color.white));
                ((LinearLayout) findViewById(R.id.btnHome)).setBackground(getResources().getDrawable(R.drawable.custom_background_blue_75r));
                break;
            case "notes":
                tvHeading.setText("Notes");
                tvSubHeading.setText("IES Civil Engineering");
                ((ImageView) findViewById(R.id.ic_notes)).setImageTintList(getResources().getColorStateList(R.color.white));
                ((TextView) findViewById(R.id.text_notes)).setTextColor(getResources().getColorStateList(R.color.white));
                ((LinearLayout) findViewById(R.id.btnNotes)).setBackground(getResources().getDrawable(R.drawable.custom_background_blue_75r));
                break;
            case "books":
                tvHeading.setText("Books");
                tvSubHeading.setText("IES Civil Engineering");
                ((ImageView) findViewById(R.id.ic_books)).setImageTintList(getResources().getColorStateList(R.color.white));
                ((TextView) findViewById(R.id.text_books)).setTextColor(getResources().getColorStateList(R.color.white));
                ((LinearLayout) findViewById(R.id.btnBooks)).setBackground(getResources().getDrawable(R.drawable.custom_background_blue_75r));
                break;
        }
    }

    @OnClick(R.id.btnHome)
    public void OnClickButtonHome() {
        activeIcon("home");
        viewPager2.setCurrentItem(0, false);
    }

    @OnClick(R.id.btnNotes)
    public void OnClickButtonNotes() {
        activeIcon("notes");
        viewPager2.setCurrentItem(1, false);
    }

    @OnClick(R.id.btnBooks)
    public void OnClickButtonBooks() {
        activeIcon("books");
        viewPager2.setCurrentItem(2, false);
    }

    @OnClick(R.id.avatar)
    public void OnClickProfile() {
        if (Helper.getBooleanSharedPreference("anonymousLogin", this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Profile")
                    .setMessage("You are not logged in. Please login first.")
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });
            builder.create().show();
        } else
            startActivity(new Intent(this, ProfileActivity.class));
    }

    private void setupViewpager() {
        viewPager2.setAdapter(new HomeViewPagerAdapter(MainActivity.this));
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.setUserInputEnabled(false);
    }

    private void sendRegistrationIdToServer() {
        new Thread(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("fcm_id", FirebaseInstanceId.getInstance().getToken());
            map.put("app_version", BuildConfig.VERSION_CODE);
            map.put("last_usage", Helper.currentDateTime());

            mDataAccess.updateUser(map, complete -> {
            });
        }).start();
    }

    private void initializeAdMob(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }
}