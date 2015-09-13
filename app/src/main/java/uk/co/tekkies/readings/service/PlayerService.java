package uk.co.tekkies.readings.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.util.Analytics;
import android.app.Activity;
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
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class PlayerService extends Service implements OnCompletionListener {
    private static final String INTENT_EXTRA_PASSAGE_ID = "passageId";
    private static final String INTENT_EXTRA_POSITION = "position";
    private static final String LOG_TAG = "PLAYSVC";
    private static final String SERVICE_NAME = "uk.co.tekkies.readings.service.PlayerService";
    private static final String INTENT_STOP = "stop";
    ReadingsPlayer readingsPlayer;
    PlayerBroadcastReceiver playerBroadcastReceiver;
    MediaPlayer mediaPlayer;
    Notification notification;
    NotificationCompat.Builder notificationBuilder;
    private int passageId = 0;
    Boolean beep = false;
    private final Binder binder = new PlayerServiceBinder();
    private Map<Activity, IPlayerUi> clients = new ConcurrentHashMap<Activity, IPlayerUi>();

    public int getPassageId() {
        return passageId;
    }

    public void setPassageId(int passageId) {
        this.passageId = passageId;
    }

    public interface IPlayerUi {
        void onPassageChange(int passageId);
        void onEndAll();
        void onPassageEnding(int passageId);
    }

    public interface IPlayerService {
        void registerActivity(Activity activity, IPlayerUi callback);
        void unregisterActivity(Activity activity);
        int getPassage();
        int getProgress();
        void setPosition(int progressAsThousandth);
    }

    public static void requestPlay(PassageActivity passageActivity, int passageId, int positionAsThousandth) {
        Intent intent = new Intent(PlayerService.SERVICE_NAME);
        intent.putExtra(INTENT_EXTRA_PASSAGE_ID, passageId);
        intent.putExtra(INTENT_EXTRA_POSITION, positionAsThousandth);
        intent.putExtra(ParcelableReadings.PARCEL_NAME, passageActivity.getPassableReadings());
        passageActivity.startService(intent);
    }

    public static void requestStop(Context context) {
        Intent intent = new Intent(INTENT_STOP);
        context.sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        readingsPlayer = CreateReadingsPlayer(intent);
        int positionAsThousandth = intent.getExtras().getInt(INTENT_EXTRA_POSITION);
        readingsPlayer.doPlay(positionAsThousandth);
        return super.onStartCommand(intent, flags, startId);
    }

    private ReadingsPlayer CreateReadingsPlayer(Intent intent) {
        ParcelableReadings parcelableReadings = intent.getParcelableExtra(ParcelableReadings.PARCEL_NAME);
        int passageId = intent.getExtras().getInt(INTENT_EXTRA_PASSAGE_ID);
        return new ReadingsPlayer(this, parcelableReadings, passageId);
    }

    class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(INTENT_STOP)) {
                doStop();
            } else if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                doStop();
            }
        }
    }

    private void doStop() {
        readingsPlayer.doStop();
        readingsPlayer = null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    private void registerPlayerBroadcastReceiver() {
        playerBroadcastReceiver = new PlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_STOP);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
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




    private void updateOngoingNotification(String contentTitle) {
        notificationBuilder.setContentTitle(contentTitle).setTicker(getPassageTitle(getPassageId()));
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                (int) Notification.FLAG_FOREGROUND_SERVICE, notificationBuilder.build());
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
        if (beep == false) {
            for (Activity client : clients.keySet()) {
                clients.get(client).onPassageEnding(getPassageId());
            }
            doBeep();
        } else {
            beep = false;
            advanceOrExit();
        }
    }

    private void advanceOrExit() {
        for (int i = 0; i < passableReadings.passages.size(); i++) {
            if (getPassageId() == passableReadings.passages.get(i).getPassageId()) {
                i++;
                if (i < passableReadings.passages.size()) {
                    // Play next
                    setPassageId(passableReadings.passages.get(i).getPassageId());
                    doPlay(0);
                } else {
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

    public class PlayerServiceBinder extends Binder implements IPlayerService {
        public void registerActivity(Activity activity, IPlayerUi playerUi) {
            clients.put(activity, playerUi);
        }
        public void unregisterActivity(Activity activity) {
            clients.remove(activity);
        }
        @Override
        public int getPassage() {
            return getPassageId();
        }

        @Override
        public int getProgress() {
            int progress=0;
            if(beep) {
                progress = 0;
            } else {
                try {
                    //Split calc to determine source of exceptions
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    progress = (currentPosition * 1000) / duration;
                } catch (Exception e) {
                    Analytics.reportCaughtException(getPlayerService(), e);
                }
            }
            return progress;
        }
        
        @Override
        public void setPosition(int positionAsThousandth) {
            setPlayerPosition(positionAsThousandth);
        }
    }
    
    private void setPlayerPosition(int positionAsThousandth) {
        mediaPlayer.seekTo((mediaPlayer.getDuration() *  positionAsThousandth) / 1000);
    }
    
    public Context getPlayerService() {
        return this;
    }




}
