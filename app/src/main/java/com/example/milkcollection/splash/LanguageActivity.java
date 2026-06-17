package com.example.milkcollection.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.auth.LoginActivity;

public class LanguageActivity extends AppCompatActivity {

    Button englishBtn,hindiBtn,marathiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        englishBtn=findViewById(R.id.btnEnglish);
        hindiBtn=findViewById(R.id.btnHindi);
        marathiBtn=findViewById(R.id.btnMarathi);

        englishBtn.setOnClickListener(v -> saveLanguage("en"));
        hindiBtn.setOnClickListener(v -> saveLanguage("hi"));
        marathiBtn.setOnClickListener(v -> saveLanguage("mr"));
    }

    private void saveLanguage(String lang){

        SharedPreferences prefs=getSharedPreferences("app_prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();

        editor.putString("language",lang);
        editor.putBoolean("first_launch",false);

        editor.apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}