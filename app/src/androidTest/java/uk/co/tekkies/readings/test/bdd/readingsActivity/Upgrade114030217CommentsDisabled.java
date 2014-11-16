package uk.co.tekkies.readings.test.bdd.readingsActivity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.test.Utils;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeRight;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.text.StringContains.containsString;

public class Upgrade114030217CommentsDisabled extends ReadingsActivityTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        preferences.edit().clear().commit();
        Utils.disableComments(getInstrumentation().getTargetContext());
        getActivity(); //Start the activity
    }

    public void testCommentsAutoEnabled(){
        //todo: Also create Upgrade114030217CommentsDisabled class.
        jumpTo(2011, Calendar.NOVEMBER, 8);
        assertTrue("Make this fail by fixing disableComments", false);
        onView(withText(containsString("YHWH stirred up the spirit of Cyrus. Cyrus' decree. Cyrus brought out the temple articles taken by Nebuchadnezzar."))).check(matches(isDisplayed()));
    }

    public void testDatabaseUpgrade00051() {
        jumpTo(2011, Calendar.NOVEMBER, 8);
        onView(withText(containsString("YHWH stirred up the spirit of Cyrus. Cyrus' decree. Cyrus brought out the temple articles taken by Nebuchadnezzar."))).check(matches(isDisplayed()));
    }
}