package com.example.administrator.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.R;
import com.example.administrator.UserBean;
import com.example.administrator.interfaces.IRouter;
import com.kong.router.Router;
import com.kong.router.interfaces.IAction;
import com.shopee.router.annotation.RouterTarget;

@RouterTarget(path = "main/mainActivity")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if(intent != null) {
            UserBean userBean = intent.getParcelableExtra("user");
            if(userBean != null) {
                Toast.makeText(this, "test success ! ....userId  === " + userBean.userId, Toast.LENGTH_SHORT).show();
            }

           /* int userId = intent.getIntExtra("userId", 0);
            Toast.makeText(this, "test success ! ....userId  === " + userId, Toast.LENGTH_SHORT).show();*/
        }


        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, B_Activity.class));

               // Router.get().create(IRouter.class).jumpB_Activity("10000000", null);

                Router.get().create(IRouter.class).jumpB_Activity();

               // IRouter router = Router.get().create(IRouter.class);

                //router.jumpB_Activity("10000000");

               // router.jumpB_Activity("10000000", null);

               /* router.jumpB_Activity("10000000", new IAction() {
                    @Override
                    public void onFound(Intent intent) {
                        startActivity(intent);
                    }

                    @Override
                    public void onLost(String uri) {

                    }
                });*/
//                //test 2
//                manager.startActivityForUri("kong://www.kong.com/b_activity?id=100000c");
//
//
//                //test 3
//                IRouter router3 = manager.getIRouter();
//                UserBean user = new UserBean(100012);
//                router3.jumpB_Activity(user);
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
