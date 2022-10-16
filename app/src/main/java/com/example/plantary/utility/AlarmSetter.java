package com.example.plantary.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.plantary.notifications.AlarmBroadcast;
import com.example.plantary.notifications.AlarmHumidityBroadcast;

public final class AlarmSetter {

    public static void setAlarmAfterReboot(SharedPreferences setPendings, String docID, Context context) {
        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long timeToAlarm;

        Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("title", setPendings.getString("title", ""));
        if (setPendings.contains("timeToAlarm")) {
            intent.putExtra("message", "Время меня полить!");
            intent.putExtra("waterDays", setPendings.getInt("waterDays", 0));
            intent.putExtra("timeToAlarm", setPendings.getLong("timeToAlarm", 0));
            timeToAlarm = setPendings.getLong("timeToAlarm", 0);
        } else {
            intent.putExtra("sensorAddress", setPendings.getString("sensorAddress", ""));
            timeToAlarm = System.currentTimeMillis() + 60000L;
        }
        intent.putExtra("docID", docID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), setPendings.getInt("requestCode", 0), intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeToAlarm, pendingIntent);
    }

    public static void setAlarm(String title, long timeToAlarm, int waterDays, String docID, Context context) {
        SharedPreferences setPendings = context.getSharedPreferences(docID, Context.MODE_PRIVATE);

        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("message", "Время меня полить!");
        intent.putExtra("waterDays", waterDays);
        intent.putExtra("timeToAlarm", timeToAlarm);
        intent.putExtra("docID", docID);

        int requestCode;
        if (setPendings.contains("requestCode")) {
            requestCode = setPendings.getInt("requestCode", 0);
        } else {
            SharedPreferences.Editor editor = setPendings.edit();
            requestCode = (int)System.currentTimeMillis();
            editor.putString("title", title);
            editor.putInt("requestCode", requestCode);
            editor.putInt("waterDays", waterDays);
            editor.putLong("timeToAlarm", timeToAlarm);
            editor.apply();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeToAlarm, pendingIntent);
    }

    public static void setAlarm(String title, String sensorAddress, String docID, Context context) {
        SharedPreferences setPendings = context.getSharedPreferences(docID, Context.MODE_PRIVATE);
        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), AlarmHumidityBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("sensorAddress", sensorAddress);
        intent.putExtra("docID", docID);

        int requestCode;
        if (setPendings.contains("requestCode")) {
            requestCode = setPendings.getInt("requestCode", 0);
        } else {
            SharedPreferences.Editor editor = setPendings.edit();
            requestCode = (int)System.currentTimeMillis();
            editor.putInt("requestCode", requestCode);
            editor.putString("title", title);
            editor.putString("sensorAddress", sensorAddress);
            editor.apply();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000L, pendingIntent);
    }

    public static void cancelAlarm(String docID, Context context) {
        SharedPreferences setPendings = context.getSharedPreferences(docID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setPendings.edit();
        int requestCode = setPendings.getInt("requestCode", 0);

        AlarmManager alarmManager =(AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent;
        if (setPendings.contains("sensorAddress")) {
            intent = new Intent(context.getApplicationContext(), AlarmHumidityBroadcast.class);
            intent.putExtra("title", setPendings.getString("title", ""));
            intent.putExtra("docID", docID);
            intent.putExtra("sensorAddress", setPendings.getString("sensorAddress", ""));
        } else {
            intent = new Intent(context.getApplicationContext(), AlarmBroadcast.class);
            intent.putExtra("title", setPendings.getString("title", ""));
            intent.putExtra("docID", docID);
            intent.putExtra("message", "Время меня полить!");
            intent.putExtra("waterDays", setPendings.getInt("waterDays", 0));
            intent.putExtra("timeToAlarm", setPendings.getLong("timeToAlarm", 0));
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        editor.remove("requestCode");
        editor.apply();
    }
}
