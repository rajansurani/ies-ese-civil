package com.upscrks.iesesecivil.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.R;

import java.util.HashMap;

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvEmail)
    TextView tvEmail;

    @BindView(R.id.tvQuestionCount)
    TextView tvQuestionCount;

    ReviewInfo reviewInfo;
    ReviewManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Glide.with(ProfileActivity.this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.avatar_big)
                .apply(new RequestOptions().circleCrop())
                .into((ImageView) findViewById(R.id.ivProfile));

        mDataAccess.getCurrentUser(false, user->{
            if(!Helper.IsNullOrEmpty(user.getName()))
                tvName.setText(user.getName());
            else {
                tvName.setText(mAuth.getCurrentUser().getDisplayName());
            }
            if(!Helper.IsNullOrEmpty(user.getEmail()))
                tvEmail.setText(user.getEmail());
            else
                tvEmail.setText("Anonymous");
            tvQuestionCount.setText(""+user.getQuestionsSolved());
        });
        preCacheReviewObject();

    }

    public void preCacheReviewObject() {
        manager = ReviewManagerFactory.create(this);
        com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
                Log.d("Review", "preCacheReviewObject: ReviewObject Generated");
            } else {
                // There was some problem, continue regardless of the result.
                reviewInfo = null;
                Log.d("Review", "preCacheReviewObject: ReviewObject Error");
            }
        });
    }

    @OnClick(R.id.layoutSolve)
    public void OnClickSolveQuestions(){
        startActivity(new Intent(this, SubjectActivity.class));
        finish();
    }

    @OnClick(R.id.layoutSignOut)
    public void OnClickSignOut(){
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }

    @OnClick(R.id.back)
    public void OnClickBack(){
        finish();
    }

    @OnClick(R.id.btnShare)
    public void OnClickShare(){
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store/apps/details?id=com.upscrks.iesesecivil"))
                .setDomainUriPrefix("https://iesesecivil.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT,
                                    "Hey, go check out this amazing app which helps you prepare for IES Civil :\n " + shortLink.toString());
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);
                            mDataAccess.logEvent("SHARE EVENT", "");
                        }
                    }
                });
    }

    @OnClick(R.id.layoutRate)
    public void OnClickRate(){
        try {
            com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task -> {
            });
        } catch (Exception e) {

        }
    }
}

