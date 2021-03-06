package com.threess.summership.treasurehunt.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.threess.summership.treasurehunt.MainActivity;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.logic.SavedData;
import com.threess.summership.treasurehunt.model.Language;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.model.User;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public final class Util {
    private static final String TAG = Util.class.getSimpleName();
    private static Random randomNumber = new Random(5);
    private static ArrayList<Language> mLanguages = new ArrayList<>();

    public static ArrayList<Treasure> treasureList = new ArrayList<>();
    public static ArrayList<User> userList = new ArrayList<>();

    public static void hideKeyboard(Context context, Button button) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
    }

    public static void makeSnackbar(View view, int textId, int length, int colorId) {
        Snackbar snackbar = Snackbar.make(view, textId, length);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), colorId));
        snackbar.show();
    }


    public static Bitmap getDrawableTreasureImage(Context context) {
        return Bitmap.createScaledBitmap(randomBitmap(context), 100, 100, false);
    }

    private static Bitmap randomBitmap(Context context) {
        switch (randomNumber.nextInt()) {
            case 0:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.t1);
            case 1:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.t2);
            case 2:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.t3);
            case 3:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.t4);
            case 4:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.t5);
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.t1);
        }
    }

    public static double distanceBetweenLatLngInMeter(LatLng currentPosition, LatLng treasurePosition) {
        double latDistance = Math.toRadians(treasurePosition.latitude - currentPosition.latitude);
        double lonDistance = Math.toRadians(treasurePosition.longitude - currentPosition.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(currentPosition.latitude)) * Math.cos(Math.toRadians(treasurePosition.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return Math.sqrt(Math.pow(((2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 6371000), 2));
    }

    public static void errorHandling(View view, String message, int requestCode) {
        if (message != null) {
            switch (requestCode) {
                case (Constant.LogIn.LOGIN_REQUEST_CODE)://LOGIN
                    if (message.contains(Constant.LogIn.INCORRECT_PASSWORD)) {
                        Util.makeSnackbar(view, R.string.incorrect_password, Snackbar.LENGTH_LONG, R.color.orange900);
                    } else if (message.contains(Constant.LogIn.USERNAME_NOT_EXISTS)) {
                        Util.makeSnackbar(view, R.string.username_not_exists, Snackbar.LENGTH_LONG, R.color.orangeA300);
                    } else {
                        Util.makeSnackbar(view, R.string.login_failed, Snackbar.LENGTH_SHORT, R.color.orange900);
                    }
                    break;
                case (Constant.Registration.REGISTRATION_REQUEST_CODE)://REGISTER
                    if (message.contains(Constant.HideTreasure.ALREADY_EXISTS)) {
                        Util.makeSnackbar(view, R.string.username_already_exists, Snackbar.LENGTH_LONG, R.color.orangeA300);
                    } else {
                        Util.makeSnackbar(view, R.string.registration_failed, Snackbar.LENGTH_SHORT, R.color.orange900);
                    }
                    break;
                case (Constant.HideTreasure.HIDE_TREASURE_REQUEST_CODE)://HIDE TREASURE
                    if (message.contains(Constant.HideTreasure.ALL_FIELDS_ARE_REQUIRED)) {
                        Util.makeSnackbar(view, R.string.all_fields_are_required, Snackbar.LENGTH_LONG, R.color.orangeA300);
                    } else {
                        Util.makeSnackbar(view, R.string.failed_operation, Snackbar.LENGTH_SHORT, R.color.orange900);
                    }
                    break;
                default:
                    Util.makeSnackbar(view, R.string.failed_operation, Snackbar.LENGTH_SHORT, R.color.orange900);
                    break;
            }
        } else {
            Util.makeSnackbar(view, R.string.failed_operation, Snackbar.LENGTH_SHORT, R.color.orange900);
        }
    }


    public static String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    /**
     * Checks if internet connection is available
     *
     * @return true if there is internet connection and false if not.
     */
    public static boolean requireInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }

    public static ArrayList<Language> getLanguages(Context context){
        if( mLanguages.isEmpty() ){
            mLanguages.add(new Language( Constant.SavedData.Language.LANGUAGE_KEY_ENGLISH, context.getResources().getString(R.string.language_eng), R.mipmap.ic_flag_eng) );
            mLanguages.add(new Language( Constant.SavedData.Language.LANGUAGE_KEY_ROMANIA, context.getResources().getString(R.string.language_ro), R.mipmap.ic_flag_eng) );
            mLanguages.add(new Language( Constant.SavedData.Language.LANGUAGE_KEY_HUNGARY, context.getResources().getString(R.string.language_hu), R.mipmap.ic_flag_eng) );
        }
        return mLanguages;
    }

    public static void changeApplicationLanguage(Context context, Language language) {
        changeApplicationLanguage(context, language.getKey());
    }

    public static void changeApplicationLanguage(Context context, String languageKey) {
        Locale locale = new Locale( languageKey );
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        new SavedData(context).setLanguage( Util.getLanguageById(context, languageKey) );
    }

    public static void loadSavedLanguage(Context context){
        Language language = new SavedData(context).getLanguage(context);
        if( language != null ){
            changeApplicationLanguage(context, language);
        }else{
            // load default english
            Language defaultLang = getLanguages(context).get(0);
            changeApplicationLanguage(context, defaultLang );
        }
    }

    public static Language getLanguageById(Context context, String id){
        for( Language lang : getLanguages(context) ){
            if( lang.getKey().equals(id) ){
                return lang;
            }
        }
        return null;
    }

    public static void restartApp(MainActivity activity) {
        Intent mStartActivity = new Intent(activity.getApplicationContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}
