package uk.co.tekkies.readings;

import java.io.File;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        webView1.loadUrl("file:///android_asset/licensed/search_help.html");
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
    
    
    public static String getMp3Path(Context context, int passageId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String basePath = settings.getString(SETTING_BASE_PATH, "");
        String mp3Path = basePath + File.separator + getPassagePath(passageId);
        return mp3Path;
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

        String mp3Path = basePath + File.separator + getPassagePath(15);

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

    
    private static String getPassagePath(int passageId)
    {
        switch (passageId)
        {
        case 1: return "1 OT/01 Gen/Gen001.mp3";
        case 2: return "1 OT/01 Gen/Gen002.mp3";
        case 3: return "1 OT/01 Gen/Gen003.mp3";
        case 4: return "1 OT/01 Gen/Gen004.mp3";
        case 5: return "1 OT/01 Gen/Gen005.mp3";
        case 6: return "1 OT/01 Gen/Gen006.mp3";
        case 7: return "1 OT/01 Gen/Gen007.mp3";
        case 8: return "1 OT/01 Gen/Gen008.mp3";
        case 9: return "1 OT/01 Gen/Gen009.mp3";
        case 10: return "1 OT/01 Gen/Gen010.mp3";
        case 11: return "1 OT/01 Gen/Gen011.mp3";
        case 12: return "1 OT/01 Gen/Gen012.mp3";
        case 13: return "1 OT/01 Gen/Gen013.mp3";
        case 14: return "1 OT/01 Gen/Gen014.mp3";
        case 15: return "1 OT/01 Gen/Gen015.mp3";
        case 16: return "1 OT/01 Gen/Gen016.mp3";
        case 17: return "1 OT/01 Gen/Gen017.mp3";
        case 18: return "1 OT/01 Gen/Gen018.mp3";
        case 19: return "1 OT/01 Gen/Gen019.mp3";
        case 20: return "1 OT/01 Gen/Gen020.mp3";
        case 21: return "1 OT/01 Gen/Gen021.mp3";
        case 22: return "1 OT/01 Gen/Gen022.mp3";
        case 23: return "1 OT/01 Gen/Gen023.mp3";
        case 24: return "1 OT/01 Gen/Gen024.mp3";
        case 25: return "1 OT/01 Gen/Gen025.mp3";
        case 26: return "1 OT/01 Gen/Gen026.mp3";
        case 27: return "1 OT/01 Gen/Gen027.mp3";
        case 28: return "1 OT/01 Gen/Gen028.mp3";
        case 29: return "1 OT/01 Gen/Gen029.mp3";
        case 30: return "1 OT/01 Gen/Gen030.mp3";
        case 31: return "1 OT/01 Gen/Gen031.mp3";
        case 32: return "1 OT/01 Gen/Gen032.mp3";
        case 33: return "1 OT/01 Gen/Gen033.mp3";
        case 34: return "1 OT/01 Gen/Gen034.mp3";
        case 35: return "1 OT/01 Gen/Gen035.mp3";
        case 36: return "1 OT/01 Gen/Gen036.mp3";
        case 37: return "1 OT/01 Gen/Gen037.mp3";
        case 38: return "1 OT/01 Gen/Gen038.mp3";
        case 39: return "1 OT/01 Gen/Gen039.mp3";
        case 40: return "1 OT/01 Gen/Gen040.mp3";
        case 41: return "1 OT/01 Gen/Gen041.mp3";
        case 42: return "1 OT/01 Gen/Gen042.mp3";
        case 43: return "1 OT/01 Gen/Gen043.mp3";
        case 44: return "1 OT/01 Gen/Gen044.mp3";
        case 45: return "1 OT/01 Gen/Gen045.mp3";
        case 46: return "1 OT/01 Gen/Gen046.mp3";
        case 47: return "1 OT/01 Gen/Gen047.mp3";
        case 48: return "1 OT/01 Gen/Gen048.mp3";
        case 49: return "1 OT/01 Gen/Gen049.mp3";
        case 50: return "1 OT/01 Gen/Gen050.mp3";
        case 51: return "1 OT/02 Exo/Exo001.mp3";
        case 52: return "1 OT/02 Exo/Exo002.mp3";
        case 53: return "1 OT/02 Exo/Exo003.mp3";
        case 54: return "1 OT/02 Exo/Exo004.mp3";
        case 55: return "1 OT/02 Exo/Exo005.mp3";
        case 56: return "1 OT/02 Exo/Exo006.mp3";
        case 57: return "1 OT/02 Exo/Exo007.mp3";
        case 58: return "1 OT/02 Exo/Exo008.mp3";
        case 59: return "1 OT/02 Exo/Exo009.mp3";
        case 60: return "1 OT/02 Exo/Exo010.mp3";
        case 61: return "1 OT/02 Exo/Exo011.mp3";
        case 62: return "1 OT/02 Exo/Exo012.mp3";
        case 63: return "1 OT/02 Exo/Exo013.mp3";
        case 64: return "1 OT/02 Exo/Exo014.mp3";
        case 65: return "1 OT/02 Exo/Exo015.mp3";
        case 66: return "1 OT/02 Exo/Exo016.mp3";
        case 67: return "1 OT/02 Exo/Exo017.mp3";
        case 68: return "1 OT/02 Exo/Exo018.mp3";
        case 69: return "1 OT/02 Exo/Exo019.mp3";
        case 70: return "1 OT/02 Exo/Exo020.mp3";
        case 71: return "1 OT/02 Exo/Exo021.mp3";
        case 72: return "1 OT/02 Exo/Exo022.mp3";
        case 73: return "1 OT/02 Exo/Exo023.mp3";
        case 74: return "1 OT/02 Exo/Exo024.mp3";
        case 75: return "1 OT/02 Exo/Exo025.mp3";
        case 76: return "1 OT/02 Exo/Exo026.mp3";
        case 77: return "1 OT/02 Exo/Exo027.mp3";
        case 78: return "1 OT/02 Exo/Exo028.mp3";
        case 79: return "1 OT/02 Exo/Exo029.mp3";
        case 80: return "1 OT/02 Exo/Exo030.mp3";
        case 81: return "1 OT/02 Exo/Exo031.mp3";
        case 82: return "1 OT/02 Exo/Exo032.mp3";
        case 83: return "1 OT/02 Exo/Exo033.mp3";
        case 84: return "1 OT/02 Exo/Exo034.mp3";
        case 85: return "1 OT/02 Exo/Exo035.mp3";
        case 86: return "1 OT/02 Exo/Exo036.mp3";
        case 87: return "1 OT/02 Exo/Exo037.mp3";
        case 88: return "1 OT/02 Exo/Exo038.mp3";
        case 89: return "1 OT/02 Exo/Exo039.mp3";
        case 90: return "1 OT/02 Exo/Exo040.mp3";
        case 91: return "1 OT/03 Lev/Lev001.mp3";
        case 92: return "1 OT/03 Lev/Lev002.mp3";
        case 93: return "1 OT/03 Lev/Lev003.mp3";
        case 94: return "1 OT/03 Lev/Lev004.mp3";
        case 95: return "1 OT/03 Lev/Lev005.mp3";
        case 96: return "1 OT/03 Lev/Lev006.mp3";
        case 97: return "1 OT/03 Lev/Lev007.mp3";
        case 98: return "1 OT/03 Lev/Lev008.mp3";
        case 99: return "1 OT/03 Lev/Lev009.mp3";
        case 100: return "1 OT/03 Lev/Lev010.mp3";
        case 101: return "1 OT/03 Lev/Lev011.mp3";
        case 102: return "1 OT/03 Lev/Lev012.mp3";
        case 103: return "1 OT/03 Lev/Lev013.mp3";
        case 104: return "1 OT/03 Lev/Lev014.mp3";
        case 105: return "1 OT/03 Lev/Lev015.mp3";
        case 106: return "1 OT/03 Lev/Lev016.mp3";
        case 107: return "1 OT/03 Lev/Lev017.mp3";
        case 108: return "1 OT/03 Lev/Lev018.mp3";
        case 109: return "1 OT/03 Lev/Lev019.mp3";
        case 110: return "1 OT/03 Lev/Lev020.mp3";
        case 111: return "1 OT/03 Lev/Lev021.mp3";
        case 112: return "1 OT/03 Lev/Lev022.mp3";
        case 113: return "1 OT/03 Lev/Lev023.mp3";
        case 114: return "1 OT/03 Lev/Lev024.mp3";
        case 115: return "1 OT/03 Lev/Lev025.mp3";
        case 116: return "1 OT/03 Lev/Lev026.mp3";
        case 117: return "1 OT/03 Lev/Lev027.mp3";
        case 118: return "1 OT/04 Num/Num001.mp3";
        case 119: return "1 OT/04 Num/Num002.mp3";
        case 120: return "1 OT/04 Num/Num003.mp3";
        case 121: return "1 OT/04 Num/Num004.mp3";
        case 122: return "1 OT/04 Num/Num005.mp3";
        case 123: return "1 OT/04 Num/Num006.mp3";
        case 124: return "1 OT/04 Num/Num007.mp3";
        case 125: return "1 OT/04 Num/Num008.mp3";
        case 126: return "1 OT/04 Num/Num009.mp3";
        case 127: return "1 OT/04 Num/Num010.mp3";
        case 128: return "1 OT/04 Num/Num011.mp3";
        case 129: return "1 OT/04 Num/Num012.mp3";
        case 130: return "1 OT/04 Num/Num013.mp3";
        case 131: return "1 OT/04 Num/Num014.mp3";
        case 132: return "1 OT/04 Num/Num015.mp3";
        case 133: return "1 OT/04 Num/Num016.mp3";
        case 134: return "1 OT/04 Num/Num017.mp3";
        case 135: return "1 OT/04 Num/Num018.mp3";
        case 136: return "1 OT/04 Num/Num019.mp3";
        case 137: return "1 OT/04 Num/Num020.mp3";
        case 138: return "1 OT/04 Num/Num021.mp3";
        case 139: return "1 OT/04 Num/Num022.mp3";
        case 140: return "1 OT/04 Num/Num023.mp3";
        case 141: return "1 OT/04 Num/Num024.mp3";
        case 142: return "1 OT/04 Num/Num025.mp3";
        case 143: return "1 OT/04 Num/Num026.mp3";
        case 144: return "1 OT/04 Num/Num027.mp3";
        case 145: return "1 OT/04 Num/Num028.mp3";
        case 146: return "1 OT/04 Num/Num029.mp3";
        case 147: return "1 OT/04 Num/Num030.mp3";
        case 148: return "1 OT/04 Num/Num031.mp3";
        case 149: return "1 OT/04 Num/Num032.mp3";
        case 150: return "1 OT/04 Num/Num033.mp3";
        case 151: return "1 OT/04 Num/Num034.mp3";
        case 152: return "1 OT/04 Num/Num035.mp3";
        case 153: return "1 OT/04 Num/Num036.mp3";
        case 154: return "1 OT/05 Deu/Deu001.mp3";
        case 155: return "1 OT/05 Deu/Deu002.mp3";
        case 156: return "1 OT/05 Deu/Deu003.mp3";
        case 157: return "1 OT/05 Deu/Deu004.mp3";
        case 158: return "1 OT/05 Deu/Deu005.mp3";
        case 159: return "1 OT/05 Deu/Deu006.mp3";
        case 160: return "1 OT/05 Deu/Deu007.mp3";
        case 161: return "1 OT/05 Deu/Deu008.mp3";
        case 162: return "1 OT/05 Deu/Deu009.mp3";
        case 163: return "1 OT/05 Deu/Deu010.mp3";
        case 164: return "1 OT/05 Deu/Deu011.mp3";
        case 165: return "1 OT/05 Deu/Deu012.mp3";
        case 166: return "1 OT/05 Deu/Deu013.mp3";
        case 167: return "1 OT/05 Deu/Deu014.mp3";
        case 168: return "1 OT/05 Deu/Deu015.mp3";
        case 169: return "1 OT/05 Deu/Deu016.mp3";
        case 170: return "1 OT/05 Deu/Deu017.mp3";
        case 171: return "1 OT/05 Deu/Deu018.mp3";
        case 172: return "1 OT/05 Deu/Deu019.mp3";
        case 173: return "1 OT/05 Deu/Deu020.mp3";
        case 174: return "1 OT/05 Deu/Deu021.mp3";
        case 175: return "1 OT/05 Deu/Deu022.mp3";
        case 176: return "1 OT/05 Deu/Deu023.mp3";
        case 177: return "1 OT/05 Deu/Deu024.mp3";
        case 178: return "1 OT/05 Deu/Deu025.mp3";
        case 179: return "1 OT/05 Deu/Deu026.mp3";
        case 180: return "1 OT/05 Deu/Deu027.mp3";
        case 181: return "1 OT/05 Deu/Deu028.mp3";
        case 182: return "1 OT/05 Deu/Deu029.mp3";
        case 183: return "1 OT/05 Deu/Deu030.mp3";
        case 184: return "1 OT/05 Deu/Deu031.mp3";
        case 185: return "1 OT/05 Deu/Deu032.mp3";
        case 186: return "1 OT/05 Deu/Deu033.mp3";
        case 187: return "1 OT/05 Deu/Deu034.mp3";
        case 188: return "1 OT/06 Jos/Jos001.mp3";
        case 189: return "1 OT/06 Jos/Jos002.mp3";
        case 190: return "1 OT/06 Jos/Jos003.mp3";
        case 191: return "1 OT/06 Jos/Jos004.mp3";
        case 192: return "1 OT/06 Jos/Jos005.mp3";
        case 193: return "1 OT/06 Jos/Jos006.mp3";
        case 194: return "1 OT/06 Jos/Jos007.mp3";
        case 195: return "1 OT/06 Jos/Jos008.mp3";
        case 196: return "1 OT/06 Jos/Jos009.mp3";
        case 197: return "1 OT/06 Jos/Jos010.mp3";
        case 198: return "1 OT/06 Jos/Jos011.mp3";
        case 199: return "1 OT/06 Jos/Jos012.mp3";
        case 200: return "1 OT/06 Jos/Jos013.mp3";
        case 201: return "1 OT/06 Jos/Jos014.mp3";
        case 202: return "1 OT/06 Jos/Jos015.mp3";
        case 203: return "1 OT/06 Jos/Jos016.mp3";
        case 204: return "1 OT/06 Jos/Jos017.mp3";
        case 205: return "1 OT/06 Jos/Jos018.mp3";
        case 206: return "1 OT/06 Jos/Jos019.mp3";
        case 207: return "1 OT/06 Jos/Jos020.mp3";
        case 208: return "1 OT/06 Jos/Jos021.mp3";
        case 209: return "1 OT/06 Jos/Jos022.mp3";
        case 210: return "1 OT/06 Jos/Jos023.mp3";
        case 211: return "1 OT/06 Jos/Jos024.mp3";
        case 212: return "1 OT/07 Jdg/Jdg001.mp3";
        case 213: return "1 OT/07 Jdg/Jdg002.mp3";
        case 214: return "1 OT/07 Jdg/Jdg003.mp3";
        case 215: return "1 OT/07 Jdg/Jdg004.mp3";
        case 216: return "1 OT/07 Jdg/Jdg005.mp3";
        case 217: return "1 OT/07 Jdg/Jdg006.mp3";
        case 218: return "1 OT/07 Jdg/Jdg007.mp3";
        case 219: return "1 OT/07 Jdg/Jdg008.mp3";
        case 220: return "1 OT/07 Jdg/Jdg009.mp3";
        case 221: return "1 OT/07 Jdg/Jdg010.mp3";
        case 222: return "1 OT/07 Jdg/Jdg011.mp3";
        case 223: return "1 OT/07 Jdg/Jdg012.mp3";
        case 224: return "1 OT/07 Jdg/Jdg013.mp3";
        case 225: return "1 OT/07 Jdg/Jdg014.mp3";
        case 226: return "1 OT/07 Jdg/Jdg015.mp3";
        case 227: return "1 OT/07 Jdg/Jdg016.mp3";
        case 228: return "1 OT/07 Jdg/Jdg017.mp3";
        case 229: return "1 OT/07 Jdg/Jdg018.mp3";
        case 230: return "1 OT/07 Jdg/Jdg019.mp3";
        case 231: return "1 OT/07 Jdg/Jdg020.mp3";
        case 232: return "1 OT/07 Jdg/Jdg021.mp3";
        case 233: return "1 OT/08 Ruth/Ruth001.mp3";
        case 234: return "1 OT/08 Ruth/Ruth002.mp3";
        case 235: return "1 OT/08 Ruth/Ruth003.mp3";
        case 236: return "1 OT/08 Ruth/Ruth004.mp3";
        case 237: return "1 OT/09 1Sa/1Sa001.mp3";
        case 238: return "1 OT/09 1Sa/1Sa002.mp3";
        case 239: return "1 OT/09 1Sa/1Sa003.mp3";
        case 240: return "1 OT/09 1Sa/1Sa004.mp3";
        case 241: return "1 OT/09 1Sa/1Sa005.mp3";
        case 242: return "1 OT/09 1Sa/1Sa006.mp3";
        case 243: return "1 OT/09 1Sa/1Sa007.mp3";
        case 244: return "1 OT/09 1Sa/1Sa008.mp3";
        case 245: return "1 OT/09 1Sa/1Sa009.mp3";
        case 246: return "1 OT/09 1Sa/1Sa010.mp3";
        case 247: return "1 OT/09 1Sa/1Sa011.mp3";
        case 248: return "1 OT/09 1Sa/1Sa012.mp3";
        case 249: return "1 OT/09 1Sa/1Sa013.mp3";
        case 250: return "1 OT/09 1Sa/1Sa014.mp3";
        case 251: return "1 OT/09 1Sa/1Sa015.mp3";
        case 252: return "1 OT/09 1Sa/1Sa016.mp3";
        case 253: return "1 OT/09 1Sa/1Sa017.mp3";
        case 254: return "1 OT/09 1Sa/1Sa018.mp3";
        case 255: return "1 OT/09 1Sa/1Sa019.mp3";
        case 256: return "1 OT/09 1Sa/1Sa020.mp3";
        case 257: return "1 OT/09 1Sa/1Sa021.mp3";
        case 258: return "1 OT/09 1Sa/1Sa022.mp3";
        case 259: return "1 OT/09 1Sa/1Sa023.mp3";
        case 260: return "1 OT/09 1Sa/1Sa024.mp3";
        case 261: return "1 OT/09 1Sa/1Sa025.mp3";
        case 262: return "1 OT/09 1Sa/1Sa026.mp3";
        case 263: return "1 OT/09 1Sa/1Sa027.mp3";
        case 264: return "1 OT/09 1Sa/1Sa028.mp3";
        case 265: return "1 OT/09 1Sa/1Sa029.mp3";
        case 266: return "1 OT/09 1Sa/1Sa030.mp3";
        case 267: return "1 OT/09 1Sa/1Sa031.mp3";
        case 268: return "1 OT/10 2Sa/2Sa001.mp3";
        case 269: return "1 OT/10 2Sa/2Sa002.mp3";
        case 270: return "1 OT/10 2Sa/2Sa003.mp3";
        case 271: return "1 OT/10 2Sa/2Sa004.mp3";
        case 272: return "1 OT/10 2Sa/2Sa005.mp3";
        case 273: return "1 OT/10 2Sa/2Sa006.mp3";
        case 274: return "1 OT/10 2Sa/2Sa007.mp3";
        case 275: return "1 OT/10 2Sa/2Sa008.mp3";
        case 276: return "1 OT/10 2Sa/2Sa009.mp3";
        case 277: return "1 OT/10 2Sa/2Sa010.mp3";
        case 278: return "1 OT/10 2Sa/2Sa011.mp3";
        case 279: return "1 OT/10 2Sa/2Sa012.mp3";
        case 280: return "1 OT/10 2Sa/2Sa013.mp3";
        case 281: return "1 OT/10 2Sa/2Sa014.mp3";
        case 282: return "1 OT/10 2Sa/2Sa015.mp3";
        case 283: return "1 OT/10 2Sa/2Sa016.mp3";
        case 284: return "1 OT/10 2Sa/2Sa017.mp3";
        case 285: return "1 OT/10 2Sa/2Sa018.mp3";
        case 286: return "1 OT/10 2Sa/2Sa019.mp3";
        case 287: return "1 OT/10 2Sa/2Sa020.mp3";
        case 288: return "1 OT/10 2Sa/2Sa021.mp3";
        case 289: return "1 OT/10 2Sa/2Sa022.mp3";
        case 290: return "1 OT/10 2Sa/2Sa023.mp3";
        case 291: return "1 OT/10 2Sa/2Sa024.mp3";
        case 292: return "1 OT/11 1Ki/1Ki001.mp3";
        case 293: return "1 OT/11 1Ki/1Ki002.mp3";
        case 294: return "1 OT/11 1Ki/1Ki003.mp3";
        case 295: return "1 OT/11 1Ki/1Ki004.mp3";
        case 296: return "1 OT/11 1Ki/1Ki005.mp3";
        case 297: return "1 OT/11 1Ki/1Ki006.mp3";
        case 298: return "1 OT/11 1Ki/1Ki007.mp3";
        case 299: return "1 OT/11 1Ki/1Ki008.mp3";
        case 300: return "1 OT/11 1Ki/1Ki009.mp3";
        case 301: return "1 OT/11 1Ki/1Ki010.mp3";
        case 302: return "1 OT/11 1Ki/1Ki011.mp3";
        case 303: return "1 OT/11 1Ki/1Ki012.mp3";
        case 304: return "1 OT/11 1Ki/1Ki013.mp3";
        case 305: return "1 OT/11 1Ki/1Ki014.mp3";
        case 306: return "1 OT/11 1Ki/1Ki015.mp3";
        case 307: return "1 OT/11 1Ki/1Ki016.mp3";
        case 308: return "1 OT/11 1Ki/1Ki017.mp3";
        case 309: return "1 OT/11 1Ki/1Ki018.mp3";
        case 310: return "1 OT/11 1Ki/1Ki019.mp3";
        case 311: return "1 OT/11 1Ki/1Ki020.mp3";
        case 312: return "1 OT/11 1Ki/1Ki021.mp3";
        case 313: return "1 OT/11 1Ki/1Ki022.mp3";
        case 314: return "1 OT/12 2Ki/2Ki001.mp3";
        case 315: return "1 OT/12 2Ki/2Ki002.mp3";
        case 316: return "1 OT/12 2Ki/2Ki003.mp3";
        case 317: return "1 OT/12 2Ki/2Ki004.mp3";
        case 318: return "1 OT/12 2Ki/2Ki005.mp3";
        case 319: return "1 OT/12 2Ki/2Ki006.mp3";
        case 320: return "1 OT/12 2Ki/2Ki007.mp3";
        case 321: return "1 OT/12 2Ki/2Ki008.mp3";
        case 322: return "1 OT/12 2Ki/2Ki009.mp3";
        case 323: return "1 OT/12 2Ki/2Ki010.mp3";
        case 324: return "1 OT/12 2Ki/2Ki011.mp3";
        case 325: return "1 OT/12 2Ki/2Ki012.mp3";
        case 326: return "1 OT/12 2Ki/2Ki013.mp3";
        case 327: return "1 OT/12 2Ki/2Ki014.mp3";
        case 328: return "1 OT/12 2Ki/2Ki015.mp3";
        case 329: return "1 OT/12 2Ki/2Ki016.mp3";
        case 330: return "1 OT/12 2Ki/2Ki017.mp3";
        case 331: return "1 OT/12 2Ki/2Ki018.mp3";
        case 332: return "1 OT/12 2Ki/2Ki019.mp3";
        case 333: return "1 OT/12 2Ki/2Ki020.mp3";
        case 334: return "1 OT/12 2Ki/2Ki021.mp3";
        case 335: return "1 OT/12 2Ki/2Ki022.mp3";
        case 336: return "1 OT/12 2Ki/2Ki023.mp3";
        case 337: return "1 OT/12 2Ki/2Ki024.mp3";
        case 338: return "1 OT/12 2Ki/2Ki025.mp3";
        case 339: return "1 OT/13 1Ch/1Ch001.mp3";
        case 340: return "1 OT/13 1Ch/1Ch002.mp3";
        case 341: return "1 OT/13 1Ch/1Ch003.mp3";
        case 342: return "1 OT/13 1Ch/1Ch004.mp3";
        case 343: return "1 OT/13 1Ch/1Ch005.mp3";
        case 344: return "1 OT/13 1Ch/1Ch006.mp3";
        case 345: return "1 OT/13 1Ch/1Ch007.mp3";
        case 346: return "1 OT/13 1Ch/1Ch008.mp3";
        case 347: return "1 OT/13 1Ch/1Ch009.mp3";
        case 348: return "1 OT/13 1Ch/1Ch010.mp3";
        case 349: return "1 OT/13 1Ch/1Ch011.mp3";
        case 350: return "1 OT/13 1Ch/1Ch012.mp3";
        case 351: return "1 OT/13 1Ch/1Ch013.mp3";
        case 352: return "1 OT/13 1Ch/1Ch014.mp3";
        case 353: return "1 OT/13 1Ch/1Ch015.mp3";
        case 354: return "1 OT/13 1Ch/1Ch016.mp3";
        case 355: return "1 OT/13 1Ch/1Ch017.mp3";
        case 356: return "1 OT/13 1Ch/1Ch018.mp3";
        case 357: return "1 OT/13 1Ch/1Ch019.mp3";
        case 358: return "1 OT/13 1Ch/1Ch020.mp3";
        case 359: return "1 OT/13 1Ch/1Ch021.mp3";
        case 360: return "1 OT/13 1Ch/1Ch022.mp3";
        case 361: return "1 OT/13 1Ch/1Ch023.mp3";
        case 362: return "1 OT/13 1Ch/1Ch024.mp3";
        case 363: return "1 OT/13 1Ch/1Ch025.mp3";
        case 364: return "1 OT/13 1Ch/1Ch026.mp3";
        case 365: return "1 OT/13 1Ch/1Ch027.mp3";
        case 366: return "1 OT/13 1Ch/1Ch028.mp3";
        case 367: return "1 OT/13 1Ch/1Ch029.mp3";
        case 368: return "1 OT/14 2Ch/2Ch001.mp3";
        case 369: return "1 OT/14 2Ch/2Ch002.mp3";
        case 370: return "1 OT/14 2Ch/2Ch003.mp3";
        case 371: return "1 OT/14 2Ch/2Ch004.mp3";
        case 372: return "1 OT/14 2Ch/2Ch005.mp3";
        case 373: return "1 OT/14 2Ch/2Ch006.mp3";
        case 374: return "1 OT/14 2Ch/2Ch007.mp3";
        case 375: return "1 OT/14 2Ch/2Ch008.mp3";
        case 376: return "1 OT/14 2Ch/2Ch009.mp3";
        case 377: return "1 OT/14 2Ch/2Ch010.mp3";
        case 378: return "1 OT/14 2Ch/2Ch011.mp3";
        case 379: return "1 OT/14 2Ch/2Ch012.mp3";
        case 380: return "1 OT/14 2Ch/2Ch013.mp3";
        case 381: return "1 OT/14 2Ch/2Ch014.mp3";
        case 382: return "1 OT/14 2Ch/2Ch015.mp3";
        case 383: return "1 OT/14 2Ch/2Ch016.mp3";
        case 384: return "1 OT/14 2Ch/2Ch017.mp3";
        case 385: return "1 OT/14 2Ch/2Ch018.mp3";
        case 386: return "1 OT/14 2Ch/2Ch019.mp3";
        case 387: return "1 OT/14 2Ch/2Ch020.mp3";
        case 388: return "1 OT/14 2Ch/2Ch021.mp3";
        case 389: return "1 OT/14 2Ch/2Ch022.mp3";
        case 390: return "1 OT/14 2Ch/2Ch023.mp3";
        case 391: return "1 OT/14 2Ch/2Ch024.mp3";
        case 392: return "1 OT/14 2Ch/2Ch025.mp3";
        case 393: return "1 OT/14 2Ch/2Ch026.mp3";
        case 394: return "1 OT/14 2Ch/2Ch027.mp3";
        case 395: return "1 OT/14 2Ch/2Ch028.mp3";
        case 396: return "1 OT/14 2Ch/2Ch029.mp3";
        case 397: return "1 OT/14 2Ch/2Ch030.mp3";
        case 398: return "1 OT/14 2Ch/2Ch031.mp3";
        case 399: return "1 OT/14 2Ch/2Ch032.mp3";
        case 400: return "1 OT/14 2Ch/2Ch033.mp3";
        case 401: return "1 OT/14 2Ch/2Ch034.mp3";
        case 402: return "1 OT/14 2Ch/2Ch035.mp3";
        case 403: return "1 OT/14 2Ch/2Ch036.mp3";
        case 404: return "1 OT/15 Ezra/Ezra001.mp3";
        case 405: return "1 OT/15 Ezra/Ezra002.mp3";
        case 406: return "1 OT/15 Ezra/Ezra003.mp3";
        case 407: return "1 OT/15 Ezra/Ezra004.mp3";
        case 408: return "1 OT/15 Ezra/Ezra005.mp3";
        case 409: return "1 OT/15 Ezra/Ezra006.mp3";
        case 410: return "1 OT/15 Ezra/Ezra007.mp3";
        case 411: return "1 OT/15 Ezra/Ezra008.mp3";
        case 412: return "1 OT/15 Ezra/Ezra009.mp3";
        case 413: return "1 OT/15 Ezra/Ezra010.mp3";
        case 414: return "1 OT/16 Neh/Neh001.mp3";
        case 415: return "1 OT/16 Neh/Neh002.mp3";
        case 416: return "1 OT/16 Neh/Neh003.mp3";
        case 417: return "1 OT/16 Neh/Neh004.mp3";
        case 418: return "1 OT/16 Neh/Neh005.mp3";
        case 419: return "1 OT/16 Neh/Neh006.mp3";
        case 420: return "1 OT/16 Neh/Neh007.mp3";
        case 421: return "1 OT/16 Neh/Neh008.mp3";
        case 422: return "1 OT/16 Neh/Neh009.mp3";
        case 423: return "1 OT/16 Neh/Neh010.mp3";
        case 424: return "1 OT/16 Neh/Neh011.mp3";
        case 425: return "1 OT/16 Neh/Neh012.mp3";
        case 426: return "1 OT/16 Neh/Neh013.mp3";
        case 427: return "1 OT/17 Est/Est001.mp3";
        case 428: return "1 OT/17 Est/Est002.mp3";
        case 429: return "1 OT/17 Est/Est003.mp3";
        case 430: return "1 OT/17 Est/Est004.mp3";
        case 431: return "1 OT/17 Est/Est005.mp3";
        case 432: return "1 OT/17 Est/Est006.mp3";
        case 433: return "1 OT/17 Est/Est007.mp3";
        case 434: return "1 OT/17 Est/Est008.mp3";
        case 435: return "1 OT/17 Est/Est009.mp3";
        case 436: return "1 OT/17 Est/Est010.mp3";
        case 437: return "1 OT/18 Job/Job001.mp3";
        case 438: return "1 OT/18 Job/Job002.mp3";
        case 439: return "1 OT/18 Job/Job003.mp3";
        case 440: return "1 OT/18 Job/Job004.mp3";
        case 441: return "1 OT/18 Job/Job005.mp3";
        case 442: return "1 OT/18 Job/Job006.mp3";
        case 443: return "1 OT/18 Job/Job007.mp3";
        case 444: return "1 OT/18 Job/Job008.mp3";
        case 445: return "1 OT/18 Job/Job009.mp3";
        case 446: return "1 OT/18 Job/Job010.mp3";
        case 447: return "1 OT/18 Job/Job011.mp3";
        case 448: return "1 OT/18 Job/Job012.mp3";
        case 449: return "1 OT/18 Job/Job013.mp3";
        case 450: return "1 OT/18 Job/Job014.mp3";
        case 451: return "1 OT/18 Job/Job015.mp3";
        case 452: return "1 OT/18 Job/Job016.mp3";
        case 453: return "1 OT/18 Job/Job017.mp3";
        case 454: return "1 OT/18 Job/Job018.mp3";
        case 455: return "1 OT/18 Job/Job019.mp3";
        case 456: return "1 OT/18 Job/Job020.mp3";
        case 457: return "1 OT/18 Job/Job021.mp3";
        case 458: return "1 OT/18 Job/Job022.mp3";
        case 459: return "1 OT/18 Job/Job023.mp3";
        case 460: return "1 OT/18 Job/Job024.mp3";
        case 461: return "1 OT/18 Job/Job025.mp3";
        case 462: return "1 OT/18 Job/Job026.mp3";
        case 463: return "1 OT/18 Job/Job027.mp3";
        case 464: return "1 OT/18 Job/Job028.mp3";
        case 465: return "1 OT/18 Job/Job029.mp3";
        case 466: return "1 OT/18 Job/Job030.mp3";
        case 467: return "1 OT/18 Job/Job031.mp3";
        case 468: return "1 OT/18 Job/Job032.mp3";
        case 469: return "1 OT/18 Job/Job033.mp3";
        case 470: return "1 OT/18 Job/Job034.mp3";
        case 471: return "1 OT/18 Job/Job035.mp3";
        case 472: return "1 OT/18 Job/Job036.mp3";
        case 473: return "1 OT/18 Job/Job037.mp3";
        case 474: return "1 OT/18 Job/Job038.mp3";
        case 475: return "1 OT/18 Job/Job039.mp3";
        case 476: return "1 OT/18 Job/Job040.mp3";
        case 477: return "1 OT/18 Job/Job041.mp3";
        case 478: return "1 OT/18 Job/Job042.mp3";
        case 479: return "1 OT/19 Ps/Ps001.mp3";
        case 480: return "1 OT/19 Ps/Ps002.mp3";
        case 481: return "1 OT/19 Ps/Ps003.mp3";
        case 482: return "1 OT/19 Ps/Ps004.mp3";
        case 483: return "1 OT/19 Ps/Ps005.mp3";
        case 484: return "1 OT/19 Ps/Ps006.mp3";
        case 485: return "1 OT/19 Ps/Ps007.mp3";
        case 486: return "1 OT/19 Ps/Ps008.mp3";
        case 487: return "1 OT/19 Ps/Ps009.mp3";
        case 488: return "1 OT/19 Ps/Ps010.mp3";
        case 489: return "1 OT/19 Ps/Ps011.mp3";
        case 490: return "1 OT/19 Ps/Ps012.mp3";
        case 491: return "1 OT/19 Ps/Ps013.mp3";
        case 492: return "1 OT/19 Ps/Ps014.mp3";
        case 493: return "1 OT/19 Ps/Ps015.mp3";
        case 494: return "1 OT/19 Ps/Ps016.mp3";
        case 495: return "1 OT/19 Ps/Ps017.mp3";
        case 496: return "1 OT/19 Ps/Ps018.mp3";
        case 497: return "1 OT/19 Ps/Ps019.mp3";
        case 498: return "1 OT/19 Ps/Ps020.mp3";
        case 499: return "1 OT/19 Ps/Ps021.mp3";
        case 500: return "1 OT/19 Ps/Ps022.mp3";
        case 501: return "1 OT/19 Ps/Ps023.mp3";
        case 502: return "1 OT/19 Ps/Ps024.mp3";
        case 503: return "1 OT/19 Ps/Ps025.mp3";
        case 504: return "1 OT/19 Ps/Ps026.mp3";
        case 505: return "1 OT/19 Ps/Ps027.mp3";
        case 506: return "1 OT/19 Ps/Ps028.mp3";
        case 507: return "1 OT/19 Ps/Ps029.mp3";
        case 508: return "1 OT/19 Ps/Ps030.mp3";
        case 509: return "1 OT/19 Ps/Ps031.mp3";
        case 510: return "1 OT/19 Ps/Ps032.mp3";
        case 511: return "1 OT/19 Ps/Ps033.mp3";
        case 512: return "1 OT/19 Ps/Ps034.mp3";
        case 513: return "1 OT/19 Ps/Ps035.mp3";
        case 514: return "1 OT/19 Ps/Ps036.mp3";
        case 515: return "1 OT/19 Ps/Ps037.mp3";
        case 516: return "1 OT/19 Ps/Ps038.mp3";
        case 517: return "1 OT/19 Ps/Ps039.mp3";
        case 518: return "1 OT/19 Ps/Ps040.mp3";
        case 519: return "1 OT/19 Ps/Ps041.mp3";
        case 520: return "1 OT/19 Ps/Ps042.mp3";
        case 521: return "1 OT/19 Ps/Ps043.mp3";
        case 522: return "1 OT/19 Ps/Ps044.mp3";
        case 523: return "1 OT/19 Ps/Ps045.mp3";
        case 524: return "1 OT/19 Ps/Ps046.mp3";
        case 525: return "1 OT/19 Ps/Ps047.mp3";
        case 526: return "1 OT/19 Ps/Ps048.mp3";
        case 527: return "1 OT/19 Ps/Ps049.mp3";
        case 528: return "1 OT/19 Ps/Ps050.mp3";
        case 529: return "1 OT/19 Ps/Ps051.mp3";
        case 530: return "1 OT/19 Ps/Ps052.mp3";
        case 531: return "1 OT/19 Ps/Ps053.mp3";
        case 532: return "1 OT/19 Ps/Ps054.mp3";
        case 533: return "1 OT/19 Ps/Ps055.mp3";
        case 534: return "1 OT/19 Ps/Ps056.mp3";
        case 535: return "1 OT/19 Ps/Ps057.mp3";
        case 536: return "1 OT/19 Ps/Ps058.mp3";
        case 537: return "1 OT/19 Ps/Ps059.mp3";
        case 538: return "1 OT/19 Ps/Ps060.mp3";
        case 539: return "1 OT/19 Ps/Ps061.mp3";
        case 540: return "1 OT/19 Ps/Ps062.mp3";
        case 541: return "1 OT/19 Ps/Ps063.mp3";
        case 542: return "1 OT/19 Ps/Ps064.mp3";
        case 543: return "1 OT/19 Ps/Ps065.mp3";
        case 544: return "1 OT/19 Ps/Ps066.mp3";
        case 545: return "1 OT/19 Ps/Ps067.mp3";
        case 546: return "1 OT/19 Ps/Ps068.mp3";
        case 547: return "1 OT/19 Ps/Ps069.mp3";
        case 548: return "1 OT/19 Ps/Ps070.mp3";
        case 549: return "1 OT/19 Ps/Ps071.mp3";
        case 550: return "1 OT/19 Ps/Ps072.mp3";
        case 551: return "1 OT/19 Ps/Ps073.mp3";
        case 552: return "1 OT/19 Ps/Ps074.mp3";
        case 553: return "1 OT/19 Ps/Ps075.mp3";
        case 554: return "1 OT/19 Ps/Ps076.mp3";
        case 555: return "1 OT/19 Ps/Ps077.mp3";
        case 556: return "1 OT/19 Ps/Ps078.mp3";
        case 557: return "1 OT/19 Ps/Ps079.mp3";
        case 558: return "1 OT/19 Ps/Ps080.mp3";
        case 559: return "1 OT/19 Ps/Ps081.mp3";
        case 560: return "1 OT/19 Ps/Ps082.mp3";
        case 561: return "1 OT/19 Ps/Ps083.mp3";
        case 562: return "1 OT/19 Ps/Ps084.mp3";
        case 563: return "1 OT/19 Ps/Ps085.mp3";
        case 564: return "1 OT/19 Ps/Ps086.mp3";
        case 565: return "1 OT/19 Ps/Ps087.mp3";
        case 566: return "1 OT/19 Ps/Ps088.mp3";
        case 567: return "1 OT/19 Ps/Ps089.mp3";
        case 568: return "1 OT/19 Ps/Ps090.mp3";
        case 569: return "1 OT/19 Ps/Ps091.mp3";
        case 570: return "1 OT/19 Ps/Ps092.mp3";
        case 571: return "1 OT/19 Ps/Ps093.mp3";
        case 572: return "1 OT/19 Ps/Ps094.mp3";
        case 573: return "1 OT/19 Ps/Ps095.mp3";
        case 574: return "1 OT/19 Ps/Ps096.mp3";
        case 575: return "1 OT/19 Ps/Ps097.mp3";
        case 576: return "1 OT/19 Ps/Ps098.mp3";
        case 577: return "1 OT/19 Ps/Ps099.mp3";
        case 578: return "1 OT/19 Ps/Ps100.mp3";
        case 579: return "1 OT/19 Ps/Ps101.mp3";
        case 580: return "1 OT/19 Ps/Ps102.mp3";
        case 581: return "1 OT/19 Ps/Ps103.mp3";
        case 582: return "1 OT/19 Ps/Ps104.mp3";
        case 583: return "1 OT/19 Ps/Ps105.mp3";
        case 584: return "1 OT/19 Ps/Ps106.mp3";
        case 585: return "1 OT/19 Ps/Ps107.mp3";
        case 586: return "1 OT/19 Ps/Ps108.mp3";
        case 587: return "1 OT/19 Ps/Ps109.mp3";
        case 588: return "1 OT/19 Ps/Ps110.mp3";
        case 589: return "1 OT/19 Ps/Ps111.mp3";
        case 590: return "1 OT/19 Ps/Ps112.mp3";
        case 591: return "1 OT/19 Ps/Ps113.mp3";
        case 592: return "1 OT/19 Ps/Ps114.mp3";
        case 593: return "1 OT/19 Ps/Ps115.mp3";
        case 594: return "1 OT/19 Ps/Ps116.mp3";
        case 595: return "1 OT/19 Ps/Ps117.mp3";
        case 596: return "1 OT/19 Ps/Ps118.mp3";
        case 597: return "1 OT/19 Ps/Ps120.mp3";
        case 598: return "1 OT/19 Ps/Ps121.mp3";
        case 599: return "1 OT/19 Ps/Ps122.mp3";
        case 600: return "1 OT/19 Ps/Ps123.mp3";
        case 601: return "1 OT/19 Ps/Ps124.mp3";
        case 602: return "1 OT/19 Ps/Ps125.mp3";
        case 603: return "1 OT/19 Ps/Ps126.mp3";
        case 604: return "1 OT/19 Ps/Ps127.mp3";
        case 605: return "1 OT/19 Ps/Ps128.mp3";
        case 606: return "1 OT/19 Ps/Ps129.mp3";
        case 607: return "1 OT/19 Ps/Ps130.mp3";
        case 608: return "1 OT/19 Ps/Ps131.mp3";
        case 609: return "1 OT/19 Ps/Ps132.mp3";
        case 610: return "1 OT/19 Ps/Ps133.mp3";
        case 611: return "1 OT/19 Ps/Ps134.mp3";
        case 612: return "1 OT/19 Ps/Ps135.mp3";
        case 613: return "1 OT/19 Ps/Ps136.mp3";
        case 614: return "1 OT/19 Ps/Ps137.mp3";
        case 615: return "1 OT/19 Ps/Ps138.mp3";
        case 616: return "1 OT/19 Ps/Ps139.mp3";
        case 617: return "1 OT/19 Ps/Ps140.mp3";
        case 618: return "1 OT/19 Ps/Ps141.mp3";
        case 619: return "1 OT/19 Ps/Ps142.mp3";
        case 620: return "1 OT/19 Ps/Ps143.mp3";
        case 621: return "1 OT/19 Ps/Ps144.mp3";
        case 622: return "1 OT/19 Ps/Ps145.mp3";
        case 623: return "1 OT/19 Ps/Ps146.mp3";
        case 624: return "1 OT/19 Ps/Ps147.mp3";
        case 625: return "1 OT/19 Ps/Ps148.mp3";
        case 626: return "1 OT/19 Ps/Ps149.mp3";
        case 627: return "1 OT/19 Ps/Ps150.mp3";
        case 628: return "1 OT/20 Prov/Prov001.mp3";
        case 629: return "1 OT/20 Prov/Prov002.mp3";
        case 630: return "1 OT/20 Prov/Prov003.mp3";
        case 631: return "1 OT/20 Prov/Prov004.mp3";
        case 632: return "1 OT/20 Prov/Prov005.mp3";
        case 633: return "1 OT/20 Prov/Prov006.mp3";
        case 634: return "1 OT/20 Prov/Prov007.mp3";
        case 635: return "1 OT/20 Prov/Prov008.mp3";
        case 636: return "1 OT/20 Prov/Prov009.mp3";
        case 637: return "1 OT/20 Prov/Prov010.mp3";
        case 638: return "1 OT/20 Prov/Prov011.mp3";
        case 639: return "1 OT/20 Prov/Prov012.mp3";
        case 640: return "1 OT/20 Prov/Prov013.mp3";
        case 641: return "1 OT/20 Prov/Prov014.mp3";
        case 642: return "1 OT/20 Prov/Prov015.mp3";
        case 643: return "1 OT/20 Prov/Prov016.mp3";
        case 644: return "1 OT/20 Prov/Prov017.mp3";
        case 645: return "1 OT/20 Prov/Prov018.mp3";
        case 646: return "1 OT/20 Prov/Prov019.mp3";
        case 647: return "1 OT/20 Prov/Prov020.mp3";
        case 648: return "1 OT/20 Prov/Prov021.mp3";
        case 649: return "1 OT/20 Prov/Prov022.mp3";
        case 650: return "1 OT/20 Prov/Prov023.mp3";
        case 651: return "1 OT/20 Prov/Prov024.mp3";
        case 652: return "1 OT/20 Prov/Prov025.mp3";
        case 653: return "1 OT/20 Prov/Prov026.mp3";
        case 654: return "1 OT/20 Prov/Prov027.mp3";
        case 655: return "1 OT/20 Prov/Prov028.mp3";
        case 656: return "1 OT/20 Prov/Prov029.mp3";
        case 657: return "1 OT/20 Prov/Prov030.mp3";
        case 658: return "1 OT/20 Prov/Prov031.mp3";
        case 659: return "1 OT/21 Ecc/Ecc001.mp3";
        case 660: return "1 OT/21 Ecc/Ecc002.mp3";
        case 661: return "1 OT/21 Ecc/Ecc003.mp3";
        case 662: return "1 OT/21 Ecc/Ecc004.mp3";
        case 663: return "1 OT/21 Ecc/Ecc005.mp3";
        case 664: return "1 OT/21 Ecc/Ecc006.mp3";
        case 665: return "1 OT/21 Ecc/Ecc007.mp3";
        case 666: return "1 OT/21 Ecc/Ecc008.mp3";
        case 667: return "1 OT/21 Ecc/Ecc009.mp3";
        case 668: return "1 OT/21 Ecc/Ecc010.mp3";
        case 669: return "1 OT/21 Ecc/Ecc011.mp3";
        case 670: return "1 OT/21 Ecc/Ecc012.mp3";
        case 671: return "1 OT/22 SS/SS001.mp3";
        case 672: return "1 OT/22 SS/SS002.mp3";
        case 673: return "1 OT/22 SS/SS003.mp3";
        case 674: return "1 OT/22 SS/SS004.mp3";
        case 675: return "1 OT/22 SS/SS005.mp3";
        case 676: return "1 OT/22 SS/SS006.mp3";
        case 677: return "1 OT/22 SS/SS007.mp3";
        case 678: return "1 OT/22 SS/SS008.mp3";
        case 679: return "1 OT/23 Isa/Isa001.mp3";
        case 680: return "1 OT/23 Isa/Isa002.mp3";
        case 681: return "1 OT/23 Isa/Isa003.mp3";
        case 682: return "1 OT/23 Isa/Isa004.mp3";
        case 683: return "1 OT/23 Isa/Isa005.mp3";
        case 684: return "1 OT/23 Isa/Isa006.mp3";
        case 685: return "1 OT/23 Isa/Isa007.mp3";
        case 686: return "1 OT/23 Isa/Isa008.mp3";
        case 687: return "1 OT/23 Isa/Isa009.mp3";
        case 688: return "1 OT/23 Isa/Isa010.mp3";
        case 689: return "1 OT/23 Isa/Isa011.mp3";
        case 690: return "1 OT/23 Isa/Isa012.mp3";
        case 691: return "1 OT/23 Isa/Isa013.mp3";
        case 692: return "1 OT/23 Isa/Isa014.mp3";
        case 693: return "1 OT/23 Isa/Isa015.mp3";
        case 694: return "1 OT/23 Isa/Isa016.mp3";
        case 695: return "1 OT/23 Isa/Isa017.mp3";
        case 696: return "1 OT/23 Isa/Isa018.mp3";
        case 697: return "1 OT/23 Isa/Isa019.mp3";
        case 698: return "1 OT/23 Isa/Isa020.mp3";
        case 699: return "1 OT/23 Isa/Isa021.mp3";
        case 700: return "1 OT/23 Isa/Isa022.mp3";
        case 701: return "1 OT/23 Isa/Isa023.mp3";
        case 702: return "1 OT/23 Isa/Isa024.mp3";
        case 703: return "1 OT/23 Isa/Isa025.mp3";
        case 704: return "1 OT/23 Isa/Isa026.mp3";
        case 705: return "1 OT/23 Isa/Isa027.mp3";
        case 706: return "1 OT/23 Isa/Isa028.mp3";
        case 707: return "1 OT/23 Isa/Isa029.mp3";
        case 708: return "1 OT/23 Isa/Isa030.mp3";
        case 709: return "1 OT/23 Isa/Isa031.mp3";
        case 710: return "1 OT/23 Isa/Isa032.mp3";
        case 711: return "1 OT/23 Isa/Isa033.mp3";
        case 712: return "1 OT/23 Isa/Isa034.mp3";
        case 713: return "1 OT/23 Isa/Isa035.mp3";
        case 714: return "1 OT/23 Isa/Isa036.mp3";
        case 715: return "1 OT/23 Isa/Isa037.mp3";
        case 716: return "1 OT/23 Isa/Isa038.mp3";
        case 717: return "1 OT/23 Isa/Isa039.mp3";
        case 718: return "1 OT/23 Isa/Isa040.mp3";
        case 719: return "1 OT/23 Isa/Isa041.mp3";
        case 720: return "1 OT/23 Isa/Isa042.mp3";
        case 721: return "1 OT/23 Isa/Isa043.mp3";
        case 722: return "1 OT/23 Isa/Isa044.mp3";
        case 723: return "1 OT/23 Isa/Isa045.mp3";
        case 724: return "1 OT/23 Isa/Isa046.mp3";
        case 725: return "1 OT/23 Isa/Isa047.mp3";
        case 726: return "1 OT/23 Isa/Isa048.mp3";
        case 727: return "1 OT/23 Isa/Isa049.mp3";
        case 728: return "1 OT/23 Isa/Isa050.mp3";
        case 729: return "1 OT/23 Isa/Isa051.mp3";
        case 730: return "1 OT/23 Isa/Isa052.mp3";
        case 731: return "1 OT/23 Isa/Isa053.mp3";
        case 732: return "1 OT/23 Isa/Isa054.mp3";
        case 733: return "1 OT/23 Isa/Isa055.mp3";
        case 734: return "1 OT/23 Isa/Isa056.mp3";
        case 735: return "1 OT/23 Isa/Isa057.mp3";
        case 736: return "1 OT/23 Isa/Isa058.mp3";
        case 737: return "1 OT/23 Isa/Isa059.mp3";
        case 738: return "1 OT/23 Isa/Isa060.mp3";
        case 739: return "1 OT/23 Isa/Isa061.mp3";
        case 740: return "1 OT/23 Isa/Isa062.mp3";
        case 741: return "1 OT/23 Isa/Isa063.mp3";
        case 742: return "1 OT/23 Isa/Isa064.mp3";
        case 743: return "1 OT/23 Isa/Isa065.mp3";
        case 744: return "1 OT/23 Isa/Isa066.mp3";
        case 745: return "1 OT/24 Jer/Jer001.mp3";
        case 746: return "1 OT/24 Jer/Jer002.mp3";
        case 747: return "1 OT/24 Jer/Jer003.mp3";
        case 748: return "1 OT/24 Jer/Jer004.mp3";
        case 749: return "1 OT/24 Jer/Jer005.mp3";
        case 750: return "1 OT/24 Jer/Jer006.mp3";
        case 751: return "1 OT/24 Jer/Jer007.mp3";
        case 752: return "1 OT/24 Jer/Jer008.mp3";
        case 753: return "1 OT/24 Jer/Jer009.mp3";
        case 754: return "1 OT/24 Jer/Jer010.mp3";
        case 755: return "1 OT/24 Jer/Jer011.mp3";
        case 756: return "1 OT/24 Jer/Jer012.mp3";
        case 757: return "1 OT/24 Jer/Jer013.mp3";
        case 758: return "1 OT/24 Jer/Jer014.mp3";
        case 759: return "1 OT/24 Jer/Jer015.mp3";
        case 760: return "1 OT/24 Jer/Jer016.mp3";
        case 761: return "1 OT/24 Jer/Jer017.mp3";
        case 762: return "1 OT/24 Jer/Jer018.mp3";
        case 763: return "1 OT/24 Jer/Jer019.mp3";
        case 764: return "1 OT/24 Jer/Jer020.mp3";
        case 765: return "1 OT/24 Jer/Jer021.mp3";
        case 766: return "1 OT/24 Jer/Jer022.mp3";
        case 767: return "1 OT/24 Jer/Jer023.mp3";
        case 768: return "1 OT/24 Jer/Jer024.mp3";
        case 769: return "1 OT/24 Jer/Jer025.mp3";
        case 770: return "1 OT/24 Jer/Jer026.mp3";
        case 771: return "1 OT/24 Jer/Jer027.mp3";
        case 772: return "1 OT/24 Jer/Jer028.mp3";
        case 773: return "1 OT/24 Jer/Jer029.mp3";
        case 774: return "1 OT/24 Jer/Jer030.mp3";
        case 775: return "1 OT/24 Jer/Jer031.mp3";
        case 776: return "1 OT/24 Jer/Jer032.mp3";
        case 777: return "1 OT/24 Jer/Jer033.mp3";
        case 778: return "1 OT/24 Jer/Jer034.mp3";
        case 779: return "1 OT/24 Jer/Jer035.mp3";
        case 780: return "1 OT/24 Jer/Jer036.mp3";
        case 781: return "1 OT/24 Jer/Jer037.mp3";
        case 782: return "1 OT/24 Jer/Jer038.mp3";
        case 783: return "1 OT/24 Jer/Jer039.mp3";
        case 784: return "1 OT/24 Jer/Jer040.mp3";
        case 785: return "1 OT/24 Jer/Jer041.mp3";
        case 786: return "1 OT/24 Jer/Jer042.mp3";
        case 787: return "1 OT/24 Jer/Jer043.mp3";
        case 788: return "1 OT/24 Jer/Jer044.mp3";
        case 789: return "1 OT/24 Jer/Jer045.mp3";
        case 790: return "1 OT/24 Jer/Jer046.mp3";
        case 791: return "1 OT/24 Jer/Jer047.mp3";
        case 792: return "1 OT/24 Jer/Jer048.mp3";
        case 793: return "1 OT/24 Jer/Jer049.mp3";
        case 794: return "1 OT/24 Jer/Jer050.mp3";
        case 795: return "1 OT/24 Jer/Jer051.mp3";
        case 796: return "1 OT/24 Jer/Jer052.mp3";
        case 797: return "1 OT/25 Lam/Lam001.mp3";
        case 798: return "1 OT/25 Lam/Lam002.mp3";
        case 799: return "1 OT/25 Lam/Lam003.mp3";
        case 800: return "1 OT/25 Lam/Lam004.mp3";
        case 801: return "1 OT/25 Lam/Lam005.mp3";
        case 802: return "1 OT/26 Eze/Eze001.mp3";
        case 803: return "1 OT/26 Eze/Eze002.mp3";
        case 804: return "1 OT/26 Eze/Eze003.mp3";
        case 805: return "1 OT/26 Eze/Eze004.mp3";
        case 806: return "1 OT/26 Eze/Eze005.mp3";
        case 807: return "1 OT/26 Eze/Eze006.mp3";
        case 808: return "1 OT/26 Eze/Eze007.mp3";
        case 809: return "1 OT/26 Eze/Eze008.mp3";
        case 810: return "1 OT/26 Eze/Eze009.mp3";
        case 811: return "1 OT/26 Eze/Eze010.mp3";
        case 812: return "1 OT/26 Eze/Eze011.mp3";
        case 813: return "1 OT/26 Eze/Eze012.mp3";
        case 814: return "1 OT/26 Eze/Eze013.mp3";
        case 815: return "1 OT/26 Eze/Eze014.mp3";
        case 816: return "1 OT/26 Eze/Eze015.mp3";
        case 817: return "1 OT/26 Eze/Eze016.mp3";
        case 818: return "1 OT/26 Eze/Eze017.mp3";
        case 819: return "1 OT/26 Eze/Eze018.mp3";
        case 820: return "1 OT/26 Eze/Eze019.mp3";
        case 821: return "1 OT/26 Eze/Eze020.mp3";
        case 822: return "1 OT/26 Eze/Eze021.mp3";
        case 823: return "1 OT/26 Eze/Eze022.mp3";
        case 824: return "1 OT/26 Eze/Eze023.mp3";
        case 825: return "1 OT/26 Eze/Eze024.mp3";
        case 826: return "1 OT/26 Eze/Eze025.mp3";
        case 827: return "1 OT/26 Eze/Eze026.mp3";
        case 828: return "1 OT/26 Eze/Eze027.mp3";
        case 829: return "1 OT/26 Eze/Eze028.mp3";
        case 830: return "1 OT/26 Eze/Eze029.mp3";
        case 831: return "1 OT/26 Eze/Eze030.mp3";
        case 832: return "1 OT/26 Eze/Eze031.mp3";
        case 833: return "1 OT/26 Eze/Eze032.mp3";
        case 834: return "1 OT/26 Eze/Eze033.mp3";
        case 835: return "1 OT/26 Eze/Eze034.mp3";
        case 836: return "1 OT/26 Eze/Eze035.mp3";
        case 837: return "1 OT/26 Eze/Eze036.mp3";
        case 838: return "1 OT/26 Eze/Eze037.mp3";
        case 839: return "1 OT/26 Eze/Eze038.mp3";
        case 840: return "1 OT/26 Eze/Eze039.mp3";
        case 841: return "1 OT/26 Eze/Eze040.mp3";
        case 842: return "1 OT/26 Eze/Eze041.mp3";
        case 843: return "1 OT/26 Eze/Eze042.mp3";
        case 844: return "1 OT/26 Eze/Eze043.mp3";
        case 845: return "1 OT/26 Eze/Eze044.mp3";
        case 846: return "1 OT/26 Eze/Eze045.mp3";
        case 847: return "1 OT/26 Eze/Eze046.mp3";
        case 848: return "1 OT/26 Eze/Eze047.mp3";
        case 849: return "1 OT/26 Eze/Eze048.mp3";
        case 850: return "1 OT/27 Dan/Dan001.mp3";
        case 851: return "1 OT/27 Dan/Dan002.mp3";
        case 852: return "1 OT/27 Dan/Dan003.mp3";
        case 853: return "1 OT/27 Dan/Dan004.mp3";
        case 854: return "1 OT/27 Dan/Dan005.mp3";
        case 855: return "1 OT/27 Dan/Dan006.mp3";
        case 856: return "1 OT/27 Dan/Dan007.mp3";
        case 857: return "1 OT/27 Dan/Dan008.mp3";
        case 858: return "1 OT/27 Dan/Dan009.mp3";
        case 859: return "1 OT/27 Dan/Dan010.mp3";
        case 860: return "1 OT/27 Dan/Dan011.mp3";
        case 861: return "1 OT/27 Dan/Dan012.mp3";
        case 862: return "1 OT/28 Hos/Hos001.mp3";
        case 863: return "1 OT/28 Hos/Hos002.mp3";
        case 864: return "1 OT/28 Hos/Hos003.mp3";
        case 865: return "1 OT/28 Hos/Hos004.mp3";
        case 866: return "1 OT/28 Hos/Hos005.mp3";
        case 867: return "1 OT/28 Hos/Hos006.mp3";
        case 868: return "1 OT/28 Hos/Hos007.mp3";
        case 869: return "1 OT/28 Hos/Hos008.mp3";
        case 870: return "1 OT/28 Hos/Hos009.mp3";
        case 871: return "1 OT/28 Hos/Hos010.mp3";
        case 872: return "1 OT/28 Hos/Hos011.mp3";
        case 873: return "1 OT/28 Hos/Hos012.mp3";
        case 874: return "1 OT/28 Hos/Hos013.mp3";
        case 875: return "1 OT/28 Hos/Hos014.mp3";
        case 876: return "1 OT/29 Joel/Joel001.mp3";
        case 877: return "1 OT/29 Joel/Joel002.mp3";
        case 878: return "1 OT/29 Joel/Joel003.mp3";
        case 879: return "1 OT/30 Amos/Amos001.mp3";
        case 880: return "1 OT/30 Amos/Amos002.mp3";
        case 881: return "1 OT/30 Amos/Amos003.mp3";
        case 882: return "1 OT/30 Amos/Amos004.mp3";
        case 883: return "1 OT/30 Amos/Amos005.mp3";
        case 884: return "1 OT/30 Amos/Amos006.mp3";
        case 885: return "1 OT/30 Amos/Amos007.mp3";
        case 886: return "1 OT/30 Amos/Amos008.mp3";
        case 887: return "1 OT/30 Amos/Amos009.mp3";
        case 888: return "1 OT/31 Oba/Oba001.mp3";
        case 889: return "1 OT/32 Jnh/Jnh001.mp3";
        case 890: return "1 OT/32 Jnh/Jnh002.mp3";
        case 891: return "1 OT/32 Jnh/Jnh003.mp3";
        case 892: return "1 OT/32 Jnh/Jnh004.mp3";
        case 893: return "1 OT/33 Mic/Mic001.mp3";
        case 894: return "1 OT/33 Mic/Mic002.mp3";
        case 895: return "1 OT/33 Mic/Mic003.mp3";
        case 896: return "1 OT/33 Mic/Mic004.mp3";
        case 897: return "1 OT/33 Mic/Mic005.mp3";
        case 898: return "1 OT/33 Mic/Mic006.mp3";
        case 899: return "1 OT/33 Mic/Mic007.mp3";
        case 900: return "1 OT/34 Nah/Nah001.mp3";
        case 901: return "1 OT/34 Nah/Nah002.mp3";
        case 902: return "1 OT/34 Nah/Nah003.mp3";
        case 903: return "1 OT/35 Hab/Hab001.mp3";
        case 904: return "1 OT/35 Hab/Hab002.mp3";
        case 905: return "1 OT/35 Hab/Hab003.mp3";
        case 906: return "1 OT/36 Zep/Zep001.mp3";
        case 907: return "1 OT/36 Zep/Zep002.mp3";
        case 908: return "1 OT/36 Zep/Zep003.mp3";
        case 909: return "1 OT/37 Hag/Hag001.mp3";
        case 910: return "1 OT/37 Hag/Hag002.mp3";
        case 911: return "1 OT/38 Zec/Zec001.mp3";
        case 912: return "1 OT/38 Zec/Zec002.mp3";
        case 913: return "1 OT/38 Zec/Zec003.mp3";
        case 914: return "1 OT/38 Zec/Zec004.mp3";
        case 915: return "1 OT/38 Zec/Zec005.mp3";
        case 916: return "1 OT/38 Zec/Zec006.mp3";
        case 917: return "1 OT/38 Zec/Zec007.mp3";
        case 918: return "1 OT/38 Zec/Zec008.mp3";
        case 919: return "1 OT/38 Zec/Zec009.mp3";
        case 920: return "1 OT/38 Zec/Zec010.mp3";
        case 921: return "1 OT/38 Zec/Zec011.mp3";
        case 922: return "1 OT/38 Zec/Zec012.mp3";
        case 923: return "1 OT/38 Zec/Zec013.mp3";
        case 924: return "1 OT/38 Zec/Zec014.mp3";
        case 925: return "1 OT/39 Mal/Mal001.mp3";
        case 926: return "1 OT/39 Mal/Mal002.mp3";
        case 927: return "1 OT/39 Mal/Mal003.mp3";
        case 928: return "1 OT/39 Mal/Mal004.mp3";
        case 929: return "2 NT/40 Mat/Mat001.mp3";
        case 930: return "2 NT/40 Mat/Mat002.mp3";
        case 931: return "2 NT/40 Mat/Mat003.mp3";
        case 932: return "2 NT/40 Mat/Mat004.mp3";
        case 933: return "2 NT/40 Mat/Mat005.mp3";
        case 934: return "2 NT/40 Mat/Mat006.mp3";
        case 935: return "2 NT/40 Mat/Mat007.mp3";
        case 936: return "2 NT/40 Mat/Mat008.mp3";
        case 937: return "2 NT/40 Mat/Mat009.mp3";
        case 938: return "2 NT/40 Mat/Mat010.mp3";
        case 939: return "2 NT/40 Mat/Mat011.mp3";
        case 940: return "2 NT/40 Mat/Mat012.mp3";
        case 941: return "2 NT/40 Mat/Mat013.mp3";
        case 942: return "2 NT/40 Mat/Mat014.mp3";
        case 943: return "2 NT/40 Mat/Mat015.mp3";
        case 944: return "2 NT/40 Mat/Mat016.mp3";
        case 945: return "2 NT/40 Mat/Mat017.mp3";
        case 946: return "2 NT/40 Mat/Mat018.mp3";
        case 947: return "2 NT/40 Mat/Mat019.mp3";
        case 948: return "2 NT/40 Mat/Mat020.mp3";
        case 949: return "2 NT/40 Mat/Mat021.mp3";
        case 950: return "2 NT/40 Mat/Mat022.mp3";
        case 951: return "2 NT/40 Mat/Mat023.mp3";
        case 952: return "2 NT/40 Mat/Mat024.mp3";
        case 953: return "2 NT/40 Mat/Mat025.mp3";
        case 954: return "2 NT/40 Mat/Mat026.mp3";
        case 955: return "2 NT/40 Mat/Mat027.mp3";
        case 956: return "2 NT/40 Mat/Mat028.mp3";
        case 957: return "2 NT/41 Mark/Mark001.mp3";
        case 958: return "2 NT/41 Mark/Mark002.mp3";
        case 959: return "2 NT/41 Mark/Mark003.mp3";
        case 960: return "2 NT/41 Mark/Mark004.mp3";
        case 961: return "2 NT/41 Mark/Mark005.mp3";
        case 962: return "2 NT/41 Mark/Mark006.mp3";
        case 963: return "2 NT/41 Mark/Mark007.mp3";
        case 964: return "2 NT/41 Mark/Mark008.mp3";
        case 965: return "2 NT/41 Mark/Mark009.mp3";
        case 966: return "2 NT/41 Mark/Mark010.mp3";
        case 967: return "2 NT/41 Mark/Mark011.mp3";
        case 968: return "2 NT/41 Mark/Mark012.mp3";
        case 969: return "2 NT/41 Mark/Mark013.mp3";
        case 970: return "2 NT/41 Mark/Mark014.mp3";
        case 971: return "2 NT/41 Mark/Mark015.mp3";
        case 972: return "2 NT/41 Mark/Mark016.mp3";
        case 973: return "2 NT/42 Luke/Luke001.mp3";
        case 974: return "2 NT/42 Luke/Luke002.mp3";
        case 975: return "2 NT/42 Luke/Luke003.mp3";
        case 976: return "2 NT/42 Luke/Luke004.mp3";
        case 977: return "2 NT/42 Luke/Luke005.mp3";
        case 978: return "2 NT/42 Luke/Luke006.mp3";
        case 979: return "2 NT/42 Luke/Luke007.mp3";
        case 980: return "2 NT/42 Luke/Luke008.mp3";
        case 981: return "2 NT/42 Luke/Luke009.mp3";
        case 982: return "2 NT/42 Luke/Luke010.mp3";
        case 983: return "2 NT/42 Luke/Luke011.mp3";
        case 984: return "2 NT/42 Luke/Luke012.mp3";
        case 985: return "2 NT/42 Luke/Luke013.mp3";
        case 986: return "2 NT/42 Luke/Luke014.mp3";
        case 987: return "2 NT/42 Luke/Luke015.mp3";
        case 988: return "2 NT/42 Luke/Luke016.mp3";
        case 989: return "2 NT/42 Luke/Luke017.mp3";
        case 990: return "2 NT/42 Luke/Luke018.mp3";
        case 991: return "2 NT/42 Luke/Luke019.mp3";
        case 992: return "2 NT/42 Luke/Luke020.mp3";
        case 993: return "2 NT/42 Luke/Luke021.mp3";
        case 994: return "2 NT/42 Luke/Luke022.mp3";
        case 995: return "2 NT/42 Luke/Luke023.mp3";
        case 996: return "2 NT/42 Luke/Luke024.mp3";
        case 997: return "2 NT/43 John/John001.mp3";
        case 998: return "2 NT/43 John/John002.mp3";
        case 999: return "2 NT/43 John/John003.mp3";
        case 1000: return "2 NT/43 John/John004.mp3";
        case 1001: return "2 NT/43 John/John005.mp3";
        case 1002: return "2 NT/43 John/John006.mp3";
        case 1003: return "2 NT/43 John/John007.mp3";
        case 1004: return "2 NT/43 John/John008.mp3";
        case 1005: return "2 NT/43 John/John009.mp3";
        case 1006: return "2 NT/43 John/John010.mp3";
        case 1007: return "2 NT/43 John/John011.mp3";
        case 1008: return "2 NT/43 John/John012.mp3";
        case 1009: return "2 NT/43 John/John013.mp3";
        case 1010: return "2 NT/43 John/John014.mp3";
        case 1011: return "2 NT/43 John/John015.mp3";
        case 1012: return "2 NT/43 John/John016.mp3";
        case 1013: return "2 NT/43 John/John017.mp3";
        case 1014: return "2 NT/43 John/John018.mp3";
        case 1015: return "2 NT/43 John/John019.mp3";
        case 1016: return "2 NT/43 John/John020.mp3";
        case 1017: return "2 NT/43 John/John021.mp3";
        case 1018: return "2 NT/44 Acts/Acts001.mp3";
        case 1019: return "2 NT/44 Acts/Acts002.mp3";
        case 1020: return "2 NT/44 Acts/Acts003.mp3";
        case 1021: return "2 NT/44 Acts/Acts004.mp3";
        case 1022: return "2 NT/44 Acts/Acts005.mp3";
        case 1023: return "2 NT/44 Acts/Acts006.mp3";
        case 1024: return "2 NT/44 Acts/Acts007.mp3";
        case 1025: return "2 NT/44 Acts/Acts008.mp3";
        case 1026: return "2 NT/44 Acts/Acts009.mp3";
        case 1027: return "2 NT/44 Acts/Acts010.mp3";
        case 1028: return "2 NT/44 Acts/Acts011.mp3";
        case 1029: return "2 NT/44 Acts/Acts012.mp3";
        case 1030: return "2 NT/44 Acts/Acts013.mp3";
        case 1031: return "2 NT/44 Acts/Acts014.mp3";
        case 1032: return "2 NT/44 Acts/Acts015.mp3";
        case 1033: return "2 NT/44 Acts/Acts016.mp3";
        case 1034: return "2 NT/44 Acts/Acts017.mp3";
        case 1035: return "2 NT/44 Acts/Acts018.mp3";
        case 1036: return "2 NT/44 Acts/Acts019.mp3";
        case 1037: return "2 NT/44 Acts/Acts020.mp3";
        case 1038: return "2 NT/44 Acts/Acts021.mp3";
        case 1039: return "2 NT/44 Acts/Acts022.mp3";
        case 1040: return "2 NT/44 Acts/Acts023.mp3";
        case 1041: return "2 NT/44 Acts/Acts024.mp3";
        case 1042: return "2 NT/44 Acts/Acts025.mp3";
        case 1043: return "2 NT/44 Acts/Acts026.mp3";
        case 1044: return "2 NT/44 Acts/Acts027.mp3";
        case 1045: return "2 NT/44 Acts/Acts028.mp3";
        case 1046: return "2 NT/45 Rom/Rom001.mp3";
        case 1047: return "2 NT/45 Rom/Rom002.mp3";
        case 1048: return "2 NT/45 Rom/Rom003.mp3";
        case 1049: return "2 NT/45 Rom/Rom004.mp3";
        case 1050: return "2 NT/45 Rom/Rom005.mp3";
        case 1051: return "2 NT/45 Rom/Rom006.mp3";
        case 1052: return "2 NT/45 Rom/Rom007.mp3";
        case 1053: return "2 NT/45 Rom/Rom008.mp3";
        case 1054: return "2 NT/45 Rom/Rom009.mp3";
        case 1055: return "2 NT/45 Rom/Rom010.mp3";
        case 1056: return "2 NT/45 Rom/Rom011.mp3";
        case 1057: return "2 NT/45 Rom/Rom012.mp3";
        case 1058: return "2 NT/45 Rom/Rom013.mp3";
        case 1059: return "2 NT/45 Rom/Rom014.mp3";
        case 1060: return "2 NT/45 Rom/Rom015.mp3";
        case 1061: return "2 NT/45 Rom/Rom016.mp3";
        case 1062: return "2 NT/46 1Co/1Co001.mp3";
        case 1063: return "2 NT/46 1Co/1Co002.mp3";
        case 1064: return "2 NT/46 1Co/1Co003.mp3";
        case 1065: return "2 NT/46 1Co/1Co004.mp3";
        case 1066: return "2 NT/46 1Co/1Co005.mp3";
        case 1067: return "2 NT/46 1Co/1Co006.mp3";
        case 1068: return "2 NT/46 1Co/1Co007.mp3";
        case 1069: return "2 NT/46 1Co/1Co008.mp3";
        case 1070: return "2 NT/46 1Co/1Co009.mp3";
        case 1071: return "2 NT/46 1Co/1Co010.mp3";
        case 1072: return "2 NT/46 1Co/1Co011.mp3";
        case 1073: return "2 NT/46 1Co/1Co012.mp3";
        case 1074: return "2 NT/46 1Co/1Co013.mp3";
        case 1075: return "2 NT/46 1Co/1Co014.mp3";
        case 1076: return "2 NT/46 1Co/1Co015.mp3";
        case 1077: return "2 NT/46 1Co/1Co016.mp3";
        case 1078: return "2 NT/47 2Co/2Co001.mp3";
        case 1079: return "2 NT/47 2Co/2Co002.mp3";
        case 1080: return "2 NT/47 2Co/2Co003.mp3";
        case 1081: return "2 NT/47 2Co/2Co004.mp3";
        case 1082: return "2 NT/47 2Co/2Co005.mp3";
        case 1083: return "2 NT/47 2Co/2Co006.mp3";
        case 1084: return "2 NT/47 2Co/2Co007.mp3";
        case 1085: return "2 NT/47 2Co/2Co008.mp3";
        case 1086: return "2 NT/47 2Co/2Co009.mp3";
        case 1087: return "2 NT/47 2Co/2Co010.mp3";
        case 1088: return "2 NT/47 2Co/2Co011.mp3";
        case 1089: return "2 NT/47 2Co/2Co012.mp3";
        case 1090: return "2 NT/47 2Co/2Co013.mp3";
        case 1091: return "2 NT/48 Gal/Gal001.mp3";
        case 1092: return "2 NT/48 Gal/Gal002.mp3";
        case 1093: return "2 NT/48 Gal/Gal003.mp3";
        case 1094: return "2 NT/48 Gal/Gal004.mp3";
        case 1095: return "2 NT/48 Gal/Gal005.mp3";
        case 1096: return "2 NT/48 Gal/Gal006.mp3";
        case 1097: return "2 NT/49 Eph/Eph001.mp3";
        case 1098: return "2 NT/49 Eph/Eph002.mp3";
        case 1099: return "2 NT/49 Eph/Eph003.mp3";
        case 1100: return "2 NT/49 Eph/Eph004.mp3";
        case 1101: return "2 NT/49 Eph/Eph005.mp3";
        case 1102: return "2 NT/49 Eph/Eph006.mp3";
        case 1103: return "2 NT/50 Php/Php001.mp3";
        case 1104: return "2 NT/50 Php/Php002.mp3";
        case 1105: return "2 NT/50 Php/Php003.mp3";
        case 1106: return "2 NT/50 Php/Php004.mp3";
        case 1107: return "2 NT/51 Col/Col001.mp3";
        case 1108: return "2 NT/51 Col/Col002.mp3";
        case 1109: return "2 NT/51 Col/Col003.mp3";
        case 1110: return "2 NT/51 Col/Col004.mp3";
        case 1111: return "2 NT/52 1Th/1Th001.mp3";
        case 1112: return "2 NT/52 1Th/1Th002.mp3";
        case 1113: return "2 NT/52 1Th/1Th003.mp3";
        case 1114: return "2 NT/52 1Th/1Th004.mp3";
        case 1115: return "2 NT/52 1Th/1Th005.mp3";
        case 1116: return "2 NT/53 2Th/2Th001.mp3";
        case 1117: return "2 NT/53 2Th/2Th002.mp3";
        case 1118: return "2 NT/53 2Th/2Th003.mp3";
        case 1119: return "2 NT/54 1Ti/1Ti001.mp3";
        case 1120: return "2 NT/54 1Ti/1Ti002.mp3";
        case 1121: return "2 NT/54 1Ti/1Ti003.mp3";
        case 1122: return "2 NT/54 1Ti/1Ti004.mp3";
        case 1123: return "2 NT/54 1Ti/1Ti005.mp3";
        case 1124: return "2 NT/54 1Ti/1Ti006.mp3";
        case 1125: return "2 NT/55 2Ti/2Ti001.mp3";
        case 1126: return "2 NT/55 2Ti/2Ti002.mp3";
        case 1127: return "2 NT/55 2Ti/2Ti003.mp3";
        case 1128: return "2 NT/55 2Ti/2Ti004.mp3";
        case 1129: return "2 NT/56 Tit/Tit001.mp3";
        case 1130: return "2 NT/56 Tit/Tit002.mp3";
        case 1131: return "2 NT/56 Tit/Tit003.mp3";
        case 1132: return "2 NT/57 Phl/Phl001.mp3";
        case 1133: return "2 NT/58 Heb/Heb001.mp3";
        case 1134: return "2 NT/58 Heb/Heb002.mp3";
        case 1135: return "2 NT/58 Heb/Heb003.mp3";
        case 1136: return "2 NT/58 Heb/Heb004.mp3";
        case 1137: return "2 NT/58 Heb/Heb005.mp3";
        case 1138: return "2 NT/58 Heb/Heb006.mp3";
        case 1139: return "2 NT/58 Heb/Heb007.mp3";
        case 1140: return "2 NT/58 Heb/Heb008.mp3";
        case 1141: return "2 NT/58 Heb/Heb009.mp3";
        case 1142: return "2 NT/58 Heb/Heb010.mp3";
        case 1143: return "2 NT/58 Heb/Heb011.mp3";
        case 1144: return "2 NT/58 Heb/Heb012.mp3";
        case 1145: return "2 NT/58 Heb/Heb013.mp3";
        case 1146: return "2 NT/59 Jas/Jas001.mp3";
        case 1147: return "2 NT/59 Jas/Jas002.mp3";
        case 1148: return "2 NT/59 Jas/Jas003.mp3";
        case 1149: return "2 NT/59 Jas/Jas004.mp3";
        case 1150: return "2 NT/59 Jas/Jas005.mp3";
        case 1151: return "2 NT/60 1Pe/1Pe001.mp3";
        case 1152: return "2 NT/60 1Pe/1Pe002.mp3";
        case 1153: return "2 NT/60 1Pe/1Pe003.mp3";
        case 1154: return "2 NT/60 1Pe/1Pe004.mp3";
        case 1155: return "2 NT/60 1Pe/1Pe005.mp3";
        case 1156: return "2 NT/61 2Pe/2Pe001.mp3";
        case 1157: return "2 NT/61 2Pe/2Pe002.mp3";
        case 1158: return "2 NT/61 2Pe/2Pe003.mp3";
        case 1159: return "2 NT/62 1Jn/1Jn001.mp3";
        case 1160: return "2 NT/62 1Jn/1Jn002.mp3";
        case 1161: return "2 NT/62 1Jn/1Jn003.mp3";
        case 1162: return "2 NT/62 1Jn/1Jn004.mp3";
        case 1163: return "2 NT/62 1Jn/1Jn005.mp3";
        case 1164: return "2 NT/63 2Jn/2Jn001.mp3";
        case 1165: return "2 NT/64 3Jn/3Jn001.mp3";
        case 1166: return "2 NT/65 Jude/Jude001.mp3";
        case 1167: return "2 NT/66 Rev/Rev001.mp3";
        case 1168: return "2 NT/66 Rev/Rev002.mp3";
        case 1169: return "2 NT/66 Rev/Rev003.mp3";
        case 1170: return "2 NT/66 Rev/Rev004.mp3";
        case 1171: return "2 NT/66 Rev/Rev005.mp3";
        case 1172: return "2 NT/66 Rev/Rev006.mp3";
        case 1173: return "2 NT/66 Rev/Rev007.mp3";
        case 1174: return "2 NT/66 Rev/Rev008.mp3";
        case 1175: return "2 NT/66 Rev/Rev009.mp3";
        case 1176: return "2 NT/66 Rev/Rev010.mp3";
        case 1177: return "2 NT/66 Rev/Rev011.mp3";
        case 1178: return "2 NT/66 Rev/Rev012.mp3";
        case 1179: return "2 NT/66 Rev/Rev013.mp3";
        case 1180: return "2 NT/66 Rev/Rev014.mp3";
        case 1181: return "2 NT/66 Rev/Rev015.mp3";
        case 1182: return "2 NT/66 Rev/Rev016.mp3";
        case 1183: return "2 NT/66 Rev/Rev017.mp3";
        case 1184: return "2 NT/66 Rev/Rev018.mp3";
        case 1185: return "2 NT/66 Rev/Rev019.mp3";
        case 1186: return "2 NT/66 Rev/Rev020.mp3";
        case 1187: return "2 NT/66 Rev/Rev021.mp3";
        case 1188: return "2 NT/66 Rev/Rev022.mp3";
        case 1189: return "2_Ps119v129-176.mp3";
        case 1190: return "split/2_Ps119v1-40.mp3";
        case 1191: return "split/2_Ps119v81-128.mp3";
        case 1192: return "split/3_Ps119v41-80.mp3";
        default: return "";
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
