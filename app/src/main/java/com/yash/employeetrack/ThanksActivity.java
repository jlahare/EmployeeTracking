package com.yash.employeetrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ThanksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThanksActivity.this.finish();
            }
        });
    }
}
