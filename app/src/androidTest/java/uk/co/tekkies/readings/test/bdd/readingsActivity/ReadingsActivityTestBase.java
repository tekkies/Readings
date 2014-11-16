package uk.co.tekkies.readings.test.bdd.readingsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.model.Prefs;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.text.StringContains.containsString;

public class ReadingsActivityTestBase extends ActivityInstrumentationTestCase2<ReadingsActivity> {
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
        onView(withId(R.id.menu_date)).perform(click());
        onView(withText("Done")).perform(click());
        onView(withText(containsString("Today"))).check(matches(isDisplayed()));
    }

    public static void suppressStartupWhatsNew(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        new Prefs(context).checkForUpgrade(); //Checks and updates
    }

}