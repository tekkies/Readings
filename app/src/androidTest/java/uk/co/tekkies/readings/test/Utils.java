package uk.co.tekkies.readings.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import uk.co.tekkies.readings.day.DayFragment;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.util.Analytics;

public class Utils {
    public static void setSummariesEnabled(Context context, boolean enabled) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Prefs.PREF_SHOW_SUMMARY, enabled);
        editor.commit();
    }
}
