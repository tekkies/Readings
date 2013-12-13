package uk.co.tekkies.readings.activity;

import java.io.File;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.content.BaseContent;
import uk.co.tekkies.readings.model.content.LaridianNltMp3Content;
import uk.co.tekkies.readings.service.PlayerService;
import android.app.Activity;
import android.content.SharedPreferences;
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


public class ContentLocationActivity extends Activity implements OnClickListener {

    protected static final String TAG = "MP3Bible";
    private String basePath = "";
    Button searchButton = null;
    Button testButton = null;
    TextView basePathTextView = null;
    WebView webView1 = null;
    BaseContent baseContent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActivity();
        setupLayout();

        baseContent = new LaridianNltMp3Content();
        basePath = baseContent.getBasePath(this);
        if (!baseContent.confirmKeyFileFound(this)) {
            // Show the activity

            Toast.makeText(this, R.string.mp3_folder_not_found, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, R.string.searching, Toast.LENGTH_SHORT).show();
            doSearchForKeyFile();
        }
    }

    private void setupActivity() {
        setTitle(R.string.mp3_search);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        setBasePath(settings.getString(BaseContent.SETTING_BASE_PATH, ""));
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
        webView1.loadUrl("file:///android_asset/licensed/search_help.html");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button_search) {
            doSearchForKeyFile();
        }
        else if(id == R.id.button_test) {
            doTest();
        }
    }

    private void doTest() {
        String mp3File = new LaridianNltMp3Content().getMp3Path(this, 2); 
        PlayerService.requestPlay(this, mp3File);
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
        doTest();
    }
    
    private void setBasePath(String basePath) {
        // Store value
        this.basePath = basePath;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(BaseContent.SETTING_BASE_PATH, basePath);
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
                String keyFileName = baseContent.getKeyFileName();
                File root = new File("/");
                File found = findFile(root, keyFileName);
                if (found != null) {
                    basePath = baseContent.getBaseFolder(found);
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
                        if (baseContent.confirmKeyFileFound(baseContent.getBaseFolder(found))) {
                            return found;
                        }
                    }
                }
            }

        }
        return null;
    }
    
}
