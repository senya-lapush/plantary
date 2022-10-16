package com.example.plantary.add_edit_user_plant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantary.Main;
import com.example.plantary.R;
import com.example.plantary.utility.AlarmSetter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEditPlant extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DocAdd";

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore db;

    private EditText etPlantName;
    private EditText etWaterDays;
    private TimePicker tpWaterTime;
    private EditText etNotes;
    private EditText etSensorAddress;
    private ImageView imagePlant;
    private Button btnNotification;
    private CheckBox cbSensor;

    private TextView tvWaterDays;
    private TextView tvWaterTime;
    private TextView tvSensor;
    private TextView tvSensorAddress;

    private String docID;
    private DocumentReference docRef;
    private StorageReference gsReference;

    private ActivityResultLauncher<String> getPlantImage;
    private SharedPreferences setPendings;
    private boolean notifyUser;
    private boolean newImage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_plant_act);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();

        etPlantName = findViewById(R.id.et_plant_name);
        etWaterDays = findViewById(R.id.et_water_days);
        etNotes = findViewById(R.id.et_plant_notes);
        imagePlant = findViewById(R.id.add_image);
        btnNotification = findViewById(R.id.btn_notification_add);
        tpWaterTime = findViewById(R.id.et_water_time);
        cbSensor = findViewById(R.id.btn_water_device);
        etSensorAddress = findViewById(R.id.et_sensor_address);

        tvWaterDays = findViewById(R.id.tv_water_days);
        tvWaterTime = findViewById(R.id.tv_water_time);
        tvSensor = findViewById(R.id.tv_water_device);
        tvSensorAddress = findViewById(R.id.tv_sensor);

        tvWaterDays.setVisibility(View.GONE);
        etWaterDays.setVisibility(View.GONE);
        tvWaterTime.setVisibility(View.GONE);
        tpWaterTime.setVisibility(View.GONE);
        tvSensor.setVisibility(View.GONE);
        tvSensorAddress.setVisibility(View.GONE);
        cbSensor.setVisibility(View.GONE);
        etSensorAddress.setVisibility(View.GONE);

        tpWaterTime.setIs24HourView(true);

        findViewById(R.id.btn_add_plant).setOnClickListener(this);
        imagePlant.setOnClickListener(this);
        btnNotification.setOnClickListener(this);

        if (getIntent().hasExtra("plantID")) {
            FirebaseUser user = mAuth.getCurrentUser();
            assert user != null;
            docID = getIntent().getExtras().getString("plantID");
            docRef = db.collection("users").document(user.getUid())
                    .collection("userPlants").document(docID);
            gsReference = storage.getReference("user_images/" + user.getUid() + "/" + docID + ".jpg");
        }

        getPlantImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imagePlant.setImageURI(uri);
                        imagePlant.setBackground(null);

                        if (docID != null)
                            newImage = true;
                    }
                });

        if (docRef != null) {
            TextView act_name = findViewById(R.id.act_name_add_edit);
            act_name.setText(R.string.your_plant);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    etPlantName.setText(document.get("plantName").toString());
                    etNotes.setText(document.get("notes").toString());
                    notifyUser = (boolean) document.get("notification");

                    if (!newImage) {
                        gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            Picasso.with(this)
                                    .load(uri.toString())
                                    .into(imagePlant);
                            imagePlant.setBackground(null);
                        }).addOnFailureListener(exception -> {
                            // Handle any errors
                        });
                    }

                    if (notifyUser) {
                        if (document.contains("sensorAddress")) {
                            cbSensor.setChecked(true);
                            etSensorAddress.setText(document.get("sensorAddress").toString());
                            tvSensor.setVisibility(View.VISIBLE);
                            tvSensorAddress.setVisibility(View.VISIBLE);
                            cbSensor.setVisibility(View.VISIBLE);
                            etSensorAddress.setVisibility(View.VISIBLE);
                        } else {
                            etWaterDays.setText(document.get("waterDays").toString());

                            tpWaterTime.setHour(Integer.parseInt(document.get("waterTimeHour").toString()));
                            tpWaterTime.setMinute(Integer.parseInt(document.get("waterTimeMinute").toString()));
                            tvSensor.setVisibility(View.GONE);
                            tvSensorAddress.setVisibility(View.GONE);
                            cbSensor.setVisibility(View.GONE);
                            etSensorAddress.setVisibility(View.GONE);
                        }
                    }

                    toggleNotificationIcon(notifyUser);
                    findViewById(R.id.btn_delete_plant_add).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_delete_plant_add).setOnClickListener(this);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        }

        cbSensor.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                tvWaterDays.setVisibility(View.GONE);
                tvWaterTime.setVisibility(View.GONE);
                tpWaterTime.setVisibility(View.GONE);
                etWaterDays.setVisibility(View.GONE);
                etSensorAddress.setVisibility(View.VISIBLE);
                tvSensorAddress.setVisibility(View.VISIBLE);
            } else {
                tvWaterDays.setVisibility(View.VISIBLE);
                tvWaterTime.setVisibility(View.VISIBLE);
                tpWaterTime.setVisibility(View.VISIBLE);
                etWaterDays.setVisibility(View.VISIBLE);
                etSensorAddress.setVisibility(View.GONE);
                tvSensorAddress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_add_plant) {
            String plantName = etPlantName.getText().toString();
            String plantNotes = etNotes.getText().toString();

            if (plantName.length() != 0) {
                if (notifyUser) {
                    if (cbSensor.isChecked()) {
                        String sensorAddress = etSensorAddress.getText().toString();

                        if (etSensorAddress.length() != 0) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("plantName", plantName);
                            data.put("sensorAddress", sensorAddress);
                            data.put("notification", notifyUser);
                            data.put("notes", plantNotes);
                            String docID = addData(data);

                            AlarmSetter.setAlarm(plantName, sensorAddress, docID, this);
                        } else {
                            Toast.makeText(AddEditPlant.this, "Введите MAC адрес устройства.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String plantWaterDays = etWaterDays.getText().toString();
                        int plantWaterTimeHour = tpWaterTime.getHour();
                        int plantWaterTimeMinute = tpWaterTime.getMinute();

                        if (plantWaterDays.length() != 0) {

                            Calendar calendar = Calendar.getInstance();
                            Log.d(TAG, "отложка0: " + calendar.getTimeInMillis());
                            calendar.set(Calendar.SECOND, 0);
                            Log.d(TAG, "отложка01: " + calendar.getTimeInMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, tpWaterTime.getHour());
                            Log.d(TAG, "отложка02: " + calendar.getTimeInMillis());
                            calendar.set(Calendar.MINUTE, tpWaterTime.getMinute());
                            Log.d(TAG, "отложка1: " + calendar.getTimeInMillis());
                            calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(plantWaterDays));

                            Map<String, Object> data = new HashMap<>();
                            data.put("plantName", plantName);
                            data.put("waterDays", plantWaterDays);
                            data.put("waterTimeHour", plantWaterTimeHour);
                            data.put("waterTimeMinute", plantWaterTimeMinute);
                            data.put("notification", notifyUser);
                            data.put("notes", plantNotes);
                            data.put("notifyTime", String.valueOf(calendar.getTimeInMillis()));
                            String docID = addData(data);

                            Log.d(TAG, "отложка2: " + calendar.getTimeInMillis());
                            Log.d(TAG, "__время: " + System.currentTimeMillis());
                            AlarmSetter.setAlarm(plantName, calendar.getTimeInMillis(), Integer.parseInt(plantWaterDays), docID, this);
                        } else {
                            Toast.makeText(AddEditPlant.this, "Введите время полива и количество дней, через которые напоминать о поливе..",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("plantName", plantName);
                    data.put("notification", notifyUser);
                    data.put("notes", plantNotes);
                    addData(data);

                    if (docID != null) {
                        setPendings = getSharedPreferences(docID, Context.MODE_PRIVATE);
                        if (setPendings.contains("requestCode")) {
                            AlarmSetter.cancelAlarm(docID, this);
                        }
                    }
                }
            } else {
                Toast.makeText(AddEditPlant.this, "Введите название растения.",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.add_image) {
            getPlantImage.launch("image/*");
        } else if (view.getId() == R.id.btn_notification_add) {
            notifyUser = !notifyUser;
            toggleNotificationIcon(notifyUser);
        } else if (view.getId() == R.id.btn_delete_plant_add) {
            docRef.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    gsReference.delete();
                    Toast.makeText(AddEditPlant.this, "Растение было удалено.",
                            Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(AddEditPlant.this, "Ошибка удаления данных.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String addData(Map<String, Object> data) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        if (this.docRef == null) {
            docRef = db.collection("users").document(user.getUid())
                    .collection("userPlants").document();
        }

        docRef.set(data)
                .addOnSuccessListener(documentReference -> {
                    if (this.gsReference == null) {
                        gsReference = storageReference.child("user_images/" + user.getUid() + "/" + docRef.getId() + ".jpg");
                    }
                    Bitmap bitmap = ((BitmapDrawable) imagePlant.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dataB = baos.toByteArray();

                    UploadTask uploadTask = gsReference.putBytes(dataB);
                    uploadTask.addOnFailureListener(exception -> {

                    }).addOnSuccessListener(taskSnapshot -> {

                    });

                    Log.d(TAG, "DocumentSnapshot written with ID: " + docRef.getId());
                    Toast.makeText(AddEditPlant.this, "Данные о растении занесены.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, Main.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error adding document", e);
                    Toast.makeText(AddEditPlant.this, "Не удалось занести данные о растении.",
                            Toast.LENGTH_SHORT).show();
                });

        return docRef.getId();
    }

    private void toggleNotificationIcon(boolean notifyUser) {
        if (notifyUser) {
            btnNotification.setBackground(getDrawable(R.drawable.notification_on));

            if (cbSensor.isChecked()) {
                tvWaterDays.setVisibility(View.GONE);
                tvWaterTime.setVisibility(View.GONE);
                tpWaterTime.setVisibility(View.GONE);
                etWaterDays.setVisibility(View.GONE);

                tvSensor.setVisibility(View.VISIBLE);
                cbSensor.setVisibility(View.VISIBLE);
            } else {
                tvWaterDays.setVisibility(View.VISIBLE);
                tvWaterTime.setVisibility(View.VISIBLE);
                tpWaterTime.setVisibility(View.VISIBLE);
                etWaterDays.setVisibility(View.VISIBLE);

                tvSensor.setVisibility(View.GONE);
                cbSensor.setVisibility(View.GONE);
            }
        } else {
            btnNotification.setBackground(getDrawable(R.drawable.notification_off));
            tvWaterDays.setVisibility(View.GONE);
            tvWaterTime.setVisibility(View.GONE);
            tvSensor.setVisibility(View.GONE);
            cbSensor.setVisibility(View.GONE);
            tpWaterTime.setVisibility(View.GONE);
            etWaterDays.setVisibility(View.GONE);
            etSensorAddress.setVisibility(View.GONE);
            tvSensorAddress.setVisibility(View.GONE);

            cbSensor.setChecked(false);
        }
    }
}
