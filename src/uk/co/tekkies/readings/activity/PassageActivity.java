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
import uk.co.tekkies.readings.service.PlayerService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

public class PassageActivity extends BaseActivity implements PlayerService.IPlayerServiceListenerInterface {

    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    private ParcelableReadings passableReadings;
    private PlayerService.IServiceInterface serviceInterface = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
        super.onCreate(savedInstanceState);
        ReadingsApplication.checkForMP3(this);
        setContentView(R.layout.passage_activity);
        passableReadings = (ParcelableReadings) (getIntent().getParcelableExtra(ParcelableReadings.PARCEL_NAME));
        if(getPassableReadings() != null) {
            setupPager();
            gotoPage(getPassableReadings().selected);
        }
    }

    private void gotoPage(String selected) {
        for (int page = 0; page < getPassableReadings().passages.size(); page++) {
            if (getPassableReadings().passages.get(page).getTitle().equalsIgnoreCase(selected)) {
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
            args.putString("passage", getPassableReadings().passages.get(i).getTitle());
            args.putInt("passageId", getPassableReadings().passages.get(i).getPassageId());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return getPassableReadings().passages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPassableReadings().passages.get(position).getTitle();
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

        case R.id.menu_brightness:
        case R.id.menu_brightness_overflow:
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

    public ParcelableReadings getPassableReadings() {
        return passableReadings;
    }

    public void bindService() {
        if(PlayerService.isServiceRunning(this)) {
            bindService(new Intent(this, PlayerService.class), serviceConnection, Activity.BIND_AUTO_CREATE);
        }
    }
    
    public PassageActivity getPassageActivity() {
        return this;
    }
    
    // The service connection to talk to the service
    private ServiceConnection serviceConnection = new ServiceConnection() {
          public void onServiceConnected(ComponentName className, IBinder binder) {
              serviceInterface = (PlayerService.IServiceInterface) binder;

              try {
                  serviceInterface.registerActivity(PassageActivity.this, getPassageActivity());
              } catch (Throwable t) {
              }
          }

          public void onServiceDisconnected(ComponentName className) {
              serviceInterface = null;
          }
      };

                                      
protected void onDestroy() {
    super.onDestroy();
    serviceInterface.unregisterActivity(this);
    unbindService(serviceConnection);
};                                      
                         

@Override
public void setPassageId(int passageId) {
    Toast.makeText(this, "PassageID="+passageId, Toast.LENGTH_SHORT).show();
}


}