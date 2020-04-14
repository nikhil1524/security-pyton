package com.lti.mfa.api;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.lti.mfa.data.LoginRepository;
import com.lti.mfa.data.model.User;
import com.lti.mfa.security.EncryptionUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class OtpAPI extends AsyncTask<Void, Void, String> {

    private String userId;

    public OtpAPI(int userId) {
        this.userId = String.valueOf(userId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection httpURLConnection = null;
        String text = null;

        try {
            URL uri = new URL("http://www.pawankpandey.info/otpuri.php");
            httpURLConnection = (HttpURLConnection) uri.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            HashMap<String, String> inputParam = new HashMap<String, String>();
            inputParam.put("userid", this.userId);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(inputParam));
            writer.flush();
            writer.close();
            os.close();

            int statusCode = httpURLConnection.getResponseCode();
            if (statusCode != HttpsURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader isReader = new InputStreamReader(inputStream);
            //Creating a BufferedReader object
            BufferedReader reader = new BufferedReader(isReader);
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            text = sb.toString();
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
        return text;
    }

    @Override
    protected void onPostExecute(String results) {
        if (results != null && results.length() > 0) {
            LoginRepository.getLoginRepository().setOtpResponse(results);
            LoginRepository.getLoginRepository().setOtpResponseReceived(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        EncryptionUtil.setKey("MFA123");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            // result.append(EncryptionUtil.encrypt(URLEncoder.encode(entry.getValue(), "UTF-8"), "MFASecure"));
        }

        return result.toString();
    }


}
