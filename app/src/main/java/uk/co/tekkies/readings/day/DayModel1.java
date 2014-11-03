package uk.co.tekkies.readings.day;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.List;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.util.DatabaseHelper;

public class DayModel1 implements DayModel {

    @Override
    public void load(DayPresenter presenter, DayView dayView, Calendar calendar) {
        Context context = ((android.support.v4.app.Fragment)dayView).getActivity();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

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
                presenter.addItem(new Passage(cursor.getString(0),
                        cursor.isNull(1) ? context.getString(R.string.sorry_no_summary_available_yet) : cursor.getString(1),
                        cursor.getInt(2)));
                cursor.moveToNext();
            }
            cursor.close();
            presenter.notifyDataSetChanged();
        } finally {
            databaseHelper.close();
        }

    }

}
