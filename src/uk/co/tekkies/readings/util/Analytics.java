package uk.co.tekkies.readings.util;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.BaseActivity;

import android.content.Context;
import android.preference.PreferenceManager;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class Analytics {

    private static final String CATEGORY_ACTION = "action";

    private static final String ACTION_BUTTON_PRESS = "button_press";
    private static final String ACTION_GENERAL = "action_general";

    private static final String LABEL_NIGHT_MODE = "night_mode";
    private static final String LABEL_TEXT_SIZE = "text_size";

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
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.pref_key_enable_analytics), true);
    }

    private static void SendEvent(Context context, String category, String action, String label, long value) {
        if (isEnabled(context)) {
            EasyTracker easyTracker = EasyTracker.getInstance(context);
            easyTracker.send(MapBuilder.createEvent(category, action, label, value).build());
        }
    }
}
