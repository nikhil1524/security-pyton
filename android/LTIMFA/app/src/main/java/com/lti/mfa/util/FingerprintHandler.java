package com.lti.mfa.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.lti.mfa.R;
import com.lti.mfa.ui.AuthenticateActivity;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;
    private static int resultVal = 0;
    AuthenticateActivity authenticateActivity;

    public FingerprintHandler(Context context) {
        appContext = context;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject, AuthenticateActivity authenticateActivity) {

        this.authenticateActivity = authenticateActivity;
        cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    public void stopFingerAuth(){
        if(cancellationSignal != null && !cancellationSignal.isCanceled()){
            cancellationSignal.cancel();
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {

       // Toast.makeText(appContext, "Authentication error\n" + errString, Toast.LENGTH_SHORT).show();
        authenticateActivity.faceImgView.setImageResource(R.drawable.finger_icon2);
        resultVal = 2;
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        // Toast.makeText(appContext, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(appContext, "Unknown Finger Print", Toast.LENGTH_SHORT).show();
        authenticateActivity.faceImgView.setImageResource(R.drawable.finger_icon2);
        resultVal = 2;
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        Toast.makeText(appContext, "Finger Print verified", Toast.LENGTH_SHORT).show();
        authenticateActivity.faceImgView.setImageResource(R.drawable.finger_icon2);
        //if (fingerPrintResult == 0) {//not received finger scan
         //   faceImgView.setImageResource(R.drawable.finger_icon1);
        //}/ else {
        //    faceImgView.setImageResource(R.drawable.finger_icon2);
        //}
        resultVal = 1;
    }

    public static int getResult(){
        return resultVal;
    }
}
