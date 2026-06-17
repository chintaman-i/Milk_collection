package com.example.milkcollection.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.model.FeedModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FeedRequestAdapter
        extends RecyclerView.Adapter<FeedRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<FeedModel> list;
    private final FirebaseFirestore db;

    public FeedRequestAdapter(
            Context context,
            List<FeedModel> list
    ) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_feed_request,
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

        FeedModel model = list.get(position);

        holder.farmer.setText(
                "Farmer: " + model.getUid()
        );

        holder.feed.setText(
                "Feed: " + model.getFeedName()
        );

        holder.quantity.setText(
                "Quantity: " +
                        model.getQuantity() +
                        " Kg"
        );

        holder.amount.setText(
                "Amount: ₹" +
                        model.getTotalAmount()
        );

        /*
         * SHOW CURRENT STOCK
         */
        if (model.getFeedId() != null &&
                !model.getFeedId().isEmpty()) {

            db.collection("feed_stock")
                    .document(model.getFeedId())
                    .get()
                    .addOnSuccessListener(feedDoc -> {

                        if (feedDoc.exists()) {

                            Long stock =
                                    feedDoc.getLong("stockKg");

                            holder.stock.setText(
                                    "Current Stock : "
                                            + (stock != null ? stock : 0)
                                            + " Kg"
                            );
                        }
                    });
        }

        String status =
                model.getOrderStatus();

        if (status == null) {
            status = "pending";
        }

        holder.status.setText(
                status.toUpperCase()
        );

        if (status.equalsIgnoreCase("approved")) {

            holder.status.setTextColor(
                    Color.parseColor("#4CAF50")
            );

            holder.buttonLayout.setVisibility(
                    View.GONE
            );

        } else if (status.equalsIgnoreCase("rejected")) {

            holder.status.setTextColor(
                    Color.parseColor("#F44336")
            );

            holder.buttonLayout.setVisibility(
                    View.GONE
            );

        } else {

            holder.status.setTextColor(
                    Color.parseColor("#FF9800")
            );

            holder.buttonLayout.setVisibility(
                    View.VISIBLE
            );
        }

        holder.approve.setOnClickListener(v -> {

            String feedId =
                    model.getFeedId();

            if (feedId == null ||
                    feedId.isEmpty()) {

                Toast.makeText(
                        context,
                        "Feed ID missing",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            db.collection("feed_stock")
                    .document(feedId)
                    .get()

                    .addOnSuccessListener(feedDoc -> {

                        if (!feedDoc.exists()) {

                            Toast.makeText(
                                    context,
                                    "Feed not found",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        Long stockLong =
                                feedDoc.getLong(
                                        "stockKg"
                                );

                        long currentStock =
                                stockLong != null
                                        ? stockLong
                                        : 0;

                        long orderQty =
                                model.getQuantity();

                        if (orderQty > currentStock) {

                            Toast.makeText(
                                    context,
                                    "Only "
                                            + currentStock
                                            + " Kg available",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        long newStock =
                                currentStock - orderQty;

                        db.collection("feed_stock")
                                .document(feedId)
                                .update(
                                        "stockKg",
                                        newStock
                                )

                                .addOnSuccessListener(unused -> {

                                    db.collection("feed_orders")
                                            .document(
                                                    model.getId()
                                            )
                                            .update(
                                                    "orderStatus",
                                                    "approved"
                                            )

                                            .addOnSuccessListener(unused1 -> {

                                                model.setOrderStatus(
                                                        "approved"
                                                );

                                                holder.stock.setText(
                                                        "Current Stock : "
                                                                + newStock
                                                                + " Kg"
                                                );

                                                notifyItemChanged(
                                                        holder.getAdapterPosition()
                                                );

                                                Toast.makeText(
                                                        context,
                                                        "Order Approved",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            });
                                });
                    });
        });

        holder.reject.setOnClickListener(v -> {

            db.collection("feed_orders")
                    .document(model.getId())
                    .update(
                            "orderStatus",
                            "rejected"
                    )

                    .addOnSuccessListener(unused -> {

                        model.setOrderStatus(
                                "rejected"
                        );

                        notifyItemChanged(
                                holder.getAdapterPosition()
                        );

                        Toast.makeText(
                                context,
                                "Order Rejected",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView farmer;
        TextView feed;
        TextView quantity;
        TextView amount;
        TextView status;
        TextView stock;

        Button approve;
        Button reject;

        LinearLayout buttonLayout;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            farmer =
                    itemView.findViewById(
                            R.id.txtFarmerId
                    );

            feed =
                    itemView.findViewById(
                            R.id.txtFeed
                    );

            quantity =
                    itemView.findViewById(
                            R.id.txtQuantity
                    );

            amount =
                    itemView.findViewById(
                            R.id.txtAmount
                    );

            status =
                    itemView.findViewById(
                            R.id.txtStatus
                    );

            stock =
                    itemView.findViewById(
                            R.id.txtStock
                    );

            approve =
                    itemView.findViewById(
                            R.id.btnApprove
                    );

            reject =
                    itemView.findViewById(
                            R.id.btnReject
                    );

            buttonLayout =
                    itemView.findViewById(
                            R.id.buttonLayout
                    );
        }
    }
}