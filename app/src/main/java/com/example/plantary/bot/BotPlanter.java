package com.example.plantary.bot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantary.Main;
import com.example.plantary.R;
import com.example.plantary.plant_library.PlantLibrary;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BotPlanter extends AppCompatActivity implements View.OnClickListener {

    FirebaseStorage storage;
    StorageReference storageReference;

    private ListView lvChat;
    private List<Message> messages;

    private Button btn1;
    private Button btn2;
    private Button btn3;

    private int question;
    private Map<String, Object> data = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bot_act);

        messages = new ArrayList<>();
        question = 1;
        data = new HashMap<>();

        lvChat = findViewById(R.id.lv_chat);
        btn1 = findViewById(R.id.btn_choice1);
        btn2 = findViewById(R.id.btn_choice2);
        btn3 = findViewById(R.id.btn_choice3);

        findViewById(R.id.my_plants_button).setOnClickListener(this);
        findViewById(R.id.btn_library).setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        messages.add(new Message("Привет! Меня зовут Плэнтер. Я помогу подобрать Вам растение для дома.", true));
        messages.add(new Message("Сколько времени Вы готовы уделять растению?", true));

        updateUI(messages);

        btn1.setText("Немного");
        btn2.setText("Сколько потребуется");
        btn3.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateUI(List<Message> messages) {
        BotAdapter botAdapter = new BotAdapter(this, R.layout.bot_item, messages);
        lvChat.setAdapter(botAdapter);
        lvChat.setSelection(messages.size() - 1);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_library) {
            Intent intent = new Intent(this, PlantLibrary.class);
            startActivity(intent);
        } else if (view.getId() == R.id.my_plants_button) {
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_choice1 || view.getId() == R.id.btn_choice2 || view.getId() == R.id.btn_choice3) {
            getAnswer((Button)view);
            updateUI(messages);
        }
    }

    private void getAnswer(Button btn) {
        switch(question) {
            case 1:
                if (btn.getText().toString().equals("Немного")) {
                    messages.add(new Message("Немного", false));
                    data.put("timeNeeded", false);
                } else {
                    messages.add(new Message("Сколько потребуется", false));
                    data.put("timeNeeded", true);
                }
                question++;

                messages.add(new Message("Какого размера должно быть растение?", true));
                btn1.setText("Маленькое");
                btn2.setText("Среднее");
                btn3.setText("Большое");
                btn3.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (btn.getText().toString().equals("Маленькое")) {
                    messages.add(new Message("Маленькое", false));
                    data.put("size", "Маленькое");
                } else  if (btn.getText().toString().equals("Среднее")) {
                    messages.add(new Message("Среднее", false));
                    data.put("size", "Среднее");
                } else {
                    messages.add(new Message("Большое", false));
                    data.put("size", "Большое");
                }
                question++;

                messages.add(new Message("Ваше растение должно приносить плоды?", true));
                btn1.setText("Да, хочу плоды.");
                btn2.setText("Нет, не хочу плоды.");
                btn3.setVisibility(View.GONE);
                break;
            case 3:
                if (btn.getText().toString().equals("Да, хочу плоды.")) {
                    messages.add(new Message("Да, хочу плоды", false));
                    data.put("isFertile", true);
                    data.put("isBlossom", true);
                    question = 5;
                    btn1.setText("Начать сначала.");
                    btn2.setVisibility(View.GONE);
                    btn3.setVisibility(View.GONE);
                    getResult();
                } else {
                    messages.add(new Message("Нет, не хочу плоды", false));
                    data.put("isFertile", false);
                    messages.add(new Message("Вы хотите цветущее растение?", true));
                    btn1.setText("Да, хочу цветущее.");
                    btn2.setText("Нет, не хочу цветущее.");
                    question++;
                }
                break;
            case 4:
                if (btn.getText().toString().equals("Да, хочу цветущее.")) {
                    messages.add(new Message("Да, хочу цветущее", false));
                    data.put("isBlossom", true);
                } else {
                    messages.add(new Message("Нет, не хочу цветущее", false));
                    data.put("isBlossom", false);
                }
                question++;
                btn1.setText("Начать сначала.");
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
                getResult();
                break;
            case 5:
                btn1.setText("Немного");
                btn2.setText("Сколько потребуется");
                btn2.setVisibility(View.VISIBLE);
                messages = new ArrayList<>();
                question = 1;
                messages.add(new Message("Привет! Меня зовут Плэнтер. Я помогу подобрать Вам растение для дома.", true));
                messages.add(new Message("Сколько времени Вы готовы уделять растению?", true));

                updateUI(messages);
                break;
        }
    }

    private void getResult() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        CollectionReference colRef = db.collection("plants");

        colRef
                .whereEqualTo("timeNeeded", data.get("timeNeeded"))
                .whereEqualTo("size", data.get("size"))
                .whereEqualTo("isFertile", data.get("isFertile"))
                .whereEqualTo("isBlossom", data.get("isBlossom"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Random random = new Random();
                            int count = task.getResult().getDocuments().size();
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(random.nextInt(count));
                            messages.add(new Message("Вам подойдет " + document.get("name").toString(), true));
                        } else {
                            messages.add(new Message("Подходящего растения Вам не нашлось :(", true));
                        }
                    }
                    updateUI(messages);
                });
    }
}
