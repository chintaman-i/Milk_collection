package com.example.milkcollection.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.adapter.MilkHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MilkHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseFirestore db;

    FirebaseUser currentUser;

    ArrayList<QueryDocumentSnapshot> milkList;

    MilkHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_milk_history);

        recyclerView =
                findViewById(R.id.recyclerView);

        db =
                FirebaseFirestore.getInstance();

        currentUser =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

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

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "User not logged in",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String uid =
                currentUser.getUid();

        Log.d(
                "MILK_HISTORY",
                "Current UID: " + uid
        );

        db.collection("milk_entries")
                .whereEqualTo("uid", uid)
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    milkList.clear();

                    Log.d(
                            "MILK_HISTORY",
                            "Documents Found: "
                                    + queryDocumentSnapshots.size()
                    );

                    for (QueryDocumentSnapshot doc :
                            queryDocumentSnapshots) {

                        Log.d(
                                "MILK_HISTORY",
                                "DOC: " + doc.getData()
                        );

                        milkList.add(doc);
                    }

                    adapter.notifyDataSetChanged();

                    // EMPTY DATA CHECK
                    if (milkList.size() == 0) {

                        Toast.makeText(
                                this,
                                "No milk history found",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                })
                .addOnFailureListener(e -> {

                    Log.e(
                            "MILK_HISTORY",
                            "ERROR: " + e.getMessage()
                    );

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}