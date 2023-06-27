package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.checkGPSStatus;
import static com.malawi.dmvicverification.MainActivity.isNetworkConnected;
import static com.malawi.dmvicverification.MainActivity.loadlocale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    public static String sessionID,otp;
    EditText PhoneNumberField;
    Button sendOtpButton;
    Context context;
    public static String mobileNumber,phoneNumberWithCode;
    ProgressDialog p;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;
        try {
            this.getSupportActionBar().hide();
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        init();
    }
    void init(){
        try{
            PhoneNumberField=findViewById(R.id.PhoneNumberField);
            sendOtpButton=findViewById(R.id.sendotpbutton);
            basicFunction();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void basicFunction(){
        try{
            sendOtpButton.setOnClickListener(l->{
                try{
                    if(PhoneNumberField.length()==9){  //as of now given the mobile number to ten digits. Have to get the detail about what will be the number of digits

                        phoneNumberWithCode="+265 "+PhoneNumberField.getText().toString().trim();
                        mobileNumber=PhoneNumberField.getText().toString().trim();
                        /*startActivity(new Intent(context,LoginOTP.class));*/
                        sendOTP();
                    }else{
                        alertTheUser(context,"",getString(R.string.enter_mobile_num))
                                .setCancelable(false)
                                .setNegativeButton(getString(R.string.OK),
                                        (dialogInterface, i) -> dialogInterface.dismiss())
                                .show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void sendOTP() {
        try {
            if (isNetworkConnected(Login.this)) {
                if (checkGPSStatus(Login.this)) {

                    //creating the thread to run operations in background thread.
                    Thread t = new Thread(() -> {

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
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/VerifyingAuthority/SendVerificationCode";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        Details.addProperty("countryCode","+265");
                        Details.addProperty("phoneNumber",mobileNumber);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postUrl)
                                .header("clientInfo", MainActivity.InsertMobileParameters())
                                .header("fingerprint",MainActivity.uniqueidval)
                                .post(body)
                                .build();
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        Response staticResponse = null;
                        try {
                            staticResponse = client.newCall(request).execute();
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);


                            if(staticResponse.code()==200) {
                                final JSONObject staticJsonObj = new JSONObject(staticRes);

                                if (staticJsonObj.getInt("rcode") == 200) {
                                    runOnUiThread(() -> {
                                        try {
                                            if(p.isShowing()){
                                                p.dismiss();
                                            }
                                            otp=staticJsonObj.getJSONObject("rObj").getString("VerificationCode");
                                            sessionID=staticJsonObj.getJSONObject("rObj").getString("sessionID");
                                            startActivity(new Intent(context, LoginOTP.class));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });
                                } else if (staticJsonObj.getInt("rcode") == 502 || staticJsonObj.getInt("rcode")==503) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                if(p.isShowing()){
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

                                                alertTheUser(context,errorCode,errorText)
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.dismiss();
                                                            }
                                                        }).show();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        try{
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
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });

                                }
                            }else if(staticResponse.code()==401){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
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
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try{
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
                                        }catch (Exception e){
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
                            alertTheUser(Login.this, "", getString(R.string.gps_not_enabled))
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
    public void onBackPressed() {
        try {
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}