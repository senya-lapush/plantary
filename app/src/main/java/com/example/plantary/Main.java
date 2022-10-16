package com.example.plantary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.plantary.add_edit_user_plant.AddEditPlant;
import com.example.plantary.auth_reg.Authorisation;
import com.example.plantary.bot.BotPlanter;
import com.example.plantary.plant_item_main.PlantAdapter;
import com.example.plantary.plant_item_main.PlantItem;
import com.example.plantary.plant_library.PlantLibrary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Doc34";
    private FirebaseAuth mAuth;
    private DatabaseReference rtdb;

    private ListView plantList;

    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);

        mAuth = FirebaseAuth.getInstance();
        rtdb = FirebaseDatabase.getInstance("https://plantary-d0321-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        plantList = findViewById(R.id.plant_list);

        findViewById(R.id.btn_signOut).setOnClickListener(this);
        findViewById(R.id.btn_library).setOnClickListener(this);
        findViewById(R.id.bot_button).setOnClickListener(this);
        findViewById(R.id.btn_add_plant_main).setOnClickListener(this);

        getPlants();
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            getPlants();
            swipeRefresh.setRefreshing(false);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, Authorisation.class);
            startActivity(intent);
            finish();
        } else if (view.getId() == R.id.btn_library) {
            Intent intent = new Intent(this, PlantLibrary.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_add_plant_main) {
            Intent intent = new Intent(this, AddEditPlant.class);
            startActivity(intent);
        } else if (view.getId() == R.id.bot_button) {
            Intent intent = new Intent(this, BotPlanter.class);
            startActivity(intent);
        }
    }

    private void updateUI(List<PlantItem> plants) {
        PlantAdapter plantAdapter = new PlantAdapter(this, R.layout.plant_item, plants);
        plantList.setAdapter(plantAdapter);
    }

    private void getPlants() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<PlantItem> plants = new ArrayList<>();
        CollectionReference colRef = db.collection("users").document(user.getUid()).collection("userPlants");

        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.contains("sensorAddress")) {
                        String path = document.get("sensorAddress").toString();
                        rtdb.child(path).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                float sensorData = Float.parseFloat(snapshot.getValue().toString());
                                sensorData = (sensorData / 1023) * 100;

                                PlantItem plant = new PlantItem(100 - (int)sensorData, document.get("plantName").toString(),
                                        document.getId(), user.getUid(), true);

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

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        if (document.contains("waterDays")) {
                            PlantItem plant = new PlantItem(Long.parseLong(document.get("notifyTime").toString()), document.get("plantName").toString(),
                                    document.getId(), user.getUid(), false);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference gsReference = storage.getReference(plant.getImagePath());

                            gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                plant.setPlantUri(uri);
                                plants.add(plant);
                                updateUI(plants);
                            }).addOnFailureListener(exception -> {
                                // Handle any errors
                            });
                        } else {
                            PlantItem plant = new PlantItem(document.get("plantName").toString(),
                                    document.getId(), user.getUid());

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
                    }
                }
                updateUI(plants);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

}
