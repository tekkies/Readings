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
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.scrollTo;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeRight;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.text.StringContains.containsString;
import static org.hamcrest.Matchers.allOf;

public class ReadingsActivityTest extends ReadingsActivityTestBase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        preferences.edit().clear().commit();
        suppressStartupWhatsNew(getInstrumentation().getTargetContext());
        getActivity();
    }

    public void testOpenSpecificDate() {
        jumpToToday();
        jumpTo(2011, Calendar.FEBRUARY, 1);
        onView(withText(containsString("Exodus 5"))).check(matches(isDisplayed()));
        onView(withText(containsString("Tue 1 Feb 11"))).check(matches(isDisplayed()));
    }

    public void testOpenToday() {
        jumpTo(2011, Calendar.MARCH, 20);
        onView(withId(R.id.menu_date)).perform(click());
        onView(withText("Done")).perform(click());
        onView(withText(containsString("Today"))).check(matches(isDisplayed()));
    }

    public void testSwipeRight() {
        jumpTo(2011, Calendar.FEBRUARY, 1);
        onView(withText(containsString("Exodus 5"))).perform(swipeRight());
        onView(withText(containsString("Exodus 3"))).check(matches(isDisplayed()));
        onView(withText(containsString("Mon 31 Jan 11"))).check(matches(isDisplayed()));
    }

    public void testSwipeLeft() {
        jumpTo(2011, Calendar.FEBRUARY, 1);
        onView(withText(containsString("Exodus 5"))).perform(swipeLeft());
        onView(withText(containsString("Exodus 7"))).check(matches(isDisplayed()));
        onView(withText(containsString("Wed 2 Feb 11"))).check(matches(isDisplayed()));
    }

    public void testShowAbout() {
        getActivity().openOptionsMenu();
        onView(withId(R.id.menu_about)).perform(click());
        onView(withText(containsString("Licensed under the Apache License"))).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
    }


    public void testPsalm119() {
        final String summary = "How blessed those who walk in law of YHWH. Love God's law, meditation day & night. Seek me Your lost sheep.";
        jumpTo(2011, Calendar.MARCH, 11);
        onView(allOf(withText(containsString(summary)), isDisplayed())).check(matches(isDisplayed())); //next tab has same summary, is in the view hierarchy, but not visible.
    }
}