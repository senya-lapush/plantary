package com.example.plantary.plant_library;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantary.bot.BotPlanter;
import com.example.plantary.Main;
import com.example.plantary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PlantLibrary extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private PlantLibraryAdapter plantAdapter;
    private ListView plantList;

    private SearchView searchView;

    private List<PlantLibraryItem> plants;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        mAuth = FirebaseAuth.getInstance();

        plantList = findViewById(R.id.lv_library);
        plantList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(PlantLibrary.this, PlantLibraryInfo.class);
            intent.putExtra("plantID", plantAdapter.getItem(i).getDocID());
            intent.putExtra("plantUri", plantAdapter.getItem(i).getPlantUri().toString());
            startActivity(intent);
        });

        searchView = findViewById(R.id.search_bar);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.BLACK);

        findViewById(R.id.my_plants_button).setOnClickListener(this);
        findViewById(R.id.bot_button).setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getAllPlants();
    }

    private void updateUI(List<PlantLibraryItem> plants) {
        plantAdapter = new PlantLibraryAdapter(this, R.layout.library_item, plants);
        plantList.setAdapter(plantAdapter);
    }

    private void getAllPlants() {
        db = FirebaseFirestore.getInstance();

        plants = new ArrayList<>();
        CollectionReference colRef = db.collection("plants");

        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    PlantLibraryItem plant = new PlantLibraryItem(document.get("name").toString(), document.getId(),
                            document.get("description").toString().substring(0, 20).trim());

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference gsReference = storage.getReference(plant.getImagePath());

                    gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        plant.setPlantUri(uri);
                        plants.add(plant);
                        updateUI(plants);
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                    });

                }
            } else {
                //Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        updateUI(plants);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.my_plants_button) {
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
        } else if (view.getId() == R.id.bot_button) {
            Intent intent = new Intent(this, BotPlanter.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        plantAdapter.filter(s);
        return false;
    }
}
