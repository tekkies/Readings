package uk.co.tekkies.readings.day;

import uk.co.tekkies.readings.model.Passage;

public interface DayView {
    void clearList();

    void addItem(Passage passage);

    void notifyDataSetChanged();
}
