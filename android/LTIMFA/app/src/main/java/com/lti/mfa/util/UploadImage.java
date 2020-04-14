package com.lti.mfa.util;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lti.mfa.ui.AuthenticateActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadImage {

    AuthenticateActivity authenticateActivity;
    Bitmap bitmap;
    String URL;
    ProgressDialog progressDialog;

    public UploadImage(){}

    public UploadImage(String URL, Bitmap bitmap, AuthenticateActivity authenticateActivity){
        this.authenticateActivity  = authenticateActivity;
        this.bitmap = bitmap;
    }

    public void startUpload() {
        int PICK_IMAGE_REQUEST = 111;
        //String URL = "http://192.168.1.101/JavaRESTfullWS/DemoService/upload";
        //converting image to base64 string
        progressDialog = new ProgressDialog(authenticateActivity);
        progressDialog.setMessage("Uploading biometric data to server, please wait...");
        progressDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (s.equals("true")) {
                    Toast.makeText(authenticateActivity, "Uploaded Successful", Toast.LENGTH_LONG).show();
                    //start authentication now
                } else {
                    Toast.makeText(authenticateActivity, "Some error occurred!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(authenticateActivity, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                ;
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", imageString);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(authenticateActivity);
        rQueue.add(request);
    }
}
