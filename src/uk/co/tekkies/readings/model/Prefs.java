package uk.co.tekkies.readings.model;

import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {

    public static final String PREF_PASSAGE_TEXT_SIZE = "passageTextSize";
    public static final String PREF_MP3_BASE_PATH = "mp3BasePath";
    public static final String PREF_MP3_PRODUCT = "mp3Product";

    SharedPreferences sharedPreferences;

    public Prefs(Context context) {
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
        saveString(PREF_MP3_PRODUCT, product);
    }

    public void saveBasePath(Mp3ContentLocator mp3ContentLocator) {
        saveString(mp3ContentLocator.getClass().getName(), mp3ContentLocator.getBasePath()); 
    }
    
    public void loadBasePath(Mp3ContentLocator mp3ContentLocator) {
        mp3ContentLocator.setBasePath(sharedPreferences.getString(mp3ContentLocator.getClass().getName(), ""));
    }

}
