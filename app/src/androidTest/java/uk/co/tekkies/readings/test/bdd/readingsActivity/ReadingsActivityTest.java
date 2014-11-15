package uk.co.tekkies.readings.test.bdd.readingsActivity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.test.Utils;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

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
//        onView(withId(R.id.menu_date)).perform(click());
//        onView(withId(R.id.whatsNewLinearLayout)).perform(click());
        final ReadingsActivity readingsActivity = getActivity();

        readingsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                readingsActivity.onDateSet(null, 2011, 1, 1);
            }
        });

        onView(withId(R.id.whatsNewLinearLayout)).check(matches(isDisplayed()));
    }
}