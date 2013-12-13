package uk.co.tekkies.readings.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {

    public static final String PREF_PASSAGE_TEXT_SIZE = "passageTextSize";
    public static final String PREF_MP3_BASE_PATH = "mp3BasePath";

    SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
    
    private void putFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    private void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public double getPassageTextSize() {
        return sharedPreferences.getFloat(PREF_PASSAGE_TEXT_SIZE, 1);
    }

    public void setPassageTextSize(double textSize) {
        putFloat(PREF_PASSAGE_TEXT_SIZE, (float) textSize);
    }

    /**
     * @return Empty string if no path set
     */
    public String getMp3BasePath() {
        return sharedPreferences.getString(PREF_MP3_BASE_PATH, "");
    }
    
    public void setMp3BasePath(String mp3BasePath) {
        putString(PREF_PASSAGE_TEXT_SIZE, mp3BasePath);
    }

}
