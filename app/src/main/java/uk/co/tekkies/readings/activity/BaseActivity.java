package uk.co.tekkies.readings.activity;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.util.Analytics;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
    SharedPreferences sharedPreferences;
    Boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
        super.onCreate(savedInstanceState);
    }

    protected void setChosenTheme() {
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sharedPreferences.getBoolean(getString(R.string.pref_key_night_mode), false);
        setTheme(nightMode ? R.style.Night : R.style.Day);
    }

    protected void doDayNightToggle() {
        nightMode = !nightMode;
        sharedPreferences.edit().putBoolean(getString(R.string.pref_key_night_mode), nightMode).commit();
        Analytics.UIClick(this, "night_mode", (long) (nightMode ? 1 : 0));
        recreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Analytics.startActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Analytics.stopActivity(this);
    }

}
