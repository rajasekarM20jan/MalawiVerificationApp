package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.Login.mobileNumber;
import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.checkGPSStatus;
import static com.malawi.dmvicverification.MainActivity.isNetworkConnected;
import static com.malawi.dmvicverification.MainActivity.loadlocale;
import static com.malawi.dmvicverification.MainActivity.unAuthorize;

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
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginOTP extends AppCompatActivity {
    TextView txtotptimer,txtresendotp,mobileNumberTextField,numberRetype;
    Button btnVerifyOtp;
    Context context;
    EditText otpEditText1 ,otpEditText2,otpEditText3,otpEditText4,otpEditText5,otpEditText6;
    ProgressDialog p;
    LinearLayout OTPSeconds,resendLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_o_t_p);
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
            numberRetype=findViewById(R.id.numberRetype);
            mobileNumberTextField=findViewById(R.id.mobileNumberTextField);
            btnVerifyOtp=findViewById(R.id.BtnVerifyotp);
            txtotptimer=findViewById(R.id.txtotptimer);
            OTPSeconds =findViewById(R.id.OTPseconds);
            resendLayout=findViewById(R.id.resendotlay);
            txtresendotp=findViewById(R.id.txtresendotp);
            otpEditText1 = findViewById(R.id.otp_edit_text_1);
            otpEditText2 = findViewById(R.id.otp_edit_text_2);
            otpEditText3 = findViewById(R.id.otp_edit_text_3);
            otpEditText4 = findViewById(R.id.otp_edit_text_4);
            otpEditText5 = findViewById(R.id.otp_edit_text_5);
            otpEditText6 = findViewById(R.id.otp_edit_text_6);

            basicFunctions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void basicFunctions(){
        try{

            mobileNumberTextField.setText(Login.phoneNumberWithCode);
            numberRetype.setOnClickListener(l->onBackPressed());
            String otp=Login.otp;
            System.out.println("OTP is: "+otp);
            otpEditText1.setText(String.valueOf(otp.charAt(0)));
            otpEditText2.setText(String.valueOf(otp.charAt(1)));
            otpEditText3.setText(String.valueOf(otp.charAt(2)));
            otpEditText4.setText(String.valueOf(otp.charAt(3)));
            otpEditText5.setText(String.valueOf(otp.charAt(4)));
            otpEditText6.setText(String.valueOf(otp.charAt(5)));
            otpEditText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() == 1) {
                            otpEditText2.requestFocus();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpEditText2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() == 1) {
                            otpEditText3.requestFocus();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpEditText3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() == 1) {
                            otpEditText4.requestFocus();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpEditText4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() == 1) {
                            otpEditText5.requestFocus();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpEditText5.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() == 1) {
                            otpEditText6.requestFocus();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpEditText2.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try {
                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (otpEditText2.getText().length() == 0) {
                                otpEditText1.setText("");
                                otpEditText1.requestFocus();
                                return true; // Consume the event
                            }
                        }
                        return false;
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
            });

            otpEditText3.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try {
                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (otpEditText3.getText().length() == 0) {
                                otpEditText2.setText("");
                                otpEditText2.requestFocus();
                                return true; // Consume the event
                            }
                        }
                        return false;
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
            });

            otpEditText4.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try {
                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (otpEditText4.getText().length() == 0) {
                                otpEditText3.setText("");
                                otpEditText3.requestFocus();
                                return true; // Consume the event
                            }
                        }
                        return false;
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
            });

            otpEditText5.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try {
                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (otpEditText5.getText().length() == 0) {
                                otpEditText4.setText("");
                                otpEditText4.requestFocus();
                                return true; // Consume the event
                            }
                        }
                        return false;
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
            });

            otpEditText6.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try {
                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (otpEditText6.getText().length() == 0) {
                                otpEditText5.setText("");
                                otpEditText5.requestFocus();
                                return true; // Consume the event
                            }
                        }
                        return false;
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
            });

            getTimerText();

            txtresendotp.setOnClickListener(l->{
                try{
                    resendLayout.setVisibility(View.GONE);
                    OTPSeconds.setVisibility(View.VISIBLE);
                    getTimerText();
                    resendOtp();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            btnVerifyOtp.setOnClickListener(l->{
                try{
                    if(otpEditText1.length()==1 && otpEditText2.length()==1 && otpEditText3.length()==1 && otpEditText4.length()==1 && otpEditText5.length()==1 && otpEditText6.length()==1){
                        String enteredOTP=otpEditText1.getText().toString().trim()+otpEditText2.getText().toString().trim()+
                                otpEditText3.getText().toString().trim() +otpEditText4.getText().toString().trim()+
                                otpEditText5.getText().toString().trim()+otpEditText6.getText().toString().trim();
                        verifyOtp(enteredOTP);
                    }else{
                        alertTheUser(context,"",getString(R.string.enter_otp))
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

    void getTimerText(){
        try{
            new CountDownTimer(59999,1000){
                @Override
                public void onTick(long l) {
                    String seconds= String.valueOf(l/1000);
                    String timer;
                    if(seconds.length()==1){
                        timer=getString(R.string.timer2)+seconds;
                    }else{
                        timer=getString(R.string.timer)+seconds;
                    }
                    txtotptimer.setText(timer);
                }

                @Override
                public void onFinish() {
                    OTPSeconds.setVisibility(View.GONE);
                    resendLayout.setVisibility(View.VISIBLE);
                }
            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void verifyOtp(String enteredOTP) {
        try {
            if (isNetworkConnected(context)) {
                if (checkGPSStatus(context)) {

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
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/VerifyingAuthority/ValidateVerificationCode";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        Details.addProperty("sessionID",Login.sessionID);
                        Details.addProperty("verificationCode",enteredOTP);
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
                                        try {
                                            //storing the token value for future usage.
                                            String sToken = staticJsonObj.getJSONObject("rObj").getString("token");

                                            DataBaseHelper mydb = new DataBaseHelper(context);
                                            if (mydb.getTokenDetails().getCount() != 0) {
                                                mydb.deleteTokenData();
                                            }
                                            boolean isInserted = mydb.insertToken(sToken);
                                            if (isInserted) {
                                                Intent intent = new Intent(context, Dashboard.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(context, "Token is not stored...", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }else if (staticJsonObj.getInt("rcode") == 502||staticJsonObj.getInt("rcode")==503) {
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
                                            unAuthorize(context);

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

    void resendOtp() {
        try {
            if (isNetworkConnected(context)) {
                if (checkGPSStatus(context)) {

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
                                .header("fingerprint",MainActivity.uniqueidval)
                                .header("clientInfo", MainActivity.InsertMobileParameters())
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
                                            try {
                                                if (p.isShowing()) {
                                                    p.dismiss();
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            Login.otp=staticJsonObj.getJSONObject("rObj").getString("VerificationCode");
                                            Login.sessionID=staticJsonObj.getJSONObject("rObj").getString("sessionID");
                                            String otp=Login.otp;
                                            System.out.println("OTP is: "+otp);
                                            otpEditText1.setText(String.valueOf(otp.charAt(0)));
                                            otpEditText2.setText(String.valueOf(otp.charAt(1)));
                                            otpEditText3.setText(String.valueOf(otp.charAt(2)));
                                            otpEditText4.setText(String.valueOf(otp.charAt(3)));
                                            otpEditText5.setText(String.valueOf(otp.charAt(4)));
                                            otpEditText6.setText(String.valueOf(otp.charAt(5)));
                                            otpEditText1.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    try {
                                                        if (s.length() == 1) {
                                                            otpEditText2.requestFocus();
                                                        }
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                }
                                            });

                                            otpEditText2.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    try {
                                                        if (s.length() == 1) {
                                                            otpEditText3.requestFocus();
                                                        }
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                }
                                            });

                                            otpEditText3.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    try {
                                                        if (s.length() == 1) {
                                                            otpEditText4.requestFocus();
                                                        }
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                }
                                            });

                                            otpEditText4.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    try {
                                                        if (s.length() == 1) {
                                                            otpEditText5.requestFocus();
                                                        }
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                }
                                            });

                                            otpEditText5.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    try {
                                                        if (s.length() == 1) {
                                                            otpEditText6.requestFocus();
                                                        }
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                }
                                            });

                                            otpEditText2.setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                    try {
                                                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                                            if (otpEditText2.getText().length() == 0) {
                                                                otpEditText1.setText("");
                                                                otpEditText1.requestFocus();
                                                                return true; // Consume the event
                                                            }
                                                        }
                                                        return false;
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                        return false;
                                                    }
                                                }
                                            });

                                            otpEditText3.setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                    try {
                                                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                                            if (otpEditText3.getText().length() == 0) {
                                                                otpEditText2.setText("");
                                                                otpEditText2.requestFocus();
                                                                return true; // Consume the event
                                                            }
                                                        }
                                                        return false;
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                        return false;
                                                    }
                                                }
                                            });

                                            otpEditText4.setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                    try {
                                                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                                            if (otpEditText4.getText().length() == 0) {
                                                                otpEditText3.setText("");
                                                                otpEditText3.requestFocus();
                                                                return true; // Consume the event
                                                            }
                                                        }
                                                        return false;
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                        return false;
                                                    }
                                                }
                                            });

                                            otpEditText5.setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                    try {
                                                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                                            if (otpEditText5.getText().length() == 0) {
                                                                otpEditText4.setText("");
                                                                otpEditText4.requestFocus();
                                                                return true; // Consume the event
                                                            }
                                                        }
                                                        return false;
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                        return false;
                                                    }
                                                }
                                            });

                                            otpEditText6.setOnKeyListener(new View.OnKeyListener() {
                                                @Override
                                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                    try {
                                                        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                                            if (otpEditText6.getText().length() == 0) {
                                                                otpEditText5.setText("");
                                                                otpEditText5.requestFocus();
                                                                return true; // Consume the event
                                                            }
                                                        }
                                                        return false;
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                        return false;
                                                    }
                                                }
                                            });

                                            resendLayout.setVisibility(View.GONE);
                                            OTPSeconds.setVisibility(View.VISIBLE);
                                            getTimerText();
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
    public void onBackPressed() {
        try {
            startActivity(new Intent(context,Login.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}