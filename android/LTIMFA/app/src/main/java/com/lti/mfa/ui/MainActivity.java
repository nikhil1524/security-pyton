package com.lti.mfa.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lti.mfa.R;
import com.lti.mfa.data.LoginRepository;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin = null;
    EditText textEditMobile = null;
    EditText textEditPassword = null;
    public TextView textErrMsg = null;
    Switch switchOtp = null;
    Switch switchFace = null;
    Switch switchIris = null;
    Switch switchFinger = null;
    public Boolean switchOTPVal = false;
    public Boolean switchFingerVal = false;
    public Boolean switchFaceVal = false;
    public Boolean switchIrisVal = false;
    public static int backBtnPressed =  0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(backBtnPressed == 1){
            backBtnPressed = 0;
            finish();
        }

        buttonLogin = findViewById(R.id.btnLogin);
        textEditMobile = findViewById(R.id.editTextMobile);
        textEditPassword = findViewById(R.id.editTextPasswd);
        textErrMsg = findViewById(R.id.textViewErrorMsg);
        textErrMsg.setVisibility(View.INVISIBLE);

        textEditMobile.setText("");
        textEditPassword.setText("");

        switchOtp = findViewById(R.id.btnSwitchOtp);
        switchFace = findViewById(R.id.btnSwitchFace);
        switchIris = findViewById(R.id.btnSwitchIris);
        switchFinger = findViewById(R.id.btnSwitchFinger);
        switchFace.setEnabled(false);
        switchIris.setEnabled(false);

        if(!LoginRepository.getLoginRepository().isUserLoggedIn()){
            switchOtp.setChecked(false);
            switchFace.setChecked(false);
            switchIris.setChecked(false);
            switchFinger.setChecked(false);
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobileText = textEditMobile.getText().toString();
                String passwordText = textEditPassword.getText().toString();
                switchOTPVal = switchOtp.isChecked();
                switchFingerVal = switchFinger.isChecked();
                switchFaceVal = switchFace.isChecked();
                switchIrisVal = switchIris.isChecked();

                if(mobileText != null && mobileText.length() == 10) {
                    if(passwordText != null && passwordText.length() > 0) {
                        LoginRepository.getLoginRepository().setMainActivity(MainActivity.this);
                        LoginRepository.getLoginRepository().login(mobileText, passwordText);
                        LoginRepository.getLoginRepository().setUserLoggedIn(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onBackPressed() {
        //put the AlertDialog code here
            Toast.makeText(MainActivity.this,
                    "Press Back button once more to exit iShield",
                    Toast.LENGTH_LONG).show();
            //Intent i = new Intent(MainActivity.this, MainActivity.class);
            //startActivity(i);


        if(backBtnPressed == 1) {
            Toast.makeText(MainActivity.this,
                    "Exiting iShield now",
                    Toast.LENGTH_LONG).show();
            backBtnPressed = 2;
            MainActivity.this.finishAffinity();
            //this.finish();
        }
        backBtnPressed = 1;
    }
}
