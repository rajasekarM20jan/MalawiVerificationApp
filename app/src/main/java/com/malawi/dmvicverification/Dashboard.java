package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.alertTheUser;
import static com.malawi.dmvicverification.MainActivity.loadlocale;
import static com.malawi.dmvicverification.MainActivity.setLocale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class Dashboard extends AppCompatActivity {

    CardView vcPrintCode,vcNoPrintCode,vcSecure,verifyHistory,vcWithCertificate;
    Context context;
    Intent nextPage;
    ImageView switchLanguageIcon,menuButton;
    ConstraintLayout parentLayout;
    NavigationView menuNavigation;
    LinearLayout dashboardLayout,powerOff;
    View dummyView;
    Boolean isMenuNotOpen;

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
            menuButton=findViewById(R.id.menuButton);
            menuNavigation=findViewById(R.id.menuNavigation);
            dashboardLayout=findViewById(R.id.dashboardLayout);
            powerOff = findViewById(R.id.powerOff);
            dummyView=findViewById(R.id.dummyView);
            isMenuNotOpen=true;
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

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        if(isMenuNotOpen){
                            isMenuNotOpen=false;
                            dashboardLayout.setEnabled(false);
                            dummyView.setVisibility(View.VISIBLE);
                            menuNavigation.setVisibility(View.VISIBLE);
                        }else{
                            isMenuNotOpen=true;
                            dashboardLayout.setEnabled(true);
                            dummyView.setVisibility(View.GONE);
                            menuNavigation.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            dummyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isMenuNotOpen=true;
                    dashboardLayout.setEnabled(true);
                    dummyView.setVisibility(View.GONE);
                    menuNavigation.setVisibility(View.GONE);
                }
            });

            //on item selected listener for navigation view option
            menuNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.termsAndConditions: {
                            startActivity(new Intent(context,TermsDash.class));
                            finish();
                        }
                    }
                    return true;
                }
            });

            powerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        alertTheUser(context, "", getString(R.string.logoutMsg))
                                .setIcon(getDrawable(R.drawable.warning))
                                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DataBaseHelper mydb = new DataBaseHelper(context);
                                        if (mydb.getTokenDetails().getCount() != 0) {
                                            mydb.deleteTokenData();
                                        }
                                        startActivity(new Intent(context,Authority.class));
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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