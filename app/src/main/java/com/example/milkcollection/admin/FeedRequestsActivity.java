package com.example.milkcollection.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.adapter.FeedRequestAdapter;
import com.example.milkcollection.model.FeedModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<FeedModel> list;
    private FeedRequestAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_requests);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();

        adapter = new FeedRequestAdapter(
                this,
                list
        );

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadOrders();
    }

    private void loadOrders() {

        db.collection("feed_orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    list.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        String uid =
                                doc.getString("uid");

                        if (uid == null) {
                            continue;
                        }

                        db.collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(userDoc -> {

                                    String farmerName =
                                            "Unknown Farmer";

                                    if (userDoc.exists()) {

                                        String firstName =
                                                userDoc.getString(
                                                        "firstName"
                                                );

                                        String lastName =
                                                userDoc.getString(
                                                        "lastName"
                                                );

                                        farmerName =
                                                (firstName != null
                                                        ? firstName
                                                        : "")
                                                        + " "
                                                        + (lastName != null
                                                        ? lastName
                                                        : "");
                                    }

                                    int quantity = 0;
                                    int price = 0;
                                    int total = 0;

                                    Number quantityNum =
                                            (Number) doc.get(
                                                    "quantity"
                                            );

                                    Number priceNum =
                                            (Number) doc.get(
                                                    "pricePerBag"
                                            );

                                    if (priceNum == null) {
                                        priceNum =
                                                (Number) doc.get(
                                                        "pricePerKg"
                                                );
                                    }

                                    Number totalNum =
                                            (Number) doc.get(
                                                    "totalAmount"
                                            );

                                    if (quantityNum != null) {
                                        quantity =
                                                quantityNum.intValue();
                                    }

                                    if (priceNum != null) {
                                        price =
                                                priceNum.intValue();
                                    }

                                    if (totalNum != null) {
                                        total =
                                                totalNum.intValue();
                                    }

                                    FeedModel model =
                                            new FeedModel(

                                                    doc.getId(),

                                                    doc.getString(
                                                            "feedId"
                                                    ),

                                                    farmerName,

                                                    doc.getString(
                                                            "feedName"
                                                    ),

                                                    quantity,

                                                    price,

                                                    total,

                                                    doc.getString(
                                                            "orderStatus"
                                                    )
                                            );

                                    list.add(model);

                                    adapter.notifyDataSetChanged();
                                });
                    }
                });
    }
}