package com.example.milkcollection.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.model.FeedModel;

import java.util.ArrayList;

public class UserFeedOrderAdapter
        extends RecyclerView.Adapter<UserFeedOrderAdapter.ViewHolder> {

    Context context;
    ArrayList<FeedModel> list;

    public UserFeedOrderAdapter(
            Context context,
            ArrayList<FeedModel> list
    ) {
        this.context = context;
        this.list = list;
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

        holder.farmer.setVisibility(View.GONE);

        holder.feed.setText(
                "Feed: " + model.getFeedName()
        );

        holder.quantity.setText(
                "Quantity: " + model.getQuantity()
        );

        holder.amount.setText(
                "Amount: ₹" + model.getTotalAmount()
        );

        String status = model.getOrderStatus();

        if(status == null)
            status = "pending";

        holder.status.setText(
                status.toUpperCase()
        );

        holder.buttonLayout.setVisibility(
                View.GONE
        );

        if(status.equalsIgnoreCase("approved")) {

            holder.status.setTextColor(
                    Color.GREEN
            );

        } else if(status.equalsIgnoreCase("rejected")) {

            holder.status.setTextColor(
                    Color.RED
            );

        } else {

            holder.status.setTextColor(
                    Color.parseColor("#FF9800")
            );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView farmer,
                feed,
                quantity,
                amount,
                status;

        View buttonLayout;

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

            buttonLayout =
                    itemView.findViewById(
                            R.id.buttonLayout
                    );
        }
    }
}