package com.lti.mfa.data;

import android.content.Intent;
import android.util.Log;

import com.lti.mfa.api.AuthAPI;
import com.lti.mfa.data.model.Session;
import com.lti.mfa.ui.AuthActivity;
import com.lti.mfa.ui.HomeActivity;
import com.lti.mfa.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthRepository {

    private static volatile com.lti.mfa.data.AuthRepository instance;
    private String response;
    private boolean responseReceived = false;
    private static volatile Session session = null;
    private AuthActivity authActivity = null;
    private int responseStatus = 1;
    private String userName = "";

    public void setAuthActivity(AuthActivity authActivity){
        this.authActivity = authActivity;
    }

    private AuthRepository(){
    }

    public static com.lti.mfa.data.AuthRepository getAuthRepository() {
        if (instance != null) {
        } else {
            instance = new com.lti.mfa.data.AuthRepository();
        }
        return instance;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
        if (responseReceived) {
            try {
                Log.i("Authentication Response", response);
                JSONObject jsonObject = new JSONObject(response);
                responseStatus = Integer.parseInt(jsonObject.getString("status"));

                Intent intent = new Intent(authActivity, HomeActivity.class);
                if (responseStatus == 0) {
                    intent.putExtra("username", userName);
                    intent.putExtra("authmsg", "Authentication successful");
                    intent.putExtra("autherr", "");
                } else {
                    intent.putExtra("username", "");
                    intent.putExtra("authmsg", "Authentication failed");
                    intent.putExtra("autherr", jsonObject.getString("error_desc"));
                }
                authActivity.startActivity(intent);
            } catch (JSONException e) {
                this.setResponse("-2");  //Json parsing failed
            }
        }
    }

    public void logout() {
        session = null;
    }

    public void authenticate(int userId, String sessionId, String otp) {
        new AuthAPI(userId, sessionId, otp).execute();
    }

}
