package uk.co.tekkies.readings.model.content;

import java.io.File;
import java.util.ArrayList;

import uk.co.tekkies.readings.model.Prefs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

public abstract class Mp3ContentLocator {
    
    protected final String TAG = "CONTENT_LOCATOR";
    
    private String basePath="";

    public abstract String getBaseFolder(File potentialKeyFile);
    public abstract boolean confirmKeyFileFound(String baseFolder);
    public abstract String getKeyFileName();
    public abstract String getPassagePath(int passageId);
    public abstract String getTitle();
    
    public boolean confirmKeyFileFound(Context context) {
        boolean confirmed = false;
        if(getBasePath() != null) {
            confirmed = confirmKeyFileFound(getBasePath());
        }
        return confirmed;
    }

    public static Mp3ContentLocator createChosenMp3ContentDescription(Context context) {
        Mp3ContentLocator contentLocator=null;
        String mp3Product = new Prefs(context).loadMp3Product();
        contentLocator = newContentLocator(context, mp3Product);
        new Prefs(context).loadBasePath(contentLocator);
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

    public static ArrayList<Mp3ContentLocator> createSupportedMp3ContentLocators() {
        ArrayList<Mp3ContentLocator> mp3ContentLocators= new ArrayList<Mp3ContentLocator>();
        mp3ContentLocators.add(new KjvChristadelphianMp3ContentLocator());
        mp3ContentLocators.add(new NltLaridianTreeMp3ContentLocator());
        mp3ContentLocators.add(new NltLaridianMp3ContentLocator());
        mp3ContentLocators.add(new KjvScourbyMp3ContentLocator());
        mp3ContentLocators.add(new KjvListenersMp3ContentLocator());
        mp3ContentLocators.add(new KjvFirefightersMp3ContentLocator());
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
        if(KjvChristadelphianMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new KjvChristadelphianMp3ContentLocator();    
        }
        if(KjvScourbyMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new KjvScourbyMp3ContentLocator();    
        }
        else if(NltLaridianTreeMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new NltLaridianTreeMp3ContentLocator();    
        }
        else if(NltLaridianMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new NltLaridianMp3ContentLocator();    
        }
        else if(KjvListenersMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new KjvListenersMp3ContentLocator();    
        }
        else if(KjvFirefightersMp3ContentLocator.class.getName().equalsIgnoreCase(mp3Product)) {
            contentLocator = new KjvFirefightersMp3ContentLocator();    
        }
        return contentLocator;
    }

    public static void resetBasePaths(ArrayList<Mp3ContentLocator> mp3ContentLocators) {
        for (Mp3ContentLocator mp3ContentLocator : mp3ContentLocators) {
            mp3ContentLocator.setBasePath("");
        }
    }
    public static void loadBasePaths(Context context, ArrayList<Mp3ContentLocator> mp3ContentLocators) {
        Prefs prefs = new Prefs(context);
        for (Mp3ContentLocator mp3ContentLocator : mp3ContentLocators) {
            prefs.loadBasePath(mp3ContentLocator);
        }
        
    }
    public static void saveBasePaths(Context context, ArrayList<Mp3ContentLocator> mp3ContentLocators) {
        Prefs prefs = new Prefs(context);
        for (Mp3ContentLocator mp3ContentLocator : mp3ContentLocators) {
            prefs.saveBasePath(mp3ContentLocator);
        }
    }
    public String getMp3Path(FragmentActivity activity, int passageId) {
        return getBasePath()+"/"+getPassagePath(passageId);
    }
    

}
