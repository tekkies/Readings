package uk.co.tekkies.readings.day;

import uk.co.tekkies.readings.model.Passage;

public interface DayPresenter {
    void reLoad();

    void setCalendar(int year, int month, int day);
    
    void addItem(Passage passage);

    void notifyDataSetChanged();
}
