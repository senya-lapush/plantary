package com.example.plantary.auth_reg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantary.Main;
import com.example.plantary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authorisation extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorisation_act);
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_login);
        etPassword = findViewById(R.id.et_password);

        findViewById(R.id.btn_signIn).setOnClickListener(this);
        findViewById(R.id.create_acc).setOnClickListener(this);
        findViewById(R.id.tv_forgotpassword).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updateUI();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_signIn) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.length() != 0 || password.length() != 0) {
                signIn(email, password);
            } else {
                Toast.makeText(Authorisation.this, "Заполните все поля.", Toast.LENGTH_SHORT).show();
            }

        }
        else if (view.getId() == R.id.tv_forgotpassword) {
            String email = etEmail.getText().toString().trim();
            if (email.length() != 0) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Authorisation.this, "Письмо для сброса пароля было отправлено.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(Authorisation.this, "Введите адрес электронной почты.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (view.getId() == R.id.create_acc) {
            registrationActivity();
        }
    }

    private void signIn (String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updateUI();
                    } else {
                        Toast.makeText(Authorisation.this, "Ошибка авторизации. Попробуйте снова.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }

    public void registrationActivity() {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }
}
