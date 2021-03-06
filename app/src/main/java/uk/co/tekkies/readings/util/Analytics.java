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
    private static final String CATEGORY_CAUGHT_EXCEPTION = "caught_exception";

    public static final String ACTION_GENERAL = "action_general";
    public static final String ACTION_FOUND = "mp3_found";
    public static final String LABEL_TEXT_SIZE = "text_size";
    public static final String LABEL_MP3_SEARCH = "settings_mp3_search";
    public static final String LABEL_TOTAL = "total";
    public static final String LABEL_CUSTOM_DATE = "custom_date";
    public static final String LABEL_ABOUT = "about";
    public static final String LABEL_WHATS_NEW = "whats_new";

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

    public static void UIClick(Context context, String label, long value) {
        SendEvent(context, CATEGORY_ACTION, ACTION_GENERAL, label, value);
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

    /**
     * Extracts minimal essential exception data and reports it as a Google Analytics event 
     * (Not reported as an App exception :).<br/>
     * <br/>
     * Reported action: Exception type & message<br/>
     * Reported label: File & line no
     * <code><pre>
     * } catch (Exception e) {
     *    Analytics.reportCaughtException(getActivity(), e);
     * }
     * </pre></code>  
     * @param context
     * @param exception 
     */
    public static void reportCaughtException(Context context, Exception e) {
        if (isEnabled(context)) {
            String message=e.getClass().getSimpleName();
            if(e.getMessage() != null) {
                message += ":"+e.getMessage();
            }
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            String location="";
            for(int i=0;i<stackTraceElements.length; i++) {
                StackTraceElement stackTraceElement = stackTraceElements[i];
                if(stackTraceElement.getClassName().contains("uk.co.tekkies")) {
                    location += stackTraceElement.getFileName().replace(".java","")+":"+stackTraceElement.getLineNumber();
                    break;
                }
            }
            SendEvent(context, CATEGORY_CAUGHT_EXCEPTION, message, location, 0);
        }
    }
}
