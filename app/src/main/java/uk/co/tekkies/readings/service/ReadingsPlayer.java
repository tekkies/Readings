package uk.co.tekkies.readings.service;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.notification.IPlayerNotification;
import uk.co.tekkies.readings.notification.PlayerNotificationApi14;

public class ReadingsPlayer implements AudioManager.OnAudioFocusChangeListener {
    private static final String LOG_TAG = "PLAYER";

    private final Context context;
    private final ParcelableReadings parcelableReadings;
    private final int passageId;
    private IPlayerNotification playerNotification;

    public ReadingsPlayer(Context context, ParcelableReadings parcelableReadings, int passageId) {
        this.context = context;
        this.parcelableReadings = parcelableReadings;
        this.passageId = passageId;
        playerNotification = new PlayerNotificationApi14(context, this);
        playerNotification.show();
    }



    public int getPassageId() {
        return passageId;
    }

    void doPlay(int positionAsThousandth) {
        registerPlayerBroadcastReceiver();
        Log.i(LOG_TAG, "Play:" + getPassageId() + "(" + positionAsThousandth + ")");
        if(getAudioFocus()) {
            beep = false;
            String filePath = Mp3ContentLocator.getPassageFullPath(this, getPassageId());
            File file = new File(filePath);
            if(file.exists()) {
                mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
                mediaPlayer.setOnCompletionListener(this);
                setPlayerPosition(positionAsThousandth);
                mediaPlayer.start();
                updateOngoingNotification(getNotificationTitle(getPassageId()));
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
}
