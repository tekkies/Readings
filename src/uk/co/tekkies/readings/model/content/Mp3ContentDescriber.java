package uk.co.tekkies.readings.model.content;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class Mp3ContentDescriber {
    
    protected final String TAG = "CONTENT";
    public static final String SETTING_BASE_PATH = "basePath";


    public abstract String getBaseFolder(File potentialKeyFile);
    public abstract boolean confirmKeyFileFound(String baseFolder);
    public abstract String getKeyFileName();
    public abstract String getLiveDatabasePath();
    public abstract String getPassagePath(int passageId);
    
    public String getMp3Path(Context context, int passageId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String basePath = settings.getString(SETTING_BASE_PATH, "");
        String mp3Path = basePath + File.separator + getPassagePath(passageId);
        return mp3Path;
    }
    public boolean confirmKeyFileFound(Context context) {
        boolean confirmed = false;
        String basePath = getBasePath(context);
        if(basePath != null) {
            confirmed = confirmKeyFileFound(basePath);
        }
        return confirmed;
    }
    
    public String getBasePath(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(Mp3ContentDescriber.SETTING_BASE_PATH, null);
    }
    
    public static Mp3ContentDescriber createChosenMp3ContentDescription(Context context) {
        //TODO: Read chosen Mp3ContentDescriber from prefs, instantiate and confirm installation 
        Mp3ContentDescriber content = new LaridianNltMp3ContentDescriber();
        if(!content.confirmKeyFileFound(context)) {
            content = null;
        }
        return content;
    }

}
