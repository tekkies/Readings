package uk.co.tekkies.readings.model.content;

import java.io.File;

import android.util.Log;

public class KjvListenersMp3ContentLocator extends Mp3ContentLocator {

    @Override
    public String getTitle() {
        return "Listeners KJV MP3";
    }
    
    @Override
    public String getBaseFolder(File potentialKeyFile) {
        return potentialKeyFile.getParent();
    }

    @Override
    public boolean confirmKeyFileFound(String baseFolder) {
        Boolean confirmed = false;

        File prov15 = new File(baseFolder + "/2_352_Proverbs15_KJV.mp3");
        if (prov15.exists()) {
            Log.v(TAG, "confirmKeyFileFound: Found: Prov 15");
            File james3 = new File(baseFolder + "/4_220_James3_KJV.mp3");
            if (james3.exists()) {
                Log.v(TAG, "confirmKeyFileFound: Found: James 3");
                confirmed = true;
            }
        }
        return confirmed;
    }

    @Override
    public String getKeyFileName() {
        return "1_001_Genesis01_KJV.mp3";
    }
    
    @Override
    public String getLiveDatabasePath() {
        // TODO Auto-generated method stub
        return "/data/data/uk.co.tekkies.plugin.mp3bible.listeners.kjv/databases/";
    }

    @Override
    public String getPassagePath(int passageId) {
        // TODO Auto-generated method stub
        return null;
    }

}
