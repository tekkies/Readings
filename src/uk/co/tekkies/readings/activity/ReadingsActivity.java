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

package uk.co.tekkies.readings.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.ReadingsApplication;
import uk.co.tekkies.readings.fragment.DatePickerFragment;
import uk.co.tekkies.readings.fragment.ReadingsFragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Toast;

public class ReadingsActivity extends FragmentActivity implements OnDateSetListener, OnClickListener {

    private static final String VERSION_KEY = "version_number";
    static final String NEWS_TOAST_URL = "http://tekkies.co.uk/readings/api/news-toast/";
    private static final int CENTER_PAGE = 100;
    public static Calendar centerCalendar = null;
    SimpleDateFormat thisYearDateFormat;
    SimpleDateFormat anotherYearDateFormat;
    SimpleDateFormat dayDateFormat;
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    Boolean today = true;
    static ReadingsActivity readingsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readingsActivity = this;
        dayDateFormat = new SimpleDateFormat("E");
        thisYearDateFormat = new SimpleDateFormat("E d MMM");
        anotherYearDateFormat = new SimpleDateFormat("E d MMM yy");
        ReadingsApplication.checkForMP3(this);
        setContentView(R.layout.readings_activity);
        initialiseWhatsNew();
        setupPager();
        if (!loadInstanceState(savedInstanceState)) {
            setDate(Calendar.getInstance());
        }
        showNewsToast();
    }

    private static Handler newsToastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(readingsActivity, msg.obj.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private void showNewsToast() {
        final String versionName = getVersionName();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String summary = backgroundDownloadNewsToast(versionName);
                if(summary != null && summary != "") {
                    Message message = Message.obtain(newsToastHandler, 0, summary);
                    newsToastHandler.sendMessage(message);
                }
            }
        });
        thread.setName("Download news toast");
        thread.start();
    }

    private String getVersionName() {
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private String backgroundDownloadNewsToast(String versionName) {
        String summary = null;
        URL url;
        try {
            //Append version, so we can easily prompt users to upgrade, if necessary.
            url = new URL(NEWS_TOAST_URL + "?" + versionName);
            URLConnection connection = url.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            //Sanity check message:  e.g. We wouldn't want to toast html from a hotspot paywall
            if(line.equals("uk.co.tekkies.readings.news-toast")) {
                line = reader.readLine();
                summary = "";
                while (line != null) {
                    summary += line + "\n";
                    line = reader.readLine();
                }
            }
            reader.close();
        } catch (Exception e) {
            //Ignore any problems.  It's no big deal if the toast doesn't show.
            e.printStackTrace();
        }
        return summary;
    }

    private void setDate(Calendar calendar) {
        Log.v("DATE", "setDate(" + anotherYearDateFormat.format(new Date(calendar.getTimeInMillis())));
        centerCalendar = calendar;
        today = isToday(calendar);
        viewPager.setAdapter(pagerAdapter); // force refresh
        viewPager.setCurrentItem(CENTER_PAGE);
    }

    private Boolean loadInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Calendar calendar = Calendar.getInstance();
            if (savedInstanceState.getBoolean("today")) {
                setDate(calendar);
            } else {
                calendar.set(savedInstanceState.getInt("year"), savedInstanceState.getInt("month"),
                        savedInstanceState.getInt("dayOfMonth"));
                setDate(calendar);
            }
        }
        return savedInstanceState != null;
    }

    private void setupPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(CENTER_PAGE);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            ReadingsFragment fragment = new ReadingsFragment();
            Bundle args = new Bundle();
            Calendar fragmentCalendar = getCalendar(i);
            args.putInt(ReadingsFragment.ARG_YEAR, fragmentCalendar.get(Calendar.YEAR));
            args.putInt(ReadingsFragment.ARG_MONTH, fragmentCalendar.get(Calendar.MONTH));
            args.putInt(ReadingsFragment.ARG_DAY, fragmentCalendar.get(Calendar.DAY_OF_MONTH));
            fragment.setArguments(args);
            return fragment;
        }

        public Calendar getCalendar(int i) {
            Calendar fragmentCalendar = (Calendar) centerCalendar.clone();
            fragmentCalendar.add(Calendar.DATE, i - CENTER_PAGE);
            return fragmentCalendar;
        }

        @Override
        public int getCount() {
            return 200;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar pageCalendar = getCalendar(position);
            int pageDay = pageCalendar.get(Calendar.YEAR) * 1000 + pageCalendar.get(Calendar.DAY_OF_YEAR);
            Calendar nowCalendar = Calendar.getInstance();
            int nowDay = nowCalendar.get(Calendar.YEAR) * 1000 + nowCalendar.get(Calendar.DAY_OF_YEAR);
            String title = "";
            switch (pageDay - nowDay) {
            case -1:
                title += getResources().getString(R.string.yesterday);
                break;
            case 0:
                title += getResources().getString(R.string.today);
                break;
            case 1:
                title += getResources().getString(R.string.tomorrow);
                break;
            default: {
                Date date = new Date(pageCalendar.getTimeInMillis());
                if (pageCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)) {
                    title += thisYearDateFormat.format(date);
                } else {
                    title += anotherYearDateFormat.format(date);
                }
            }
            }
            return title;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            Calendar calendarPage = getCalendar(viewPager.getCurrentItem());
            today = isToday(calendarPage);
        }

    }

    private void initialiseWhatsNew() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int currentVersionNumber = 0;
        int savedVersionNumber = sharedPref.getInt(VERSION_KEY, 0);
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception e) {
        }
        if (currentVersionNumber > savedVersionNumber) {
            showWhatsNewDialog();
            Editor editor = sharedPref.edit();
            editor.putInt(VERSION_KEY, currentVersionNumber);
            editor.commit();
        }
    }

    private void showWhatsNewDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_whatsnew, null);
        Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setTitle(getString(R.string.whats_new))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_date:
            doPickDate();
            return true;
        case R.id.menu_settings:
            doShowSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void doShowSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void doFeedback() {
        String url = "http://goo.gl/pSraf";
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        webIntent.setData(uri);
        startActivity(webIntent);
    }

    private void doFacebook() {
        String url = "http://goo.gl/sE2oy";
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        webIntent.setData(uri);
        startActivity(webIntent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        setDate(calendar);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.imageViewFeedback:
            doFeedback();
            break;
        case R.id.imageViewFacebook:
            doFacebook();
            break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        Calendar calendar = pagerAdapter.getCalendar(viewPager.getCurrentItem());
        outState.putInt("year", calendar.get(Calendar.YEAR));
        outState.putInt("month", calendar.get(Calendar.MONTH));
        outState.putInt("dayOfMonth", calendar.get(Calendar.DAY_OF_MONTH));
        outState.putBoolean("today", today);
    }

    private void doPickDate() {
        DialogFragment newFragment = new DatePickerFragment(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onResume() {
        if (today) {
            // If was showing today, but current page is not today
            Calendar calendar = pagerAdapter.getCalendar(viewPager.getCurrentItem());
            if (!isToday(calendar)) {
                setDate(Calendar.getInstance());
            }
        }
        super.onResume();
    }

    Boolean isToday(Calendar calendar) {
        Calendar calendarNow = Calendar.getInstance();
        return (calendar.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR))
                && (calendar.get(Calendar.DAY_OF_YEAR) == calendarNow.get(Calendar.DAY_OF_YEAR));
    }

}