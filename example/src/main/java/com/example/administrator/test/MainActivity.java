package com.example.administrator.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.R;
import com.example.administrator.interfaces.IRouter;
import com.kong.router.Router;
import com.kong.router.RouterJumpHandler;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, B_Activity.class));

                IRouter router = (IRouter) Router.get().getIRouter();
                router.jumpBActivity("10000000");

                // test 1
                /*Intent in = new Intent();
                in.putExtra("id", "100000000a");
                router.jumpBActivity(in);*/

                // test 2
                /*router.jumpBActivity("1000000b", new RouterJumpHandler() {
                    @Override
                    public void handleStartActivity(Intent intent) {
                        startActivityForResult(intent, 0x100);
                    }
                });*/

                // test 3
               // router.jumpBActivity();

                // test 4
                /*router.jumpBActivity(new RouterJumpHandler() {
                    @Override
                    public void handleStartActivity(Intent intent) {
                        startActivityForResult(intent, 0x100);
                    }
                });*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == 0x100) {
                Toast.makeText(this, "test success ! ......", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
