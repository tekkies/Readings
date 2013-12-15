package uk.co.tekkies.readings.activity;

import java.io.File;
import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.model.content.LaridianNltMp3ContentLocator;
import uk.co.tekkies.readings.model.content.LaridianNltMp3ContentLocator2;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.service.PlayerService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContentLocationActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = "MP3Bible";
    private String basePath = "";
    Button searchButton = null;
    Button testButton = null;
    TextView basePathTextView = null;
    WebView webView1 = null;
    Mp3ContentLocator mp3ContentLocator = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
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
        
        private static final boolean FIND_FIRST_ONLY = false;
        private boolean stop=false;
        
        @Override
        protected Mp3ContentLocator[] doInBackground(Mp3ContentLocator... locators) {
            stop = false;
            File root = new File("/");
            try {
                File found = findFile(root, locators);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return locators;
        }
        
        @Override
        protected void onProgressUpdate(String... values) {
            basePathTextView.setText(values[0]);
        }
        
        @Override
        protected void onPostExecute(Mp3ContentLocator[] results) {
            Prefs prefs = new Prefs(getActivity());

            String product = "";
            String basePath = "";
            
            //See if we got any hits
            LinearLayout container = (LinearLayout)findViewById(R.id.mp3_content_checkbox_holder);

            for (Mp3ContentLocator result : results) {
                if(result.getBasePath() != "") {
                    basePath = result.getBasePath();
                    product = result.getClass().getName();
                    
                    TextView textView = new TextView(getBaseContext());
                    textView.setText(product);
                    
                    container.addView(textView);
                    
                }
            }
            
            prefs.setMp3BasePath(basePath);
            prefs.setMp3Product(product);
            
            updateUi();
            //Try playing mp3 again
            //doTest();
        }
        
        private File findFile(File folder, Mp3ContentLocator[] locators) {
            publishProgress(folder.getAbsolutePath());
            Log.v(TAG, "Search:" + folder.getAbsolutePath());
            // Check files in this folder
            File[] files = folder.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (child.isFile()) {
                        Log.v(TAG, "File:" + child);
                        for (int i = 0; i < locators.length; i++) {
                            Mp3ContentLocator locator = locators[i];
                            if (child.getName().contains(locator.getKeyFileName())) {
                                // Candidate found, now confirm
                                String baseFolder = locator.getBaseFolder(child);
                                if (locator.confirmKeyFileFound(baseFolder)) {
                                    // Found! Store the base path in the
                                    // locator, in case we want all matches,
                                    // instead of the first matched mp3
                                    locator.setBasePath(baseFolder);
                                    if(FIND_FIRST_ONLY) {
                                        return child.getAbsoluteFile();
                                    }
                                }
                            }
                        }
                    }
                }

                // Not found at this level. Recurse through the folders
                for (File child : files) {
                    if (child.isDirectory()) {
                        // Do not traverse system folders
                        if ((folder.getAbsolutePath().indexOf("/proc") != 0)
                                && (folder.getAbsolutePath().indexOf("/sys") != 0)) {
                            Log.v(TAG, "Directory:" + child);
                            File found = findFile(child, locators);
                            // TODO: Remove this return if you want to keep
                            // searching for additional matches (e.g. if 2 mp3
                            // bibles are installed)
                            // if found, don't search any more folders
                            if(FIND_FIRST_ONLY) {
                                if (found != null) {
                                    return found; // Pass a found result up the tree
                                }
                            }
                        }
                    }
                }
            }

            // Not found in this folder. Caller will try next folder
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
           new LaridianNltMp3ContentLocator(),
           new LaridianNltMp3ContentLocator2()
        };
        
        SearchTask searchTask = new SearchTask();
        searchTask.execute(mp3ContentLocators);
    }



}
