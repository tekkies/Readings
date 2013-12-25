package uk.co.tekkies.readings.service;

import java.util.List;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class PlayerService extends Service implements OnCompletionListener {

    private static final String INTENT_EXTRA_PASSAGE_ID = "passageId";
    private static final String LOG_TAG = "PLAYER";
    private static final String SERVICE_NAME = "uk.co.tekkies.readings.service.PlayerService";
    private static final String INTENT_STOP = "stop";
    PlayerBroadcastReceiver playerBroadcastReceiver;
    MediaPlayer mediaPlayer;
    private ParcelableReadings passableReadings;
    Notification notification;
    NotificationCompat.Builder notificationBuilder;
    int passageId=0;
    Boolean beep = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerPlayerBroadcastReceiver();
        passableReadings = (ParcelableReadings) (intent.getParcelableExtra(ParcelableReadings.PARCEL_NAME));
        createOngoingNotification();
        int passageId = intent.getExtras().getInt(INTENT_EXTRA_PASSAGE_ID);
        doPlay(passageId);
        return super.onStartCommand(intent, flags, startId);
    }

    private void createOngoingNotification() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this)
                .addParentStack(PassageActivity.class); //Read parents from manifest
        taskStackBuilder.addNextIntent(
                new Intent(this, PassageActivity.class).putExtra(ParcelableReadings.PARCEL_NAME, passableReadings));
        notificationBuilder = new NotificationCompat.Builder(
            this)
            .setTicker("Ticker text")
            .setSmallIcon(R.drawable.ic_action_hardware_headphones_holo_dark)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setContentTitle("Content Title")
            .setContentText("ContentText")
            .setAutoCancel(true)
            .setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT));
        notification = notificationBuilder.build();
        startForeground((int) Notification.FLAG_FOREGROUND_SERVICE, notification);
    }

    private void registerPlayerBroadcastReceiver() {
        playerBroadcastReceiver = new PlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_STOP);
        registerReceiver(playerBroadcastReceiver, intentFilter);
    }

    public static Boolean isServiceRunning(Context context) {
        Boolean serviceRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().equals(SERVICE_NAME)) {
                serviceRunning = true;
                break;
            }
        }
        return serviceRunning;
    }

    public static void requestPlay(PassageActivity passageActivity, int passageId) {
        Intent intent = new Intent(PlayerService.SERVICE_NAME);
        intent.putExtra(INTENT_EXTRA_PASSAGE_ID, passageId);
        intent.putExtra(ParcelableReadings.PARCEL_NAME, passageActivity.getPassableReadings());
        passageActivity.startService(intent);
    }

    public static void requestStop(Context context) {
        Intent intent = new Intent(INTENT_STOP);
        context.sendBroadcast(intent);
    }

    class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(INTENT_STOP)) {
                doStop();
            }
        }
    }

    private void doStop() {
        Log.i(LOG_TAG, "Stop");
        passageId = 0;
        notification = null;
        mediaPlayer.stop();
        mediaPlayer.release();
        stopSelf();
    }

    private void doPlay(int passageId) {
        Log.i(LOG_TAG, "Play:"+passageId);
        this.passageId= passageId;
        beep = false;
        String filePath = Mp3ContentLocator.getPassageFullPath(this, passageId);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
        updateOngoingNotification("Passage:"+passageId);
    }

    private void updateOngoingNotification(String contentText) {
        notificationBuilder.setContentText(contentText);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify((int) Notification.FLAG_FOREGROUND_SERVICE, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        
        Log.i(LOG_TAG, "Service stopped");
        if (playerBroadcastReceiver != null) {
            unregisterReceiver(playerBroadcastReceiver);
            playerBroadcastReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(beep == false) {
            doBeep();
        } else {
            beep = false;
            advanceOrExit();
        }
    }

    private void advanceOrExit() {
        for(int i=0;i< passableReadings.passages.size(); i++) {
            if(passageId == passableReadings.passages.get(i).getPassageId()) {
                i++;
                if(i < passableReadings.passages.size()) {
                    //Play next
                    doPlay(passableReadings.passages.get(i).getPassageId());
                }else {
                    //End
                    doStop();
                }
                break;
            }
        }
    }

    private void doBeep() {
        beep = true;
        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }

}
