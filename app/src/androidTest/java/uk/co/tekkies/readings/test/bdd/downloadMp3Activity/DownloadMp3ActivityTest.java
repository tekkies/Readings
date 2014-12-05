package uk.co.tekkies.readings.test.bdd.downloadMp3Activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.downloadmp3.DownloadMp3Activity;
import uk.co.tekkies.readings.test.bdd.readingsActivity.ReadingsActivityTestBase;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeRight;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.text.StringContains.containsString;


public class DownloadMp3ActivityTest extends ActivityInstrumentationTestCase2<DownloadMp3Activity> {
    public DownloadMp3ActivityTest() {
        super(DownloadMp3Activity.class);
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testShowDownloadButton() {
        onView(withText("Download")).check(matches(isDisplayed()));
    }


}