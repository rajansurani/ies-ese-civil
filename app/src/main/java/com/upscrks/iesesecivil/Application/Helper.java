package com.upscrks.iesesecivil.Application;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.LruCache;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Helper {

    static int cacheSize = 4 * 1024 * 1024; // 4MiB
    public static LruCache<String, Drawable> imageCache = new LruCache<>(cacheSize);
    static LruCache<String, String> cache = new LruCache<>(cacheSize);

    private static FirebaseFirestore mFirestore;
    public static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public static FirebaseFirestore getFirestore() {
        if (mFirestore == null) {
            mFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            mFirestore.setFirestoreSettings(settings);
        }
        return mFirestore;
    }

    public static void setIntegerSharedPreference(String key, int value, Context context) {

        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntegerSharedPreference(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getInt(key, 0);
    }

    public static void setLongSharedPreference(String key, long value, Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLongSharedPreference(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getLong(key, 0);
    }

    public static void setStringSharedPreference(String key, String value, Context context) {

        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    public static void setBooleanSharedPreference(String key, boolean value, Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanSharedPreference(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    public static void clearSharedPreference(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    public static String getStringSharedPreference(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static String getStringSharedPreference(String key, String def, Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(key, def);
    }

    public static void removeSharedPreference(String key, Context context) {

        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(key);
        editor.commit();
    }

    public static void deleteAllUserData(Context context) {

        SharedPreferences.Editor editor = getEditor(context);
        editor.apply();

        SharedPreferences.Editor pref_reg = context.getApplicationContext().getSharedPreferences("BDFREG2", 0).edit();
        pref_reg.remove("registration_id");
        pref_reg.apply();

    }

    public static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        return editor;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String currentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String currentDateNumber() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String currentDateTimeNumber() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getDateTimeNumber(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    public static Date parseDateTimeNumber(String dateNumber) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return dateFormat.parse(dateNumber);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String millisToDate(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }

    public static <T> List<T> filter(Collection<T> target, IPredicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T element : target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static String toCamelCase(String value) {
        if (value != null && value.length() > 0) {
            String a = value.substring(0, 1);
            String b = value.substring(1);
            return a.toUpperCase() + b.toLowerCase();
        } else
            return "";
    }

    public static String formateDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static String formatCurrency(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(value);
    }

    public static String formatCurrencywithoutDecimal(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(value);
    }

    public static String formatCurrencyWithoutDecimal(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String formatted = formatter.format(value);
        return formatted.substring(0, formatted.indexOf("."));
    }

    public static boolean IsNullOrEmpty(String value) {
        return "".equals(value) || value == null;
    }

    public static boolean checkPermission(Context context) {
        if (
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            return false;

        } else {
            return true;
        }
    }

    public static String formatNumber(int number) {
        return String.format("%020d", number);
    }

    public static void createNotificationChannel(Context context, String channel_name, String channel_description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_name, channel_name, importance);
            channel.setDescription(channel_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void cacheMcq(String link, String key, Context context) {
        Glide.with(context)
                .load(link)
                .fitCenter() //fits given dimensions maintaining ratio
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Helper.imageCache.put(key, resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public interface IPredicate<T> {
        boolean apply(T type);
    }
}
