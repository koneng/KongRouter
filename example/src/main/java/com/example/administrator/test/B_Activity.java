package com.example.administrator.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.R;

public class B_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
       /* Intent user = getIntent().getParcelableExtra("user");
        Toast.makeText(this, "id == " + user.getStringExtra("id"), Toast.LENGTH_SHORT).show();*/

        Toast.makeText(this, "id == " + getIntent().getStringExtra("id"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }
}
