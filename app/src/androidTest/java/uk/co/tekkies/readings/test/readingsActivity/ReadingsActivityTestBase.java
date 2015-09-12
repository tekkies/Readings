package uk.co.tekkies.readings.test.readingsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.apps.common.testing.testrunner.InstrumentationRegistry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.util.Analytics;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.text.StringContains.containsString;

public class ReadingsActivityTestBase extends ActivityInstrumentationTestCase2<ReadingsActivity> {
    protected final String CalendarConfirmButtonText="OK";

    public ReadingsActivityTestBase() {
        super(ReadingsActivity.class);
    }
    protected void jumpTo(final int year, final int month, final int day) {
        final ReadingsActivity readingsActivity = getActivity();
        readingsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                readingsActivity.onDateSet(null, year, month, day);
            }
        });
    }

    protected void jumpToToday() {
        //openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstance().getTargetContext());
        onView(withId(R.id.menu_date)).perform(click());
        onView(withText(CalendarConfirmButtonText)).perform(click());
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        String expected = "Today (" +new SimpleDateFormat("E").format(date)+")";
        onView(withText(containsString(expected))).check(matches(isDisplayed()));
    }

    public static void suppressStartupWhatsNew(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Prefs.VERSION_KEY, pi.versionCode);
            editor.commit();
        } catch (Exception e) {
            Analytics.reportCaughtException(context, e);
        }
    }

    public static void setLastRunAppVersion(Context context, int versionCode) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Prefs.VERSION_KEY, versionCode);
            editor.commit();
        } catch (Exception e) {
            Analytics.reportCaughtException(context, e);
        }
    }

    public static void deleteDatabase(Context context) {
        File file = context.getDatabasePath("Readings.db3");
        if(file.exists()) {
            file.delete();
        }
    }

}