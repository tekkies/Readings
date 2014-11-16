package uk.co.tekkies.readings.model;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.util.Analytics;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

public class Prefs {

    public static final String PREF_PASSAGE_TEXT_SIZE = "passageTextSize";
    public static final String PREF_MP3_BASE_PATH = "mp3BasePath";
    public static final String PREF_MP3_PRODUCT = "mp3Product";
    public static final String VERSION_KEY = "version_number";
    public final static String PREF_SHOW_SUMMARY = "ShowSummary";



    SharedPreferences sharedPreferences;
    Context context;

    public Prefs(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
    
    private void saveFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String loadString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    private void saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean loadBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    
    public double loadPassageTextSize() {
        return sharedPreferences.getFloat(PREF_PASSAGE_TEXT_SIZE, 1);
    }

    public void savePassageTextSize(double textSize) {
        saveFloat(PREF_PASSAGE_TEXT_SIZE, (float) textSize);
    }

    /**
     * @return Empty string if no path set
     */
    public String loadMp3Product() {
        return loadString(PREF_MP3_PRODUCT, "");
    }
    
    public void saveMp3Product(String product) {
        Analytics.PrefsChange(context, PREF_MP3_PRODUCT);
        saveString(PREF_MP3_PRODUCT, product);
    }

    public void saveBasePath(Mp3ContentLocator mp3ContentLocator) {
        saveString(mp3ContentLocator.getClass().getName(), mp3ContentLocator.getBasePath()); 
    }
    
    public void loadBasePath(Mp3ContentLocator mp3ContentLocator) {
        mp3ContentLocator.setBasePath(sharedPreferences.getString(mp3ContentLocator.getClass().getName(), ""));
    }

    public boolean loadAnalyticsEnabled() {
        String key = context.getString(R.string.pref_key_enable_analytics);
        boolean enabled=sharedPreferences.getBoolean(key, false);
        if(!enabled) {
            //Check if it just wan't set 
            enabled = sharedPreferences.getBoolean(key, true);
            if(enabled) {
                //different result: Pref was never set.
                enabled = setDefaultAnalyticsEnabled(context, sharedPreferences, key);
            }
        }
        return enabled;
    }
    
    /** Sets analytics enabled for release (even) version code, disabled for internal (odd) version code.
     */
    private boolean setDefaultAnalyticsEnabled(Context context, SharedPreferences sharedPreferences, String key) {
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            Analytics.reportCaughtException(context, e);
        }
        boolean enabled = ((versionCode % 2) == 0); //Enabled by default in even (release) 
        saveBoolean(key, enabled);
        return enabled;
    }

    public boolean isMp3ProductUndefined() {
        final String notSet = "not-set";
        return loadString(Prefs.PREF_MP3_PRODUCT, notSet).equals(notSet);
    }

    public boolean checkForUpgrade() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int currentVersionNumber = 0;
        int savedVersionNumber = sharedPref.getInt(VERSION_KEY, 0);
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception e) {
            Analytics.reportCaughtException(context, e);
        }
        if (currentVersionNumber > savedVersionNumber) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(VERSION_KEY, currentVersionNumber);
            editor.commit();
        }
        return (currentVersionNumber > savedVersionNumber);
    }

    public void saveShowSummaries(boolean show) {
        saveBoolean(PREF_SHOW_SUMMARY, show);
    }
}
