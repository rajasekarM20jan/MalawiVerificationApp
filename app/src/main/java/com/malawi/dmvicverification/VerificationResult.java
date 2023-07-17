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
import static com.malawi.dmvicverification.MainActivity.vehicleNum;
import static com.malawi.dmvicverification.MainActivity.yearOfManufacture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerificationResult extends AppCompatActivity {
    Context context;
    ImageView statusImage;
    TextView txtcertificateval,txtVehicleval,txtInsucmpnyval,txtChassisval
            ,txtpolicystdtval,txtpolicyenddtval,txtVchmakeval
            ,txtvchmodelval,txtcanceldtval,txtcancelresval,txtYomModelVal;
    Button doneButton;
    LinearLayout linearcancelid,dmviccertificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_result);
        context=this;
        try {

            getSupportActionBar().setTitle(getString(R.string.verifyResult));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        init();
    }

    void init(){
        try{
            dmviccertificate=findViewById(R.id.dmviccertificate);
            statusImage=findViewById(R.id.statusImage);
            txtcertificateval=findViewById(R.id.txtcertificateval);
            txtVehicleval=findViewById(R.id.txtVehicleval);
            txtInsucmpnyval=findViewById(R.id.txtInsucmpnyval);
            txtChassisval=findViewById(R.id.txtChassisval);
            txtpolicystdtval=findViewById(R.id.txtpolicystdtval);
            txtpolicyenddtval=findViewById(R.id.txtpolicyenddtval);
            txtVchmakeval=findViewById(R.id.txtVchmakeval);
            txtvchmodelval=findViewById(R.id.txtvchmodelval);
            txtcanceldtval=findViewById(R.id.txtcanceldtval);
            txtcancelresval=findViewById(R.id.txtcancelresval);
            doneButton=findViewById(R.id.doneButton);
            linearcancelid=findViewById(R.id.linearcancelid);
            txtYomModelVal=findViewById(R.id.txtYomModelVal);
            basicFunctions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void basicFunctions(){
        try{
            doneButton.setOnClickListener(l->onBackPressed());
            if(!status.equals("Invalid")){
                dmviccertificate.setVisibility(View.VISIBLE);
                linearcancelid.setVisibility(View.GONE);
                txtcertificateval.setText(certificateNum);
                txtVehicleval.setText(vehicleNum);
                txtInsucmpnyval.setText(insuranceCompany);
                txtChassisval.setText(chassisNum);
                txtpolicystdtval.setText(policyStartDate);
                txtpolicyenddtval.setText(policyEndDate);
                txtVchmakeval.setText(make);
                txtvchmodelval.setText(model);
                txtYomModelVal.setText(yearOfManufacture);
            }else{
                dmviccertificate.setVisibility(View.GONE);
                linearcancelid.setVisibility(View.GONE);
            }
            switch (status){
                case "Active":{
                    statusImage.setImageDrawable(getDrawable(R.drawable.active));
                    break;
                }
                case "Invalid":{
                    int heightInDp = 300; // Desired height in dp

                    // Convert dp to pixels
                    float density = getResources().getDisplayMetrics().density;
                    int heightInPx = (int) (heightInDp * density + 0.5f);

                    // Set the height of the ImageView
                    ViewGroup.LayoutParams params = statusImage.getLayoutParams();
                    params.height = heightInPx;
                    statusImage.setLayoutParams(params);
                    statusImage.setImageDrawable(getDrawable(R.drawable.invalid));
                    break;
                }
                case "Cancelled":{
                    statusImage.setImageDrawable(getDrawable(R.drawable.cancelled));
                    break;
                }
                case "Expired":{
                    statusImage.setImageDrawable(getDrawable(R.drawable.expired));
                    break;
                }
                case "Upcoming":{
                    statusImage.setImageDrawable(getDrawable(R.drawable.upcoming));
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void fetchCertDetails() {
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
                        String postUrl = ""+"";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
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
                                        txtcertificateval.setText("");
                                        txtVehicleval.setText("");
                                        txtInsucmpnyval.setText("");
                                        txtChassisval.setText("");
                                        txtpolicystdtval.setText("");
                                        txtpolicyenddtval.setText("");
                                        txtVchmakeval.setText("");
                                        txtvchmodelval.setText("");
                                        txtcanceldtval.setText("");
                                        txtcancelresval.setText("");
                                    });
                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(() -> {

                                    });
                                }else if (staticJsonObj.getInt("rcode") == 502) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    });
                                } else {
                                    runOnUiThread(() -> {

                                    });

                                }
                            }else if(staticResponse.code()==401){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {

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