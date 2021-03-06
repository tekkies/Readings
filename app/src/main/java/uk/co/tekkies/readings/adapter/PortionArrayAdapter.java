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
import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.activity.ReadingsActivity;
import uk.co.tekkies.readings.day.DayFragment;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.model.Prefs;
import uk.co.tekkies.readings.util.Analytics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

    private ReadingsActivity readingsActivity;
    private ArrayList<Passage> passages;
    Prefs prefs;
    

    public PortionArrayAdapter(Activity activity, ArrayList<Passage> values) {
        super(activity, R.layout.listitem_portion, values);
        this.readingsActivity = (ReadingsActivity) activity;
        this.passages = values;
        prefs = new Prefs(activity);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) readingsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.listitem_portion, parent, false);
        TextView textViewPassageTitle = (TextView) view.findViewById(R.id.passage_title);
        TextView textViewSummary = (TextView) view.findViewById(R.id.textViewSummary);
        textViewPassageTitle.setText(passages.get(position).getTitle());
        view.setTag(passages.get(position).getTitle());
        textViewPassageTitle.setOnClickListener(this);
        view.findViewById(R.id.imageViewReadOffline).setOnClickListener(this);
        view.findViewById(R.id.imageViewReadOnline).setOnClickListener(this);
        textViewSummary.setOnClickListener(this);

        if (ReadingsApplication.getMp3Installed()) {
            View listenView = view.findViewById(R.id.imageListen);
            listenView.setVisibility(View.VISIBLE);
            listenView.setOnClickListener(this);
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(readingsActivity);
        Boolean showSummary = settings.getBoolean(Prefs.PREF_SHOW_SUMMARY, true);
        if (showSummary) {
            textViewSummary.setText(passages.get(position).getSummary());
        } else {
            textViewSummary.setVisibility(View.GONE);
        }
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.passage_title:
            Analytics.UIClick(readingsActivity, "passage_title");
            tryOpenIntegratedReader(((View) v.getParent().getParent()).getTag().toString());
            break;
        case R.id.textViewSummary:
            Analytics.UIClick(readingsActivity, "passage_summay");
            tryOpenIntegratedReader(((View) v.getParent()).getTag().toString());
            break;
        case R.id.imageViewReadOffline:
            Analytics.UIClick(readingsActivity, "passage_read_offline");
            tryOpenIntegratedReader(((View) v.getParent().getParent()).getTag().toString());
            break;
        case R.id.imageListen:
            Analytics.UIClick(readingsActivity, "passage_listen");
            openMp3(((View) v.getParent().getParent()).getTag().toString());
            break;
        case R.id.imageViewReadOnline:
            Analytics.UIClick(readingsActivity, "passage_read_online");
            openPositiveWord(((View) v.getParent().getParent()).getTag().toString());
            break;
        }
    }

    private void tryOpenIntegratedReader(String passage) {
        PackageInfo packageInfo = getOfflineKgvPackageInfo();
        if(packageInfo == null){
            askUserToInstallKjvPlugin();
        } else {
            if(packageInfo.versionCode < 103030000) {
                //openOfflineBible(passage);
                upgradeKjvBiblePlugin(passage);
            } else {
                openIntegratedReader(passage);
            }
        }
    }

    private void upgradeKjvBiblePlugin(String passage) {
        final String finalPassage = passage;
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(readingsActivity);
        dlgAlert.setMessage(readingsActivity.getString(R.string.please_upgrade_the_kjv_bible_plugin));
        dlgAlert.setTitle(readingsActivity.getString(R.string.upgrade_plugin));
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Download update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        installKjvPlugin();
                    }
                });
        dlgAlert.setNegativeButton("Use old version",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openOfflineBible(finalPassage);
                    }
                });
        dlgAlert.create().show();
    }

    private PackageInfo getOfflineKgvPackageInfo() {
        PackageInfo packageInfo=null;
        try {
            packageInfo = readingsActivity.getPackageManager().getPackageInfo("uk.co.tekkies.plugin.kjv", 0);
        } catch (NameNotFoundException e) {
            //swallow it
        }
        return packageInfo;
    }

    private void openOfflineBible(String passage) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.item/vnd.uk.co.tekkies.bible.passage");
        intent.putExtra("passage", passage);
        //Look for plugin in packages
        PackageManager pm = readingsActivity.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            // Intent can be serviced, try it.
            readingsActivity.startActivity(intent);
        } else {
            // install the off-line Bible
            Toast.makeText(readingsActivity, "The offline bible must be installed from Google Play.", Toast.LENGTH_LONG).show();
            installKjvPlugin();
        }

    }
    
    private void askUserToInstallKjvPlugin() {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(readingsActivity);
        dlgAlert.setMessage(R.string.install_kjv_bible_plugin);
        dlgAlert.setTitle(R.string.title_install);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        installKjvPlugin();
                    }
                });
        dlgAlert.create().show();
    }

    private boolean installKjvPlugin() {
        boolean installed = false;
        Uri marketUri = Uri.parse("market://details?id=uk.co.tekkies.plugin.kjv");
        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
        PackageManager pm = readingsActivity.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(marketIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0)
            readingsActivity.startActivity(marketIntent);
        else {
            Toast.makeText(readingsActivity, R.string.sorry_no_market_installed, Toast.LENGTH_LONG).show();
        }
        return installed;
    }

    private void openPositiveWord(String passage) {
        String url = "http://read.thepositiveword.com/index.php?ref=" + Uri.encode(passage);
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        webIntent.setData(uri);
        readingsActivity.startActivity(webIntent);
    }

    private void openIntegratedReader(String selectedPassage) {
        Intent intent = new Intent(readingsActivity, PassageActivity.class);
        ParcelableReadings passableReadings = new ParcelableReadings(passages, selectedPassage, readingsActivity.getSelectedDate());
        intent.putExtra(ParcelableReadings.PARCEL_NAME, passableReadings);
        readingsActivity.startActivity(intent);
    }

    private void openMp3(String passage) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.item/vnd.uk.co.tekkies.mp3bible.passage");
        intent.putExtra("passage", passage);
        //Look for plugin in packages
        PackageManager pm = readingsActivity.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            // Intent can be serviced, try it.
            readingsActivity.startActivity(intent);
        } else {
            // install the off-line Bible
            Toast.makeText(readingsActivity, "The MP3 plugin must be installed from Google Play.", Toast.LENGTH_LONG).show();
            installKjvPlugin();
        }
    }
}
