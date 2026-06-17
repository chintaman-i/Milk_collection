package com.example.milkcollection.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.adapter.FeedAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FeedStoreActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FeedAdapter adapter;

    ArrayList<QueryDocumentSnapshot> feedList;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_store);

        recyclerView =
                findViewById(R.id.recyclerView);

        db = FirebaseFirestore.getInstance();

        feedList = new ArrayList<>();

        adapter =
                new FeedAdapter(
                        this,
                        feedList
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        loadFeeds();
    }

private void loadFeeds() {

        db.collection("feed_stock")
                .get()

                .addOnSuccessListener(snapshot -> {

                    feedList.clear();

                    for (QueryDocumentSnapshot doc :
                            snapshot) {

                        feedList.add(doc);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}