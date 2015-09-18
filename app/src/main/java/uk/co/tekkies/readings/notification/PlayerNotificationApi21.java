package uk.co.tekkies.readings.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.session.MediaSession;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.service.PlayerService;
import uk.co.tekkies.readings.service.ReadingsPlayer;

public class PlayerNotificationApi21 implements IPlayerNotification {
    private static final String LOG_TAG = "NOTIFY";
    private final Context context;
    private final ReadingsPlayer readingsPlayer;
    private final MediaSession mediaSession;
    private Notification notification;
    private Notification.Builder notificationBuilder;

    public PlayerNotificationApi21(Context context, ReadingsPlayer readingsPlayer, MediaSession mediaSession) {
        this.context = context;
        this.readingsPlayer = readingsPlayer;
        this.mediaSession = mediaSession;
    }

    @Override
    public void show() {
        Log.i(LOG_TAG,"show()");
        showOngoingNotification();
    }

    @Override
    public void update(int passageId) {
        Log.i(LOG_TAG,"update("+passageId+")");
        updateOngoingNotification(getNotificationTitle(passageId));
    }

    @Override
    public void destroy() {
        Log.i(LOG_TAG,"destroy()");
        notification = null;
    }

    private void showOngoingNotification() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context).addParentStack(PassageActivity.class);
        String title = getNotificationTitle(readingsPlayer.getPassageId());
        String content = getPassageTitles();
//        taskStackBuilder.addNextIntent(new Intent(context, PassageActivity.class).putExtra(ParcelableReadings.PARCEL_NAME,
//                readingsPlayer.getParcelableReadings()));
//        notificationBuilder = new NotificationCompat.Builder(context).setTicker(getPassageTitle(readingsPlayer.getPassageId()))
//                .setSmallIcon(R.drawable.ic_action_av_play_holo_dark)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
//                .setContentTitle(title).setContentText(content).setAutoCancel(true)
//                .setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT));
//        notification = notificationBuilder.build();


        notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Track title")
                .setContentText("Artist - Album")
                //.setLargeIcon(R.drawable.ic_launcher)
                .setStyle(new Notification.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken()));

        notification = notificationBuilder.build();

        ((PlayerService) context).startForeground((int) Notification.FLAG_FOREGROUND_SERVICE, notification);
    }

    private void updateOngoingNotification(String contentTitle) {
        notificationBuilder.setContentTitle(contentTitle).setTicker(getPassageTitle(readingsPlayer.getPassageId()));
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                (int) Notification.FLAG_FOREGROUND_SERVICE, notificationBuilder.build());
    }

    private String getNotificationTitle(int passageId) {
        return context.getString(R.string.app_name) + ":" + getPassageTitle(passageId);
    }

    private String getPassageTitles() {
        String passageTitles = "";
        for (Passage passage : readingsPlayer.getParcelableReadings().passages) {
            if (passageTitles.length() > 0) {
                passageTitles += ", ";
            }
            passageTitles += passage.getTitle();
        }
        return passageTitles;
    }

    protected String getPassageTitle(int passageId) {
        String passageName = "Unknown";
        for (Passage passage : readingsPlayer.getParcelableReadings().passages) {
            if (passage.getPassageId() == passageId) {
                passageName = passage.getTitle();
            }
        }
        return passageName;
    }
}