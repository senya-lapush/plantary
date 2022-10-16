package com.example.plantary.notifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.example.plantary.utility.AlarmSetter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BootService extends Service {
    private Context context;

    @Override
    public void onCreate() {
        for (String docID : getDocIDs()) {
            SharedPreferences setPendings = context.getSharedPreferences(docID, MODE_PRIVATE);
            AlarmSetter.setAlarmAfterReboot(setPendings, docID, context);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = intent.getParcelableExtra("content");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<String> getDocIDs() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("users").document(user.getUid()).collection("userPlants");
        List<String> docIDs = new ArrayList<>();

        colRef.whereEqualTo("notification", true).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();

                for (QueryDocumentSnapshot doc : documents) {
                    docIDs.add(doc.getId());
                }
            }
            });

        return docIDs;
    }
}
