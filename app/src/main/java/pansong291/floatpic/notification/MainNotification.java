package pansong291.floatpic.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import pansong291.floatpic.R;
import pansong291.floatpic.service.MainService;

public class MainNotification {
  private static final int MYNOTIFICATION_ID = 274041;
  private static final String CHANNEL_ID = "paso-floatpic-notification-channel";
  private static final String CHANNEL_NAME = "Float Picture Control Panel";
  private static NotificationManager mNotifyManager;
  private static Notification mNotification;
  private static boolean isStartedNotification = false;

  private MainNotification() {}

  public static boolean isStartedNotification() {
    return isStartedNotification;
  }

  public static void startNotification(Service sv) {
    //懒汉式
    if (mNotification == null)
      synchronized (MainNotification.class) {
        if (mNotification == null) {
          Intent it = new Intent(sv, MainService.class);
          it.putExtra(MainService.START_FROM_NOTIFICATION, true);
          PendingIntent pi = PendingIntent.getService(sv, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
          Notification.Builder builder = null;
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyManager = (NotificationManager)sv.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotifyManager != null) {
              mNotifyManager.createNotificationChannel(notificationChannel);
            }
            builder = new Notification.Builder(sv, CHANNEL_ID);
          } else {
            builder = new Notification.Builder(sv);
          }
          mNotification = builder
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle(sv.getResources().getString(R.string.app_name))
            .setContentText("Click me to resize the images")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build();
        }
      }
    if (!isStartedNotification) {
      sv.startForeground(MYNOTIFICATION_ID, mNotification);
      isStartedNotification = true;
    }
  }

  public static void stopNotification(Service sv, boolean b) {
    if (isStartedNotification) {
      sv.stopForeground(b);
      isStartedNotification = false;
    }
  }
}

