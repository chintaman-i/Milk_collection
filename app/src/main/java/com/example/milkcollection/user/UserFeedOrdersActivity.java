package com.example.milkcollection.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.adapter.UserFeedOrderAdapter;
import com.example.milkcollection.model.FeedModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserFeedOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<FeedModel> list;
    UserFeedOrderAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed_orders);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();

        adapter = new UserFeedOrderAdapter(
                this,
                list
        );

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadOrders();
    }

   private void loadOrders() {

        if (FirebaseAuth.getInstance()
                .getCurrentUser() == null) {

            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        db.collection("feed_orders")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    list.clear();

                    snapshot.forEach(doc -> {

                        int quantity = 0;
                        int price = 0;
                        int total = 0;

                        Long quantityLong =
                                doc.getLong("quantity");

                        if (quantityLong != null) {
                            quantity =
                                    quantityLong.intValue();
                        }

                        Long priceLong =
                                doc.getLong("pricePerBag");

                        if (priceLong == null) {
                            priceLong =
                                    doc.getLong("pricePerKg");
                        }

                        if (priceLong != null) {
                            price =
                                    priceLong.intValue();
                        }

                        Long totalLong =
                                doc.getLong("totalAmount");

                        if (totalLong != null) {
                            total =
                                    totalLong.intValue();
                        }

                        FeedModel model =
                                new FeedModel(
                                        doc.getId(),
                                        doc.getString("feedId"),
                                        uid,
                                        doc.getString("feedName"),
                                        quantity,
                                        price,
                                        total,
                                        doc.getString("orderStatus")
                                );

                        list.add(model);
                    });

                    // Pending orders first
                    java.util.Collections.sort(
                            list,
                            (o1, o2) -> {

                                String s1 =
                                        o1.getOrderStatus() == null
                                                ? ""
                                                : o1.getOrderStatus();

                                String s2 =
                                        o2.getOrderStatus() == null
                                                ? ""
                                                : o2.getOrderStatus();

                                boolean p1 =
                                        s1.equalsIgnoreCase(
                                                "pending"
                                        );

                                boolean p2 =
                                        s2.equalsIgnoreCase(
                                                "pending"
                                        );

                                if (p1 && !p2) {
                                    return -1;
                                }

                                if (!p1 && p2) {
                                    return 1;
                                }

                                return 0;
                            }
                    );

                    adapter.notifyDataSetChanged();

                    recyclerView.scrollToPosition(0);
                });
    }
}