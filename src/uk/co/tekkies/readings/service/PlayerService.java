package uk.co.tekkies.readings.service;

import java.util.List;

import uk.co.tekkies.readings.R;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class PlayerService extends Service {

    private static final String LOG_TAG = "PLAYER";
    private static final String SERVICE_NAME = "uk.co.tekkies.readings.service.PlayerService";
    private static final String INTENT_STOP = "stop";
    PlayerBroadcastReceiver playerBroadcastReceiver;
    MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerPlayerBroadcastReceiver();
        createOngoingNotification();
        doPlay();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createOngoingNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent returnIntent = getPackageManager().getLaunchIntentForPackage("uk.co.tekkies.readings");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, returnIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification ongoingNotification = new Notification(R.drawable.ic_launcher, "Readings Player", 10000);
        ongoingNotification.setLatestEventInfo(this, "ReaingsPlayer", "Sarted player", pendingIntent);
        startForeground((int) Notification.FLAG_FOREGROUND_SERVICE, ongoingNotification);
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

    public static void requestPlay(Context context) {
        context.startService(new Intent(PlayerService.SERVICE_NAME));
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
        mediaPlayer.stop();
        mediaPlayer.release();
        stopSelf();
    }

    private void doPlay() {
        Log.i(LOG_TAG, "Play");
        mediaPlayer = MediaPlayer.create(this, Uri
                .parse("file:///storage/extSdCard/Podcasts/NLT Tree 97bD lame -B 48 -h -v -a/1 OT/01 Gen/Gen001.mp3"));
        mediaPlayer.start();
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

}
