package com.maxchehab.remotelinuxunlocker;

import static android.support.v4.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class PersistenceService extends Service {

    private UnlockReceiver unlockReceiver;
    private final String CHANNEL_ID = "PERSISTENCE_SERVICE";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean createMode = intent.getBooleanExtra("create", true);
        if (!createMode) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.deleteNotificationChannel(CHANNEL_ID);
                return START_NOT_STICKY;
            }
        }
        createNotificationChannel();
        Notification notification = buildNotification();
        try {
            startForeground(1, notification);
        } catch (Exception e) {
            //Oh dear!
            e.printStackTrace();
        }
        unlockReceiver = new UnlockReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockReceiver, filter);
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(unlockReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Disable me!", NotificationManager.IMPORTANCE_HIGH);
        serviceChannel.setDescription("thing");
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Unlock with phone enabled!")
                .setContentText("To hide this notification, please disable it in Notification Settings.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

}
