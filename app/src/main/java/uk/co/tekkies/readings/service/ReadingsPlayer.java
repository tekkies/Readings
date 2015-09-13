package uk.co.tekkies.readings.service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;

public class ReadingsPlayer implements AudioManager.OnAudioFocusChangeListener {
    private final ParcelableReadings parcelableReadings;
    private final int passageId;

    public ReadingsPlayer(ParcelableReadings parcelableReadings, int passageId) {

        this.parcelableReadings = parcelableReadings;
        this.passageId = passageId;
        showOngoingNotification();
    }


    private void showOngoingNotification() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this).addParentStack(PassageActivity.class);
        String title = getNotificationTitle(getPassageId());
        String content = getPassageTitles();
        taskStackBuilder.addNextIntent(new Intent(this, PassageActivity.class).putExtra(ParcelableReadings.PARCEL_NAME,
                passableReadings));
        notificationBuilder = new NotificationCompat.Builder(this).setTicker(getPassageTitle(getPassageId()))
                .setSmallIcon(R.drawable.ic_action_av_play_holo_dark)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle(title).setContentText(content).setAutoCancel(true)
                .setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT));
        notification = notificationBuilder.build();
        startForeground((int) Notification.FLAG_FOREGROUND_SERVICE, notification);
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

    protected String getPassageTitle(int passageId) {
        String passageName = "Unknown";
        for(Passage passage: parcelableReadings.passages) {
            if(passage.getPassageId() == passageId) {
                passageName = passage.getTitle();
            }
        }
        return passageName;
    }


    private String getNotificationTitle(int passageId) {
        return getString(R.string.app_name)+":"+getPassageTitle(passageId);
    }

    private String getPassageTitles() {
        String passageTitles="";
        for(Passage passage: parcelableReadings.passages) {
            if(passageTitles.length() > 0){
                passageTitles += ", ";
            }
            passageTitles += passage.getTitle();
        }
        return passageTitles;
    }


}
