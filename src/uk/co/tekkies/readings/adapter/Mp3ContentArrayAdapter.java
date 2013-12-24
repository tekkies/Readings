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

package uk.co.tekkies.readings.adapter;

import java.util.ArrayList;
import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.ContentLocationActivity;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Mp3ContentArrayAdapter extends ArrayAdapter<Mp3ContentLocator> implements OnClickListener {

    private ContentLocationActivity activity;
    private ArrayList<Mp3ContentLocator> mp3ContentLocators;
    float defaultTextSize=0;
    Prefs prefs;
    
    public Mp3ContentArrayAdapter(Activity activity, ArrayList<Mp3ContentLocator> values) {
        super(activity, R.layout.listitem_portion, values);
        this.activity = (ContentLocationActivity) activity;
        this.mp3ContentLocators = values;
        prefs = new Prefs(activity);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.listitem_mp3_content, parent, false);
        TextView textViewPassageTitle = (TextView) view.findViewById(R.id.passage_title);
        TextView textViewSummary = (TextView) view.findViewById(R.id.textViewSummary);
        if(defaultTextSize == 0) {
            defaultTextSize = textViewSummary.getTextSize();
        }
        textViewSummary.setTextSize((float) (prefs.loadPassageTextSize() * defaultTextSize));
        textViewPassageTitle.setText(mp3ContentLocators.get(position).getTitle());
        view.setTag(mp3ContentLocators.get(position).getClass().getName());
        
        //textViewPassageTitle.setOnClickListener(this);
        //textViewSummary.setOnClickListener(this);
        
        if (mp3ContentLocators.get(position).getBasePath().length() != 0) {
            View listenView = view.findViewById(R.id.imageListen);
            listenView.setVisibility(View.VISIBLE);
            listenView.setOnClickListener(this);
        }
        textViewSummary.setText(mp3ContentLocators.get(position).getBasePath());
        return view;
    }

    public void onClick(View v) {
//        switch (v.getId()) {
//        case R.id.passage_title:
//            tryOpenIntegratedReader(((View) v.getParent().getParent()).getTag().toString());
//            break;
//        case R.id.textViewSummary:
//            tryOpenIntegratedReader(((View) v.getParent()).getTag().toString());
//            break;
//        case R.id.imageViewReadOffline:
//            tryOpenIntegratedReader(((View) v.getParent().getParent()).getTag().toString());
//            break;
//        case R.id.imageListen:
//            openMp3(((View) v.getParent().getParent()).getTag().toString());
//            break;
//        case R.id.imageViewReadOnline:
//            openPositiveWord(((View) v.getParent().getParent()).getTag().toString());
//            // openIntegratedReader(((View)v.getParent().getParent()).getTag().toString());
//            break;
//        }
    }
    
}
