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

package uk.co.tekkies.readings.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import uk.co.tekkies.readings.Injector;
import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.adapter.PortionArrayAdapter;
import uk.co.tekkies.readings.day.DayPresenter;
import uk.co.tekkies.readings.day.DayView;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.util.DatabaseHelper;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ReadingsFragment extends Fragment implements DayView {

    private DayPresenter presenter;

    public static final String ARG_YEAR = "year";
    public static final String ARG_MONTH = "month";
    public static final String ARG_DAY = "day";
    public final static String PREFS_SHOW_SUMMARY = "ShowSummary";

    ArrayList<Passage> listItems = new ArrayList<Passage>();
    PortionArrayAdapter adapter;
    private Boolean showSummary = true;
    private Calendar calendar = Calendar.getInstance();

    public ReadingsFragment() {
        presenter = Injector.getDayPresenter(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        calendar.set(args.getInt(ARG_YEAR), args.getInt(ARG_MONTH), args.getInt(ARG_DAY));
        adapter = new PortionArrayAdapter(getActivity(), listItems);
        ListView listViewMain = (ListView) view.findViewById(R.id.listView);
        listViewMain.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        showSummary = settings.getBoolean(PREFS_SHOW_SUMMARY, true);
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.readings_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.startPresenting();
        showReadings();
    }

    public void showReadings() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        listItems.clear();
        try {
            SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
            String[] params = { Integer.toString(calendar.get(Calendar.MONTH) + 1),
                    Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) };
            final String QUERY = "                            SELECT Coalesce(book.Name || ' ' || override, book.Name || ' ' || passage.chapter) AS passage, summary.summary_text, passage._id"
                    + "                            FROM plan"
                    + "                            LEFT JOIN passage ON passage._id = plan.passage_id"
                    + "                            LEFT JOIN book ON book._id = passage.book_id"
                    + "                            LEFT JOIN summary ON summary.book_id = book._id AND summary.chapter = passage.chapter"
                    + "                            WHERE month = ? and day = ?";
            Cursor cursor = sqliteDatabase
                    .rawQuery(QUERY, params);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listItems.add(new Passage(cursor.getString(0),
                        cursor.isNull(1) ? getString(R.string.sorry_no_summary_available_yet) : cursor.getString(1),
                        cursor.getInt(2)));
                cursor.moveToNext();
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        } finally {
            databaseHelper.close();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_readings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_summary:
            doToggleSummary();
            return true;

        default:
            return false;
        }
    }

    private void doToggleSummary() {
        showSummary = !showSummary;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFS_SHOW_SUMMARY, showSummary);
        editor.commit();
        showReadings();
    }
}