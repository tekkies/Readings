package uk.co.tekkies.readings.test.bdd.whatsNew;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.util.Analytics;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */



//public class ApplicationTest extends ApplicationTestCase<Application> {
public class ShowWhatsNewOnUpgradeTest extends ActivityInstrumentationTestCase2<ReadingsActivity> {
    public ShowWhatsNewOnUpgradeTest() {
        super(ReadingsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getContext());
//        preferences.edit().clear().commit();
        getActivity(); //Start the activity
    }

    public void testStartupWithWhatsNew() {
        onView(withId(R.id.whatsNewLinearLayout)).check(matches(isDisplayed()));
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