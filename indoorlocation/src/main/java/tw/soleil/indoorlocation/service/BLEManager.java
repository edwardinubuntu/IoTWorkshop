package tw.soleil.indoorlocation.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import tw.soleil.indoorlocation.R;
import tw.soleil.indoorlocation.activity.MainActivity;
import tw.soleil.indoorlocation.object.ScanRecord;

/**
 * Created by edward_chiang on 6/23/16.
 */
public class BLEManager {

    private static final int NOTIFICATION_ID = 123;

    private static NotificationManager currentNotificationManager;

    private static  Context currentContext;

    private ScanRecord scanRecord;

    public void create(NotificationManager notificationManager,
                       Context context, final Intent i, ScanRecord scanRecord) {
        currentContext = context;
        currentNotificationManager = notificationManager;
        this.scanRecord = scanRecord;

        postNotification(i);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void postNotification(Intent intent) {

        final Intent notifyIntent = new Intent(currentContext, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(currentContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN){

            String message = "Hello! This UUID is " + this.scanRecord.getUUID() + ", major = " + this.scanRecord.getMajor() + " , minor = " + this.scanRecord.getMinor();

            Notification notification = new Notification.Builder(currentContext)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(this.scanRecord.getDeviceName())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setContentIntent(pendingIntent).build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_LIGHTS;
            currentNotificationManager.notify(scanRecord.getMinor(), notification);
        }


    }
}
