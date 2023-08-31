package com.malawi.dmvicverification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

public class TermsPage extends AppCompatActivity {

    Context context;
    Activity activity;
    Button acceptTerms;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_page);
        context=this;
        activity=this;
        try{

            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Terms And Conditions");
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void init(){
        try{
            webView=findViewById(R.id.webView);
            webView.loadUrl("https://insurancedevelopment.blob.core.windows.net/termscondition/ProfileTermsCondition.html");
            acceptTerms=findViewById(R.id.acceptTerms);
            acceptTerms.setOnClickListener(l->{
                DataBaseHelper mydb = new DataBaseHelper(context);
                if (mydb.getTokenDetails().getCount() != 0) {
                    mydb.deleteTerms();
                }
                boolean isInserted = mydb.insertTerms("checked");
                if (isInserted) {
                    Intent intent = new Intent(context,Authority.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "terms is not stored...", Toast.LENGTH_SHORT).show();
                }
                });
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
            finishAffinity();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}