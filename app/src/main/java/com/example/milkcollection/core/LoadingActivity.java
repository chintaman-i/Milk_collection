package com.example.milkcollection.core;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // 🔥 Direct navigation (no delay needed)
        NavigationController.goToNextScreen(this);
    }
}