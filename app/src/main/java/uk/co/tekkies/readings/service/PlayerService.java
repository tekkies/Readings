package uk.co.tekkies.readings.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.model.ParcelableReadings;

public class PlayerService extends Service {
    private static final String INTENT_EXTRA_PASSAGE_ID = "passageId";
    private static final String INTENT_EXTRA_POSITION = "position";
    private static final String LOG_TAG = "PLAYSVC";
    private static final String SERVICE_NAME = "uk.co.tekkies.readings.service.PlayerService";
    private final Binder binder = new PlayerServiceBinder();
    ReadingsPlayer readingsPlayer;

    public static void requestPlay(PassageActivity passageActivity, int passageId, int positionAsThousandth) {
        Intent intent = new Intent(passageActivity, PlayerService.class);
        intent.putExtra(INTENT_EXTRA_PASSAGE_ID, passageId);
        intent.putExtra(INTENT_EXTRA_POSITION, positionAsThousandth);
        intent.putExtra(ParcelableReadings.PARCEL_NAME, passageActivity.getPassableReadings());
        passageActivity.startService(intent);
    }

    public static void requestStop(Context context) {
        Intent intent = new Intent(ReadingsPlayer.INTENT_STOP);
        context.sendBroadcast(intent);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            readingsPlayer = CreateReadingsPlayer(intent);
            int positionAsThousandth = intent.getExtras().getInt(INTENT_EXTRA_POSITION);
            readingsPlayer.doPlay(positionAsThousandth);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private ReadingsPlayer CreateReadingsPlayer(Intent intent) {
        ParcelableReadings parcelableReadings = intent.getParcelableExtra(ParcelableReadings.PARCEL_NAME);
        int passageId = intent.getExtras().getInt(INTENT_EXTRA_PASSAGE_ID);
        return new ReadingsPlayer(this, parcelableReadings, passageId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "Service stopped");
        readingsPlayer.destroy();
        readingsPlayer = null;
        super.onDestroy();
    }

    public interface IPlayerService {
        void registerActivity(Activity activity, IPlayerUi callback);
        void unregisterActivity(Activity activity);
        int getPassage();
        int getProgress();
        void setPosition(int progressAsThousandth);
    }

    public class PlayerServiceBinder extends Binder implements IPlayerService {
        public void registerActivity(Activity activity, IPlayerUi playerUi) {
            readingsPlayer.registerActivity(activity, playerUi);
        }
        public void unregisterActivity(Activity activity) {
            if(readingsPlayer != null)
                readingsPlayer.unregisterActivity(activity);
        }
        @Override
        public int getPassage() {
            return readingsPlayer.getPassageId();
        }

        @Override
        public int getProgress() {
            return readingsPlayer.getProgress();
        }
        
        @Override
        public void setPosition(int positionAsThousandth) {
            readingsPlayer.setPlayerPosition(positionAsThousandth);
        }
    }
}
