package com.example.milkcollection.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.adapter.FarmerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FarmerManagementActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseFirestore db;

    ArrayList<QueryDocumentSnapshot> farmerList;

    FarmerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_management);

        recyclerView =
                findViewById(R.id.recyclerView);

        db = FirebaseFirestore.getInstance();

        farmerList = new ArrayList<>();

        adapter = new FarmerAdapter(
                this,
                farmerList
        );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        loadFarmers();
    }

    private void loadFarmers() {

        db.collection("users")
                .whereEqualTo("accountStatus", "approved")
                .orderBy("farmerId")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    farmerList.clear();

                    for (QueryDocumentSnapshot doc :
                            queryDocumentSnapshots) {

                        farmerList.add(doc);
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
}