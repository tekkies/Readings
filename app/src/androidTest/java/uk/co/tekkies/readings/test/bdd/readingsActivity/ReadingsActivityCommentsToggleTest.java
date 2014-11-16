package uk.co.tekkies.readings.test.bdd.readingsActivity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.common.testing.ui.espresso.ViewInteraction;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.test.bdd.readingsActivity.ReadingsActivityTestBase;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

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
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withId(R.id.menu_summary)).perform(click());
        onView(withId(R.id.menu_summary)).perform(click());
    }
}