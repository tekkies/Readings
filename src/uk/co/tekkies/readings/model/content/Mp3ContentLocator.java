package uk.co.tekkies.readings.model.content;

import java.io.File;

import uk.co.tekkies.readings.model.Prefs;

import android.content.Context;

public abstract class Mp3ContentLocator {
    
    protected final String TAG = "CONTENT_LOCATOR";
    
    private String basePath="";

    public abstract String getBaseFolder(File potentialKeyFile);
    public abstract boolean confirmKeyFileFound(String baseFolder);
    public abstract String getKeyFileName();
    public abstract String getLiveDatabasePath();
    public abstract String getPassagePath(int passageId);
    public abstract String getTitle();
    
    public String getMp3Path(Context context, int passageId) {
        String basePath = new Prefs(context).getMp3BasePath(); 
        String mp3Path = basePath + File.separator + getPassagePath(passageId);
        return mp3Path;
    }
    public boolean confirmKeyFileFound(Context context) {
        boolean confirmed = false;
        if(getBasePath() != null) {
            confirmed = confirmKeyFileFound(getBasePath());
        }
        return confirmed;
    }
    
    public void loadBasePath(Context context) {
        setBasePath(new Prefs(context).getMp3BasePath());
    }
    
    public static Mp3ContentLocator createChosenMp3ContentDescription(Context context) {
        Mp3ContentLocator contentLocator=null;
        String mp3Product = new Prefs(context).getMp3Product();
        contentLocator = newContentLocator(context, mp3Product);
        if(contentLocator != null) {
            if(!contentLocator.confirmKeyFileFound(context)) {
                contentLocator = null;
            }
        }
        return contentLocator;
    }

    public String getBasePath() {
        return basePath;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public static Mp3ContentLocator[] createSupportedMp3ContentLocators() {
        Mp3ContentLocator[] mp3ContentLocators = { 
                new NltLaridianMp3ContentLocator(),
                new KjvScourbyMp3ContentLocator(),
                new KjvListenersMp3ContentLocator()
             };
        return mp3ContentLocators; 
    }
    
    /**
     * Creates an instance of the named mp3Contentlocator and loads the base path
     * @param context 
     * @param mp3Product Name of the Mp3Contentlocator class to create 
     * @return Null if no match
     */
    private static Mp3ContentLocator newContentLocator(Context context, String mp3Product) {
        Mp3ContentLocator contentLocator = null;
        if(KjvScourbyMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new KjvScourbyMp3ContentLocator();    
        }
        else if(NltLaridianMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new NltLaridianMp3ContentLocator();    
        }
        else if(KjvListenersMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new KjvListenersMp3ContentLocator();    
        }
        if(contentLocator != null) {
            contentLocator.loadBasePath(context);
        }
        return contentLocator;
    }
    

}
