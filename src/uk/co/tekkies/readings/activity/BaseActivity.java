package uk.co.tekkies.readings.activity;

import uk.co.tekkies.readings.R;
import android.app.Dialog;
import android.app.ActionBar.OnMenuVisibilityListener;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends FragmentActivity implements OnCancelListener, OnMenuVisibilityListener {
    SharedPreferences sharedPreferences;
    Boolean nightMode;
    Dialog dimmerOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
        super.onCreate(savedInstanceState);
        createDimmerOverlay();
        dimmerOverlay.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().addOnMenuVisibilityListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    protected void setChosenTheme() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sharedPreferences.getBoolean(getString(R.string.pref_key_night_mode), false);
        setTheme(nightMode ? R.style.Night : R.style.Day);
    }

    private void createDimmerOverlay() {
        dimmerOverlay = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dimmerOverlay.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dimmerOverlay.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 255, 0)));
        dimmerOverlay.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dimmerOverlay.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        dimmerOverlay.setOnCancelListener(this);
        dimmerOverlay.setCancelable(true);
    }

    protected void doDayNightToggle() {
        nightMode = !nightMode;
        sharedPreferences.edit().putBoolean(getString(R.string.pref_key_night_mode), nightMode).commit();
        recreate();
    }

    @Override
    public void onMenuVisibilityChanged(boolean isVisible) {
        if (dimmerOverlay != null) {
            if (isVisible) {
                if (dimmerOverlay.isShowing()) {
                    dimmerOverlay.dismiss();
                }
            } else {
                if (!dimmerOverlay.isShowing()) {
                    dimmerOverlay.show();
                }
            }
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    protected void onPause() {
        if (dimmerOverlay != null) {
            if (dimmerOverlay.isShowing()) {
                dimmerOverlay.dismiss();
            }
        }
        super.onPause();
    }

}
