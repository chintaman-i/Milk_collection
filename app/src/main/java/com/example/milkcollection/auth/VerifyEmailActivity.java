package com.example.milkcollection.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.splash.SplashActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    TextView emailText, timerText;
    Button resendBtn, loginBtn, changeEmailBtn, checkBtn;

    FirebaseAuth auth;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        emailText = findViewById(R.id.emailText);
        timerText = findViewById(R.id.timerText);

        resendBtn = findViewById(R.id.resendBtn);
        loginBtn = findViewById(R.id.loginBtn);
        changeEmailBtn = findViewById(R.id.changeEmailBtn);
        checkBtn = findViewById(R.id.checkBtn);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            emailText.setText(user.getEmail());
        }

        startTimer(); // start timer initially

        resendBtn.setOnClickListener(v -> resendEmail());

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        changeEmailBtn.setOnClickListener(v -> changeEmail());

        checkBtn.setOnClickListener(v -> checkVerification());
    }

    private void resendEmail(){

        FirebaseUser user = auth.getCurrentUser();

        if(user != null){

            user.sendEmailVerification()
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(this,
                                "Verification email sent",
                                Toast.LENGTH_SHORT).show();

                        startTimer(); // restart timer

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();

                    });
        }
    }

    private void startTimer(){

        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        resendBtn.setEnabled(false);

        countDownTimer = new CountDownTimer(60000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;
                timerText.setText("Resend available in " + seconds + " sec");

            }

            @Override
            public void onFinish() {

                resendBtn.setEnabled(true);
                timerText.setText("You can resend email now");

            }

        }.start();
    }

    private void changeEmail(){

        // 🔥 No Intent data needed anymore
        // SignupActivity will fetch from Firestore

        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkVerification(){

        FirebaseUser user = auth.getCurrentUser();

        if(user == null) return;

        user.reload().addOnCompleteListener(task -> {

            if(user.isEmailVerified()){

                startActivity(new Intent(this, SplashActivity.class));
                finish();

            }else{

                Toast.makeText(this,
                        "Email not verified yet",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}