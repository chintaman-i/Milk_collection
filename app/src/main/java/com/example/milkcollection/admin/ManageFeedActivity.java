package com.example.milkcollection.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManageFeedActivity extends AppCompatActivity {

    EditText price, stock;
    Spinner qualitySpinner;
    Button addFeedBtn;

    FirebaseFirestore db;

    String[] qualities = {
            "Basic Feed",
            "Standard Feed",
            "Premium Feed"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_feed);

        price = findViewById(R.id.price);
        stock = findViewById(R.id.stock);
        qualitySpinner = findViewById(R.id.qualitySpinner);
        addFeedBtn = findViewById(R.id.addFeedBtn);

        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        qualities
                );

        qualitySpinner.setAdapter(adapter);

        addFeedBtn.setOnClickListener(v -> saveFeed());
    }

    private void saveFeed() {

        String quality =
                qualitySpinner.getSelectedItem().toString();

        String priceText =
                price.getText().toString().trim();

        String stockText =
                stock.getText().toString().trim();

        if (priceText.isEmpty() || stockText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String docId;

        switch (quality) {

            case "Basic Feed":
                docId = "economy";
                break;

            case "Standard Feed":
                docId = "regular";
                break;

            default:
                docId = "premium";
                break;
        }

        Map<String, Object> feed =
                new HashMap<>();

        feed.put("feedName", quality);
        feed.put("pricePerKg",
                Long.parseLong(priceText));
        feed.put("stockKg",
                Long.parseLong(stockText));
        feed.put("available", true);

        db.collection("feed_stock")
                .document(docId)
                .set(feed)

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Feed Updated Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    price.setText("");
                    stock.setText("");
                })

                .addOnFailureListener(e ->

                        Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }
}