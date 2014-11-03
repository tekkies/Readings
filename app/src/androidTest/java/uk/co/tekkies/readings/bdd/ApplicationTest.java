package uk.co.tekkies.readings.bdd;

import android.app.Application;
import android.test.ApplicationTestCase;

import uk.co.tekkies.readings.day.DayModel1;
import uk.co.tekkies.readings.day.DayPresenter1;
import uk.co.tekkies.readings.day.DayView;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testOne() {
        assertTrue(false);
    }

}