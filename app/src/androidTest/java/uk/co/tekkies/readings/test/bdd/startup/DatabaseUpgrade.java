package uk.co.tekkies.readings.test.bdd.startup;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import java.io.File;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class DatabaseUpgrade extends ActivityInstrumentationTestCase2<ReadingsActivity> {
    private static final String DB_FILE = "/data/data/uk.co.tekkies.readings/databases/Readings.db3";

    public DatabaseUpgrade() {
        super(ReadingsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation ().getTargetContext());
        preferences.edit().clear().commit();
        new File(DB_FILE).delete();
        getActivity();
    }

    public void testStartupWithWhatsNew() {
        onView(withText("Loading")).check(matches(isDisplayed()));
    }

    public void testDatabaseCreated() {
        assertTrue("Database created", new File(DB_FILE).exists());
    }
}