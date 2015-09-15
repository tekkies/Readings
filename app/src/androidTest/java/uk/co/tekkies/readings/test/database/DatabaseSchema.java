package uk.co.tekkies.readings.test.tdd.database;

import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import java.io.File;
import java.util.Calendar;

import uk.co.tekkies.readings.ReadingsApplication;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.util.DatabaseHelper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.text.StringContains.containsString;

public class DatabaseSchema extends ApplicationTestCase<ReadingsApplication> {
    private static final String DB_FILE = "/data/data/uk.co.tekkies.readings/databases/Readings.db3";

    public DatabaseSchema() {
        super(ReadingsApplication.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testUpgradeFromEmpty() {
        createApplication();
        File file = new File(DB_FILE);
        if (file.exists()) {
            file.delete();
        }
        assertTrue(!file.exists());
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        assertTrue("Upgrade should be required", databaseHelper.isUpgradeRequired());
        databaseHelper.getWritableDatabase();
        assertTrue("Upgrade no longer required", !databaseHelper.isUpgradeRequired());
        assertTrue(file.exists());
    }

}
