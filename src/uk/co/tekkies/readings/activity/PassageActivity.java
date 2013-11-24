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

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.ReadingsApplication;
import uk.co.tekkies.readings.fragment.PassageFragment;
import uk.co.tekkies.readings.model.ParcelableReadings;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class PassageActivity extends FragmentActivity implements OnClickListener {

    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    ParcelableReadings passableReadings;
    SharedPreferences sharedPreferences;
    Boolean nightMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
        super.onCreate(savedInstanceState);
        ReadingsApplication.checkForMP3(this);
        getActionBar().setIcon(R.drawable.ic_action_listen);
        setContentView(R.layout.passage_activity);
        passableReadings = (ParcelableReadings) (getIntent().getParcelableExtra(ParcelableReadings.PARCEL_NAME));
        setupPager();
    }

    private void setChosenTheme() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sharedPreferences.getBoolean(getString(R.string.pref_key_night_mode), false);
        setTheme(nightMode ? R.style.Night : R.style.Day);
    }

    private void setupPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment fragment = new PassageFragment();
            Bundle args = new Bundle();
            args.putString("passage", passableReadings.passages.get(i).getTitle());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return passableReadings.passages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return passableReadings.passages.get(position).getTitle();
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_passage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // case R.id.menu_feedback:
        // doFeedback();
        // return true;
        case R.id.menu_brightness:
            doDayNightToggle();
            return true;
        case R.id.menu_date:
            // doPickDate();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void doDayNightToggle() {
        nightMode = !nightMode;
        sharedPreferences.edit().putBoolean(getString(R.string.pref_key_night_mode), nightMode).commit();
        recreate();
    }

    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}