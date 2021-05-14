package com.upscrks.iesesecivil.Utils;

import android.app.Activity;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.upscrks.iesesecivil.R;

public class AdsUtils {

    public static void loadBannerAd(Activity activity) {
        AdView mAdView = new AdView(activity);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(activity.getResources().getString(R.string.bannerAdId));
        LinearLayout adContainer = activity.findViewById(R.id.banner_ad_container);
        adContainer.removeAllViews();
        adContainer.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("Ads", "onAdLoaded: Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d("Ads", "onAdLoaded: Ad Failed "+loadAdError.getMessage());
            }
        });

    }
}
