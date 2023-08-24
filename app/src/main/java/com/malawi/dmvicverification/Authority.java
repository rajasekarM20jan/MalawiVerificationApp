package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.PERMISSIONS_REQUEST;
import static com.malawi.dmvicverification.MainActivity.PERMISSION_CAMERA;
import static com.malawi.dmvicverification.MainActivity.PERMISSION_LOCATION;
import static com.malawi.dmvicverification.MainActivity.PERMISSION_READSTORAGE;
import static com.malawi.dmvicverification.MainActivity.PERMISSION_READ_CONTACTS;
import static com.malawi.dmvicverification.MainActivity.PERMISSION_RECORD_AUDIO;
import static com.malawi.dmvicverification.MainActivity.PERMISSION_Readphonestate;
import static com.malawi.dmvicverification.MainActivity.PERMISSION_STORAGE;
import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.checkGPSStatus;
import static com.malawi.dmvicverification.MainActivity.isNetworkConnected;
import static com.malawi.dmvicverification.MainActivity.loadlocale;
import static com.malawi.dmvicverification.MainActivity.setLocale;
import static com.malawi.dmvicverification.MainActivity.unAuthorize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.widget.Button;
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

public class Authority extends AppCompatActivity {

    Button BtnLoginGuest,BtnLoginPolice;
    Context context;
    ImageView logo;
    ProgressDialog p;
    ImageView switchLanguageIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority);
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
            logo=findViewById(R.id.dmvicloginphoto);
            BtnLoginGuest=findViewById(R.id.BtnLoginGuest);
            BtnLoginPolice=findViewById(R.id.BtnLoginPolice);
            switchLanguageIcon=findViewById(R.id.switchLanguageIcon);

            basicFunctions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return checkSelfPermission(PERMISSION_CAMERA) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_READSTORAGE) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_Readphonestate) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED;
        } else {
            return checkSelfPermission(PERMISSION_CAMERA) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_READSTORAGE) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_Readphonestate) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED ;
        }
    }
    //checking the permissions
    private void requestPermissions() {
        try {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) ||
                    shouldShowRequestPermissionRationale(PERMISSION_RECORD_AUDIO) ||
                    shouldShowRequestPermissionRationale(PERMISSION_STORAGE) ||
                    shouldShowRequestPermissionRationale(PERMISSION_READSTORAGE) ||
                    shouldShowRequestPermissionRationale(PERMISSION_LOCATION) ||
                    shouldShowRequestPermissionRationale(PERMISSION_READ_CONTACTS) ||
                    shouldShowRequestPermissionRationale(PERMISSION_Readphonestate) ||
                    shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(new String[]{PERMISSION_LOCATION, PERMISSION_CAMERA, PERMISSION_STORAGE, PERMISSION_READSTORAGE, PERMISSION_Readphonestate, PERMISSION_RECORD_AUDIO, PERMISSION_READ_CONTACTS}, PERMISSIONS_REQUEST);
            } else {
                requestPermissions(new String[]{PERMISSION_LOCATION, PERMISSION_CAMERA, PERMISSION_STORAGE, PERMISSION_READSTORAGE, PERMISSION_Readphonestate, PERMISSION_RECORD_AUDIO, PERMISSION_READ_CONTACTS}, PERMISSIONS_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //on result of permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                // Redirect to app settings permissions page
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Dear User!");
                dialog.setMessage("We request you to allow the required permissions inorder to a stable app performance.");
                dialog.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    public void showChangeLanguage(Activity activity) {
        try {


            final String[] items = {"French", "English"};
            android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(activity);
            mBuilder.setTitle("Choose Language");
            mBuilder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        setLocale(activity, "fr");
                        recreate();
                    } else if (i == 1) {
                        setLocale(activity, "en");
                        recreate();
                    }
                    dialogInterface.dismiss();
                }
            });
            android.app.AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
    }
    void basicFunctions(){
        try{
            switchLanguageIcon.setOnClickListener(l->showChangeLanguage(Authority.this));
            BtnLoginPolice.setOnClickListener(l->{
                if(!hasPermission()){
                    requestPermissions();
                }else{
                    startActivity(new Intent(context,Login.class));
                }
            });
            BtnLoginGuest.setOnClickListener(l->{
                if(!hasPermission()){
                    requestPermissions();
                }else {
                    confirmAuthority();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void confirmAuthority(){
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
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/Account/PublicLogin";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
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
                                }else if (staticJsonObj.getInt("rcode") == 502 || staticJsonObj.getInt("rcode")==503 || staticJsonObj.getInt("rcode")==504) {
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
                                        try {
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
    public void onBackPressed() {
        try{
            alertTheUser(context,"",getString(R.string.exit_alert))
                    .setCancelable(false)
                    .setPositiveButton("No",(dialogInterface, i) -> dialogInterface.dismiss())
                    .setNegativeButton("Yes",(dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finishAffinity();
                    }).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}