package com.malawi.dmvicverification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

import java.util.Objects;

public class TermsDash extends AppCompatActivity {

    Context context;
    Activity activity;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_dash);
        context=this;
        activity=this;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Terms And Conditions");
        try{
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void init(){
        try{
            webView=findViewById(R.id.webView);
            webView.loadUrl("https://www.bimayangu.ke/api/document/ProfileTermsCondition.html");
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