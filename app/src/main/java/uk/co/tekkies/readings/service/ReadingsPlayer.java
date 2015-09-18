package uk.co.tekkies.readings.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.content.Mp3ContentLocator;
import uk.co.tekkies.readings.notification.IPlayerNotification;
import uk.co.tekkies.readings.notification.PlayerNotificationApi14;
import uk.co.tekkies.readings.notification.PlayerNotificationApi21;
import uk.co.tekkies.readings.util.Analytics;

public class ReadingsPlayer implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {
    public static final String INTENT_STOP = "stop";
    public static final String INTENT_RESUME = "resume";
    public static final String INTENT_PAUSE = "pause";
    private static final String LOG_TAG = "PLAYER";
    private static final String SESSION_TAG = "uk.co.tekkies.mediaSessionTag";
    private final PlayerService playerService;
    private final ParcelableReadings parcelableReadings;
    PlayerBroadcastReceiver playerBroadcastReceiver;
    MediaPlayer mediaPlayer;
    MediaSession mediaSession;
    Boolean beep = false;
    private IPlayerNotification playerNotification;
    private int passageId = 0;
    private Map<Activity, IPlayerUi> clients = new ConcurrentHashMap<Activity, IPlayerUi>();

    public ReadingsPlayer(PlayerService playerService, ParcelableReadings parcelableReadings, int passageId) {
        Log.i(LOG_TAG, "ReadingsPlayer.ReadingsPlayer(...)");
        this.playerService = playerService;
        this.parcelableReadings = parcelableReadings;
        this.setPassageId(passageId);
        createMediaSession(playerService, parcelableReadings);
        playerNotification = new PlayerNotificationApi21(playerService, this, mediaSession);
        playerNotification.show();
        registerPlayerBroadcastReceiver();
    }

