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
import uk.co.tekkies.readings.activity.PassageActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class PassageFragment extends Fragment implements OnSharedPreferenceChangeListener {

    private static final String PREF_PASSAGE_TEXT_SIZE = "passageTextSize";
    TextView textView;
    ScaleGestureDetector scaleGestureDetector;
    float defaultTextSize;
    double textSize;
    String passage="Unknown";
   
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        passage = args.getString("passage");
        String html = render(getPassageXml(passage));
        textView.setText(Html.fromHtml(html));
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadTextSize() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        textSize= sharedPreferences.getFloat(PREF_PASSAGE_TEXT_SIZE, 1);
        textView.setTextSize((float) (textSize * defaultTextSize));
    }

    private void saveTextSize() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(PREF_PASSAGE_TEXT_SIZE, (float) textSize);
        editor.commit();
    }
        
    protected String render(String html) {
        html = html.replace("<summary>", "<i><font color=\"blue\">");
        html = html.replace("</summary>", "</font></i>");
        html = html.replace("<v>", "<sup><font color=\"red\">");
        html = html.replace("</v>", "</font></sup>");
        return html;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.passage_fragment, container, false);
        textView = (TextView) (mainView.findViewById(R.id.textView1));
        defaultTextSize = textView.getTextSize();
        loadTextSize();
        
        scaleGestureDetector = new ScaleGestureDetector(getActivity(), new TextViewOnScaleGestureListener());
        ScrollView rootView = (ScrollView)mainView.findViewById(R.id.scrollView1);
        rootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return scaleGestureDetector.isInProgress();
            }
        });
        return mainView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_readings, menu);
    }

    protected String getPassageXml(String passage) {
        String passageXml = "Error";
        String[] row = new String[] { "passage" };
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.parse("content://uk.co.tekkies.plugin.bible.kjv/passage/" + passage), 
                row, 
                "", 
                row,
                "");
        if (cursor.moveToFirst()) {
            passageXml = cursor.getString(cursor.getColumnIndex("passage"));
        }
        return passageXml;
    }
  
    public class TextViewOnScaleGestureListener extends
    SimpleOnScaleGestureListener {
        
 
   @Override
   public boolean onScale(ScaleGestureDetector detector) {
       textSize *= detector.getScaleFactor();
       textView.setTextSize((float) (textSize * defaultTextSize));
    return true;
   }

   @Override
   public boolean onScaleBegin(ScaleGestureDetector detector) {
       ((PassageActivity)getActivity()).requestViewPagerDisallowInterceptTouchEvent(true);
       return true;
   }

   @Override
   public void onScaleEnd(ScaleGestureDetector detector) {
       saveTextSize();
   }

  }
 
 @Override
public void onResume() {
     Log.v("FRAG","OnResume:"+passage);
     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
     sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    super.onResume();
}

 @Override
public void onPause() {
    Log.v("FRAG","OnPause:"+passage);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    super.onPause();
}

@Override
public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if(key.equals(PREF_PASSAGE_TEXT_SIZE)){
        loadTextSize();
    }
}
    
}