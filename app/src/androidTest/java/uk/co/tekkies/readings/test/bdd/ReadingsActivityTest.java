package uk.co.tekkies.readings.test.bdd;

import android.test.ActivityInstrumentationTestCase2;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ReadingsActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */



//public class ApplicationTest extends ApplicationTestCase<Application> {
public class ReadingsActivityTest extends ActivityInstrumentationTestCase2<ReadingsActivity> {
    public ReadingsActivityTest() {
        super(ReadingsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Espresso will not launch our activity for us, we must launch it via getActivity().
        getActivity();
    }

    public void testOne() {

        com.google.android.apps.common.testing.ui.espresso.ViewInteraction whatsNew = onView(withId(R.id.whatsNewDialogRoot));
        com.google.android.apps.common.testing.ui.espresso.ViewInteraction feedback = onView(withId(R.id.buttonFeedback));

        whatsNew.perform(click());
        feedback.perform(click());

    }

}