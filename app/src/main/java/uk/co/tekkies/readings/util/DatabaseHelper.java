/*
Copyright 2013 Andrew Joiner

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package uk.co.tekkies.readings.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "Readings.db3";
    public static int DB_VERSION = 50;
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int upgrade = oldVersion + 1; upgrade <= newVersion; upgrade++) {
            applyUpgrade(db, upgrade);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Version 3.x.x starts at DB version 3
        onUpgrade(db, 3, DB_VERSION);
    }

    // TODO: Review exception handling
    private void applyUpgrade(SQLiteDatabase db, int upgrade) {
        Log.i("UpgradeDB", "Start:Upgrade to v" + upgrade);
        String upgradePath = "db_upgrade/" + String.format("%05d", upgrade) + ".sql";
        InputStream instream = null;
        try {
            instream = AssetHelper.openAsset(context, upgradePath);
        } catch (IOException e) {
            Analytics.reportCaughtException(context, e);
        }
        if (instream != null) {
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            //Transactions can improve performance
            db.beginTransaction();
            try {

                StringBuilder sql = new StringBuilder();
                try {
                    line = buffreader.readLine();
                    //Statements are separated by a semicolon on a line by itself
                    while (line != null) {
                        if (line.equals(";")) {
                            Log.v("SQL", sql.toString());
                            db.execSQL(sql.toString() + ";");
                            sql = new StringBuilder();
                        } else {
                            sql.append(line + "\n");
                        }
                        line = buffreader.readLine();
                    }
                    instream.close();
                } catch (IOException e) {
                    Analytics.reportCaughtException(context, e);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        Log.i("UpgradeDB", "End:Upgrade to v" + upgrade);
    }

    /**
     * Allows a developerto run a select statement against the installed database from the debugger.
     * 
     * <pre>
     * {@code
     * AppDataBaseHelper.debugRawQuery(activity, "select * from summary");
     * }
     * </pre>
     * 
     * @param context
     *            Just grab any context in scope, e.g. activity
     * @param query
     * @return
     */
    public static StringBuilder debugRawQuery(Context context, String query) {
        StringBuilder result = new StringBuilder();
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        try {
            SQLiteDatabase db = myDbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                for (int columnIndex = 0; columnIndex < cursor.getColumnCount(); columnIndex++) {
                    String columnString = cursor.getColumnName(columnIndex) + ":";
                    columnString += (cursor.isNull(columnIndex) ? "<NULL>" : cursor.getString(columnIndex));
                    Log.d("SQL", columnString);
                    result.append(columnString + '\n');
                }
                cursor.moveToNext();
            }
        } finally {
            myDbHelper.close();
        }
        return result;
    }
}