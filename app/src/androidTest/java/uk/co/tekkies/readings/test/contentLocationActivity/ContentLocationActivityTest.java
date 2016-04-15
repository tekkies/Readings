package uk.co.tekkies.readings.test.contentLocationActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import junit.framework.Assert;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ContentLocationActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ContentLocationActivityTest{

    protected final File SAMPLE_FOLDER = new File(Environment.getExternalStorageDirectory(), "readings-samples");


    @Rule
    public IntentsTestRule<ContentLocationActivity> mActivityRule = new IntentsTestRule<ContentLocationActivity>(
            ContentLocationActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
            preferences.edit().clear().commit();
        }
    };

    @Before
    public void setup() {
        Intent resultData = new Intent();
        Uri uri = Uri.fromFile(SAMPLE_FOLDER);
        resultData.setData(uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(toPackage("com.estrongs.android.pop")).respondWith(result);
    }

    @Test
    public void testContentIsFound() {
        Assert.assertTrue("Device must be prepared with dummy content using Generate-DummyMp3Files.ps1", SAMPLE_FOLDER.exists());

        onView(withId(R.id.button_browse)).perform(click());

        onView(withText(getFoundMessge("KjvScourby2"))).check(matches(isDisplayed()));
    }

    private String getFoundMessge(String version) {
        File folder = new File(SAMPLE_FOLDER, version);
        String message = String.format("Found: %1s", folder.toString());
        return message;
    }


}
