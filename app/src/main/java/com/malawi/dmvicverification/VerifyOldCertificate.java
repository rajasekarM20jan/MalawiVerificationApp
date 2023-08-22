package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.chaNum;
import static com.malawi.dmvicverification.MainActivity.checkGPSStatus;
import static com.malawi.dmvicverification.MainActivity.coverType;
import static com.malawi.dmvicverification.MainActivity.crComments;
import static com.malawi.dmvicverification.MainActivity.crRef;
import static com.malawi.dmvicverification.MainActivity.engineNum;
import static com.malawi.dmvicverification.MainActivity.isNetworkConnected;
import static com.malawi.dmvicverification.MainActivity.loadlocale;
import static com.malawi.dmvicverification.MainActivity.policyStartDate;
import static com.malawi.dmvicverification.MainActivity.status;
import static com.malawi.dmvicverification.MainActivity.unAuthorize;
import static com.malawi.dmvicverification.MainActivity.validFrom;
import static com.malawi.dmvicverification.MainActivity.vehicleRegNum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerifyOldCertificate extends AppCompatActivity {

    EditText RegisterNumber;
    Spinner insuranceSpinner;
    Button Btnverify,Btnscan;
    Context context;
    String checked;
    ProgressDialog p;
    RadioGroup radioGroup;
    GradientDrawable drawable;
    ColorStateList colorStateList;
    RadioButton regRadio,chaRadio;
    ArrayList spinnerTextList;
    ArrayList<VCSpinnerModel> spinnerModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_old_certificate);
        context=this;
        try {
            getSupportActionBar().setTitle(getString(R.string.vc_no_printCode));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        init();
    }

    void init(){
        try{

            insuranceSpinner=findViewById(R.id.insuranceSpinner);
            RegisterNumber=findViewById(R.id.RegisterNumber);
            chaRadio=findViewById(R.id.chaRadio);
            regRadio=findViewById(R.id.regRadio);
            Btnverify=findViewById(R.id.Btnverify);
            Btnscan=findViewById(R.id.Btnscan);
            radioGroup=findViewById(R.id.selectRadio);
            basicFunctions();
            getSpinnerList();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void basicFunctions(){
        try{
            checked="RegNo";
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    try {
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.regRadio: {
                                try {
                                    checked = "RegNo";
                                    RegisterNumber.setHint(getString(R.string.regNum));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case R.id.chaRadio: {
                                try {
                                    checked = "ChaNo";
                                    RegisterNumber.setHint(getString(R.string.chassisNum));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            Btnscan.setOnClickListener(l->onBackPressed());
            Btnverify.setOnClickListener(l->{
                try{
                    System.out.println("insurance company: "+insuranceSpinner.getSelectedItem().toString()+" is at position "+insuranceSpinner.getSelectedItemPosition());
                    if(insuranceSpinner.getSelectedItemPosition()==0) {
                        try {
                            alertTheUser(context, "", getString(R.string.insuranceAlert))
                                    .setPositiveButton(getString(R.string.OK), (dialogInterface, i) -> dialogInterface.dismiss())
                                    .show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return;
                    }
                    if(RegisterNumber.length()<=0){
                        try {
                            alertTheUser(context, "", getString(R.string.registerNumAlert))
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
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/CertificateVerification/ValidateExistingCertificate";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        String insuranceCompany=insuranceSpinner.getSelectedItem().toString();
                        String a=String.valueOf(insuranceSpinner.getSelectedItemPosition());

                        String insuranceCompanyID;
                        insuranceCompanyID=spinnerModel.get(insuranceSpinner.getSelectedItemPosition()-1).getMemberCompanyId();
                        System.out.println("Insurance Company Position and ID with name:"+a +", "+ insuranceCompanyID+", "+spinnerModel.get(insuranceSpinner.getSelectedItemPosition()-1).getMemberCompanyName());
                        Details.addProperty("insuranceCompanyID",insuranceCompanyID);
                        System.out.println("Input For Reg or Cha Num: "+RegisterNumber.getText().toString().toUpperCase(Locale.ROOT).trim());
                        if(checked.equals("RegNo")){
                            Details.addProperty("vehicleRegistrationNumber",RegisterNumber.getText().toString().toUpperCase(Locale.ROOT).trim());
                        }else if (checked.equals("ChaNo")){
                            Details.addProperty("chassisNumber",RegisterNumber.getText().toString().toUpperCase(Locale.ROOT).trim());
                        }
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
                                            vehicleRegNum=insuranceDetails.getString("vehicleRegistrationNumber");
                                            chaNum=insuranceDetails.getString("chassisNumber");
                                            engineNum=insuranceDetails.getString("engineNumber");
                                            coverType=insuranceDetails.getString("typeOfCover");
                                            SimpleDateFormat sf=new SimpleDateFormat("dd-MMM-yyyy");
                                            String startDate=insuranceDetails.getString("validFrom");
                                            Date d=new Date(startDate);
                                            validFrom=sf.format(d);
                                            crRef=(insuranceDetails.getString("crRef")!=null)?insuranceDetails.getString("crRef"):"";
                                            crComments=(insuranceDetails.getString("crComments")!=null)?insuranceDetails.getString("crComments"):"";
                                            status="Active";
                                            System.out.println(vehicleRegNum+chaNum+engineNum+coverType+validFrom);
                                            startActivity(new Intent(context,VerificationResultOld.class));
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
                                }else if (staticJsonObj.getInt("rcode") == 502 || staticJsonObj.getInt("rcode")==503|| staticJsonObj.getInt("rcode")==504) {
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
                                                if(errorText.equals("No data found")||errorCode.equals("ERR028")) {
                                                    status = "Invalid";
                                                    startActivity(new Intent(context, VerificationResultOld.class));
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

    //method for getting the spinnerValues for unable to verify option
    void getSpinnerList() {
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

                        String token=null;
                        DataBaseHelper mydb = new DataBaseHelper(context);
                        if (mydb.getTokenDetails().getCount() != 0) {
                            Cursor curseattachfbtoken = mydb.getTokenDetails();
                            int countfbtoken = curseattachfbtoken.getCount();
                            if (countfbtoken >= 1) {
                                while (curseattachfbtoken.moveToNext()) {
                                    token = curseattachfbtoken.getString(0);
                                }
                            }
                        }
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/CertificateVerification/GetAllMemberCompanyId";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postUrl)
                                .header("Authorization", "Bearer " + token)
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
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                            }
                                            spinnerModel = new ArrayList<>();

                                            JSONArray spinnerValues = staticJsonObj.getJSONObject("rObj").getJSONArray("MemberCompanyIDs");
                                            for (int i = 0; i < spinnerValues.length(); i++) {
                                                JSONObject verification = spinnerValues.getJSONObject(i);
                                                String memberCompanyId = verification.getString("memberCompanyId");
                                                String memberCompanyName = verification.getString("memberCompanyName");
                                                spinnerModel.add(new VCSpinnerModel(memberCompanyId, memberCompanyName));
                                            }
                                            spinnerTextList = new ArrayList<>();
                                            spinnerTextList.add("-- Select --");
                                            for (int j = 0; j < spinnerModel.size(); j++) {
                                                spinnerTextList.add(spinnerModel.get(j).getMemberCompanyName());
                                            }
                                            ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, spinnerTextList);

                                            insuranceSpinner.setAdapter(adapter);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(() -> {
                                        try {
                                            try {
                                                if (p.isShowing()) {
                                                    p.dismiss();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            unAuthorize(context);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }else if (staticJsonObj.getInt("rcode") == 502 || staticJsonObj.getInt("rcode")==503|| staticJsonObj.getInt("rcode")==504) {
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
                                                        .setCancelable(false)
                                                        .setPositiveButton("Ok",((dialogInterface, i) -> {
                                                            dialogInterface.dismiss();
                                                        }
                                                        )).show();

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        try {
                                            try {
                                                if (p.isShowing()) {
                                                    p.dismiss();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            alertTheUser(context, "", getString(R.string.cmn_wrong_msg))
                                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    }).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });

                                }
                            }else if(staticResponse.code()==401){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unAuthorize(context);
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            if (p.isShowing()) {
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