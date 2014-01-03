package uk.co.tekkies.readings.util;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.BaseActivity;
import uk.co.tekkies.readings.model.Prefs;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class Analytics {

    private static final String CATEGORY_ACTION = "action";
    public  static final String CATEGORY_PREFS = "prefs";
    public static final String CATEGORY_MP3_CONTENT = "mp3_content";

    public static final String ACTION_BUTTON_PRESS = "button_press";
    public static final String ACTION_GENERAL = "action_general";
    public static final String ACTION_FOUND = "mp3_found";

    public static final String LABEL_NIGHT_MODE = "night_mode";
    public static final String LABEL_TEXT_SIZE = "text_size";
    public static final String LABEL_MP3_SEARCH = "settings_mp3_search";
    public static final String LABEL_TOTAL = "total";

    public static void startActivity(BaseActivity activity) {
        if (isEnabled(activity)) {
            EasyTracker.getInstance(activity).activityStart(activity);
        }
    }

    public static void stopActivity(BaseActivity activity) {
        if (isEnabled(activity)) {
            EasyTracker.getInstance(activity).activityStop(activity);
        }
    }

    public static void EventDayNightToggle(Context context, Boolean nightMode) {
        SendEvent(context, CATEGORY_ACTION, ACTION_BUTTON_PRESS, LABEL_NIGHT_MODE, (long) (nightMode ? 1 : 0));
    }

    public static void EventTextSize(Context context, double textSize) {
        long textSizeLong = (long) (textSize * 100.0);
        SendEvent(context, CATEGORY_ACTION, ACTION_GENERAL, LABEL_TEXT_SIZE, textSizeLong);
    }

    private static boolean isEnabled(Context context) {
        return (new Prefs(context)).loadAnalyticsEnabled();
    }


    public static void SendEvent(Context context, String category, String action, String label, long value) {
        if (isEnabled(context)) {
            EasyTracker easyTracker = EasyTracker.getInstance(context);
            easyTracker.send(MapBuilder.createEvent(category, action, label, value).build());
        }
    }

    public static void UIClick(Context context, String label) {
        SendEvent(context, CATEGORY_ACTION, ACTION_GENERAL, label, 0);
    }

    public static void PrefsChange(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = "(unsupported)";
        value = getPrefValue(key, prefs, value);
        //Case for enable/disable analytics
        if(key.equals(context.getString(R.string.pref_key_enable_analytics))) {
            EasyTracker easyTracker = EasyTracker.getInstance(context);
            easyTracker.send(MapBuilder.createEvent(CATEGORY_PREFS, key, value, (long)0).build());
        } else {
            SendEvent(context, CATEGORY_PREFS, key, value, 0);
        }
    }

    private static String getPrefValue(String key, SharedPreferences prefs, String value) {
        try {
            value = prefs.getString(key, "default");
        } catch (Exception e) {
            try {
                value = prefs.getBoolean(key, false) ? "true" : "false";
            } catch (Exception e1) {
            }
        }
        return value;
    }
}
