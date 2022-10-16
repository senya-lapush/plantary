package com.example.plantary.auth_reg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_act);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_login_reg);
        etPassword = findViewById(R.id.et_password_reg);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        findViewById(R.id.btn_create_account).setOnClickListener(this);
        findViewById(R.id.tv_signIn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_create_account) {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (email.length() != 0 || password.length() != 0 || confirmPassword.length() != 0) {
                if (password.equals(confirmPassword)) {
                    Register(email, password);
                } else {
                    Toast.makeText(Registration.this, "Пароли не совпадают. Попробуйте снова.",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Registration.this, "Заполните все поля.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (view.getId() == R.id.tv_signIn) {
            authorisationActivity();
        }
    }

    private void Register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DocumentReference userDoc = db.collection("users").document(task.getResult().getUser().getUid());
                        Map<String, Object> data = new HashMap<>();
                        data.put("email", email);

                        userDoc.set(data)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(Registration.this, "Вы зарегистрированы.",
                                            Toast.LENGTH_SHORT).show();
                                    authorisationActivity();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Registration.this, "Ошибка. Попробуйте снова.",
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(Registration.this, "Аккаунт с такой почтой уже существует, либо вы ввели некорректный адрес.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void authorisationActivity() {
        Intent intent = new Intent(this, Authorisation.class);
        startActivity(intent);
    }
}