    private void createMediaSession(Context context, ParcelableReadings parcelableReadings) {

        PlaybackState.Builder stateBuilder = new PlaybackState.Builder();
        stateBuilder.setActions(PlaybackState.ACTION_PLAY_PAUSE);
        stateBuilder.setState(PlaybackState.STATE_PLAYING, 0, 1.0f);


        mediaSession = new MediaSession(context, SESSION_TAG);
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setPlaybackState(stateBuilder.build());
        MediaSession.Callback callback = new MediaSession.Callback() {

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                doPause();
                return super.onMediaButtonEvent(mediaButtonIntent);
            }

            @Override
            public void onPause() {
                super.onPause();
                doPause();
            }
        };
        mediaSession.setCallback(callback);
        mediaSession.setActive(true);
    }

    public void registerActivity(Activity activity, IPlayerUi playerUi) {
        Log.i(LOG_TAG, "registerActivity:" + activity);
        clients.put(activity, playerUi);
    }

    public void unregisterActivity(Activity activity) {
        Log.i(LOG_TAG, "unregisterActivity:"+activity);
        clients.remove(activity);
    }

    public void destroy() {
        Log.i(LOG_TAG, "ReadingsPlayer.destroy()");
        if (playerBroadcastReceiver != null) {
            playerService.unregisterReceiver(playerBroadcastReceiver);
            playerBroadcastReceiver = null;
        }
    }

    public int getPassageId() {
        return passageId;
    }

    public void setPassageId(int passageId) {
        this.passageId = passageId;
    }

    void doPlay(int positionAsThousandth) {
        Log.i(LOG_TAG, "Play:" + getPassageId() + "(" + positionAsThousandth + ")");
        if (getAudioFocus()) {
            beep = false;
            if(createPlayerIfRequired(positionAsThousandth)) {
                doResume();
            }
            checkAudioFocus();
        }
    }

    private void checkAudioFocus() {
        if(mediaPlayer == null || !mediaPlayer.isPlaying()) {
            abandonAudioFocus();
        }
    }

    private boolean createPlayerIfRequired(int positionAsThousandth) {
        boolean success = true;
        if(mediaPlayer == null) {
            success = createMediaPlayer(positionAsThousandth);
        }
        return success;
    }

    private boolean createMediaPlayer(int positionAsThousandth) {
        String filePath = Mp3ContentLocator.getPassageFullPath(playerService, getPassageId());
        File file = new File(filePath);
        boolean success = file.exists();
        if (success) {
            mediaPlayer = MediaPlayer.create(playerService, Uri.parse(filePath));
            mediaPlayer.setOnCompletionListener(this);
            setPlayerPosition(positionAsThousandth);
            mediaPlayer.start();
            playerNotification.update(getPassageId());
            for (Activity client : clients.keySet()) {
                clients.get(client).onPassageChange(getPassageId());
            }
        } else {
            Toast.makeText(playerService, playerService.getString(R.string.mp3_not_found_goto_settings), Toast.LENGTH_LONG).show();
        }
        return success;
    }

    void doStop() {
        Log.i(LOG_TAG, "doStop");
        abandonAudioFocus();
        for (Activity client : clients.keySet()) {
            clients.get(client).onEndAll();
        }
        setPassageId(0);
        destroyNotification();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        playerService.stopSelf();
    }

    private void destroyNotification() {
        Log.i(LOG_TAG, "destroyNotification");
        if (playerNotification != null) {
            playerNotification.destroy();
            playerNotification = null;
        }
    }

    void doPause() {
        Log.i(LOG_TAG, "doPause");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    void doResume() {
        Log.i(LOG_TAG, "doResume");
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void onAudioFocusChange(int focusChange) {
        Log.i(LOG_TAG, "onAudioFocusChange=" + focusChange);
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
        AudioManager audioManager = (AudioManager) playerService.getSystemService(Context.AUDIO_SERVICE);
        return (audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private void abandonAudioFocus() {
        AudioManager audioManager = (AudioManager) playerService.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
    }

    public ParcelableReadings getParcelableReadings() {
        return parcelableReadings;
    }

    private void registerPlayerBroadcastReceiver() {
        playerBroadcastReceiver = new PlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_STOP);
        intentFilter.addAction(INTENT_PAUSE);
        intentFilter.addAction(INTENT_RESUME);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        playerService.registerReceiver(playerBroadcastReceiver, intentFilter);
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
        int passageIndex = getPassageIndex(getPassageId());
        passageIndex++;
        if (passageIndex < parcelableReadings.passages.size()) {
            // Play next
            mediaPlayer.release();
            mediaPlayer = null;
            setPassageId(parcelableReadings.passages.get(passageIndex).getPassageId());
            doPlay(0);
        } else {
            doStop();
        }
    }

    private int getPassageIndex(int passageId) {
        int passageIndex = Integer.MAX_VALUE;
        for (int i = 0; i < parcelableReadings.passages.size(); i++) {
            if (passageId == parcelableReadings.passages.get(i).getPassageId()) {
                passageIndex = i;
                break;
            }
        }
        return passageIndex;
    }

    private void doBeep() {
        beep = true;
        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(playerService, R.raw.beep);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }

    public int getProgress() {
        int progress = 0;
        if (beep) {
            progress = 0;
        } else {
            try {
                //Split calc to determine source of exceptions
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                progress = (currentPosition * 1000) / duration;
            } catch (Exception e) {
                Analytics.reportCaughtException(playerService, e);
            }
        }
        return progress;
    }

    void setPlayerPosition(int positionAsThousandth) {
        mediaPlayer.seekTo((mediaPlayer.getDuration() * positionAsThousandth) / 1000);
    }

    public boolean isPlaying() {
        return (mediaPlayer != null) && mediaPlayer.isPlaying();
    }

    class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(INTENT_STOP)) {
                doStop();
            }
            if (action.equals(INTENT_PAUSE)) {
                doPause();
            }
            if (action.equals(INTENT_RESUME)) {
                //Todo: Deal with resuming in new passage and new position
                doResume();
            } else if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                doStop();
            }
        }
    }
}
