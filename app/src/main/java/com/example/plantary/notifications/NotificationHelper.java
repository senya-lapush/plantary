package com.example.plantary.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.plantary.Main;
import com.example.plantary.R;

public class NotificationHelper {
    private final String CHANNEL_ID = "plant_reminder";

    private final Context context;

    NotificationHelper(Context context) {
        this.context = context;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Reminder to water plants");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void createNotification(String title, String message, String advice, int humLevel, int id) {
        createNotificationChannel();

        Intent intent = new Intent(context, Main.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.plants_button)
                .setContentTitle(title + ": " + message)
                .setContentText(advice)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (humLevel == 1) {
            builder.setColor(Color.YELLOW);
            builder.setColorized(true);
        } else if (humLevel == 2) {
            builder.setColor(Color.RED);
            builder.setColorized(true);
        }

        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
