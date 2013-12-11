package uk.co.tekkies.readings;

import java.io.File;
import android.util.Log;

public class LaridianNltMp3SearchActivity extends Mp3SearchActivity {

    @Override
    public String getBaseFolder(File potentialKeyFile) {
        File bookFolder = potentialKeyFile.getParentFile();
        File testamentFolder = bookFolder.getParentFile();
        return testamentFolder.getParent();
    }

    @Override
    public boolean confirmKeyFileFound(String baseFolder) {
        Boolean confirmed = false;

        File prov15 = new File(baseFolder + "/1 OT/20 Prov/Prov015.mp3");
        if (prov15.exists()) {
            Log.v(TAG, "confirmKeyFileFound: Found: Prov 15");
            File james3 = new File(baseFolder + "/2 NT/59 Jas/Jas003.mp3");
            if (james3.exists()) {
                Log.v(TAG, "confirmKeyFileFound: Found: James 3");
                confirmed = true;
            }
        }
        return confirmed;
    }

    @Override
    public String getKeyFileName() {
        return "Gen001.mp3";
    }
    
    @Override
    public String getLiveDatabasePath() {
        // TODO Auto-generated method stub
        return "/data/data/uk.co.tekkies.plugin.mp3bible.laridian.nlt/databases/";
    }
    
}
