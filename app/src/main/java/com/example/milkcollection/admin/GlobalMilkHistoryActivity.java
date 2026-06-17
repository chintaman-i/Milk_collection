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

public class GlobalMilkHistoryActivity
        extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseFirestore db;

    ArrayList<QueryDocumentSnapshot> milkList;

    MilkHistoryAdapter adapter;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_global_milk_history
        );

        recyclerView =
                findViewById(
                        R.id.recyclerView
                );

        db =
                FirebaseFirestore.getInstance();

        milkList =
                new ArrayList<>();

        adapter =
                new MilkHistoryAdapter(
                        this,
                        milkList,
                        true
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {

        db.collection("milk_entries")
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