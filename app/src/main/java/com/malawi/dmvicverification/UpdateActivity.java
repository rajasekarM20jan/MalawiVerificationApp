package com.malawi.dmvicverification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class UpdateActivity extends AppCompatActivity {

    Context context;
    TextView newAppVersion;
    TextView currentVersion;
    Button updateAppBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        context=this;
        init();
    }
    public void init() {
        try {
            currentVersion = findViewById(R.id.stepoldverid);

            updateAppBtn = findViewById(R.id.updateAppBtn);
            newAppVersion = findViewById(R.id.stepnewverid);

            currentVersion.setText(getString(R.string.app_version));
            newAppVersion.setText(MainActivity.latestVersion);

            updateAppBtn.setOnClickListener(onClickUpdate -> {
                //Handle update redirect here.
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(MainActivity.versionUpdateURL));
                startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}