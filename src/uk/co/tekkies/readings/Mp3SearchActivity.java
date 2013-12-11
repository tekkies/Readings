package uk.co.tekkies.readings;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Mp3SearchActivity extends Activity implements OnClickListener {

    private static final String SETTING_BASE_PATH = "basePath";
    protected static final String TAG = "MP3Bible";
    private String basePath = "";
    Button searchButton = null;
    Button testButton = null;
    TextView basePathTextView = null;
    WebView webView1 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActivity();

        if (!tryPlay()) {
            // Show the activity
            setupLayout();

            // Begin a search for MP3 files if
            if (basePath == "") {
                Toast.makeText(this, R.string.mp3_folder_not_found, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, R.string.searching, Toast.LENGTH_SHORT).show();
                doSearchForKeyFile();
            }
        }
    }

    private void setupActivity() {
        setTitle(R.string.mp3_search);
        copyDatabaseIfRequired();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        setBasePath(settings.getString(SETTING_BASE_PATH, ""));
    }

    private void setupLayout() {
        setContentView(R.layout.mp3_search_activity);
        searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(this);
        
        testButton = (Button) findViewById(R.id.button_test);
        testButton.setOnClickListener(this);

        basePathTextView = (TextView) findViewById(R.id.textView_mp3location);
        setBasePath(basePath);
        
        webView1 = (WebView) findViewById(R.id.webView1);
        webView1.loadUrl("file:///android_asset/blurb.html");
    }

    private void copyDatabaseIfRequired() {
//        AppDataBaseHelper helper = new AppDataBaseHelper(this);
//        if (!helper.checkDataBase(getLiveDatabasePath())) {
//            try {
//                helper.createDataBase(getLiveDatabasePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button_search)
        {
            doSearchForKeyFile();
        }
        else if(id == R.id.button_test)
        {
            tryPlay("Genesis 1");
        }
    }
    
    final Handler mHandler = new Handler();
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };

    protected void updateUI() {
        // re-enable Search button
        if (searchButton != null) {
            searchButton.setEnabled(true);
        }

        // Save
        setBasePath(basePath);

        // Try playing mp3 again
        tryPlay();
    }
    
    private void setBasePath(String basePath) {
        // Store value
        this.basePath = basePath;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SETTING_BASE_PATH, basePath);
        editor.commit();

        // Build message
        String basePathMessage;
        if (basePath == "") {
            basePathMessage = getResources().getString(R.string.mp3_folder_not_found);
        } else {
            basePathMessage = String.format(getResources().getString(R.string.mp3_folder), basePath);
        }
        // Set TextView (& toast if UI visible)
        if (basePathTextView != null) {
            Toast.makeText(this, basePathMessage, Toast.LENGTH_SHORT).show();
            basePathTextView.setText(basePathMessage);
        }
    }

    private Boolean tryPlay()
    {
        return tryPlay(null);
    }
    
    private Boolean tryPlay(String passage) {
        Boolean success = false;
        if (basePath != "") {
            if (confirmKeyFileFound(basePath)) {
                if(passage == null)
                {
                    Intent intent = getIntent();
                    passage = intent.getStringExtra("passage");
                }

                if (passage != null) {
                    success = playPassage(basePath, passage);
                }
            } else {// MP3 files missing
                setBasePath("");
            }
        }
        return success;
    }

    private Boolean playPassage(String basePath, String passage) {
        Boolean success = false;
        String ref = passage.replace(" ", ""); // strip spaces
        ref = ref.toLowerCase();

        String mp3Path = basePath + File.separator + getPassagePath(ref);

        //Open in media player
        if (mp3Path != null) {
            Intent mediaIntent = new Intent(android.content.Intent.ACTION_VIEW);
            Uri data = Uri.parse("file://" + mp3Path);
            mediaIntent.setDataAndType(data, "audio/mp3");
            try {
                startActivity(mediaIntent);
                finish();
                success = true;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    private String getPassagePath(String ref) {
        String mp3SubPath = null;
//        AppDataBaseHelper dbHelper = new AppDataBaseHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        db = dbHelper.getReadableDatabase();
//        try {
//            String[] params = { ref };
//            Cursor cursor = db.rawQuery("SELECT [Path] FROM [Passage] WHERE [Ref] = ?", params);
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                mp3SubPath = cursor.getString(0);
//                cursor.moveToNext();
//            }
//        } finally {
//            db.close();
//        }
        return mp3SubPath;
    }

    private void doSearchForKeyFile() {
        if (searchButton != null) {
            searchButton.setEnabled(false);
        }
        if (basePathTextView != null) {
            basePathTextView.setText(R.string.searching);
        }

        Thread t = new Thread() {
            public void run() {
                basePath = "";
                String keyFileName = getKeyFileName();
                File root = new File("/");
                File found = findFile(root, keyFileName);
                if (found != null) {
                    basePath = getBaseFolder(found);
                }
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }

    private File findFile(File aFile, String toFind) {
        Log.v(TAG, "Find:" + aFile.getAbsolutePath());
        if ((aFile.getAbsolutePath().indexOf("/proc") == 0) || (aFile.getAbsolutePath().indexOf("/sys") == 0)) {
            return null;
        }
        if (aFile.isFile() && aFile.getName().contains(toFind)) {
            return aFile;
        } else if (aFile.isDirectory()) {
            File[] fileList = aFile.listFiles();
            if (fileList != null) {
                for (File child : aFile.listFiles()) {
                    File found = findFile(child, toFind);
                    if (found != null) {
                        Log.v(TAG, "confirmKeyFileFound: Potential Match:" + found.getAbsolutePath());
                        if (confirmKeyFileFound(getBaseFolder(found))) {
                            return found;
                        }
                    }
                }
            }

        }
        return null;
    }
    
    public String getBaseFolder(File potentialKeyFile) {
        return null; //Abstract
        };

    public boolean confirmKeyFileFound(String baseFolder) {
        return false; //Abstract
    }

    public String getKeyFileName() {
        return null; //Abstract
    }

    public String getLiveDatabasePath() {
        return null; //abstract "/data/data/uk.co.tekkies.plugin.mp3bible.laridian.nlt/databases/";
    }

}
