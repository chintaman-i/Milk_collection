package com.example.milkcollection.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.model.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<UserModel> list;
    Context context;
    FirebaseFirestore db;

    public UserAdapter(ArrayList<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserModel user = list.get(position);

        holder.name.setText(user.getFirstName() + " " + user.getLastName());
        holder.email.setText(user.getEmail());

        holder.itemView.setOnClickListener(v -> showDialog(user));
    }

    private void showDialog(UserModel user){

        String[] options = {"Approve", "Reject", "Block"};

        new AlertDialog.Builder(context)
                .setTitle("Select Action")
                .setItems(options, (dialog, which) -> {

                    String status = "pending";

                    if(which == 0) status = "approved";
                    else if(which == 1) status = "rejected";
                    else if(which == 2) status = "blocked";

                    db.collection("users")
                            .document(user.getUid())
                            .update("accountStatus", status)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                            });

                })
                .show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
        }
    }
}