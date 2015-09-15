package uk.co.tekkies.readings.test.readingsActivity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.hamcrest.Matchers;

import java.util.Calendar;

import uk.co.tekkies.readings.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ReadingsActivityCommentsToggleTest extends ReadingsActivityTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        preferences.edit().clear().commit();
        suppressStartupWhatsNew(getInstrumentation().getTargetContext());
        getActivity(); //Start the activity
    }

    public void testToggleComments() {
        final String ezra10Summary="Decision made to put away foreign wives. Assembly of all returnees, told to put away. 3 month investigation. List of offenders.";
        jumpTo(2005, Calendar.NOVEMBER, 14);
        onView(withText(Matchers.containsString(ezra10Summary))).check(matches(isDisplayed()));
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(Matchers.containsString(ezra10Summary))).check(doesNotExist());
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(Matchers.containsString(ezra10Summary))).check(matches(isDisplayed()));
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(Matchers.containsString(ezra10Summary))).check(doesNotExist());
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(Matchers.containsString(ezra10Summary))).check(matches(isDisplayed()));
    }
}