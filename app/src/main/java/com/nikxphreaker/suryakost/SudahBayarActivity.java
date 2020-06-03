package com.nikxphreaker.suryakost;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SudahBayarActivity extends AppCompatActivity {
    private String title = "Suryakost";
    TextView ket;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_sudah_bayar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBarTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ket = findViewById(R.id.ket);
    }
    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
