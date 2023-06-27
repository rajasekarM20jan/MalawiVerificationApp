package com.malawi.dmvicverification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

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
            getSupportActionBar().hide();
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void init(){
        try{
            webView=findViewById(R.id.webView);
            webView.loadUrl("https://www.bimayangu.ke/api/document/ProfileTermsCondition.html");
            acceptTerms=findViewById(R.id.acceptTerms);
            acceptTerms.setOnClickListener(l->startActivity(new Intent(context,Authority.class)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}