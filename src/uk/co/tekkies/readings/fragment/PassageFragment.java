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

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.ReadingsApplication;
import uk.co.tekkies.readings.activity.ContentLocationActivity;
import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.service.PlayerService;
import uk.co.tekkies.readings.util.Analytics;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PassageFragment extends Fragment implements OnSharedPreferenceChangeListener, OnClickListener, OnSeekBarChangeListener {

    TextView textView;
    ScaleGestureDetector scaleGestureDetector;
    float defaultTextSize;
    double textSize;
    String passage = "Unknown";
    int passageId = 0;
    Prefs prefs;
    ImageView playPauseButton;
    SeekBar seekBar; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Prefs(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.passage_fragment, container, false);
        textView = (TextView) (mainView.findViewById(R.id.textView1));
        defaultTextSize = textView.getTextSize();
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(
                getString(R.string.pref_key_night_mode), false)) {
            textView.setTextColor(Color.GRAY);
        }
        Prefs prefs = new Prefs(getActivity());
        //Legacy MP3 plugin installed but MP3 product not yet defined
        //OR MP3 product defined
        if((ReadingsApplication.getMp3Installed() && prefs.isMp3ProductUndefined())
                || prefs.loadMp3Product().length() > 0) {
            setupMediaControls(mainView);
        }
        loadTextSize();
        registerGestureDetector(mainView);
        return mainView;
    }

    private void setupMediaControls(View mainView) {
        mainView.findViewById(R.id.layoutMediaControls).setVisibility(View.VISIBLE);
        playPauseButton = (ImageView) mainView.findViewById(R.id.button_play_pause);
        setPlayPauseIcon();
        playPauseButton.setOnClickListener(this);
        seekBar = (SeekBar) mainView.findViewById(R.id.seekBar1);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(1000);
    }

    public void setPlayPauseIcon() {
        if(playPauseButton != null) {
            playPauseButton.setImageResource(resolveThemeAttribute(((PassageActivity)getActivity()).isServiceAvailable() ? R.attr.ic_action_av_pause : R.attr.ic_action_av_play));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        passage = args.getString("passage");
        passageId = args.getInt("passageId");
        String html = render(getPassageXml(passage));
        textView.setText(Html.fromHtml(html));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        prefs.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        prefs.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_readings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_larger_text:
            textSize *= 1.25;
            saveTextSize();
            Toast.makeText(getActivity(), R.string.you_can_also_pinch_to_zoom, Toast.LENGTH_SHORT).show();
            return true;
        case R.id.menu_smaller_text:
            textSize *= 0.8;
            saveTextSize();
            Toast.makeText(getActivity(), R.string.you_can_also_pinch_to_zoom, Toast.LENGTH_SHORT).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void registerGestureDetector(View mainView) {
        scaleGestureDetector = new ScaleGestureDetector(getActivity(), new TextViewOnScaleGestureListener());
        ScrollView rootView = (ScrollView) mainView.findViewById(R.id.scrollView1);
        rootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return scaleGestureDetector.isInProgress();
            }
        });
    }

    protected String getPassageXml(String passage) {
        String passageXml = "Error";
        String[] row = new String[] { "passage" };
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.parse("content://uk.co.tekkies.plugin.bible.kjv/passage/" + passage), row, "", row, "");
        if (cursor.moveToFirst()) {
            passageXml = cursor.getString(cursor.getColumnIndex("passage"));
        }
        return passageXml;
    }

    protected String render(String html) {
        // html = html.replace("<br/>","&nbsp;");
        html = html.replace("<summary>", "<i><font color=\"blue\">");
        html = html.replace("</summary>", "</font></i>");
        html = html.replace("<v>", "<sup><b>");
        html = html.replace("</v>", "</sup></b>");
        // html = html.replace("<v>", "<sup><font color=\"#000088\">");
        // html = html.replace("</v>", "</font></sup>");
        return html;
    }

    private void loadTextSize() {
        textSize = prefs.loadPassageTextSize();
        textView.setTextSize((float) (textSize * defaultTextSize));
    }

    private void saveTextSize() {
        prefs.savePassageTextSize(textSize);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Prefs.PREF_PASSAGE_TEXT_SIZE)) {
            loadTextSize();
        }
    }

    public class TextViewOnScaleGestureListener extends SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            textSize *= detector.getScaleFactor();
            textView.setTextSize((float) (textSize * defaultTextSize));
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            ((PassageActivity) getActivity()).requestViewPagerDisallowInterceptTouchEvent(true);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            saveTextSize();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button_play_pause:
            doPlayPauseSearch();
            break;
        }
    }

    private void doPlayPauseSearch() {
        Prefs prefs = new Prefs(getActivity());
        if(prefs.isMp3ProductUndefined()){
            prefs.saveMp3Product(""); //Only open settings once
            doSearch();
        } else {
            if (!PlayerService.isServiceRunning(getActivity())) {
                doPlay();
            } else {
                doPause();
            }
        }
    }
    
    
    private void doPause() {
        Analytics.UIClick(getActivity(), "Player-Pause");
        int playingPassageId = ((PassageActivity)getActivity()).getServiceInterface().getPassage();
        int displayedPassageId = ((PassageActivity)getActivity()).getCurrentPassageId();
        ((PassageActivity)getActivity()).unbindPlayerService();
        PlayerService.requestStop(getActivity());
        playPauseButton.setImageResource(resolveThemeAttribute(R.attr.ic_action_av_play));
        if(playingPassageId != displayedPassageId) {
            seekBar.setProgress(0);  
        }
    }

    private int resolveThemeAttribute(int attributeId) {
        TypedValue outValue = new TypedValue(); 
        getActivity().getTheme().resolveAttribute(attributeId, outValue, true);
        return outValue.resourceId;
    }

    private void doPlay() {
        Analytics.UIClick(getActivity(), "Player-Play");
        PassageActivity activity = (PassageActivity)getActivity();
        PlayerService.requestPlay((PassageActivity)getActivity(), passageId, seekBar.getProgress());
        playPauseButton.setImageResource(resolveThemeAttribute(R.attr.ic_action_av_pause));
        activity.bindPlayerService();
    }

    private void doSearch() {
        Intent intent = new Intent(getActivity(), ContentLocationActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Analytics.UIClick(getActivity(), "Player-Seek");
        PassageActivity passageActivity = (PassageActivity)getActivity();
        if(passageActivity.isServiceAvailable()) {
            ((PassageActivity)getActivity()).getServiceInterface().setPosition(seekBar.getProgress());
        }
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            setPlayPauseIcon();
        }
    }
}