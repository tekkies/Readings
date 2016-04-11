package uk.co.tekkies.readings.test.contentLocationActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
        Uri uri = Uri.fromFile(new File("/storage/emulated/0/readings-samples"));
        resultData.setData(uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(toPackage("com.estrongs.android.pop")).respondWith(result);
    }

    @Test
    public void testContentIsFound() {

        //Device must be prepared with dummy content using Generate-DummyMp3Files.ps1

        onView(withId(R.id.button_browse)).perform(click());

        onView(withText("Found: /storage/emulated/0/readings-samples/KjvScourby2")).check(matches(isDisplayed()));
    }



}
