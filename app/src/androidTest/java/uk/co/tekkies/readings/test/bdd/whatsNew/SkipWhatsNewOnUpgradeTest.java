package uk.co.tekkies.readings.test.bdd.whatsNew;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.model.Prefs;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */



//public class ApplicationTest extends ApplicationTestCase<Application> {
public class SkipWhatsNewOnUpgradeTest extends ActivityInstrumentationTestCase2<ReadingsActivity> {
    public SkipWhatsNewOnUpgradeTest() {
        super(ReadingsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation ().getTargetContext());
        new Prefs(getInstrumentation().getTargetContext()).checkForUpgrade(); //Checks and updates
        getActivity(); //Start the activity
    }

    public void testStartupWithoutWhatsNew() {
        onView(withId(R.id.whatsNewLinearLayout)).check(doesNotExist());
    }

//
//    private Boolean forceWhatsNewDialog() {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        int currentVersionNumber = 0;
//        int savedVersionNumber = sharedPref.getInt(ReadingsActivity.VERSION_KEY, 0);
//        try {
//            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
//            currentVersionNumber = pi.versionCode;
//        } catch (Exception e) {
//            Analytics.reportCaughtException(this, e);
//        }
//        if (currentVersionNumber > savedVersionNumber) {
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putInt(VERSION_KEY, currentVersionNumber);
//            editor.commit();
//        }
//        return (currentVersionNumber > savedVersionNumber);
//    }
//
//
//

}