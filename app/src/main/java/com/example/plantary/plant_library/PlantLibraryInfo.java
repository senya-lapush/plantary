package com.example.plantary.plant_library;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class PlantLibraryInfo extends AppCompatActivity {

    private static final String TAG = "DocInfo2";
    FirebaseStorage storage;

    private TextView plantName;
    private ImageView plantImage;
    private TextView plantDescription;

    private String docID;
    private DocumentReference docRef;
    private String uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_plant);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        plantName = findViewById(R.id.lb_plant_name);
        plantImage = findViewById(R.id.lb_plant_image);
        plantDescription = findViewById(R.id.lb_plant_description);

        uri = getIntent().getExtras().getString("plantUri");
        docID = getIntent().getExtras().getString("plantID");
        docRef = db.collection("plants").document(docID);
        Log.d(TAG, "path: 00");
    }

    @Override
    protected void onStart() {
        super.onStart();

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                plantName.setText(document.get("name").toString());
                plantDescription.setText(document.get("description").toString());

                Picasso.with(this)
                        .load(uri)
                        .into(plantImage);

                LinearLayout placeHolder = (LinearLayout) findViewById(R.id.ll_lb_plant);

                if (document.contains("wateringRegime")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Режим полива");
                    descr.setText(document.get("wateringRegime").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("soil")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Почва");
                    descr.setText(document.get("soil").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("temperature")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Температура");
                    descr.setText(document.get("temperature").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("humidity")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Влажность");
                    descr.setText(document.get("humidity").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("lightingLevel")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Освещение");
                    descr.setText(document.get("lightingLevel").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("diseases")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Болезни");
                    descr.setText(document.get("diseases").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("pests")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Паразиты");
                    descr.setText(document.get("pests").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("propagation")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Размножение");
                    descr.setText(document.get("propagation").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("topDressing")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Подкормка");
                    descr.setText(document.get("topDressing").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("transplanting")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Пересадка");
                    descr.setText(document.get("transplanting").toString());

                    placeHolder.addView(view);
                }
                if (document.contains("pruning")) {
                    View view = getLayoutInflater().inflate(R.layout.library_plant_description, null);
                    TextView title = (TextView) view.findViewById(R.id.lb_title);
                    TextView descr = (TextView) view.findViewById(R.id.lb_description);
                    title.setText("Обрезка");
                    descr.setText(document.get("pruning").toString());

                    placeHolder.addView(view);
                }

            } else {
                //Log.d(TAG, "Error getting documents: ", task.getException());
                Log.d(TAG, "DOC: " + docID);
            }
        });
    }
}
