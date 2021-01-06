package prj.demo.myveganorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderListActivity extends AppCompatActivity {


    private TextView textViewOrderNumber, textViewOrderTime, textViewOrderResult;
    private EditText editTextOrderName, editTextOrderPhone, editTextOrderAddr;
    private Button buttonOrderCancel, buttonOrderOk;
    private String isUserEmail;
    private String isUserName;
//    ArrayList<Map<String, String>> getTagOrderData;
    ArrayList<Parcelable>  getTagOrderData;
    private Context context;

    // Firebase 物件
    private FirebaseDatabase dbControl;
    private DatabaseReference dbControlRef;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // TODO:接收LoginActivity 傳過來的值
        Intent intent = getIntent(); // 接放login傳過來的值
        Bundle bundle = intent.getExtras();
        if (intent != null && bundle != null) {
            isUserEmail = bundle.getString("USER_EMAIL");
            getTagOrderData = bundle.getParcelableArrayList("USER_ORDER");
            Log.d("order", "--------------- 接收 MainActivity 傳過來的值 ---------------" +
                    "\n\tisUserEmail = " + isUserEmail +
                    "\n\tgetTagOrderData = " + getTagOrderData +
                    "\n\t------------------------------");
        }

        findView();
        setActionBarBtn();
        setOrderDateAndOrderNumber();

        // TODO: Firebase Database 資料庫 - 啟用
        dbControl = FirebaseDatabase.getInstance(); // 取得FirebaseDatabase物件

        // TODO: Firebase Storage 雲端空間  - 啟用
        storage = FirebaseStorage.getInstance(); // 取得 FirebaseStorage 物件
//        mStorageRef = storage.getReference("myVeganShop"); // 取得 Storage 參考節點

        setUserInfo();
        setOrderList();
    }

    // TODO: 方法陳述式 - 宣告區
    private void findView() {
        context = this; // MainActivity.this
        textViewOrderNumber = (TextView) findViewById(R.id.textView_order_number);
        textViewOrderTime = (TextView) findViewById(R.id.textView_order_time);
        editTextOrderName = (EditText) findViewById(R.id.editText_order_name);
        editTextOrderPhone = (EditText) findViewById(R.id.editText_order_phone);
        editTextOrderAddr = (EditText) findViewById(R.id.editText_order_addr);
        textViewOrderResult = (TextView) findViewById(R.id.textView_order_result);
        buttonOrderCancel = (Button) findViewById(R.id.button_order_cancel);
        buttonOrderOk = (Button) findViewById(R.id.button_order_ok1);

        buttonOrderCancel.setOnClickListener(new MyButton());
        buttonOrderOk.setOnClickListener(new MyButton());


    }

    // TODO: 方法陳述式 - 設置訂單號碼與訂購時間
    private void setOrderDateAndOrderNumber() {
        Calendar now = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("M-dd-Y HH:mm:ss", Locale.TAIWAN);
        DateFormat dfOrder = new SimpleDateFormat("MddYHHmmss", Locale.TAIWAN);
        // 現在時刻總秒數

//        Log.d("order", "--------------- setOrderDateAndOrderNumber ---------------" +
//                "\n\tnow.getTimeInMillis() = " + now.getTimeInMillis() +
//                "\n\tdf.format(now.getTimeInMillis()) = " + df.format(now.getTimeInMillis()) +
//                "\n\tdfOrder.format(now.getTimeInMillis()) = " + dfOrder.format(now.getTimeInMillis()));
        String nowTimes = df.format(now.getTimeInMillis());
        textViewOrderNumber.setText("Order" + dfOrder.format(now.getTimeInMillis()));
        textViewOrderTime.setText(nowTimes);
    }


    // TODO: 方法陳述式 - 使用者資料預設
    private void setUserInfo() {
        dbControlRef = dbControl.getReference("member"); // 取得 Realtime Database 參考節點
        dbControlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                dh.getCheckUser((Map<String,Object>) dataSnapshot.getValue());
                ArrayList<HashMap<String, Object>> users = (ArrayList<HashMap<String, Object>>) dataSnapshot.getValue();

                Log.d("order", "users = " + users +
                        "\n\tusers.size() = " + users.size());
                for (int i = 1; i < users.size(); i++) { // 要避開0 在資料庫是null 跑起來會當機
                    HashMap<String, Object> user = users.get(i);
//                    Log.d("login", "users.get(" + i + ") = " + users.get(i) +
//                            "\n\tuser.get(\"password\") = " + user.get("password") +
//                            "\n\tuser.get(\"email\") = " + user.get("email") +
//                            "\n\temail.equals(user.get(\"email\")) = " + email.equals(user.get("email")) +
//                            "\n\tpassword.equals(user.get(\"password\")) = " + password.equals(user.get("password")));

                    if (isUserEmail.equals(user.get("email"))) {
                        isUserName = user.get("name").toString(); // NOTE: 姓名：全域值
                        String phone = user.get("phone").toString();
                        String addr = user.get("addr").toString();

                        editTextOrderName.setText(isUserName);
                        editTextOrderPhone.setText(phone);
                        editTextOrderAddr.setText(addr);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // TODO: 方法陳述式 - 設置訂單內容
    private void setOrderList() {
        int total_price_int = 0;
        String total_price = "";
        String name = "", qty = "", price = "", totalprice = "";
        textViewOrderResult.setText("");
        for (int i = 0; i < getTagOrderData.size(); i++) {
            HashMap getTagOrderDataMap = (HashMap) getTagOrderData.get(i);
//            Log.d("order", "getTagOrderData.get(i) = " + getTagOrderData.get(i));
            for (Object list : getTagOrderDataMap.keySet()) {
                name = getTagOrderDataMap.get("NAME").toString();
                qty = getTagOrderDataMap.get("QTY").toString();
                price = getTagOrderDataMap.get("PRICE").toString();
                totalprice = getTagOrderDataMap.get("TOTALPRICE").toString();
            }
            total_price_int += Integer.parseInt(totalprice);
            textViewOrderResult.append(name + " " + price + " * " + qty + " = " + totalprice + "\n");
        }
        total_price = String.valueOf(total_price_int);
        textViewOrderResult.append("-----------------------------\n");
        textViewOrderResult.append("銷售總額：" + total_price);
    }

    // TODO: 方法陳述式 - 設置 android 返回箭頭 <-
    private void setActionBarBtn(){
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
    }

    // TODO: 方法陳述式 - 選單版面_內容設定
    // TODO: onOptionsItemSelected - 當menu option被點選以後的反應
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: 方法陳述式 - 按鈕事件_功能配置
    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_order_ok1:
                    // - TODO: 資料庫上傳 - 訂單成立
                    dbControlRef = dbControl.getReference("order"); // 取得 Realtime Database 參考節點
                    dbControlRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int dataKey = (int) (snapshot.getChildrenCount() + 1);
                            String dataKey_str = String.valueOf(dataKey);
                            Log.d("order", "dataKey_str = " + dataKey_str);
                            dbControlRef.child(dataKey_str).child("name").setValue(editTextOrderName.getText().toString());
                            dbControlRef.child(dataKey_str).child("phone").setValue(editTextOrderPhone.getText().toString());
                            dbControlRef.child(dataKey_str).child("orderTime").setValue(textViewOrderTime.getText().toString());
                            dbControlRef.child(dataKey_str).child("orderNumber").setValue(textViewOrderNumber.getText().toString());
                            dbControlRef.child(dataKey_str).child("orderList").setValue(textViewOrderResult.getText().toString());
                            dbControlRef.child(dataKey_str).child("addr").setValue(editTextOrderAddr.getText().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });


                    // - TODO: 傳輸資料
                    Intent intent = new Intent(OrderListActivity.this, OrderCmpltdActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("USER_EMAIL", isUserEmail);
                    bundle.putString("USER_NAME", isUserName);

                    bundle.putString("USER_ORDER_FINISH", textViewOrderResult.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.button_order_cancel:
                    editTextOrderName.setText("");
                    editTextOrderPhone.setText("");
                    editTextOrderAddr.setText("");
                    break;
            }

        }
    }
}