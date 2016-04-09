package uk.co.tekkies.readings.service;

public interface IPlayerUi {
    void onPassageChange(int passageId);
    void onEndAll();
    void onPassageEnding(int passageId);
}
