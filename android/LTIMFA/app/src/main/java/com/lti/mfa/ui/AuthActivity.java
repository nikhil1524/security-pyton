package com.lti.mfa.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lti.mfa.R;
import com.lti.mfa.data.AuthRepository;

public class AuthActivity extends AppCompatActivity {

    private int userId = 0;
    private String userName = "";
    private String sessionId = "";
    EditText editTextOTP = null;
    Button buttonAuthenticate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Intent myIntent = getIntent(); // gets the previously created intent
        this.userId = myIntent.getIntExtra("userId",0);
        this.userName = myIntent.getStringExtra("userName");
        this.sessionId = myIntent.getStringExtra("sessionId");

        editTextOTP = findViewById(R.id.editTextOTP);
        buttonAuthenticate = findViewById(R.id.buttonAuth);

        buttonAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = editTextOTP.getText().toString();

                if((otp != null) && (otp.length() == 4)) {
                    AuthRepository.getAuthRepository().setAuthActivity(AuthActivity.this);
                    AuthRepository.getAuthRepository().setUserName(userName);
                    AuthRepository.getAuthRepository().authenticate(userId, sessionId, otp);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter 4 digit OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
