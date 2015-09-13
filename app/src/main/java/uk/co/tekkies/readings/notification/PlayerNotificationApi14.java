package uk.co.tekkies.readings.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.activity.PassageActivity;
import uk.co.tekkies.readings.model.ParcelableReadings;
import uk.co.tekkies.readings.model.Passage;
import uk.co.tekkies.readings.service.ReadingsPlayer;

public class PlayerNotificationApi14 implements IPlayerNotification {

    private final Context context;
    private final ReadingsPlayer readingsPlayer;
    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;

    public PlayerNotificationApi14(Context context, ReadingsPlayer readingsPlayer) {
        this.context = context;
        this.readingsPlayer = readingsPlayer;
    }

    @Override
    public void show() {
        showOngoingNotification();
    }

    private void showOngoingNotification() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context).addParentStack(PassageActivity.class);
        String title = getNotificationTitle(readingsPlayer.getPassageId());
        String content = getPassageTitles();
        taskStackBuilder.addNextIntent(new Intent(context, PassageActivity.class).putExtra(ParcelableReadings.PARCEL_NAME,
                readingsPlayer.getParcelableReadings()));
        notificationBuilder = new NotificationCompat.Builder(context).setTicker(getPassageTitle(readingsPlayer.getPassageId()))
                .setSmallIcon(R.drawable.ic_action_av_play_holo_dark)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle(title).setContentText(content).setAutoCancel(true)
                .setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT));
        notification = notificationBuilder.build();
        startForeground((int) Notification.FLAG_FOREGROUND_SERVICE, notification);
    }

    private String getNotificationTitle(int passageId) {
        return context.getString(R.string.app_name)+":"+getPassageTitle(passageId);
    }

    private String getPassageTitles() {
        String passageTitles="";
        for(Passage passage: readingsPlayer.getParcelableReadings().passages) {
            if(passageTitles.length() > 0){
                passageTitles += ", ";
            }
            passageTitles += passage.getTitle();
        }
        return passageTitles;
    }
    protected String getPassageTitle(int passageId) {
        String passageName = "Unknown";
        for(Passage passage: readingsPlayer.getParcelableReadings().passages) {
            if(passage.getPassageId() == passageId) {
                passageName = passage.getTitle();
            }
        }
        return passageName;
    }



}
