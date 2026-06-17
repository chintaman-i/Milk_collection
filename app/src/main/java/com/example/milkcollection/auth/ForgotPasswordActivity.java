package com.example.milkcollection.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEdit;
    Button resetBtn, resendBtn, backToLoginBtn;
    TextView statusText, timerText;

    FirebaseAuth auth;
    CountDownTimer timer;

    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEdit = findViewById(R.id.emailEdit);
        resetBtn = findViewById(R.id.resetBtn);
        resendBtn = findViewById(R.id.resendBtn);
        backToLoginBtn = findViewById(R.id.backToLoginBtn);
        statusText = findViewById(R.id.statusText);
        timerText = findViewById(R.id.timerText);

        auth = FirebaseAuth.getInstance();

        String passedEmail = getIntent().getStringExtra("email");
        if (passedEmail != null) {
            emailEdit.setText(passedEmail);
        }

        resetBtn.setOnClickListener(v -> checkAndSendEmail());
        resendBtn.setOnClickListener(v -> sendResetEmail());

        backToLoginBtn.setOnClickListener(v -> finish());
    }

    private void checkAndSendEmail() {

        email = emailEdit.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Enter valid email");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        showAccountNotFoundDialog();
                        return;
                    }

                    auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful() && task.getResult() != null) {

                                    List<String> methods = task.getResult().getSignInMethods();

                                    if (methods != null &&
                                            methods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)) {

                                        showGoogleAccountDialog();

                                    } else {
                                        sendResetEmail();
                                    }

                                } else {
                                    sendResetEmail();
                                }
                            });

                })
                .addOnFailureListener(e -> showError("Something went wrong"));
    }

    private void sendResetEmail() {

        // 🔥 PREVENT MULTIPLE TRIGGERS
        resetBtn.setEnabled(false);
        emailEdit.setEnabled(false);

        // 🔥 CANCEL OLD TIMER
        if (timer != null) {
            timer.cancel();
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {

                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText("Reset link sent to your email.\n\nCheck inbox or spam folder.");

                    startTimer();

                })
                .addOnFailureListener(e -> {

                    resetBtn.setEnabled(true);
                    emailEdit.setEnabled(true);

                    new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", null)
                            .show();
                });
    }

    private void startTimer() {

        resendBtn.setEnabled(false);
        timerText.setVisibility(View.VISIBLE);

        timer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                timerText.setText("Resend available in " + sec + " seconds");
            }

            @Override
            public void onFinish() {
                resendBtn.setEnabled(true);
                timerText.setText("You can resend reset email now");
            }
        };

        timer.start();
    }

    private void showAccountNotFoundDialog() {

        new AlertDialog.Builder(this)
                .setTitle("No Account Found")
                .setMessage("This email is not registered.\n\nPlease create a new account.")
                .setPositiveButton("Create Account", (dialog, which) ->
                        startActivity(new Intent(this, SignupActivity.class)))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showGoogleAccountDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Use Google Sign-In")
                .setMessage("This account was created using Google.\n\nPlease sign in using Google instead.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showError(String msg) {

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}