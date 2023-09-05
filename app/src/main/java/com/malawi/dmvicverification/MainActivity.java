package com.malawi.dmvicverification;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.view.ContextThemeWrapper;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener{
    public static String latestVersion,versionUpdateURL;
    ArrayList<String> myList;
    public static final String Latitude = "latitude";
    public static final String Longitude = "longitude";
    public static Context context;
    public static String uniqueidval;
    public static final int REQUEST_PERMISSION = 100;

    public static final int PERMISSIONS_REQUEST = 99;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_READSTORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_Readphonestate = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public LocationManager locationManager;
    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    public static String certificateNum, vehicleNum, insuranceCompany, chassisNum,
            policyStartDate, policyEndDate, make, model, cancelledDate, cancelledReason,status,yearOfManufacture;
    public static String vehicleRegNum, chaNum,engineNum,coverType,validFrom,crRef,crComments;
    public static String app_version;
    public static int base_version;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        try{
            getSupportActionBar().hide();
            setLocale(this,"en");

            app_version = getString(R.string.app_version);

            String[] app_version_split = app_version.split("\\.");

            base_version = Integer.parseInt(app_version_split[app_version_split.length - 1]);


            getUpdateVersions();






        }catch (Exception e){
            e.printStackTrace();
        }

        uniqueidval = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        getUserLocationForEvery2minutes();
    }

    void getUpdateVersions(){
        try {
            if (isNetworkConnected(context)) {
                //creating the thread to run operations in background thread.
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/MasterData/GetAllMSTMobileVersion";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postUrl)
                                .post(body)
                                .build();
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120,TimeUnit.SECONDS).build();
                        Response staticResponse = null;
                        try {
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            System.out.println("qwertyuiop : "+staticRes);

                            if(staticResponse.code()==200) {

                                final JSONObject staticJsonObj = new JSONObject(staticRes);

                                if (staticJsonObj.getInt("rcode") == 200) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String appVersion=getString(R.string.app_version);
                                                myList=new ArrayList<>();
                                                JSONObject rObj=staticJsonObj.getJSONObject("rObj");
                                                JSONArray getAllAndroidLiteVersion=rObj.getJSONArray("getAllAndroidMobileVersion");
                                                for(int i=0;i<getAllAndroidLiteVersion.length();i++){
                                                    JSONObject mobileVersionObj=getAllAndroidLiteVersion.getJSONObject(i);
                                                    myList.add(mobileVersionObj.getString("mobileOSVersion"));
                                                }

                                                latestVersion=myList.get(myList.size()-1).toString();
                                                if(myList.get(myList.size()-1).equals(appVersion)){
                                                    try {
                                                        DataBaseHelper mydb = new DataBaseHelper(context);
                                                        if(mydb.getTerms().getCount()!=0){
                                                            if (mydb.getTokenDetails().getCount() == 0) {
                                                                startActivity(new Intent(context, Authority.class));
                                                            }else {
                                                                startActivity(new Intent(context,Dashboard.class));
                                                            }
                                                        }else{
                                                            startActivity(new Intent(context,TermsPage.class));
                                                        }
                                                    }
                                                    catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else{
                                                    versionUpdateURL=staticJsonObj.getJSONObject("rObj")
                                                            .getJSONArray("getAllAndroidMobileVersion")
                                                            .getJSONObject(myList.size()-1).getString("aPKURL");
                                                        /*alertTheUser(context,"Update!","Please update to the latest version of the app to continue.")
                                                                .setCancelable(false)
                                                                .setPositiveButton("UPDATE",(dialog, l) ->{
                                                                    dialog.dismiss();
                                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(versionUpdateURL)));
                                                                    finishAffinity();
                                                                }).show();*/
                                                    startActivity(new Intent(context, UpdateActivity.class));
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            alertTheUser(context,"",getString(R.string.cmn_wrong_msg))
                                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                            finishAffinity();
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
                    }
                });
                t.start();


            } else {
                Toast.makeText(this, getString(R.string.noNetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // To alert the user using alert dialog.
    public static AlertDialog.Builder alertTheUser(Context context, String Title, String Message) {
        // Returning the alert dialog.
        return new AlertDialog.Builder(context)
                .setTitle(Title)
                .setMessage(Message);
    }// End of alertTheUser().

    //checking if the internet service is provided
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm;
        try {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }// End of isNetworkConnected().

    //checking the gps status
    public static boolean checkGPSStatus(Context context) {
        try {
            LocationManager locationManager = null;
            // Boolean value to store the GPS state and Network state.
            boolean gps_enabled = false;
            boolean network_enabled = false;
            // Getting the location manager.
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // Checking if the location and network is available or not and storing the boolean accordingly.
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            /*try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/
            // returning the values.
            /*return gps_enabled || network_enabled;*/
            return gps_enabled;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }// End of checkGPSStatus().

    // Method to get user mobile details for API calls.
    public static String InsertMobileParameters() {
        try {
            // Checking the device is rooted or not.
            boolean rooteddevice;
            if (RootUtil.isDeviceRooted()) {
                rooteddevice = true;
            } else {
                rooteddevice = false;
            }
            Locale currentLocale = Locale.getDefault();
            String countryCode = currentLocale.getCountry();
            Locale locale=new Locale("en",countryCode);

            Locale locale2 = Locale.ENGLISH;
            SimpleDateFormat sf=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",locale2);
            Date d=new Date();
            String date=sf.format(d);
            // Getting basic device information and storing the values.
            String androidOS = Build.VERSION.RELEASE;
            String model = Build.MANUFACTURER + " - " + Build.MODEL;
            SharedPreferences locationPref = context.getSharedPreferences("LocationPref", MODE_PRIVATE);
            final String address1 = locationPref.getString(MainActivity.Latitude, null) + "," + locationPref.getString(MainActivity.Longitude, null);
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            String ipaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            // Storing the result in to the JSON object.
            JsonObject mobileparamters = new JsonObject();
            mobileparamters.addProperty("deviceID", uniqueidval);
            mobileparamters.addProperty("deviceID2", uniqueidval);
            mobileparamters.addProperty("deviceTimeZone", TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, locale));
            mobileparamters.addProperty("deviceDateTime", date);
            mobileparamters.addProperty("deviceLatitude", locationPref.getString(MainActivity.Latitude, null));
            mobileparamters.addProperty("deviceLongitude", locationPref.getString(MainActivity.Longitude, null));
            mobileparamters.addProperty("deviceIpAddress", ipaddress);
            mobileparamters.addProperty("deviceType", "Android");
            mobileparamters.addProperty("deviceModel", model);
            mobileparamters.addProperty("deviceVersion", androidOS);
            // mobileparamters.addProperty("fireBaseuserid", fbstokenval);
            mobileparamters.addProperty("deviceUserID", "123456789");
            mobileparamters.addProperty("deviceAppVersion", "1.0.1");
            mobileparamters.addProperty("deviceIsJailBroken", rooteddevice);
            mobileparamters.addProperty("language","en");
            // Returning the json object to the string.
            System.out.println(mobileparamters.toString());
            return mobileparamters.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }// End of InsertMobileParameters().

    // To update user current location for every minute.
    private void getUserLocationForEvery2minutes() {
        try {
            // Handler to init the user location method for every 2 min.
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // To fetch the user location.
                    getLocation();
                    // For every 2 min user location will be updated.
                    handler.postDelayed(this, 12000);
                }
            }, 1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }// End Of getUserLocationForEvery2minutes().

    // To get user location details.
    public void getLocation() {
        try {
            // Getting the location manager to fetch the user location.
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // To check if the user has given the location access or not.
            // if not the method will not execute further.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // Line to get the location update using Network provider.
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5F, (LocationListener) MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// End of getLocation().

    public static void unAuthorize(Context context)
    {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setMessage(context.getString(R.string.session_expired));
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataBaseHelper mydb;
                mydb = new DataBaseHelper(context);
                mydb.deleteTokenData();
                Intent login = new Intent(context, Authority.class);
                context.startActivity(login);
            }
        });
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public static void setLocale(Activity activity, String lang){
        try{
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            SharedPreferences pref=activity.getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("My_Lang",lang);
            editor.apply();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public static void loadlocale(Activity activity) {
        try {
            SharedPreferences pref = activity.getSharedPreferences("Settings", MODE_PRIVATE);
            String language = pref.getString("My_Lang","");
            setLocale(activity,language);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
    }


}