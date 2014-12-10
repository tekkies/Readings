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

import java.util.WeakHashMap;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.ReadingsApplication;
import uk.co.tekkies.readings.fragment.PassageFragment;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.service.PlayerService;
import uk.co.tekkies.readings.util.Analytics;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

public class PassageActivity extends BaseActivity implements PlayerService.IClientInterface {

    private static final String TAG_BIND = "BIND";
    public static final String ARG_SELECTED_DATE = "selectedDate";
	public static final String ARG_PASSAGE = "passage";
	public static final String ARG_PASSAGE_ID = "passageId";

    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    private ParcelableReadings passableReadings;
    private PlayerService.IServiceInterface serviceInterface = null;
    private AsyncTask<String, Integer, Long> progressUpdateTask;
    private boolean serviceAvailable = false;
    private PlayerServiceConnection serviceConnection; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setChosenTheme();
        super.onCreate(savedInstanceState);
        ReadingsApplication.checkForMP3Plugin(this);
        serviceConnection = new PlayerServiceConnection();
        setContentView(R.layout.passage_activity);
        passableReadings = (ParcelableReadings) (getIntent().getParcelableExtra(ParcelableReadings.PARCEL_NAME));
        if(getPassableReadings() != null) {
            setupPager();
            gotoPage(getPassableReadings().selected);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_passage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
    }
    
    public class PagerAdapter extends FragmentStatePagerAdapter {
		
    	WeakHashMap<Integer, Fragment> fragments=new WeakHashMap<Integer, Fragment>();

		public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
            
        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new PassageFragment();
            fragments.put(i, fragment);
            Bundle args = new Bundle();
            args.putString(ARG_PASSAGE, getPassableReadings().passages.get(i).getTitle());
            args.putInt(ARG_PASSAGE_ID, getPassableReadings().passages.get(i).getPassageId());
            args.putLong(ARG_SELECTED_DATE, getPassableReadings().selectedDate.getTimeInMillis()); 
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
        
        public Fragment getFragment(int position) {
            return fragments.get(position);
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

    public void bindPlayerService() {
        if(PlayerService.isServiceRunning(this)) {
            Log.i(TAG_BIND, "bindPlayerService");
            bindService(new Intent(this, PlayerService.class), getServiceConnection(), Activity.BIND_AUTO_CREATE);
        }
    }
    
    public PassageActivity getPassageActivity() {
        return this;
    }
    
    @Override
    public void onPassageChange(int passageId) {
        //Toast.makeText(this, "PassageID="+passageId, Toast.LENGTH_SHORT).show();
    }

    private int getPage(int passageId) {
        int page=0;
        for(int passageIndex=0;passageIndex<passableReadings.passages.size();passageIndex++) {
            if(passableReadings.passages.get(passageIndex).getPassageId() == passageId) {
                page = passageIndex;
                break;
            }
        }
        return page;
    }
    
    public PlayerService.IServiceInterface getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(PlayerService.IServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

   
    public class PlayerServiceConnection implements ServiceConnection
    {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.i(TAG_BIND, "onServiceConnected");
            setServiceInterface((PlayerService.IServiceInterface) binder);
            try {
                getServiceInterface().registerActivity(PassageActivity.this, getPassageActivity());
                serviceAvailable = true;
                progressUpdateTask = new ProgressUpdateTask().execute("");
                viewPager.setCurrentItem(getPage(getServiceInterface().getPassage()));
                //Toast.makeText(getPassageActivity(), "PassageID="+getServiceInterface().getPassage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Analytics.reportCaughtException(getPassageActivity(), e);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG_BIND, "onServiceDisconnected");
            serviceAvailable = false;
            setServiceInterface(null);
        }
    };

    public void unbindPlayerService() {
        Log.i(TAG_BIND, "unbindPlayerService");
        if(serviceInterface != null) {
            serviceInterface.unregisterActivity(this);
        }
        if(serviceAvailable) {
            serviceAvailable = false;
            unbindService(getServiceConnection());
        }
    }

    @Override
    public void onEndAll() {
        unbindPlayerService();
        PassageFragment passageFragment = getCurrentPageFragment();
        if(passageFragment != null) {
            passageFragment.setPlayPauseIcon();
        }
    }

    private PassageFragment getCurrentPageFragment() {
        return (PassageFragment) pagerAdapter.getFragment(viewPager.getCurrentItem());
    }
    
    public int getCurrentPassageId() {
        return passableReadings.passages.get(viewPager.getCurrentItem()).getPassageId();
    }
    
    public boolean isServiceAvailable() {
        return serviceAvailable;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void setServiceConnection(PlayerServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }
    
    private class ProgressUpdateTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... unused) {
            while(isServiceAvailable()) {
                try {
                    publishProgress(getServiceInterface().getProgress());
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    //swallow it
                }
            }
            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {
            try {
                PassageFragment passageFragment = getCurrentPageFragment();
                if(passageFragment != null) {
                    if(progress[0] >= 0) {
                        passageFragment.setProgress(progress[0]);
                    }
                }
            } catch (Exception e) {
                Analytics.reportCaughtException(getPassageActivity(), e);
            }
        }

        protected void onPostExecute(Long result) {
        }
    }

    @Override
    public void onPassageEnding(int passageId) {
        //Advance viewPager when beep starts
        int nextPage = getPage(passageId)+1;
        if(nextPage < passableReadings.passages.size()) {
            viewPager.setCurrentItem(nextPage);
        }
    }
    
    
    
    @Override
    protected void onPause() {
        super.onPause();
        if(progressUpdateTask != null) {
            progressUpdateTask.cancel(true);
        }
        unbindPlayerService();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        bindPlayerService();
    }
}
