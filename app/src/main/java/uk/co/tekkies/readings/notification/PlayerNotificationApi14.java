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

public class PlayerNotificationApi14 implements IPlayerNotification {

    @Override
    public void show() {
        showOngoingNotification();
    }

    private void showOngoingNotification() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context).addParentStack(PassageActivity.class);
        String title = getNotificationTitle(getPassageId());
        String content = getPassageTitles();
        taskStackBuilder.addNextIntent(new Intent(context, PassageActivity.class).putExtra(ParcelableReadings.PARCEL_NAME,
                parcelableReadings));
        notificationBuilder = new NotificationCompat.Builder(context).setTicker(getPassageTitle(getPassageId()))
                .setSmallIcon(R.drawable.ic_action_av_play_holo_dark)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle(title).setContentText(content).setAutoCancel(true)
                .setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT));
        notification = notificationBuilder.build();
        startForeground((int) Notification.FLAG_FOREGROUND_SERVICE, notification);
    }

}
