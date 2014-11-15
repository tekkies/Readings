package uk.co.tekkies.readings.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import uk.co.tekkies.readings.model.Prefs;

/**
 * Created by ajoiner on 09/11/2014.
 */
public class Utils {
    public static void suppressStartupWhatsNew(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        new Prefs(context).checkForUpgrade(); //Checks and updates
    }

}
