package uk.co.tekkies.readings.model.content;

import java.io.File;

import uk.co.tekkies.readings.model.Prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class Mp3ContentLocator {
    
    protected final String TAG = "CONTENT_LOCATOR";

    public abstract String getBaseFolder(File potentialKeyFile);
    public abstract boolean confirmKeyFileFound(String baseFolder);
    public abstract String getKeyFileName();
    public abstract String getLiveDatabasePath();
    public abstract String getPassagePath(int passageId);
    
    public String getMp3Path(Context context, int passageId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String basePath = new Prefs(context).getMp3BasePath(); 
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
        return new Prefs(context).getMp3BasePath();
    }
    
    public static Mp3ContentLocator createChosenMp3ContentDescription(Context context) {
        //TODO: Read chosen Mp3ContentDescriber from prefs, instantiate and confirm installation 
        Mp3ContentLocator content = new LaridianNltMp3ContentLocator();
        if(!content.confirmKeyFileFound(context)) {
            content = null;
        }
        return content;
    }

}
