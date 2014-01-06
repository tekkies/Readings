package uk.co.tekkies.readings.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.adapter.Mp3ContentArrayAdapter;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.util.Analytics;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContentLocationActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = "MP3Bible";
    Button searchButton = null;
    Button instructionsButton = null;
    TextView searchStatus = null;
    ProgressBar progressBar = null;
    WebView webView1 = null;
    ArrayList<Mp3ContentLocator> mp3ContentLocators;
    Mp3ContentArrayAdapter mp3ContentArrayAdapter;
    ListView listView;
    private SearchTask searchTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
        super.onCreate(savedInstanceState);
        setupActivity();
        setupLayout();
    }

    private void setupActivity() {
        setTitle(R.string.mp3_search);
    }

    private void setupLayout() {
        setContentView(R.layout.mp3_search_activity);
        searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(this);
        instructionsButton = (Button)findViewById(R.id.button_instructions);
        instructionsButton.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        searchStatus = (TextView) findViewById(R.id.textView_mp3location);
        listView = (ListView) findViewById(R.id.list_view);
        mp3ContentLocators = Mp3ContentLocator.createSupportedMp3ContentLocators();
        Mp3ContentLocator.loadBasePaths(this, mp3ContentLocators);
        sortList(mp3ContentLocators);
        mp3ContentArrayAdapter = new Mp3ContentArrayAdapter(getActivity(), mp3ContentLocators);
        listView.setAdapter(mp3ContentArrayAdapter);
        // listView
        updateSearchViews(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.button_search:
                doSearchForKeyFile();
                break;
            
            case R.id.button_instructions:
                doInstructions();
                break;
        }
    }

    private void doInstructions() {
        Analytics.UIClick(this, "settings-mp3-help");
        String url = "http://goo.gl/suiico";
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        webIntent.setData(uri);
        startActivity(webIntent);
    }

    ContentLocationActivity getActivity() {
        return this;
    }
    
    private void updateSearchViews(boolean searching) {
    	searchButton.setEnabled(!searching);
    	instructionsButton.setEnabled(!searching);
    	searchStatus.setVisibility(searching ? View.VISIBLE : View.GONE);
    	progressBar.setVisibility(searching ? View.VISIBLE : View.GONE);
    }

    private void doSearchForKeyFile() {
        Analytics.UIClick(this, Analytics.LABEL_MP3_SEARCH);
        clearMainList();
    	updateSearchViews(true);
        searchTask = new SearchTask();
        searchTask.execute("");
    }

    private void clearMainList() {
        mp3ContentLocators.clear();
        mp3ContentArrayAdapter.notifyDataSetChanged();
    }

    private class SearchTask extends AsyncTask<String, String, ArrayList<Mp3ContentLocator>> {

        private static final boolean FIND_FIRST_ONLY = false;
        private static final int MAX_DEPTH = 8;

        @Override
        protected ArrayList<Mp3ContentLocator> doInBackground(String... unused) {
            File root = new File("/");
            ArrayList<Mp3ContentLocator> searchLocators = Mp3ContentLocator.createSupportedMp3ContentLocators();
            Mp3ContentLocator.searchResetBasePaths(searchLocators); // Start from nothing
            try {
                findFile(root, searchLocators, 0);
            } catch (Exception e) {
                Analytics.reportCaughtException(getActivity(), e);
            }
            return searchLocators;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                searchStatus.setText(values[0]);
            } catch (Exception e) {
                Analytics.reportCaughtException(getActivity(), e);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Mp3ContentLocator> results) {
            mp3ContentLocators = results;
            sortList(mp3ContentLocators);
            Mp3ContentLocator.searchSaveBasePaths(getActivity(), mp3ContentLocators);
            chooseValidResult();
            reportFoundContent(mp3ContentLocators);
            mp3ContentArrayAdapter = new Mp3ContentArrayAdapter(getActivity(), mp3ContentLocators);
            listView.setAdapter(mp3ContentArrayAdapter);
            updateSearchViews(false);
            searchTask = null;
        }
        
        private File findFile(File folder, ArrayList<Mp3ContentLocator> locators, int level) {
            // Check files in this folder
            File[] files = folder.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (child.isFile()) {
                        for (int i = 0; i < locators.size(); i++) {
                            Mp3ContentLocator locator = locators.get(i);
                            if (child.getName().contains(locator.searchGetKeyFileName())) {
                                // Candidate found, now confirm
                                String baseFolder = locator.searchGetBaseFolderFromKeyFile(child);
                                if (locator.searchConfirmKeyFileFound(baseFolder)) {
                                    // Found! Store the base path in the
                                    // locator, in case we want all matches,
                                    // instead of the first matched mp3
                                    locator.setBasePath(baseFolder);
                                    if (FIND_FIRST_ONLY) {
                                        return child.getAbsoluteFile();
                                    }
                                }
                            }
                        }
                    }
                }

                // Not found at this level. Recurse through the folders
                if(level < MAX_DEPTH) {
                    for (File child : files) {
                        publishProgress("Folder: "+child);
                        if(isCancelled()) {
                            publishProgress("Cancelled");
                            return null;
                        }
                        if (child.isDirectory()) {
                            // Do not traverse system folders
                            if ((folder.getAbsolutePath().indexOf("/proc") != 0)
                                    && (folder.getAbsolutePath().indexOf("/sys") != 0)) {
                                File found = findFile(child, locators, level+1);
                                if (FIND_FIRST_ONLY) {
                                    if (found != null) {
                                        return found; // Pass a found result up the
                                                      // tree
                                    }
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

    private void chooseValidResult() {
        Prefs prefs = new Prefs(this);
        String selectedProduct = prefs.loadMp3Product();
        Boolean valid = false;
        sortList(mp3ContentLocators);
        //check if product is valid
        if(selectedProduct.length() != 0) {
            for (Mp3ContentLocator locator: mp3ContentLocators) {
                if(locator.getProduct() == selectedProduct) {
                    if(locator.getBasePath().length() != 0) {
                        valid = true;
                    }
                    break;
                }
            }
        }
        
        if(!valid) {
            //choose first valid item
            for (Mp3ContentLocator locator: mp3ContentLocators) {
                if(locator.getBasePath().length() > 0) {
                    prefs.saveMp3Product(locator.getProduct());
                    break;
                }
            }
        }
    }

	public void reportFoundContent(ArrayList<Mp3ContentLocator> mp3ContentLocators) {
	    int count=0;
	    for(Mp3ContentLocator mp3ContentLocator: mp3ContentLocators) {
	        if(mp3ContentLocator.getBasePath().length() > 0) {
	            count++;
	            Analytics.SendEvent(this, Analytics.CATEGORY_MP3_CONTENT, Analytics.ACTION_FOUND, mp3ContentLocator.getTitle(), 1);
	        }
	    }
	    Analytics.SendEvent(this, Analytics.CATEGORY_MP3_CONTENT, Analytics.ACTION_FOUND, Analytics.LABEL_TOTAL, count);
    }

    public void sortList(ArrayList<Mp3ContentLocator> mp3ContentLocators) {
		Collections.sort(mp3ContentLocators, new Comparator<Mp3ContentLocator>() {
	        @Override
	        public int compare(Mp3ContentLocator li1, Mp3ContentLocator li2) {
	        	//Primary: Available items to the top
	        	int result = li2.getBasePath().length() - li1.getBasePath().length(); 
        		//Secondary: Alphabetically
	        	if(result == 0) {
	        		li1.getTitle().compareToIgnoreCase(li2.getTitle());
	        	}
	            return result;
	        }
	    });
	}

    @Override
    protected void onPause() {
        super.onPause();
        if(searchTask != null) {
            searchTask.cancel(false);
            searchTask = null;
        }
    }
}
