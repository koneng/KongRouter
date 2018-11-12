package com.example.administrator.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.R;
import com.example.administrator.interfaces.IRouter;
import com.kong.router.Router;
import com.kong.router.RouterJumpCallback;
import com.kong.router.RouterUri;

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
               // test 1
               router.jumpBActivity("10000000");

                // test 2
                /*router.jumpBActivity("10000000", new RouterJumpCallback() {
                    @Override
                    public void onStartActivity(Intent intent) {
                        startActivity(intent);
                    }
                });*/

                //test 3
               // Router.get().startActivityForUri("kong://www.kong.com/b_activity?id=10000000", null);

                //test 4
                /*Router.get().startActivityForUri("kong://www.kong.com/b_activity?id=10000000", new RouterJumpCallback() {
                    @Override
                    public void onStartActivity(Intent intent) {
                        startActivity(intent);
                    }
                });*/
            }
        });
    }
}
