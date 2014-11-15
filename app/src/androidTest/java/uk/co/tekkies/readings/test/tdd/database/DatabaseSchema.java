package uk.co.tekkies.readings.test.tdd.database;

import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import java.io.File;

import uk.co.tekkies.readings.ReadingsApplication;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.util.DatabaseHelper;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class DatabaseSchema extends ApplicationTestCase<ReadingsApplication> {
    private static final String DB_FILE = "/data/data/uk.co.tekkies.readings/databases/Readings.db3";

    public DatabaseSchema() {
        super(ReadingsApplication.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testUpgrade() {
        createApplication();
        File file = new File(DB_FILE);
        if (file.exists()) {
            file.delete();
        }
        assertTrue(!file.exists());
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();

        SQLiteDatabase database = SQLiteDatabase.openDatabase(DB_FILE, null, SQLiteDatabase.OPEN_READONLY);
        try {
            assertTrue("Upgraded to current version", database.getVersion() == DatabaseHelper.DB_VERSION);
        } finally {
            database.close();
        }
        assertTrue(file.exists());
    }
}
