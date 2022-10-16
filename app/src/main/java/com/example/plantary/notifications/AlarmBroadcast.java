package com.example.plantary.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getExtras().getString("title");
        String message = intent.getExtras().getString("message");
        int waterDays = intent.getIntExtra("waterDays", 0);
        String docID = intent.getExtras().getString("docID");
        long timeToAlarm = intent.getLongExtra("timeToAlarm", 0);
        SharedPreferences setPendings = context.getSharedPreferences(docID, Context.MODE_PRIVATE);
        int requestCode = setPendings.getInt("requestCode", 0);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification(title, message, AdviceClass.getAdvice(), 0, requestCode);

        if (waterDays != 0) {
            AlarmManager alarmManager =(AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            timeToAlarm += AlarmManager.INTERVAL_DAY * waterDays;
            intent.removeExtra("timeToAlarm");
            intent.putExtra("timeToAlarm", timeToAlarm);

            SharedPreferences.Editor editor = setPendings.edit();
            editor.remove("timeToAlarm");
            editor.putLong("timeToAlarm", timeToAlarm);
            editor.apply();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid())
                    .collection("userPlants").document(docID);

            Map<String, Object> data = new HashMap<>();
            data.put("notifyTime", timeToAlarm);
            long finalTimeToAlarm = timeToAlarm;
            docRef.update(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, finalTimeToAlarm, pendingIntent);
                }
            });
        }
    }
}
