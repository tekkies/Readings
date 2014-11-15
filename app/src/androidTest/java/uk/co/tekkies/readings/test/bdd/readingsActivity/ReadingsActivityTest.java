package uk.co.tekkies.readings.test.bdd.readingsActivity;

import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.test.Utils;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.text.StringContains.containsString;

public class ReadingsActivityTest extends ActivityInstrumentationTestCase2<ReadingsActivity> {
    public ReadingsActivityTest() {
        super(ReadingsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Utils.suppressStartupWhatsNew(getInstrumentation().getTargetContext());
        getActivity(); //Start the activity
    }

    public void testOpenSpecificDate() {
        final ReadingsActivity readingsActivity = getActivity();
        readingsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                readingsActivity.onDateSet(null, 2011, Calendar.FEBRUARY, 1);
            }
        });
        onView(withText(containsString("Exodus 5"))).check(matches(isDisplayed()));
        onView(withText(containsString("Tue 1 Feb 11"))).check(matches(isDisplayed()));
    }

    public void testOpenToday() {
        onView(withId(R.id.menu_date)).perform(click());
        onView(withText("Done")).perform(click());
        onView(withText(containsString("Today"))).check(matches(isDisplayed()));
    }


}