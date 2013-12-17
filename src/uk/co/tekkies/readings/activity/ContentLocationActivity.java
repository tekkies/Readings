package uk.co.tekkies.readings.activity;

import java.io.File;
import java.util.ArrayList;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.adapter.Mp3ContentArrayAdapter;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.service.PlayerService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
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
    ArrayList<Mp3ContentLocator> mp3ContentLocators;
    Mp3ContentArrayAdapter mp3ContentArrayAdapter;
    ListView listView;

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
        basePath = new Prefs(this).loadMp3BasePath();
    }

    private void setupLayout() {
        setContentView(R.layout.mp3_search_activity);
        searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(this);
        testButton = (Button) findViewById(R.id.button_test);
        testButton.setOnClickListener(this);
        basePathTextView = (TextView) findViewById(R.id.textView_mp3location);

        listView = (ListView) findViewById(R.id.list_view);
        mp3ContentLocators = Mp3ContentLocator.createSupportedMp3ContentLocators();
        mp3ContentArrayAdapter = new Mp3ContentArrayAdapter(getActivity(), mp3ContentLocators);
        listView.setAdapter(mp3ContentArrayAdapter);

        
        //listView
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
    
    private class SearchTask extends AsyncTask<String, String, ArrayList<Mp3ContentLocator>> {
        
        private static final boolean FIND_FIRST_ONLY = false;
        @Override
        protected ArrayList<Mp3ContentLocator> doInBackground(String... unused) {
            File root = new File("/");
            ArrayList<Mp3ContentLocator> searchLocators = Mp3ContentLocator.createSupportedMp3ContentLocators();
            Mp3ContentLocator.resetBasePaths(searchLocators); //Start from nothing
            try {
                findFile(root, searchLocators);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return searchLocators;
        }
        
        @Override
        protected void onProgressUpdate(String... values) {
            basePathTextView.setText(values[0]);
        }
        
        @Override
        protected void onPostExecute(ArrayList<Mp3ContentLocator> results) {

            mp3ContentLocators = results;
            mp3ContentArrayAdapter = new Mp3ContentArrayAdapter(getActivity(), mp3ContentLocators);
            listView.setAdapter(mp3ContentArrayAdapter);
            updateUi();
        }
        
        private File findFile(File folder, ArrayList<Mp3ContentLocator> locators) {
            publishProgress(folder.getAbsolutePath());
            Log.v(TAG, "Search:" + folder.getAbsolutePath());
            // Check files in this folder
            File[] files = folder.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (child.isFile()) {
                        Log.v(TAG, "File:" + child);
                        for (int i = 0; i < locators.size(); i++) {
                            Mp3ContentLocator locator = locators.get(i);
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
        SearchTask searchTask = new SearchTask();
        mp3ContentLocators.clear();
        mp3ContentArrayAdapter.notifyDataSetChanged();        
        
        searchTask.execute("");
    }

}
