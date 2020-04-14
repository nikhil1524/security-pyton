package com.lti.mfa.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.lti.mfa.R;
import com.lti.mfa.data.LoginRepository;

public class HomeActivity extends AppCompatActivity {

    TextView welcomeMsg = null;
    TextView userName = null;
    TextView authMsg = null;
    TextView autErr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent myIntent = getIntent(); // gets the previously created intent
        String userNameText = myIntent.getStringExtra("username");
        String authMsgText = myIntent.getStringExtra("authmsg");
        String authErrText = myIntent.getStringExtra("autherr");

        userName = findViewById(R.id.textViewUserName);
        userName.setText(userNameText);
        authMsg = findViewById(R.id.textViewAuthMsg);
        authMsg.setText(authMsgText);
        autErr = findViewById(R.id.textViewErrDetail);
        autErr.setText(authErrText);

        if(userNameText != null && userNameText.length() > 0) {
            welcomeMsg = findViewById(R.id.textViewWelcome);
            welcomeMsg.setText("Welcome");
        } else {
            welcomeMsg = findViewById(R.id.textViewWelcome);
            welcomeMsg.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent j = null;

        switch(item.getItemId()) {
            case R.id.logout:
                LoginRepository.getLoginRepository().setUserLoggedIn(false);

                //put the AlertDialog code here
                new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Would you like to Logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LoginRepository.getLoginRepository().setUserLoggedIn(false);
                            Intent j = new Intent(HomeActivity.this, MainActivity.class);
                            startActivity(j);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LoginRepository.getLoginRepository().setUserLoggedIn(true);
                            // user doesn't want to logout
                        }
                    })
                    .show();
                //this.finishAffinity();
                //return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    public void onBackPressed() {
        //put the AlertDialog code here
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Would you like to Logout?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LoginRepository.getLoginRepository().setUserLoggedIn(false);
                    Intent j = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(j);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LoginRepository.getLoginRepository().setUserLoggedIn(true);
                    // user doesn't want to logout
                }
            })
            .show();
    }
}
