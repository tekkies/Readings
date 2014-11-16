package uk.co.tekkies.readings.test.bdd.readingsActivity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.common.testing.ui.espresso.ViewInteraction;

import java.util.Calendar;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.test.bdd.readingsActivity.ReadingsActivityTestBase;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.StringContains.containsString;

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
        onView(withText(containsString(ezra10Summary))).check(matches(isDisplayed()));
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(containsString(ezra10Summary))).check(doesNotExist());
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(containsString(ezra10Summary))).check(matches(isDisplayed()));
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(containsString(ezra10Summary))).check(doesNotExist());
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withText(containsString(ezra10Summary))).check(matches(isDisplayed()));
    }
}