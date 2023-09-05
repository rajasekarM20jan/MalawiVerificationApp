package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.chaNum;
import static com.malawi.dmvicverification.MainActivity.checkGPSStatus;
import static com.malawi.dmvicverification.MainActivity.coverType;
import static com.malawi.dmvicverification.MainActivity.crComments;
import static com.malawi.dmvicverification.MainActivity.crRef;
import static com.malawi.dmvicverification.MainActivity.engineNum;
import static com.malawi.dmvicverification.MainActivity.isNetworkConnected;
import static com.malawi.dmvicverification.MainActivity.status;
import static com.malawi.dmvicverification.MainActivity.unAuthorize;
import static com.malawi.dmvicverification.MainActivity.validFrom;
import static com.malawi.dmvicverification.MainActivity.vehicleRegNum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerificationHistory extends AppCompatActivity {

    HistoryAdapter historyAdapter;
    ArrayList<HistoryModel> verificationHistoryList;
    ListView verificationListView;
    TextView noRecordsTextView;
    Context context;
    ProgressDialog p;
    ImageView menuIcon;
    String fromDate,tillDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_history);
        context=this;
        try {
            getSupportActionBar().setTitle(getString(R.string.verification_history));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        init();
    }

    void init(){
        try{
            noRecordsTextView=findViewById(R.id.noRecordsTextView);
            verificationListView=findViewById(R.id.verificationListView);
            menuIcon=findViewById(R.id.menuIcon);
            basicFunctions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void basicFunctions(){
        try{
            Calendar cal=Calendar.getInstance();
            Locale locale1=Locale.ENGLISH;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy",locale1);
            tillDate=simpleDateFormat.format(cal.getTime());
            Calendar cal2=Calendar.getInstance();
            cal2.add(Calendar.DAY_OF_YEAR, -7);
            fromDate=simpleDateFormat.format(cal2.getTime());
            System.out.println("FromDate: "+fromDate+" TillDate: "+tillDate);
            menuIcon.setOnClickListener(l->{
                LayoutInflater inflater=LayoutInflater.from(context);
                View v=inflater.inflate(R.layout.datefilterdialog,null,false);
                Dialog d=new Dialog(context);
                d.setContentView(v);
                TextView startDate,endDate;
                Button searchButton;
                startDate=v.findViewById(R.id.startDate);
                endDate=v.findViewById(R.id.endDate);
                searchButton=v.findViewById(R.id.searchButton);
                startDate.setText(fromDate);
                endDate.setText(tillDate);
                startDate.setOnClickListener(m->DatePickerFunction(startDate,fromDate,"fromDate"));
                endDate.setOnClickListener(m->DatePickerFunction(endDate,tillDate,"tillDate"));
                searchButton.setOnClickListener(m->{
                    fromDate=startDate.getText().toString();
                    tillDate=endDate.getText().toString();
                    System.out.println("FromDate2: "+fromDate+" TillDate2: "+tillDate);
                    getHistory();
                    d.dismiss();
                });
                d.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                d.create();
                d.show();
            });
            getHistory();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void getHistory() {
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
                        String postUrl = getString(R.string.base_url)+"/api/digital/core/CertificateVerification/FetchVerificationHistoryByDeviceID";
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JsonObject Details = new JsonObject();
                        Details.addProperty("fromDate",fromDate);
                        Details.addProperty("tillDate",tillDate);
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
                                            verificationHistoryList=new ArrayList<>();
                                            JSONArray historyArray=staticJsonObj.getJSONObject("rObj").getJSONArray("VerificationHistoryOrg");
                                            if(historyArray.length()>0) {
                                                for (int i = 0; i < historyArray.length(); i++) {
                                                    JSONObject historyObj = historyArray.getJSONObject(i);
                                                    String RegNo, ChaNo, certNo, status;
                                                    RegNo = historyObj.getString("vehRegnNumber");
                                                    ChaNo = historyObj.getString("vehChassisNumber");
                                                    certNo = historyObj.getString("certificateNumber");
                                                    status = historyObj.getString("custom5");
                                                    verificationHistoryList.add(new HistoryModel(RegNo, ChaNo, certNo, status));
                                                }
                                                noRecordsTextView.setVisibility(View.GONE);
                                                verificationListView.setVisibility(View.VISIBLE);
                                                historyAdapter = new HistoryAdapter(context, verificationHistoryList);
                                                verificationListView.setAdapter(historyAdapter);
                                            }else if(historyArray.length()==0){
                                                verificationListView.setVisibility(View.GONE);
                                                noRecordsTextView.setVisibility(View.VISIBLE);
                                            }
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //to create time picker dialog for policy start date
    private void DatePickerFunction(TextView tv,String selectedDate,String type) {
        try {
            // Declaring DatePicker dialog box.
            DatePickerDialog datePickerDialog = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                datePickerDialog = new DatePickerDialog(context);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

                Date selDate=dateFormat.parse(selectedDate);

                // Get the year, month, and day from the Date object
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selDate);
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                if(type.equals("tillDate")){
                    Date dt=dateFormat.parse(fromDate);
                    Calendar cl=Calendar.getInstance();
                    cl.setTime(dt);
                    datePickerDialog.getDatePicker().setMinDate(cl.getTimeInMillis());
                }else if(type.equals("fromDate")){
                    Date dt=dateFormat.parse(tillDate);
                    Calendar cl=Calendar.getInstance();
                    cl.setTime(dt);
                    datePickerDialog.getDatePicker().setMaxDate(cl.getTimeInMillis());
                }
                datePickerDialog.updateDate(yy,mm,dd);
                datePickerDialog.setOnDateSetListener((datePicker, year, month, date) -> {
                    Calendar policyStartDateCalender = Calendar.getInstance();
                    policyStartDateCalender.set(year, month, date);
                    Locale locale1=Locale.ENGLISH;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy",locale1);
                    String formatDate = simpleDateFormat.format(policyStartDateCalender.getTime());
                    tv.setText(formatDate);
                    if(type.equals("tillDate")){
                        tillDate=formatDate;
                    }else if(type.equals("fromDate")){
                        fromDate=formatDate;
                    }
                });
            }
            // Showing the date picker to the user.

            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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