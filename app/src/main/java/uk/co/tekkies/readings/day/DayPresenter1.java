package uk.co.tekkies.readings.day;

import uk.co.tekkies.readings.Injector;

/**
 * Created by ajoiner on 03/11/2014.
 */
public class DayPresenter1 implements DayPresenter {
    DayView dayView;
    DayModel dayModel;
    public DayPresenter1(DayView dayView) {
        this.dayView = dayView;
        dayModel = Injector.getDayModel(this, dayView);
    }

    @Override
    public void startPresenting() {

    }
}
