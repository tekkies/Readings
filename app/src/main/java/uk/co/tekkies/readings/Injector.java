package uk.co.tekkies.readings;

import uk.co.tekkies.readings.day.DayModel;
import uk.co.tekkies.readings.day.DayModel1;
import uk.co.tekkies.readings.day.DayPresenter;
import uk.co.tekkies.readings.day.DayPresenter1;
import uk.co.tekkies.readings.day.DayView;
import uk.co.tekkies.readings.day.DayFragment;

public class Injector {
    public static DayPresenter getDayPresenter(DayFragment dayFragment) {
        return new DayPresenter1(dayFragment);
    }

    public static DayModel getDayModel(DayPresenter1 dayPresenter1, DayView dayView) {
        return new DayModel1();
    }
}
