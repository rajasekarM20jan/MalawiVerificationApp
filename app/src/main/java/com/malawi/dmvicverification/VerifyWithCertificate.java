package com.malawi.dmvicverification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
import static com.malawi.dmvicverification.QrCodeScanner.intentData;

public class VerifyWithCertificate extends AppCompatActivity {

    Context context;
    EditText certificationNumber;
    Button Btnverify,Btnscan;
    ProgressDialog p;
    GradientDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_with_certificate);
        context=this;
        try {
            getSupportActionBar().setTitle(getString(R.string.vcCert));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex) {
            ex.getStackTrace();
        }

        init();
    }
    void init(){
        try{
            certificationNumber=findViewById(R.id.certificationNumber);
            Btnverify=findViewById(R.id.Btnverify);
            Btnscan=findViewById(R.id.Btnscan);
            basicFunctions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void basicFunctions(){
        try{
            Btnscan.setOnClickListener(l->onBackPressed());
            Btnverify.setOnClickListener(l->{
                try{
                    if(certificationNumber.length()<=0) {
                        try {
                            alertTheUser(context, "", getString(R.string.certNumAlert))
                                    .setPositiveButton(getString(R.string.OK), (dialogInterface, i) -> dialogInterface.dismiss())
                                    .show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return;
                    }
                    verifyCertificate();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
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
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/CertificateVerification/ValidateCertificateInsurance";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        Details.addProperty("scanCode",intentData);
                        Details.addProperty("certificateNumber",certificationNumber.getText().toString().toUpperCase(Locale.ROOT).trim());
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postUrl)
                                .header("Authorization", "Bearer " + token)
                                .header("clientInfo", MainActivity.InsertMobileParameters())
                                .header("fingerprint",MainActivity.uniqueidval)
                                .post(body)
                                .build();
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
                            Log.i(null, staticRes);


                            if(staticResponse.code()==200) {
                                final JSONObject staticJsonObj = new JSONObject(staticRes);

                                if (staticJsonObj.getInt("rcode") == 200) {
                                    runOnUiThread(() -> {
                                        try{
                                            if(p.isShowing()){
                                                p.dismiss();
                                            }
                                            JSONObject insuranceDetails = staticJsonObj.getJSONObject("rObj").getJSONObject("insuranceDetails");
                                            certificateNum=insuranceDetails.getString("diskNo");
                                            vehicleNum=insuranceDetails.getString("registrationNumber");
                                            insuranceCompany=insuranceDetails.getString("insuranceCompany");
                                            chassisNum=insuranceDetails.getString("chassisNumber");
                                            policyStartDate=insuranceDetails.getString("commencingDate");
                                            policyEndDate=insuranceDetails.getString("expiryDate");
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
                                            startActivity(new Intent(context,VerificationResult.class));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });
                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(() -> {
                                        if(p.isShowing()){
                                            p.dismiss();
                                        }
                                        unAuthorize(context);
                                    });
                                }else if (staticJsonObj.getInt("rcode") == 502 || staticJsonObj.getInt("rcode")==503 ||staticJsonObj.getInt("rcode")==504) {
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
                                                    }
                                                }).show();
                                    });

                                }
                            }
                            else if(staticResponse.code()==401){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(p.isShowing()){
                                            p.dismiss();
                                        }
                                        unAuthorize(context);
                                    }
                                });
                            }
                            else{
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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