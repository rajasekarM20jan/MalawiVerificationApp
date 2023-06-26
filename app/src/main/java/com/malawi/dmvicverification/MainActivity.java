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

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LocationListener{
    public static final String Latitude = "latitude";
    public static final String Longitude = "longitude";
    public static Context context;
    public static String uniqueidval;
    public static final int REQUEST_PERMISSION = 100;
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
            policyStartDate, policyEndDate, make, model, cancelledDate, cancelledReason,status;
    public static String vehicleRegNum, chaNum,engineNum,coverType,validFrom,crRef,crComments;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        try{
            setLocale(this,"en");
            /*String sToken = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZDQkMtSFM1MTIiLCJraWQiOiI4YmI1NDE4YWYzYjQ0YjdiOTI2OGM3YjRhYTg1NTYzMiIsInR5cCI6IkpXVCIsImN0eSI6IkpXVCJ9.PLZeaUcd7_2Sc1vxRwH6hPEIaivip9gXkLxf33l1oLLIz2NjC3-6XZSagUKzyzdZhZfeMFPzA7b4g7sNG-_ENlOweT31YD4XIyk00ZfLGS2sT4CVI4J9tibIGqtUnNTXF4F_hapl1iLbT--bzOXqYfIMAx7xgXfBNdBYrMV8RF273ARCpm6Po21hO1XyvcLkrNfesePAUNSJqZC1FwtMZeVAxdrCqeB6fHGNnSvA8HgNVk5ro4O8FFN8rhISH_8oQcfpneAiGkUco0jSTqLXqZUc2fCKTnwTuzjmJXEUs6s1U0VQ3hDTCUK1E-6OmRNjGsxhFG_-KCNSrF6Tz-Y8yw.j18m1Y1Ku8ax1ZFOJOrymQ.FfeMH9U_1e18H84DDEvz-0ENrKwRZFOYIXacMSxqNazQGe21T4T_R2VCCu9S9mqO-KSTdClwq78gGCkOv-ima_OLYbJKFnfbR8JZ2nuqJ9jlTWfrFror-C3Dqna-2i91cy--2hpuAN2jczvGj4kQBCw-W0xTu-vZi5wkdrG8n3EosTy2d40EdxcbsbPF63YU-UthVvylGwpRfTocuwKdPWWU03xmNofsnuKt4fYdTOvsr-q27fYqQ2X2mZtDgq63DUcdtjj6L4Gam-zk0w8Z8LYk8TGMrzleaGLXOBxkFNRr7x-_f36FdxSKiSDKh7KRXakrepSZ0Rd8ZNaVfVdVFaRrvJqCnSNtAEbKpNMxNdLMuEdmS4n0_G88JvPjXSmRC9WtUtDzdk92H7GDjUGLndqHkurmWWiiMPytiEVTpp8rr-dGTFzKVW5iT0t2qB9JJkfxVZeniztO6OoWxx81INCe7Dw-YBGIUj_Vo7fGC93QDZVsHGR_pF3jJRnXZrRKkxpH73-Pxb0vaWEf520mYMdv93bN0CgwuxMX9fhP5Jcq_IdskETxfPH7zZVz5lnuKsP0jes1rpsl_jzHVC7h7wThv1jJOTdYenkXEIq5z0ZuSWgIyxv-V4WiVpINngIn81JSQ8GZ7U8-MG7PjviTO3x02TCpWL-G6z1PSfVB02oZTLBD2Cyn06QEh0GUURWmRBuu80TGwdLUW2vSpPV_xmTtWNY4daIfbpbbj8VMHR-j7UXPWfdyGPXRbnkG3N_IZC9qLvs0MYjGesnFP0BC0pxzw8_TrvTdJqOD893O82E7_nz73MXa5UuWfhIGag41OVuL3bOay8Dwf9urJipyUyGIJzRFXQlXlBtGvAFDf2larE9MNJdVop3fxq-J_cY8JZOAWUSwSC93QMJFhEhdKlzXVp2gyu7AaOicdpsNslihSQpCFVkac2hTdAPWw5kvDGrKdVDIWNZz0GvhQuMcv7neGDpBnOmK68ouzOuWvA1VSLkecSmLS-31aYm3iUr8kdpYvO7FzsvhOzsfpXIWECyUgx2yEwkyyO3O-Wgf9rK8YItj2PxshzrNe_EcX5hVNoz5C91HBHISo9QFTHo6wR9XY8xtJgsE3CbgBey2Xuj2SZu1bd4tcmXsdgKe8rQhYjpGkpS7moL-fVjJjSY-IzVngIxWAhRV-EeKX3RZ66ff09I_mFyyDc6Fk3P9qU6xC8IDZS_50SE_8O7EvGwQU3e1jIp0DMHcZnPQ_LIl9IWh0RrN_9gYeh9sp1I3EGegs3ihoRZocJXlmXLTdgMtuw9xAJNXFX2iuT__c8X1WWGqVoEjyDoBho38Uy9eGXwMLDOIYNcpFj__mA8lXKp2Q7PnNxyI2Ee8PFnw27-ffrEW531iF--By8fApTipwfi3TsbYPTcD7CFXrAJd8ETyx7ObYm8W-5khipX0M86MT1mlFLNlxVmf6pj6hOb0HbsUx6C1FKzwBY-WPAdMKsGe5w.IySOjiv0RjiiBFF4ONvBnO1GUKQ-RikvgMvNAhnfoAc";

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
            }*/

            DataBaseHelper mydb = new DataBaseHelper(context);
            if (mydb.getTokenDetails().getCount() == 0) {
                startActivity(new Intent(context,Authority.class));
            }else {
                startActivity(new Intent(context,Dashboard.class));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        uniqueidval = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        getUserLocationForEvery2minutes();
        requestPermissions();
    }

    //checking the permissions
    public void requestPermissions() {
        List<String> unGrantedPermissions = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                unGrantedPermissions.add(permission);
            }
        }

        if (!unGrantedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, unGrantedPermissions.toArray(new String[0]), REQUEST_PERMISSION);
        }
    }

    //on result of permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
            }
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
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // returning the values.
            return gps_enabled || network_enabled;
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
            Locale locale=new Locale("en","IN");
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
            mobileparamters.addProperty("deviceDateTime", DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale).format(new Date()));
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