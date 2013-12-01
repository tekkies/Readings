package uk.co.tekkies.readings.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {
    
    public static final String PREF_PASSAGE_TEXT_SIZE = "passageTextSize";
    
    SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public double getPassageTextSize() {
        return sharedPreferences.getFloat(PREF_PASSAGE_TEXT_SIZE, 1);
    }

    public void setPassageTextSize(double textSize) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(PREF_PASSAGE_TEXT_SIZE, (float) textSize);
        editor.commit();
    }
   
}
