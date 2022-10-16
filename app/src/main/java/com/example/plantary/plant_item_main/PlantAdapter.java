package com.example.plantary.plant_item_main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.plantary.R;
import com.example.plantary.add_edit_user_plant.AddEditPlant;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlantAdapter extends ArrayAdapter<PlantItem> {

    private static final String TAG = "Doc345";
    private final Context context;
    private final LayoutInflater inflater;
    private final int layout;
    private final List<PlantItem> plants;

    public PlantAdapter(@NonNull Context context, int resource, List<PlantItem> plants) {
        super(context, resource, plants);
        this.context = context;
        this.plants = plants;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return plants.size();
    }

    @Override
    public PlantItem getItem(int position) {
        return plants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
        }

        TextView plantName = convertView.findViewById(R.id.plant_name);
        TextView plantInfo = convertView.findViewById(R.id.water_days);
        Button plantInfoButton = convertView.findViewById(R.id.btn_plant_info);
        ImageView plantImage = convertView.findViewById(R.id.plant_img);

        PlantItem plant = plants.get(position);

        plantName.setText(plant.getPlantName());
        if (plant.getSensorUsed()) {
            String humidityPercent = context.getResources().getString(R.string.humidity_level_main, plant.getPlantInfo());
            plantInfo.setText(humidityPercent);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(plant.getPlantInfo());
            long time = calendar.getTimeInMillis();

            if (time > System.currentTimeMillis()) {
                calendar.setTimeInMillis(time);
                Log.d(TAG, "отложка: " + calendar.getTimeInMillis());
                long timePeriod = calendar.getTimeInMillis() - System.currentTimeMillis();
                if (TimeUnit.DAYS.convert(timePeriod, TimeUnit.MILLISECONDS) > 0) {
                    int days = (int) TimeUnit.DAYS.convert(timePeriod, TimeUnit.MILLISECONDS);
                    Log.d(TAG, "отложка_days: " + days);
                    String day = context.getResources().getQuantityString(R.plurals.water_in_days_main, days, days);
//                    switch (days) {
//                        case 1:
//                            day = context.getResources().getQuantityString(R.plurals.water_in_days_main, days, days);
//                            break;
//                        case 2:
//                        case 3:
//                        case 4:
//                            day = " дня";
//                            break;
//                        default:
//                            day = " дней";
//                            break;
//                    }
                    plantInfo.setText(day);
                } else if (TimeUnit.HOURS.convert(timePeriod, TimeUnit.MILLISECONDS) > 0) {
                    int hours = (int) TimeUnit.HOURS.convert(timePeriod, TimeUnit.MILLISECONDS);
                    String hour = context.getResources().getQuantityString(R.plurals.water_in_hours_main, hours, hours);;
//                    switch (hours) {
//                        case 1:
//                        case 21:
//                            hour = " час";
//                            break;
//                        case 2:
//                        case 3:
//                        case 4:
//                        case 22:
//                        case 23:
//                            hour = " часа";
//                            break;
//                        default:
//                            hour = " часов";
//                            break;
//                    }
                    plantInfo.setText(hour);
                } else  {
                    plantInfo.setText("Полить сейчас!");
                }
            } else {
                plantInfo.setText("Поставьте уведомление");
            }
        }

        Picasso.with(context)
                .load(plant.getPlantUri().toString())
                .fit()
                .centerCrop()
                .into(plantImage);

        plantInfoButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddEditPlant.class);
            intent.putExtra("plantID", plant.getDocID());
            context.startActivity(intent);
        });
        Log.d(TAG, "one");
        return convertView;
    }

}
