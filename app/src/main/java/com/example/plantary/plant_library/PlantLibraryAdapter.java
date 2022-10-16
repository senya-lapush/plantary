package com.example.plantary.plant_library;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.plantary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlantLibraryAdapter extends ArrayAdapter<PlantLibraryItem> {

    private static final String TAG = "DocInfo3";
    private FirebaseAuth mAuth;
    private Context context;
    private final LayoutInflater inflater;
    private final int layout;
    private final List<PlantLibraryItem> plants;
    private List<PlantLibraryItem> search_plants;

    FirebaseStorage storage;

    public PlantLibraryAdapter(@NonNull Context context, int resource, List<PlantLibraryItem> plants) {
        super(context, resource, plants);
        this.context = context;
        this.plants = plants;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.search_plants = new ArrayList<>();
        this.search_plants.addAll(plants);
    }

    @Override
    public int getCount() {
        return plants.size();
    }

    @Override
    public PlantLibraryItem getItem(int position) {
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

        TextView plantName = convertView.findViewById(R.id.plant_library_name);
        ImageView plantImage = convertView.findViewById(R.id.plant_library_img);
        TextView plantDescr = convertView.findViewById(R.id.plant_library_descr);

        PlantLibraryItem plant = plants.get(position);
        plantName.setText(plant.getPlantName());
        plantDescr.setText(plant.getPlantDescription());

        Log.d(TAG, "DOC: " + plant.getImagePath());

        Picasso.with(context)
                    .load(plant.getPlantUri().toString())
                    .fit()
                    .centerCrop()
                    .into(plantImage);

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        plants.clear();
        if (charText.length() == 0) {
            plants.addAll(search_plants);
        } else {
            for (PlantLibraryItem pl : search_plants) {
                if (pl.getPlantName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    plants.add(pl);
                }
            }
        }
        notifyDataSetChanged();
    }
}
