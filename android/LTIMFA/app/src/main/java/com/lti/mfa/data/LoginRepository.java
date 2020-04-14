package com.lti.mfa.data;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lti.mfa.api.LoginAPI;
import com.lti.mfa.api.OtpAPI;
import com.lti.mfa.data.model.Session;
import com.lti.mfa.data.model.User;
import com.lti.mfa.ui.AuthActivity;
import com.lti.mfa.ui.AuthenticateActivity;
import com.lti.mfa.ui.HomeActivity;
import com.lti.mfa.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginRepository {

    private static volatile LoginRepository instance;

    private boolean userLoggedIn = false;
    private String response;
    private boolean responseReceived = false;

    private String otpResponse;
    private boolean otpResponseReceived = false;

    private int userId = 0;
    private String userName = "";
    private String userEmail = "";
    private int responseStatus = 1;
    private int otpResponseStatus = 1;
    private String sessionId = "";
    private String errorDetails = "";
    private static volatile Session session = null;
    private MainActivity mainactivity = null;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "https://www.pawankpandey.info/loginapinew.php";

    public void setMainActivity(MainActivity mainactivity){
        this.mainactivity = mainactivity;
    }

    private LoginRepository(){
    }

    public static LoginRepository getLoginRepository() {
        if (instance != null) {
        } else {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }
    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    public void setOtpResponse(String otpResponse) {
        this.otpResponse = otpResponse;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived = responseReceived;
        if (responseReceived) {
           // try{
                //Log.i("Login Response", " response");
            String[] tuples = response.split(":");
            HashMap<String, String> result = new HashMap<String, String>();

            for (String pair: tuples) {
                String[] pairpart = pair.split("#");
                result.put(pairpart[0], pairpart[1]);
            }

            responseStatus = Integer.parseInt(result.get("status"));
                if (responseStatus == 0) {
                    //mainactivity.textErrMsg.setVisibility(View.VISIBLE);
                    //mainactivity.textErrMsg.setText("Login successful");
                    userId = Integer.parseInt(result.get("user_id"));
                    userName = result.get("user_name");
                    userEmail = result.get("email_id");
                    sessionId = result.get("session_id");
                    //mainactivity.textErrMsg.setText("Generating OTP");
                    //generateOTP(userId);


                    if(mainactivity.switchOTPVal || mainactivity.switchFaceVal || mainactivity.switchIrisVal || mainactivity.switchFingerVal) {
                        Intent intent = new Intent(mainactivity, AuthenticateActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("userName", userName);
                        intent.putExtra("sessionId", sessionId);
                        intent.putExtra("otpReq", mainactivity.switchOTPVal);
                        intent.putExtra("faceReq", mainactivity.switchFaceVal);
                        intent.putExtra("irisReq", mainactivity.switchIrisVal);
                        intent.putExtra("fingerReq", mainactivity.switchFingerVal);
                        mainactivity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mainactivity, HomeActivity.class);
                        intent.putExtra("authmsg", "Authentication Successful");
                        intent.putExtra("username", userName);
                        intent.putExtra("autherr", "");

                        mainactivity.startActivity(intent);
                    }
                } else {
                    mainactivity.textErrMsg.setVisibility(View.VISIBLE);
                    mainactivity.textErrMsg.setText("Invalid User Id or Password");
                    errorDetails = result.get("error_desc");
                 }
        }
    }

    public void setOtpResponseReceived(boolean otpResponseReceived) {
        this.otpResponseReceived = otpResponseReceived;
        if (otpResponseReceived) {
          //  try{
               // Log.i("OTP Response", otpResponse );
                //JSONObject jsonObject = new JSONObject(otpResponse);
                String[] tuples = otpResponse.split(":");
                HashMap<String, String> result = new HashMap<String, String>();

                for (String pair: tuples) {
                    String[] pairpart = pair.split("#");
                    result.put(pairpart[0], pairpart[1]);
                }

                otpResponseStatus = Integer.parseInt(result.get("status"));
                if (otpResponseStatus == 0) {
                    sessionId = result.get("session_id");

                    //mainactivity.textErrMsg.setVisibility(View.VISIBLE);
                    //mainactivity.textErrMsg.setText("OTP successfully generated");
                   Intent intent = new Intent(mainactivity, AuthenticateActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userName", userName);
                    intent.putExtra("sessionId", sessionId);
                    intent.putExtra("otpReq", mainactivity.switchOTPVal);
                    intent.putExtra("faceReq", mainactivity.switchFaceVal);
                    intent.putExtra("irisReq", mainactivity.switchIrisVal);
                    intent.putExtra("fingerReq", mainactivity.switchFingerVal);
                    mainactivity.startActivity(intent);
                } else {
                    //mainactivity.textErrMsg.setVisibility(View.VISIBLE);
                    //mainactivity.textErrMsg.setText("Failed to generate OTP");
                   //errorDetails = jsonObject.getString("desc");
                }
           // } catch (JSONException e) {
            //    this.setResponse("-2");  //Json parsing failed
            //}
        }
    }


    public void logout() {
        session = null;
    }

    public void login(String mobileNo, String password) {

        User user = new User(mobileNo, password);
        //sendAndRequestResponse(mobileNo, password);
        new LoginAPI(user).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void generateOTP(int userId){
        new OtpAPI(userId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendAndRequestResponse(String mobileNo, String password) {

        final String username = mobileNo;
        final String passwordVal = password;
                //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(mainactivity);

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(mainactivity.getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.i("","Error :" + error.toString());
                Toast.makeText(mainactivity.getApplicationContext(),"Response :" + error.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("mobileNo", username);
                params.put("password", passwordVal);
                return params;
            }
        };

        mRequestQueue.add(mStringRequest);
    }
}
