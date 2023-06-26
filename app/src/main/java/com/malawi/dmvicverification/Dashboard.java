package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.loadlocale;
import static com.malawi.dmvicverification.MainActivity.setLocale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {

    CardView vcPrintCode,vcNoPrintCode,vcSecure,verifyHistory,vcWithCertificate;
    Context context;
    Intent nextPage;
    ImageView switchLanguageIcon;
    LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadlocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
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
            parentLayout=findViewById(R.id.parentLayout);
            vcNoPrintCode=findViewById(R.id.vcNoPrintCode);
            vcPrintCode=findViewById(R.id.vcPrintCode);
            vcSecure=findViewById(R.id.vcSecure);
            verifyHistory=findViewById(R.id.verifyHistory);
            vcWithCertificate=findViewById(R.id.verifyWithCertificate);
            switchLanguageIcon=findViewById(R.id.switchLanguageIcon);
            basicFunctions();
        }catch (Exception e){
            e.printStackTrace();
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
            switchLanguageIcon.setOnClickListener(l->showChangeLanguage(Dashboard.this));
            vcPrintCode.setOnClickListener(l->{
                try {
                    nextPage = new Intent(context, QrCodeScanner.class);
                    nextPage.putExtra("selectedOption", "vcPrintCode");
                    startActivity(nextPage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            vcNoPrintCode.setOnClickListener(l->{
                try {
                    nextPage = new Intent(context, VerifyOldCertificate.class);
                    nextPage.putExtra("selectedOption", "vcNoPrintCode");
                    startActivity(nextPage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            vcSecure.setOnClickListener(l->{
                try {
                    nextPage = new Intent(context, QrCodeScanner.class);
                    nextPage.putExtra("selectedOption", "vcSecure");
                    startActivity(nextPage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            vcWithCertificate.setOnClickListener(l->{
                try {
                    nextPage = new Intent(context, QrCodeScanner.class);
                    nextPage.putExtra("selectedOption", "vcCertificate");
                    startActivity(nextPage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            verifyHistory.setOnClickListener(l->{
                //page to be created
                try {
                    startActivity(new Intent(context,VerificationHistory.class));
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try{
            alertTheUser(context,"",getString(R.string.exit_alert))
                    .setCancelable(false)
                    .setNegativeButton("No",(dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Yes",(dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finishAffinity();
                    }).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}