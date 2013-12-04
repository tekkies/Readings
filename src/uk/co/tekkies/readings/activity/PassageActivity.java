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
import android.app.ActionBar.OnMenuVisibilityListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;

public class PassageActivity extends BaseActivity implements OnCancelListener, OnMenuVisibilityListener {

    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    ParcelableReadings passableReadings;
    Dialog dimmerOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passage_activity);
        createDimmerOverlay();
        dimmerOverlay.show();
        passableReadings = (ParcelableReadings) (getIntent().getParcelableExtra(ParcelableReadings.PARCEL_NAME));
        setupPager();
        gotoPage(passableReadings.selected);
    }

    private void createDimmerOverlay() {
        dimmerOverlay = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dimmerOverlay.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dimmerOverlay.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 255, 0)));
        dimmerOverlay.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dimmerOverlay.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        dimmerOverlay.setOnCancelListener(this);
        dimmerOverlay.setCancelable(true);
    }
    
    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }


    private void gotoPage(String selected) {
        for (int page = 0; page < passableReadings.passages.size(); page++) {
            if (passableReadings.passages.get(page).getTitle().equalsIgnoreCase(selected)) {
                viewPager.setCurrentItem(page);
                break;
            }
        }
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
        getActionBar().addOnMenuVisibilityListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.menu_brightness:
            doDayNightToggle();
            return true;

        case R.id.menu_about_content:
            showAboutContentDialog();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutContentDialog() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(getContentNotices());
        dlgAlert.setTitle(R.string.about_content);
        dlgAlert.setPositiveButton(R.string.ok, null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    protected String getContentNotices() {
        String notices = getString(R.string.unknown);
        String[] row = new String[] { "version" };
        Cursor cursor = getContentResolver().query(Uri.parse("content://uk.co.tekkies.plugin.bible.kjv/about"), row,
                "", row, "");
        if (cursor.moveToFirst()) {
            notices = cursor.getString(cursor.getColumnIndex("about"));
        }
        return notices;
    }

    public void requestViewPagerDisallowInterceptTouchEvent(boolean disallowIntercept) {
        viewPager.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public void onMenuVisibilityChanged(boolean isVisible) {
      if(dimmerOverlay != null) {
          if(isVisible) {
              if(dimmerOverlay.isShowing()) {
                  dimmerOverlay.dismiss();
              }
          } else {
              if(!dimmerOverlay.isShowing()) {
                  dimmerOverlay.show();
              }
          }
      }
        
    }
    
    @Override
    protected void onPause() {
        if(dimmerOverlay != null)
        {
            if(dimmerOverlay.isShowing()) {
                dimmerOverlay.dismiss();
            }
        }
        super.onPause();
    }
 
}