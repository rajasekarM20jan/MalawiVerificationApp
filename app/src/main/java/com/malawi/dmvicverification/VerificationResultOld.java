package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.cancelledDate;
import static com.malawi.dmvicverification.MainActivity.cancelledReason;
import static com.malawi.dmvicverification.MainActivity.certificateNum;
import static com.malawi.dmvicverification.MainActivity.chaNum;
import static com.malawi.dmvicverification.MainActivity.chassisNum;
import static com.malawi.dmvicverification.MainActivity.coverType;
import static com.malawi.dmvicverification.MainActivity.crComments;
import static com.malawi.dmvicverification.MainActivity.crRef;
import static com.malawi.dmvicverification.MainActivity.engineNum;
import static com.malawi.dmvicverification.MainActivity.insuranceCompany;
import static com.malawi.dmvicverification.MainActivity.loadlocale;
import static com.malawi.dmvicverification.MainActivity.make;
import static com.malawi.dmvicverification.MainActivity.model;
import static com.malawi.dmvicverification.MainActivity.policyEndDate;
import static com.malawi.dmvicverification.MainActivity.policyStartDate;
import static com.malawi.dmvicverification.MainActivity.status;
import static com.malawi.dmvicverification.MainActivity.validFrom;
import static com.malawi.dmvicverification.MainActivity.vehicleNum;
import static com.malawi.dmvicverification.MainActivity.vehicleRegNum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VerificationResultOld extends AppCompatActivity {

    Context context;
    ImageView statusImage;
    TextView txtVehicleval,txtChassisval,txtpolicystdtval,
            txtEngineVal,txtcanceldtval,txtcancelresval,txtTocVal;
    Button doneButton;
    LinearLayout linearcancelid,dmviccertificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_result_old);
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
            txtVehicleval=findViewById(R.id.txtVehicleVal);
            txtChassisval=findViewById(R.id.txtChassisVal);
            txtpolicystdtval=findViewById(R.id.txtpolicystdtval);
            txtEngineVal=findViewById(R.id.txtEngineVal);
            txtcanceldtval=findViewById(R.id.txtcanceldtval);
            txtcancelresval=findViewById(R.id.txtcancelresval);
            txtTocVal=findViewById(R.id.txtTocVal);
            doneButton=findViewById(R.id.doneButton);
            linearcancelid=findViewById(R.id.linearcancelid);
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
                if((crComments.equals("")|| crComments == null ||crComments.equals("null")) ||(crRef.equals("")|| crRef == null|| crRef.equals("null"))){
                    linearcancelid.setVisibility(View.GONE);
                }
                else {
                    linearcancelid.setVisibility(View.VISIBLE);
                    txtcanceldtval.setText(crRef);
                    txtcancelresval.setText(crComments);
                }
                txtVehicleval.setText(vehicleRegNum);
                txtChassisval.setText(chaNum);
                txtpolicystdtval.setText(validFrom);
                txtTocVal.setText(coverType);
                txtEngineVal.setText(engineNum);
            }else{
                linearcancelid.setVisibility(View.GONE);
                dmviccertificate.setVisibility(View.GONE);
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
            }

        }catch (Exception e){
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