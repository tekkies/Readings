package uk.co.tekkies.readings.test.upgrades.schema;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.hamcrest.Matchers;

import java.util.Calendar;

import uk.co.tekkies.readings.test.readingsActivity.ReadingsActivityTestBase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class db00052 extends ReadingsActivityTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        preferences.edit().clear().commit();
        deleteDatabase(getInstrumentation().getTargetContext());
        getActivity(); //Start the activity
    }

    public void testDatabaseUpgrade00052() {
        onView(withText("OK")).perform(click()); //Suppress What's New
        jumpTo(2011, Calendar.DECEMBER, 2);
        onView(withText(Matchers.containsString("Job of Uz was blameless. He was put into Satan's hand, but no direct calamity. 4 calamities: Sabeans, fire, Chaldeans, wind."))).check(matches(isDisplayed()));
    }
}