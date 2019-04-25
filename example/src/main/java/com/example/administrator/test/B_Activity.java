package com.example.administrator.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.administrator.R;
import com.example.administrator.UserBean;
import com.example.administrator.interfaces.IRouter;
import com.kong.router.ARouter;
import com.kong.router.Router;
import com.shopee.router.annotation.RouterField;
import com.shopee.router.annotation.RouterTarget;

import butterknife.BindView;
import butterknife.ButterKnife;

@RouterTarget(path = "/b/activity")
public class B_Activity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;

    @RouterField("id")
    String userId;

    @RouterField("name")
    int userName;

    @RouterField("user")
    UserBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        ARouter.get().inject(this);
        Toast.makeText(this, "id == " + userId, Toast.LENGTH_SHORT).show();
       /* String id = getIntent().getStringExtra("id");
        Toast.makeText(this, "id == " + id, Toast.LENGTH_SHORT).show();*/

        ButterKnife.bind(this);

        button.setText("ni hao !");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBean userBean = new UserBean(888888);
                //test 1
                /*Router.get().path("/main/mainActivity")
                        .addIRouter(IRouter.class)
                        .withObject(userBean)
                        .navigation();*/

                ARouter.get().path("/main/mainActivity")
                        .withObject("user", userBean)
                        .navigation();
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }
}
