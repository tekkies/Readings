package uk.co.tekkies.readings.day;

import java.util.Calendar;

import uk.co.tekkies.readings.Injector;
import uk.co.tekkies.readings.model.Passage;

/**
 * Created by ajoiner on 03/11/2014.
 */
public class DayPresenter1 implements DayPresenter {
    DayView view;
    DayModel model;
    private Calendar calendar = Calendar.getInstance();

    public DayPresenter1(DayView dayView) {
        this.view = dayView;
        model = Injector.getDayModel(this, dayView);
    }

    @Override
    public void setCalendar(int year, int month, int day) {
        calendar.set(year, month, day);
    }

    @Override
    public void addItem(Passage passage) {
        view.addItem(passage);
    }

    @Override
    public void notifyDataSetChanged() {
        view.notifyDataSetChanged();

    }

    @Override
    public void reLoad() {
        view.clearList();
        model.load(this, view, calendar);
    }



}
