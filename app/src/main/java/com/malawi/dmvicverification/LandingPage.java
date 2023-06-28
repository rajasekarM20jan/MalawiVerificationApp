package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.alertTheUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class LandingPage extends AppCompatActivity {

    Context context;
    Activity activity;
    ViewPager imageViewPager;
    TextView skipButtonLP,lpPrevious,lpNext,lpTitle,lpSubTxt;
    TabLayout tabDottedLine;
    ArrayList<ImageModel> imageList;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        context=this;
        activity=this;
        try {
            getSupportActionBar().hide();
            init();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    void init(){
        try{
            imageViewPager=findViewById(R.id.imageViewPager);
            skipButtonLP=findViewById(R.id.skipButtonLP);
            lpPrevious=findViewById(R.id.lpPrevious);
            lpNext=findViewById(R.id.lpNext);
            lpTitle=findViewById(R.id.lpTitle);
            lpSubTxt=findViewById(R.id.lpSubTxt);
            tabDottedLine=findViewById(R.id.tabDottedLine);
            basicFunctions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void basicFunctions(){
        try{
            imageList=new ArrayList<>();
            imageList.add(new ImageModel(R.drawable.dummy_1));
            imageList.add(new ImageModel(R.drawable.dummy_2));
            imageList.add(new ImageModel(R.drawable.dummy_3));
            imageList.add(new ImageModel(R.drawable.dummy_4));
            adapter=new ImageAdapter(context,imageList);
            imageViewPager.setAdapter(adapter);
            tabDottedLine.setupWithViewPager(imageViewPager,true);

            // Set a page change listener to handle enabling/disabling of previous button/textview
            imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // Not needed for this implementation
                }

                @Override
                public void onPageSelected(int position) {
                    // Enable/disable previous TextView based on current position
                    lpPrevious.setEnabled(position > 0);
                    if(position>0){
                        lpPrevious.setTextColor(getColor(R.color.apptheme));
                    }else {
                        lpPrevious.setTextColor(getColor(R.color.grey));
                    }
                    switch (position){
                        case 0:{
                            lpTitle.setText("Crash!");
                            lpSubTxt.setText("Is that your vehicle?");
                            break;
                        }
                        case 1:{
                            lpTitle.setText("Uh-Oh!");
                            lpSubTxt.setText("Thinking on what to do?");
                            break;
                        }
                        case 2:{
                            lpTitle.setText("Don't worry!");
                            lpSubTxt.setText("We are with you at every step");
                            break;
                        }
                        case 3:{
                            lpTitle.setText("IAM Verification");
                            lpSubTxt.setText("Insurance that moves at your speed.");
                            break;
                        }

                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    // Not needed for this implementation
                }
            });

            // Set click listeners for previous and next TextViews
            lpPrevious.setOnClickListener(l -> {
                int currentPosition = imageViewPager.getCurrentItem();
                if (currentPosition > 0) {
                    imageViewPager.setCurrentItem(currentPosition - 1);
                }
            });

            lpNext.setOnClickListener(l -> {
                int currentPosition = imageViewPager.getCurrentItem();
                int totalCount = adapter.getCount();
                if (currentPosition < totalCount - 1) {
                    imageViewPager.setCurrentItem(currentPosition + 1);
                } else {
                    startActivity(new Intent(context,TermsPage.class));
                }
            });

            skipButtonLP.setOnClickListener(l->{
                startActivity(new Intent(context,TermsPage.class));
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