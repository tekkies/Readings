package uk.co.tekkies.readings.test.bdd.startup;

import android.test.ActivityInstrumentationTestCase2;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.test.Utils;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class SkipWhatsNewOnUpgradeTest extends ActivityInstrumentationTestCase2<ReadingsActivity> {
    public SkipWhatsNewOnUpgradeTest() {
        super(ReadingsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Utils.suppressStartupWhatsNew(getInstrumentation().getTargetContext());
        getActivity(); //Start the activity
    }

    public void testStartupWithoutWhatsNew() {
        onView(withId(R.id.whatsNewLinearLayout)).check(doesNotExist());
    }
}