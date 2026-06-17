package com.example.milkcollection.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedAdapter
        extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    Context context;

    ArrayList<QueryDocumentSnapshot> feedList;

    FirebaseFirestore db;

    public FeedAdapter(
            Context context,
            ArrayList<QueryDocumentSnapshot> feedList
    ) {
        this.context = context;
        this.feedList = feedList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_feed,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        QueryDocumentSnapshot doc =
                feedList.get(position);

        String feedName =
                doc.getString("feedName");

        Long stockLong =
                doc.getLong("stockKg");

        Long priceLong =
                doc.getLong("pricePerKg");

        long stock =
                stockLong != null
                        ? stockLong
                        : 0;

        long price =
                priceLong != null
                        ? priceLong
                        : 0;

        holder.feedNameText.setText(feedName);

        holder.stockText.setText(
                "Available : " +
                        stock +
                        " Kg"
        );

        holder.priceText.setText(
                "₹" +
                        price +
                        " / Kg"
        );

        if (stock <= 0) {

            holder.buyBtn.setEnabled(false);
            holder.buyBtn.setText("Out Of Stock");

        } else {

            holder.buyBtn.setEnabled(true);
            holder.buyBtn.setText("Order Feed");
        }

        holder.buyBtn.setOnClickListener(v ->
                showOrderDialog(
                        doc,
                        stock,
                        price
                ));
    }

    private void showOrderDialog(
            QueryDocumentSnapshot doc,
            long stock,
            long price
    ) {

        EditText quantityInput =
                new EditText(context);

        quantityInput.setHint(
                "Enter quantity in Kg"
        );

        new AlertDialog.Builder(context)

                .setTitle("Order Feed")

                .setView(quantityInput)

                .setPositiveButton(
                        "Place Order",
                        (dialog, which) -> {

                            String qtyText =
                                    quantityInput
                                            .getText()
                                            .toString()
                                            .trim();

                            if (qtyText.isEmpty()) {

                                Toast.makeText(
                                        context,
                                        "Enter Quantity",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            int quantity =
                                    Integer.parseInt(qtyText);

                            if (quantity <= 0) {

                                Toast.makeText(
                                        context,
                                        "Invalid Quantity",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (quantity > stock) {

                                Toast.makeText(
                                        context,
                                        "Only " +
                                                stock +
                                                " Kg Available",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            placeOrder(
                                    doc,
                                    quantity,
                                    price
                            );
                        })

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }

    private void placeOrder(
            QueryDocumentSnapshot doc,
            int quantity,
            long price
    ) {

        if (FirebaseAuth.getInstance()
                .getCurrentUser() == null) {

            return;
        }

        String uid =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        long totalAmount =
                quantity * price;

        Map<String, Object> order =
                new HashMap<>();

        order.put("uid", uid);
        order.put("feedId", doc.getId());
        order.put("feedName",
                doc.getString("feedName"));
        order.put("quantity", quantity);
        order.put("pricePerKg", price);
        order.put("totalAmount",
                totalAmount);
        order.put("orderStatus",
                "Pending");
        order.put("createdAt",
                Timestamp.now());

        db.collection("feed_orders")
                .add(order)

                .addOnSuccessListener(unused ->

                        Toast.makeText(
                                context,
                                "Order Placed Successfully",
                                Toast.LENGTH_LONG
                        ).show()
                )

                .addOnFailureListener(e ->

                        Toast.makeText(
                                context,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView feedNameText,
                stockText,
                priceText;

        Button buyBtn;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            feedNameText =
                    itemView.findViewById(
                            R.id.feedNameText
                    );

            stockText =
                    itemView.findViewById(
                            R.id.stockText
                    );

            priceText =
                    itemView.findViewById(
                            R.id.priceText
                    );

            buyBtn =
                    itemView.findViewById(
                            R.id.buyBtn
                    );
        }
    }
}