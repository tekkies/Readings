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
import java.util.List;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.ReadingsApplication;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.fragment.ReadingsFragment;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.workinprogress.PassageActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PortionArrayAdapter extends ArrayAdapter<Passage> implements OnClickListener {

    private ReadingsActivity activity;
    private ArrayList<Passage> passages;

    public PortionArrayAdapter(Activity activity, ArrayList<Passage> values) {
        super(activity, R.layout.listitem_portion, values);
        this.activity = (ReadingsActivity) activity;
        this.passages = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.listitem_portion, parent, false);
        TextView textViewPassageTitle = (TextView) view.findViewById(R.id.passage);
        TextView textViewSummary = (TextView) view.findViewById(R.id.textViewSummary);
        textViewPassageTitle.setText(passages.get(position).getTitle());
        view.setTag(passages.get(position).getTitle());

        textViewPassageTitle.setOnClickListener(this);
        view.findViewById(R.id.imageViewReadOffline).setOnClickListener(this);
        view.findViewById(R.id.imageViewReadOnline).setOnClickListener(this);
        textViewSummary.setOnClickListener(this);

        if (ReadingsApplication.mp3Installed) {
            View listenView = view.findViewById(R.id.imageListen);
            listenView.setVisibility(View.VISIBLE);
            listenView.setOnClickListener(this);
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
        Boolean showSummary = settings.getBoolean(ReadingsFragment.PREFS_SHOW_SUMMARY, true);
        if (showSummary) {
            textViewSummary.setText(passages.get(position).getSummary());
        } else {
            textViewSummary.setVisibility(View.GONE);
        }
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {

        case R.id.imageViewReadOffline:
        case R.id.passage:
            openOfflineBible(((View) v.getParent().getParent()).getTag().toString());
            break;
        case R.id.imageListen:
            openMp3(((View) v.getParent().getParent()).getTag().toString());
            break;
        case R.id.imageViewReadOnline:
            openPositiveWord(((View) v.getParent().getParent()).getTag().toString());
            // openIntegratedReader(((View)v.getParent().getParent()).getTag().toString());
            break;
        }
    }

    private void openOfflineBible(String passage) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.item/vnd.uk.co.tekkies.bible.passage");
        intent.putExtra("passage", passage);
        //Look for plugin in packages
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            // Intent can be serviced, try it.
            activity.startActivity(intent);
        } else {
            // install the off-line Bible
            Toast.makeText(activity, "The offline bible must be installed from Google Play.", Toast.LENGTH_LONG).show();
            installBibleIfRequired();
        }

    }

    private boolean installBibleIfRequired() {
        boolean installed = false;
        Uri marketUri = Uri.parse("market://details?id=uk.co.tekkies.plugin.kjv");
        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
        //Look for plugin in packages
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(marketIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0)
            activity.startActivity(marketIntent);
        else {
            Toast.makeText(activity, "Sorry the market is not installed", Toast.LENGTH_LONG).show();
        }
        return installed;
    }

    private void openPositiveWord(String passage) {
        String url = "http://read.thepositiveword.com/index.php?ref=" + Uri.encode(passage);
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        webIntent.setData(uri);
        activity.startActivity(webIntent);
    }

    private void openIntegratedReader(String selectedPassage) {
        Intent intent = new Intent(activity, PassageActivity.class);
        ParcelableReadings passableReadings = new ParcelableReadings(passages, selectedPassage);
        intent.putExtra(ParcelableReadings.PARCEL_NAME, passableReadings);
        activity.startActivity(intent);
    }

    private void openMp3(String passage) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.item/vnd.uk.co.tekkies.mp3bible.passage");
        intent.putExtra("passage", passage);
        //Look for plugin in packages
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            // Intent can be serviced, try it.
            activity.startActivity(intent);
        } else {
            // install the off-line Bible
            Toast.makeText(activity, "The offline bible must be installed from Google Play.", Toast.LENGTH_LONG).show();
            installBibleIfRequired();
        }
    }
}
