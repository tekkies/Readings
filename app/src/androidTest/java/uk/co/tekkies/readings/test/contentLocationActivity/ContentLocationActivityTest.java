package uk.co.tekkies.readings.test.contentLocationActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ContentLocationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ContentLocationActivityTest{


    @Rule
    public IntentsTestRule<ContentLocationActivity> mActivityRule = new IntentsTestRule<>(
            ContentLocationActivity.class);

    @Before
    public void stubAllExternalIntents() {
        Intent resultData = new Intent();
        Uri uri = Uri.fromFile(new File("/storage/emulated/0/readings-samples"));
        resultData.setData(uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(toPackage("com.estrongs.android.pop")).respondWith(result);
    }

    @Test
    public void testContentIsFound() {
        onView(withId(R.id.button_browse)).perform(click());


    }

}
