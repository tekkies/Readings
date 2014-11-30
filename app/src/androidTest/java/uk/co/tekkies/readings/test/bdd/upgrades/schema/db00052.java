package uk.co.tekkies.readings.test.bdd.upgrades.schema;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.test.Utils;
import uk.co.tekkies.readings.test.bdd.readingsActivity.ReadingsActivityTestBase;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.text.StringContains.containsString;

public class db00052 extends ReadingsActivityTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        preferences.edit().clear().commit();
        getActivity(); //Start the activity
    }

    public void testDatabaseUpgrade00052() {
        onView(withText("OK")).perform(click()); //Suppress What's New
        jumpTo(2011, Calendar.DECEMBER, 2);
        onView(withText(containsString("Job of Uz was blameless. He was put into Satan's hand, but no direct calamity. 4 calamities: Sabeans, fire, Chaldeans, wind."))).check(matches(isDisplayed()));
    }
}