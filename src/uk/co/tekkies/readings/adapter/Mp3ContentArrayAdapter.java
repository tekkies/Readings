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
import android.widget.RadioButton;
import android.widget.TextView;

public class Mp3ContentArrayAdapter extends ArrayAdapter<Mp3ContentLocator> implements OnClickListener {

    private ContentLocationActivity activity;
    private ArrayList<Mp3ContentLocator> mp3ContentLocators;
    private Prefs prefs;
    private String selected="";
    
    public Mp3ContentArrayAdapter(Activity activity, ArrayList<Mp3ContentLocator> values) {
        super(activity, R.layout.listitem_portion, values);
        this.activity = (ContentLocationActivity) activity;
        this.mp3ContentLocators = values;
        prefs = new Prefs(activity);
        selected = prefs.loadMp3Product();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	//No recycling yet - still a very small list.
        ViewGroup view = (ViewGroup) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listitem_mp3_content, parent, false);
        Boolean contentFound = mp3ContentLocators.get(position).getBasePath().length() != 0;
        //RadioButton
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.radioButton1);
        radioButton.setChecked(mp3ContentLocators.get(position).getClass().getName().equals(selected));
        radioButton.setTag(mp3ContentLocators.get(position).getClass().getName());
        radioButton.setOnClickListener(this);
        radioButton.setVisibility(contentFound ? View.VISIBLE : View.GONE);
        //Listen
        View listenView = view.findViewById(R.id.imageListen);
        listenView.setOnClickListener(this);
        listenView.setVisibility(contentFound ? View.VISIBLE : View.GONE);
        //Text
        ((TextView)view.findViewById(R.id.passage_title)).setText(mp3ContentLocators.get(position).getTitle());
        setPathText(position, view);
        return view;
    }

	private void setPathText(int position, ViewGroup view) {
		String basePathText=mp3ContentLocators.get(position).getBasePath();
        if(basePathText == "") {
        	basePathText = getContext().getString(R.string.not_found);
        } else {
        	basePathText = getContext().getString(R.string.found)+basePathText; 
        }
        ((TextView) view.findViewById(R.id.textViewSummary)).setText(basePathText);
	}

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.radioButton1:
        	doRadioButtonClick(v);
        break;
        }
    }

	private void doRadioButtonClick(View v) {
		selected = v.getTag().toString();
		prefs.saveMp3Product(selected);
		this.notifyDataSetChanged();
	}
}
