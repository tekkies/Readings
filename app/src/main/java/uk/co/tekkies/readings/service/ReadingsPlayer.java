package uk.co.tekkies.readings.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.notification.IPlayerNotification;
import uk.co.tekkies.readings.notification.PlayerNotificationApi14;
import uk.co.tekkies.readings.util.Analytics;

public class ReadingsPlayer implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {
    public static final String INTENT_STOP = "stop";
    private static final String LOG_TAG = "PLAYER";

    public void setPassageId(int passageId) {
        this.passageId = passageId;
    }

    public void registerActivity(Activity activity, IPlayerUi playerUi) {
        clients.put(activity, playerUi);
    }

    public void unregisterActivity(Activity activity) {
        clients.remove(activity);
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



    private final Context context;
    private final ParcelableReadings parcelableReadings;
    private IPlayerNotification playerNotification;
    PlayerBroadcastReceiver playerBroadcastReceiver;
    MediaPlayer mediaPlayer;
    private int passageId = 0;
    Boolean beep = false;
    private Map<Activity, IPlayerUi> clients = new ConcurrentHashMap<Activity, IPlayerUi>();



    public ReadingsPlayer(Context context, ParcelableReadings parcelableReadings, int passageId) {
        this.context = context;
        this.parcelableReadings = parcelableReadings;
        this.setPassageId(passageId);
        playerNotification = new PlayerNotificationApi14(context, this);
        playerNotification.show();
    }

    public void destroy() {
        if (playerBroadcastReceiver != null) {
            context.unregisterReceiver(playerBroadcastReceiver);
            playerBroadcastReceiver = null;
        }
    }


    public int getPassageId() {
        return passageId;
    }

    void doPlay(int positionAsThousandth) {
        registerPlayerBroadcastReceiver();
        Log.i(LOG_TAG, "Play:" + getPassageId() + "(" + positionAsThousandth + ")");
        if(getAudioFocus()) {
            beep = false;
            String filePath = Mp3ContentLocator.getPassageFullPath(context, getPassageId());
            File file = new File(filePath);
            if(file.exists()) {
                mediaPlayer = MediaPlayer.create(context, Uri.parse(filePath));
                mediaPlayer.setOnCompletionListener(this);
                setPlayerPosition(positionAsThousandth);
                mediaPlayer.start();
                playerNotification.update(getPassageId());
                for (Activity client : clients.keySet()) {
                    clients.get(client).onPassageChange(getPassageId());
                }
            } else {
                Toast.makeText(this, getString(R.string.mp3_not_found_goto_settings), Toast.LENGTH_LONG).show();
            }
        }
    }

    void doStop() {
        Log.i(LOG_TAG, "doStop");
        abandonAudioFocus();
        for (Activity client : clients.keySet()) {
            clients.get(client).onEndAll();
        }
        setPassageId(0);
        notification = null;
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        stopSelf();
    }

    void doPause() {
        Log.i(LOG_TAG, "doPause");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    void doResume() {
        Log.i(LOG_TAG, "doResume");
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void onAudioFocusChange(int focusChange) {
        Log.i(LOG_TAG, "onAudioFocusChange="+focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                doResume();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                doStop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                doPause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                doPause();
                //mediaPlayer.setVolume(1.0f, 1.0f);
                break;
        }
    }



    private boolean getAudioFocus() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return (audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private void abandonAudioFocus() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
    }


    public ParcelableReadings getParcelableReadings() {
        return parcelableReadings;
    }


    private void registerPlayerBroadcastReceiver() {
        playerBroadcastReceiver = new PlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_STOP);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        context.registerReceiver(playerBroadcastReceiver, intentFilter);
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
        for (int i = 0; i < parcelableReadings.passages.size(); i++) {
            if (getPassageId() == parcelableReadings.passages.get(i).getPassageId()) {
                i++;
                if (i < parcelableReadings.passages.size()) {
                    // Play next
                    setPassageId(parcelableReadings.passages.get(i).getPassageId());
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
                Analytics.reportCaughtException(context, e);
            }
        }
        return progress;
    }

    void setPlayerPosition(int positionAsThousandth) {
        mediaPlayer.seekTo((mediaPlayer.getDuration() *  positionAsThousandth) / 1000);
    }


}
