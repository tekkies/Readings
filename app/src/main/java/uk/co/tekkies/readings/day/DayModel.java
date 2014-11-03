package uk.co.tekkies.readings.day;

import java.util.Calendar;
import java.util.List;

public interface DayModel {

    public void load(DayPresenter presenter, DayView dayView, Calendar calendar);
}
