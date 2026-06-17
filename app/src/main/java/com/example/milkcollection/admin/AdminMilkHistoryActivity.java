package com.example.milkcollection.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.adapter.MilkHistoryAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminMilkHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseFirestore db;

    ArrayList<QueryDocumentSnapshot> milkList;

    MilkHistoryAdapter adapter;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_milk_history
        );

        recyclerView =
                findViewById(R.id.recyclerView);

        db =
                FirebaseFirestore.getInstance();

        uid =
                getIntent().getStringExtra("uid");

        milkList =
                new ArrayList<>();

        adapter =
                new MilkHistoryAdapter(
                        this,
                        milkList
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {

        if (uid == null) return;

        db.collection("milk_entries")
                .whereEqualTo("uid", uid)
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            milkList.clear();

                            for (
                                    QueryDocumentSnapshot doc :
                                    queryDocumentSnapshots
                            ) {

                                milkList.add(doc);

                            }

                            adapter.notifyDataSetChanged();

                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}