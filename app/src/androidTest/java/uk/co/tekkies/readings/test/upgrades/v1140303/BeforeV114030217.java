package uk.co.tekkies.readings.test.upgrades.v1140303;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import uk.co.tekkies.readings.test.Utils;
import uk.co.tekkies.readings.test.readingsActivity.ReadingsActivityTestBase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

public class BeforeV114030217 extends ReadingsActivityTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        preferences.edit().clear().commit();
        deleteDatabase(getInstrumentation().getTargetContext());
        Utils.setSummariesEnabled(getInstrumentation().getTargetContext(), false);
        setLastRunAppVersion(getInstrumentation().getTargetContext(), 114030216);
        getActivity(); //Start the activity
    }

    public void testSummariesWereEnabledOnStart(){
        onView(withText("OK")).perform(click()); //Suppress What's New
        jumpTo(2011, Calendar.NOVEMBER, 8);
        onView(withText(containsString("YHWH stirred up the spirit of Cyrus. Cyrus' decree. Cyrus brought out the temple articles taken by Nebuchadnezzar."))).check(matches(isDisplayed()));
    }

    public void testDatabaseUpgrade00051() {
        onView(withText("OK")).perform(click()); //Suppress What's New
        jumpTo(2011, Calendar.NOVEMBER, 8);
        onView(withText(containsString("YHWH stirred up the spirit of Cyrus. Cyrus' decree. Cyrus brought out the temple articles taken by Nebuchadnezzar."))).check(matches(isDisplayed()));
    }
}