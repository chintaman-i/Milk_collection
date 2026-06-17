package com.example.milkcollection.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageUsersActivity extends AppCompatActivity {

    ListView listView;

    FirebaseFirestore db;

    ArrayList<String> displayList;
    ArrayList<String> uidList;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        listView = findViewById(R.id.listView);

        db = FirebaseFirestore.getInstance();

        displayList = new ArrayList<>();
        uidList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );

        listView.setAdapter(adapter);

        loadUsers();

        listView.setOnItemClickListener((parent, view, position, id) -> {

            String uid = uidList.get(position);

            showActionDialog(uid);
        });
    }

    private void loadUsers() {

        db.collection("users")
                .whereEqualTo("accountStatus", "pending")
                .get()
                .addOnSuccessListener(query -> {

                    displayList.clear();
                    uidList.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        String name =
                                doc.getString("firstName")
                                        + " "
                                        + doc.getString("lastName");

                        String email = doc.getString("email");

                        displayList.add(name + "\n" + email);

                        uidList.add(doc.getId());
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void showActionDialog(String uid) {

        String[] options = {"Approve", "Reject", "Block"};

        new AlertDialog.Builder(this)
                .setTitle("Select Action")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        approveUser(uid);

                    } else if (which == 1) {

                        updateStatus(uid, "rejected");

                    } else if (which == 2) {

                        updateStatus(uid, "blocked");
                    }

                })
                .show();
    }

    private void approveUser(String uid) {

        db.collection("users")
                .orderBy("farmerId")
                .limitToLast(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int nextFarmerId = 1;

                    if (!queryDocumentSnapshots.isEmpty()) {

                        Long lastIdLong =
                                queryDocumentSnapshots
                                        .getDocuments()
                                        .get(0)
                                        .getLong("farmerId");

                        if (lastIdLong != null) {
                            nextFarmerId = lastIdLong.intValue() + 1;
                        }
                    }

                    Map<String, Object> updates = new HashMap<>();

                    updates.put("accountStatus", "approved");
                    updates.put("farmerId", nextFarmerId);

                    final int finalFarmerId = nextFarmerId;

                    db.collection("users")
                            .document(uid)
                            .update(updates)
                            .addOnSuccessListener(unused -> {

                                String formattedId =
                                        String.format("%04d", finalFarmerId);

                                Toast.makeText(
                                        this,
                                        "Approved. Farmer ID: " + formattedId,
                                        Toast.LENGTH_LONG
                                ).show();

                                loadUsers();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void updateStatus(String uid, String status) {

        db.collection("users")
                .document(uid)
                .update("accountStatus", status)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Updated",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadUsers();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}