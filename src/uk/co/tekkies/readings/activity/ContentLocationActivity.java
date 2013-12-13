package uk.co.tekkies.readings.activity;

import java.io.File;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.model.content.LaridianNltMp3ContentLocator;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.service.PlayerService;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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
    Mp3ContentLocator mp3ContentLocator = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActivity();
        setupLayout();

        mp3ContentLocator = Mp3ContentLocator.createChosenMp3ContentDescription(this);
        if (mp3ContentLocator == null) {
            Toast.makeText(this, R.string.mp3_folder_not_found, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, R.string.searching, Toast.LENGTH_SHORT).show();
            doSearchForKeyFile();
        }
    }

    private void setupActivity() {
        setTitle(R.string.mp3_search);
        basePath = new Prefs(this).getMp3BasePath();
    }

    private void setupLayout() {
        setContentView(R.layout.mp3_search_activity);
        searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(this);
        testButton = (Button) findViewById(R.id.button_test);
        testButton.setOnClickListener(this);
        basePathTextView = (TextView) findViewById(R.id.textView_mp3location);
        webView1 = (WebView) findViewById(R.id.webView1);
        webView1.loadUrl("file:///android_asset/licensed/search_help.html");
        updateUi();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_search) {
            doSearchForKeyFile();
        } else if (id == R.id.button_test) {
            doTest();
        }
    }

    private void doTest() {
        Mp3ContentLocator content = Mp3ContentLocator.createChosenMp3ContentDescription(this);
        if (content != null) {
            String mp3File = content.getMp3Path(this, 2);
            PlayerService.requestPlay(this, mp3File);
        }
    }
    
    ContentLocationActivity getActivity() {
        return this;
    }
    
    private class SearchTask extends AsyncTask<Mp3ContentLocator, String, Mp3ContentLocator[]> {

        @Override
        protected Mp3ContentLocator[] doInBackground(Mp3ContentLocator... locators) {
            basePath = "";
            File root = new File("/");
            File found = findFile(root, locators);
            if (found != null) {
                basePath = locators[0].getBaseFolder(found);
            }
            return locators;
        }
        
        @Override
        protected void onProgressUpdate(String... values) {
            basePathTextView.setText(values[0]);
        }
        
        @Override
        protected void onPostExecute(Mp3ContentLocator[] result) {
            Prefs prefs = new Prefs(getActivity());
            
            prefs.setMp3BasePath(basePath);
            if(basePath == "") {
                prefs.setMp3Product(getActivity(), "");
            } else {
                prefs.setMp3Product(getActivity(), mp3ContentLocator.getClass().getName());
            }
            updateUi();
            //Try playing mp3 again
            doTest();
        }
        
        private File findFile(File aFile, Mp3ContentLocator[] locators) {
            publishProgress(aFile.getAbsolutePath());
            Log.v(TAG, "Find:" + aFile.getAbsolutePath());
            if ((aFile.getAbsolutePath().indexOf("/proc") == 0) || (aFile.getAbsolutePath().indexOf("/sys") == 0)) {
                return null;
            }
            if (aFile.isFile()) {
                for(int i=0;i<locators.length;i++) {
                    if(aFile.getName().contains(locators[i].getKeyFileName())) {
                        return aFile;
                    }
                }
            } else if (aFile.isDirectory()) {
                File[] fileList = aFile.listFiles();
                if (fileList != null) {
                    for (File child : aFile.listFiles()) {
                        File found = findFile(child, locators);
                        if (found != null) {
                            Log.v(TAG, "confirmKeyFileFound: Potential Match:" + found.getAbsolutePath());
                            for(int i=0;i<locators.length;i++) {
                                if (locators[i].confirmKeyFileFound(found.getAbsolutePath())) {
                                    locators[i].setBasePath(found.getAbsolutePath());
                                    return found;
                                }
                            }
                        }
                    }
                }

            }
            return null;
        }
    }
    

    private void updateUi() {
        // re-enable Search button
        if (searchButton != null) {
            searchButton.setEnabled(true);
        }
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
        
        //TODO: Add safety net for recursion.  Depth and breadth.
        Mp3ContentLocator[] mp3ContentLocators = { 
           new LaridianNltMp3ContentLocator() 
        };
        
        SearchTask searchTask = new SearchTask();
        searchTask.execute(mp3ContentLocators);
    }



}
