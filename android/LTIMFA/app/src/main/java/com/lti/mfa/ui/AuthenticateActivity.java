package com.lti.mfa.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lti.mfa.R;
import com.lti.mfa.data.AuthRepository;
import com.lti.mfa.data.AuthenticateRepository;
import com.lti.mfa.data.LoginRepository;
import com.lti.mfa.util.FingerprintHandler;
import com.lti.mfa.util.UploadImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AuthenticateActivity extends AppCompatActivity {

    final Context context = this;
    private int userId = 0;
    private String userName = "";
    private String sessionId = "";
    Boolean otpReq = false;
    Boolean faceReq = false;
    Boolean irisReq = false;
    Boolean fingerReq = false;
    Boolean otpInputCaptured = false;
    Boolean faceInputCaptured = false;
    Boolean irisInputCaptured = false;
    Boolean fingerInputCaptured = false;
    TextView authInputTitle = null;
    EditText otpInput = null;
    public ImageView faceImgView = null;
    //ImageView irisImgView = null;
    Button buttonOTP = null;
    Button buttonFace = null;
    Button buttonIris = null;
    Button buttonFinger = null;
    Button buttonAuthenticate1 = null;
    String otpValue = "";
    Bitmap faceImage;
    Bitmap irisImage;
    int fingerPrintResult = 0;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private File output = null;
    String authInputType = "";
    int finalOutput = 0;
    int inProgressOutPut = 0;

    private static final String KEY_NAME = "test";
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintHandler fingerPrintHelper;

    UploadImage uploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!LoginRepository.getLoginRepository().isUserLoggedIn()){
            reset();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        //defining the views
        buttonOTP = findViewById(R.id.buttonOTP1);
        buttonFace = findViewById(R.id.buttonFace);
        ;
        buttonIris = findViewById(R.id.buttonIris);
        ;
        buttonFinger = findViewById(R.id.buttonFinger);
        buttonAuthenticate1 = findViewById(R.id.buttonAuthenticate1);
        buttonAuthenticate1.setText("OK");
        buttonAuthenticate1.setVisibility(View.INVISIBLE);

        authInputTitle = findViewById(R.id.textViewAuthInput);
        otpInput = findViewById(R.id.textViewOTP1);
        faceImgView = findViewById(R.id.imageViewFace);

        //initialize as blank and hidden
        authInputTitle.setText("");
        authInputTitle.setVisibility(View.INVISIBLE);
        otpInput.setText("");
        otpInput.setVisibility(View.INVISIBLE);
        faceImgView.setImageBitmap(null);
        faceImgView.setVisibility(View.INVISIBLE);
        //irisImgView.setImageBitmap(null);
        //irisImgView.setVisibility(View.INVISIBLE);

        Intent myIntent = getIntent();
        otpReq = myIntent.getBooleanExtra("otpReq", false);
        faceReq = myIntent.getBooleanExtra("faceReq", false);
        irisReq = myIntent.getBooleanExtra("irisReq", false);
        fingerReq = myIntent.getBooleanExtra("fingerReq", false);
        this.userId = myIntent.getIntExtra("userId", 0);
        this.userName = myIntent.getStringExtra("userName");
        this.sessionId = myIntent.getStringExtra("sessionId");

        //OTP : 1 , FingerPrint : 2 , Face : 4 , Iris :  8
        if (otpReq) {
            finalOutput = finalOutput + 1;
        } else {
            buttonOTP.setVisibility(View.INVISIBLE);
            buttonOTP.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        if (faceReq) {
            finalOutput = finalOutput + 4;
        } else {
            buttonFace.setVisibility(View.INVISIBLE);
            buttonFace.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        if (irisReq) {
            finalOutput = finalOutput + 8;
        } else {
            buttonIris.setVisibility(View.INVISIBLE);
            buttonIris.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        if (fingerReq) {
            finalOutput = finalOutput + 2;
        } else {
            buttonFinger.setVisibility(View.INVISIBLE);
            buttonFinger.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

        buttonAuthenticate1.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View arg0) {

                buttonAuthenticate1.setVisibility(View.VISIBLE);

                if (buttonAuthenticate1.getText().toString().equalsIgnoreCase("authenticate")) {
                    //Initiating the Authentication process
                    authInputTitle.setText("Authentication in progress");
                    authInputTitle.setVisibility(View.VISIBLE);
                    faceImgView.setVisibility(View.INVISIBLE);
                    // irisImgView.setVisibility(View.INVISIBLE);
                    otpInput.setVisibility(View.INVISIBLE);
                    //Call API to authenticate data
                    //uploadImage = new UploadImage("", faceImage, AuthenticateActivity.this);
                    //uploadImage.startUpload();//This process also trigger Authentication
                    boolean readyToAuthenticate = true;

                    if (otpReq && (otpValue == null || (otpValue != null && otpValue.length() != 4))) {
                        readyToAuthenticate = false;
                        Toast.makeText(getApplicationContext(), "Please enter 4 digit OTP", Toast.LENGTH_SHORT).show();
                    }

                    if (fingerReq && fingerPrintResult == 2){
                            readyToAuthenticate = false;
                            Intent intent = new Intent(AuthenticateActivity.this, HomeActivity.class);
                            intent.putExtra("username", "");
                            intent.putExtra("authmsg", "Authentication failed");
                            intent.putExtra("autherr", "FingerPrint Scan failed");
                            AuthenticateActivity.this.startActivity(intent);
                    }
                    if (fingerReq && fingerPrintResult == 1 && !otpReq){
                        readyToAuthenticate = false;
                        Intent intent = new Intent(AuthenticateActivity.this, HomeActivity.class);
                        intent.putExtra("username", userName);
                        intent.putExtra("authmsg", "Authentication successful");
                        intent.putExtra("autherr", "");
                        AuthenticateActivity.this.startActivity(intent);
                    }

                    if (fingerReq && fingerPrintResult == 0){
                        Toast.makeText(getApplicationContext(), "Please complete fingerprint scan", Toast.LENGTH_SHORT).show();
                    }

                    if(readyToAuthenticate){
                        AuthenticateRepository.getAuthRepository().setAuthActivity(AuthenticateActivity.this);
                        AuthenticateRepository.getAuthRepository().setUserName(userName);
                        AuthenticateRepository.getAuthRepository().authenticate(userId, sessionId, otpValue, otpReq, faceReq, irisReq , fingerReq);
                    }

                } else {
                    boolean inputError = false;

                    buttonAuthenticate1.setText("OK");
                    //Change appearance as per user Input
                    if (authInputType.equals("otp")) {
                        otpValue = otpInput.getText().toString().trim();
                        if (otpValue.length() != 4) {
                            otpValue = "";
                            Toast.makeText(AuthenticateActivity.this, "OTP should be 4 digit only", Toast.LENGTH_LONG).show();
                            inputError = true;
                        } else {
                            if(!otpInputCaptured) {
                                inProgressOutPut = inProgressOutPut + 1;
                                otpInputCaptured = true;
                                //buttonOTP.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                            //isAllInputReceived();
                        }
                    } else if (authInputType.equals("face")) {

                    } else if (authInputType.equals("iris")) {

                    } else if (authInputType.equals("finger")) {
                        fingerPrintResult = FingerprintHandler.getResult();
                        if (fingerPrintResult == 0) {
                            Toast.makeText(AuthenticateActivity.this, "Finger Print not captured. " +
                                    "Please place you finger on finger print scanner.", Toast.LENGTH_LONG).show();
                            inputError = true;
                        } else {
                            if(!fingerInputCaptured) {
                                inProgressOutPut = inProgressOutPut + 2;
                                fingerInputCaptured = true;
                                //buttonFinger.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                            fingerPrintHelper.stopFingerAuth();
                        }
                    }

                    if ((!inputError) && (!isAllInputReceived())) {
                        buttonAuthenticate1.setVisibility(View.INVISIBLE);
                        authInputTitle.setVisibility(View.INVISIBLE);
                        faceImgView.setVisibility(View.INVISIBLE);
                        // irisImgView.setVisibility(View.INVISIBLE);
                        otpInput.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });


        buttonOTP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                faceImgView.setImageBitmap(null);
                faceImgView.setVisibility(View.INVISIBLE);

                authInputTitle.setText("Enter OTP");
                authInputTitle.setVisibility(View.VISIBLE);

                otpInput.setText("");
                otpInput.setHint("OTP");
                otpInput.setVisibility(View.VISIBLE);
                buttonAuthenticate1.setVisibility(View.VISIBLE);
                authInputType = "otp";
            }
        });


        buttonFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                authInputTitle.setText("Face Authentication");
                faceImgView.setVisibility(View.VISIBLE);
                if (faceImage != null) {
                    faceImgView.setImageBitmap(faceImage);
                } else {
                    faceImgView.setImageResource(R.drawable.face_scan_logo);
                }

                authInputTitle.setVisibility(View.VISIBLE);
                // irisImgView.setVisibility(View.INVISIBLE);
                otpInput.setVisibility(View.INVISIBLE);
                buttonAuthenticate1.setVisibility(View.VISIBLE);
                //buttonAuthenticate1.setText("Capture Face");
                authInputType = "face";

                //Checking for the camera permission whether granted or not
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AuthenticateActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = getCacheDir();
                    output = new File(dir, "face.jpeg");
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        buttonIris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                authInputTitle.setText("Iris Scan");
                faceImgView.setVisibility(View.VISIBLE);
                if (irisImage != null) {
                    faceImgView.setImageBitmap(irisImage);
                } else {
                    faceImgView.setImageResource(R.drawable.iris_scan_logo);
                }
                authInputTitle.setVisibility(View.VISIBLE);
                authInputTitle.setText("Iris Scan");
                otpInput.setVisibility(View.INVISIBLE);
                buttonAuthenticate1.setVisibility(View.VISIBLE);
                authInputType = "iris";

                //Checking for the camera permission whether granted or not
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AuthenticateActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = getCacheDir();

                    output = new File(dir, "iris.jpeg");
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }

            }
        });


        buttonFinger.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                authInputTitle.setText("Finger Print");
                faceImgView.setVisibility(View.VISIBLE);
                faceImgView.setImageResource(R.drawable.finger_icon1);
                //if (fingerPrintResult == 0) {//not received finger scan
                  //  faceImgView.setImageResource(R.drawable.finger_icon1);
                //} else {
                 //   faceImgView.setImageResource(R.drawable.finger_icon2);
                //}
                authInputTitle.setVisibility(View.VISIBLE);
                otpInput.setVisibility(View.INVISIBLE);
                buttonAuthenticate1.setVisibility(View.VISIBLE);
                authInputType = "finger";

                keyguardManager =
                        (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                fingerprintManager =
                        (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                if (!keyguardManager.isKeyguardSecure()) {

                    Toast.makeText(AuthenticateActivity.this,
                            "Lock screen security not enabled in Settings",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (ActivityCompat.checkSelfPermission(AuthenticateActivity.this,
                        Manifest.permission.USE_FINGERPRINT) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AuthenticateActivity.this,
                            "Fingerprint authentication permission not enabled",
                            Toast.LENGTH_LONG).show();

                    return;
                }

                if (!fingerprintManager.hasEnrolledFingerprints()) {

                    // This happens when no fingerprints are registered.
                    Toast.makeText(AuthenticateActivity.this,
                            "Register at least one fingerprint in Settings",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                generateKey();

                if (cipherInit()) {
                    cryptoObject =
                            new FingerprintManager.CryptoObject(cipher);
                    AuthenticateActivity.this.fingerPrintHelper = new FingerprintHandler(AuthenticateActivity.this);
                    AuthenticateActivity.this.fingerPrintHelper.startAuth(fingerprintManager, cryptoObject, AuthenticateActivity.this);
                    Toast.makeText(AuthenticateActivity.this,
                            "Place your finger on fingerprint scanner",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            if (authInputType.equalsIgnoreCase("face")) {
                faceImage = (Bitmap) data.getExtras().get("data");
                faceImgView.setImageBitmap(faceImage);
                faceImgView.setVisibility(View.VISIBLE);
            } else if (authInputType.equalsIgnoreCase("iris")) {
                irisImage = (Bitmap) data.getExtras().get("data");
                faceImgView.setImageBitmap(irisImage);
                faceImgView.setVisibility(View.VISIBLE);
            }
            //imageView.setImageBitmap(photo);
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String imgType) {
        // path to /data/data/yourapp/app_data/imageDir
        File directory = context.getCacheDir();
        // Create imageDir
        File imgPath = new File(directory, imgType + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path, String imgType) {

        try {
            File f = new File(path, imgType + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private boolean isAllInputReceived() {

        //OTP : 1 , FingerPrint : 2 , Face : 4 , Iris :  8
        if (finalOutput == inProgressOutPut) {
            buttonAuthenticate1.setText("Authenticate");
            buttonAuthenticate1.setVisibility(View.VISIBLE);
            authInputTitle.setText("Input ready. Press Authenticate now.");
            authInputTitle.setVisibility(View.VISIBLE);
            faceImgView.setVisibility(View.INVISIBLE);
            otpInput.setVisibility(View.INVISIBLE);
            Log.i("Output match : ", finalOutput + "::" + inProgressOutPut);
            return true;
        } else {
            return false;
        }
    }

    public void onBackPressed() {
        //put the AlertDialog code here
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Would you like to Logout?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LoginRepository.getLoginRepository().setUserLoggedIn(false);
                    Intent j = new Intent(AuthenticateActivity.this, MainActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Intent j = null;

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
                            Intent j = new Intent(AuthenticateActivity.this, MainActivity.class);
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

                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    private void reset(){
        userId = 0;
        userName = "";
        sessionId = "";
        otpReq = false;
        faceReq = false;
        irisReq = false;
        fingerReq = false;
        otpInputCaptured = false;
        faceInputCaptured = false;
        irisInputCaptured = false;
        fingerInputCaptured = false;

        otpValue = "";
        faceImage = null;
        irisImage = null;
        fingerPrintResult = 0;
        File output = null;
        authInputType = "";
        finalOutput = 0;
        inProgressOutPut = 0;

        fingerprintManager = null;
        keyguardManager = null;
        keyStore = null;
        keyGenerator = null;
        cipher = null;
        cryptoObject = null;
    }
}
