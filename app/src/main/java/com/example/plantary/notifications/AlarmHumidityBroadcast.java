package com.example.plantary.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlarmHumidityBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseReference rtdb = FirebaseDatabase.getInstance("https://plantary-d0321-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        String title = intent.getExtras().getString("title");
        String sensorAddress = intent.getExtras().getString("sensorAddress");
        String docID = intent.getExtras().getString("docID");

        SharedPreferences setPendings = context.getSharedPreferences(docID, Context.MODE_PRIVATE);
        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = setPendings.getInt("requestCode", 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


        rtdb.child(sensorAddress).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float sensorData = Float.parseFloat(snapshot.getValue().toString());
                sensorData = (sensorData / 1023) * 100;
                int humLevel = 100 - (int)sensorData;
                long time = System.currentTimeMillis() + AlarmManager.INTERVAL_HALF_DAY;
                if (humLevel < 50) {
                    int humColor = 1;
                    time = System.currentTimeMillis() + 21600000L;
                    if (humLevel < 30) {
                        humColor = 2;
                        time = System.currentTimeMillis() + 10800000L;
                    }
                    String message = humLevel + "%. Время полить меня!";
                    NotificationHelper notificationHelper = new NotificationHelper(context);
                    notificationHelper.createNotification(title, message, AdviceClass.getAdvice(), humColor, requestCode);
                }
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
