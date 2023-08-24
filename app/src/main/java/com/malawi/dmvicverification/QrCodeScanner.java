package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.cancelledDate;
import static com.malawi.dmvicverification.MainActivity.cancelledReason;
import static com.malawi.dmvicverification.MainActivity.certificateNum;
import static com.malawi.dmvicverification.MainActivity.chassisNum;
import static com.malawi.dmvicverification.MainActivity.checkGPSStatus;
import static com.malawi.dmvicverification.MainActivity.insuranceCompany;
import static com.malawi.dmvicverification.MainActivity.isNetworkConnected;
import static com.malawi.dmvicverification.MainActivity.loadlocale;
import static com.malawi.dmvicverification.MainActivity.make;
import static com.malawi.dmvicverification.MainActivity.model;
import static com.malawi.dmvicverification.MainActivity.policyEndDate;
import static com.malawi.dmvicverification.MainActivity.policyStartDate;
import static com.malawi.dmvicverification.MainActivity.status;
import static com.malawi.dmvicverification.MainActivity.unAuthorize;
import static com.malawi.dmvicverification.MainActivity.vehicleNum;
import static com.malawi.dmvicverification.MainActivity.yearOfManufacture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QrCodeScanner extends AppCompatActivity {


    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    ImageView flash;
    Context context;
    String selectedOption;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    public Camera camera;
    public static String intentData = "";
    boolean isEmail = false;
    Intent nextPage;
    ProgressDialog p;
    GradientDrawable drawable;

    Camera.Parameters params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context=this;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Scan Disc");
        Intent intent=getIntent();
        selectedOption=intent.getStringExtra("selectedOption");
        initViews();
    }



    private void initViews() {

        surfaceView = findViewById(R.id.surfaceView);
        ImageView flashoff = (ImageView) findViewById(R.id.imageViewflasfoff);
        ImageView flashon = (ImageView) findViewById(R.id.imageViewflason);


        flashon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashon.setVisibility(View.GONE);
                flashoff.setVisibility(View.VISIBLE);
                Field[] declaredFields = CameraSource.class.getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.getType() == Camera.class) {
                        field.setAccessible(true);
                        try {
                            Camera camera = (Camera) field.get(cameraSource);
                            if (camera != null) {
                                Camera.Parameters params = camera.getParameters();
                                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(params);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        });
        flashoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashon.setVisibility(View.VISIBLE);
                flashoff.setVisibility(View.GONE);
                Field[] declaredFields = CameraSource.class.getDeclaredFields();

                for (Field field : declaredFields) {
                    if (field.getType() == Camera.class) {
                        field.setAccessible(true);
                        try {
                            Camera camera = (Camera) field.get(cameraSource);
                            if (camera != null) {
                                Camera.Parameters params = camera.getParameters();
                                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(params);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        });
    }

    private void initialiseDetectorsAndSources() {
        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QrCodeScanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(QrCodeScanner.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    intentData = barcodes.valueAt(0).displayValue;
                    if(selectedOption.equals("vcPrintCode")){
                        verifyCertificate();
                        barcodeDetector.release();
                    }else if(selectedOption.equals("vcSecure")){
                        nextPage = new Intent(QrCodeScanner.this, VerifySecure.class);
                        startActivity(nextPage);
                        finish();
                    }else if(selectedOption.equals("vcCertificate")) {
                        nextPage = new Intent(QrCodeScanner.this, VerifyWithCertificate.class);
                        startActivity(nextPage);
                        finish();
                    }
                }
            }
        });
    }

    void verifyCertificate() {
        try {
            if (isNetworkConnected(context)) {
                if (checkGPSStatus(context)) {
                    //creating the thread to run operations in background thread.
                    Thread t = new Thread(() -> {

                        String token=null;
                        DataBaseHelper mydb = new DataBaseHelper(context);
                        if (mydb.getTokenDetails().getCount() != 0) {
                            Cursor curseattachfbtoken = mydb.getTokenDetails();
                            int countfbtoken = curseattachfbtoken.getCount();
                            if (countfbtoken >= 1) {
                                while (curseattachfbtoken.moveToNext()) {
                                    token = curseattachfbtoken.getString(0);
                                    System.out.println("Token is: "+token);
                                }
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    p = new ProgressDialog(context);
                                    p.setMessage("Loading");
                                    p.show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

                        String postUrl = getString(R.string.base_url)+"/api/digital/core/CertificateVerification/ValidatePrintInsurance";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        Details.addProperty("scanCode",intentData.trim());
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postUrl)
                                .header("Authorization", "Bearer " + token)
                                .header("clientInfo", MainActivity.InsertMobileParameters())
                                .header("fingerprint",MainActivity.uniqueidval)
                                .post(body)
                                .build();
                        System.out.println("token: "+token);
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        Response staticResponse;
                        try {
                            staticResponse = client.newCall(request).execute();
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, "qwertyuiopQr "+staticRes);
                            if(staticResponse.code()==200) {
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                if (staticJsonObj.getInt("rcode") == 200) {
                                    runOnUiThread(() -> {
                                        try {
                                            if(p.isShowing()){
                                                p.dismiss();
                                            }
                                            JSONObject insuranceDetails = staticJsonObj.getJSONObject("rObj").getJSONObject("insuranceDetails");
                                            certificateNum=insuranceDetails.getString("diskNo");
                                            vehicleNum=insuranceDetails.getString("registrationNumber");
                                            insuranceCompany=insuranceDetails.getString("insuranceCompany");
                                            chassisNum=insuranceDetails.getString("chassisNumber");
                                            SimpleDateFormat sf=new SimpleDateFormat("dd-MMM-yyyy");
                                            String startDate=insuranceDetails.getString("commencingDate");
                                            Date d=new Date(startDate);
                                            policyStartDate=sf.format(d);
                                            String endDate=insuranceDetails.getString("expiryDate");
                                            Date e=new Date(endDate);
                                            policyEndDate=sf.format(e);
                                            yearOfManufacture=insuranceDetails.getString("yearOfManufacture");
                                            make=insuranceDetails.getString("make");
                                            model=insuranceDetails.getString("model");
                                            status=insuranceDetails.getString("status");
                                            if(insuranceDetails.getBoolean("isCancelled")){
                                                cancelledDate="";
                                                cancelledReason="";// No cancelled date or reason given from response
                                                /*cancelledDate=insuranceDetails.getString("");
                                                cancelledReason=insuranceDetails.getString("");*/
                                            }else{
                                                cancelledDate="";
                                                cancelledReason="";
                                            }
                                            nextPage = new Intent(context,VerificationResult.class);
                                            startActivity(nextPage);
                                            finish();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });
                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(() -> {
                                        try{
                                            if(p.isShowing()){
                                                p.dismiss();
                                            }
                                            unAuthorize(context);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });
                                }else if (staticJsonObj.getInt("rcode") == 502 || staticJsonObj.getInt("rcode")==503 || staticJsonObj.getInt("rcode")==504) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (p.isShowing()) {
                                                    p.dismiss();
                                                }
                                                String errorCode,errorText;
                                                try {
                                                    JSONArray errorMsg = staticJsonObj.getJSONArray("rmsg");
                                                    JSONObject errorData = errorMsg.getJSONObject(0);
                                                    errorCode = errorData.getString("errorCode");
                                                    errorText = errorData.getString("errorText");
                                                }catch (Exception e){
                                                    errorCode="";
                                                    errorText=getString(R.string.cmn_wrong_msg);
                                                    e.printStackTrace();
                                                }
                                                if(errorText.equals("No data found")||errorCode.equals("ERR028")) {
                                                    status = "Invalid";
                                                    startActivity(new Intent(context, VerificationResult.class));
                                                }else{
                                                    alertTheUser(context,errorCode,errorText)
                                                            .setCancelable(false)
                                                            .setPositiveButton("Ok",((dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                            }
                                                            )).show();
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        if(p.isShowing()){
                                            p.dismiss();
                                        }
                                        alertTheUser(context,"",getString(R.string.cmn_wrong_msg))
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                        finish();
                                                    }
                                                }).show();
                                    });
                                }
                            }else if(staticResponse.code()==401){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(p.isShowing()){
                                            p.dismiss();
                                        }
                                        unAuthorize(context);
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            if(p.isShowing()){
                                                p.dismiss();
                                            }
                                            alertTheUser(context,"",getString(R.string.cmn_wrong_msg))
                                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                            finish();
                                                        }
                                                    }).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                } else {
                    runOnUiThread(() ->
                            alertTheUser(context, "", getString(R.string.gps_not_enabled))
                                    .setPositiveButton("Ok", (dialog, which) -> {
                                        //this will navigate user to the device location settings screen
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }).show());
                }
            } else {
                Toast.makeText(this, getString(R.string.noNetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
    @Override
    public void onBackPressed() {
        try {
            startActivity(new Intent(context, Dashboard.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}