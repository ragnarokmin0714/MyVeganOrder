package prj.demo.myveganorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OrderCmpltdActivity extends AppCompatActivity {

    private TextView textViewOrderNumber;
    private Button buttonOrderBack;
    private String userOrderFinish;
    private String textResult;
    private String isUserEmail;
    private String isUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cmpltd);

        // TODO:接收OrderListActivity 傳過來的值
        Intent intent = getIntent(); // 接放login傳過來的值
        Bundle bundle = intent.getExtras();
        if (intent != null && bundle != null) {
            userOrderFinish = bundle.getString("USER_ORDER_FINISH");
            isUserEmail = bundle.getString("USER_EMAIL");
            isUserName = bundle.getString("USER_NAME");
        }


        Log.d("ordercmpltd", "textResult = " + textResult+
                "isUserEmail = " + isUserEmail +
                "isUserName = " + isUserName +
                "userOrderFinish = " + userOrderFinish);
        

        textViewOrderNumber = (TextView) findViewById(R.id.textView_order_cmpltd_result);
        textViewOrderNumber.setText(userOrderFinish);
        buttonOrderBack= (Button) findViewById(R.id.button_order_completed_back);

        buttonOrderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(OrderCmpltdActivity.this, MainActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("FLAG_LOGIN", true);
                bundle1.putString("USER_EMAIL", isUserEmail);
                bundle1.putString("USER_NAME", isUserName);
                intent1.putExtras(bundle1);
                startActivity(intent1);
            }
        });
    }
}