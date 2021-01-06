package prj.demo.myveganorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // TODO: 型態宣告區
    private Context context;
    private EditText editTextName, editTextPhone, editTextAddr, editTextEmail, editTextPass;
    private Switch switchPass;
    private Button buttonCancel, buttonRegister;

    private FirebaseAuth authControl;
    private FirebaseUser currentUser;
    private FirebaseDatabase dbControl;
    private DatabaseReference dbControlRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        // TODO: Firebase Authentication - 啟用
//        authControl= FirebaseAuth.getInstance();

        // TODO: Firebase Database 資料庫 - 啟用
        dbControl = FirebaseDatabase.getInstance(); // 取得FirebaseDatabase物件

        findView();
        setActionBarBtn();
    }

    // TODO: 方法陳述式 - 宣告區
    private void findView() {
        context = this;
        editTextName = (EditText) findViewById(R.id.editText_regis_name);
        editTextPhone = (EditText) findViewById(R.id.editText_regis_phone);
        editTextAddr = (EditText) findViewById(R.id.editText_regis_addr);
        editTextEmail = (EditText) findViewById(R.id.editText_regis_email);
        editTextPass = (EditText) findViewById(R.id.editText_regis_password);

        switchPass = (Switch) findViewById(R.id.switch_regis_pass);
        switchPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO: 密碼輸入 inputType 改變
                if (isChecked) {
                    switchPass.setText("On"); // NOTE:密碼不可見
                    editTextPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    switchPass.setText("Off"); // NOTE:密碼可見
                    editTextPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        buttonCancel = (Button) findViewById(R.id.button_regis_cancel);
        buttonRegister = (Button) findViewById(R.id.button_regis_ok);

        buttonCancel.setOnClickListener(new MyButton());
        buttonRegister.setOnClickListener(new MyButton());
    }

    // TODO: 方法陳述式 - 設置 android 返回箭頭 <-
    private void setActionBarBtn() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
    }

    // TODO: 方法陳述式 - 選單版面_內容設定
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // NOTE:返回圖標 回前一頁
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: 類別方法 - Mybutton
    private class MyButton implements View.OnClickListener {
        private String name, phone, addr, email, password;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_regis_ok: // NOTE:新增帳號
                    if (editTextEmail.length() == 0 || editTextPass.length() == 0) { // 帳號或密碼 沒有輸入
                        Toast.makeText(context, "Please input your email & password", Toast.LENGTH_SHORT).show();
                        break;
                    } else {

                        name = editTextName.getText().toString();
                        phone = editTextPhone.getText().toString();
                        addr = editTextAddr.getText().toString();
                        email = editTextEmail.getText().toString();
                        password = editTextPass.getText().toString();

                        dbControlRef = dbControl.getReference("member"); // 取得 Realtime Database 參考節點

                        // TODO: 取得資料庫資料
                        dbControlRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Get map of users in datasnapshot
//                                        collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
//                                        Log.d("regis", "dataSnapshot = " + dataSnapshot +
//                                                "\n\tdataSnapshot.getKey() = " + dataSnapshot.getKey() +
//                                                "\n\tdataSnapshot.getChildrenCount() = " + dataSnapshot.getChildrenCount() +
//                                                "\n\tdataSnapshot.getValue() = " + dataSnapshot.getValue());

                                        ArrayList<HashMap<String, Object>> users = (ArrayList<HashMap<String, Object>>) dataSnapshot.getValue();
                                        for (int i = 1; i < users.size(); i++) { // 要避開0 在資料庫是null 跑起來會當機
                                            HashMap<String, Object> user = users.get(i);
                                            Log.d("regis", "\n\tuser.get(\"email\") = " + user.get("email") +
                                                    "\n\temail = " + email +
                                                    "\n\temail.equals(user.get(\"email\")) = " + email.equals(user.get("email")));
                                            if (email.equals(user.get("email"))) {
                                                Toast.makeText(context, "此帳號已有人登記使用", Toast.LENGTH_SHORT).show();
                                                Log.d("regis", "此帳號已有人登記使用");
                                                return;
                                            }
                                        }
                                        Log.d("regis", "註冊成功");
                                        int dataKey = (int) (dataSnapshot.getChildrenCount() + 1);
                                        String dataKey_str = String.valueOf(dataKey);
                                        dbControlRef.child(dataKey_str).child("image").setValue("system.jpg");
                                        dbControlRef.child(dataKey_str).child("name").setValue(name);
                                        dbControlRef.child(dataKey_str).child("email").setValue(email);
                                        dbControlRef.child(dataKey_str).child("password").setValue(password);
                                        dbControlRef.child(dataKey_str).child("phone").setValue(phone);
                                        dbControlRef.child(dataKey_str).child("addr").setValue(addr);
                                        Toast.makeText(context, "註冊成功", Toast.LENGTH_SHORT).show();
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        //handle databaseError
                                    }
                                });
//                        Log.d("regis", "dbControlRef.getKey() = " + dbControlRef.getKey());
//                        dbControlRef.child("1").child("image").setValue("member1.jpg");
//                        dbControlRef.child("1").child("name").setValue("蔡菜子");
//                        dbControlRef.child("1").child("email").setValue("vegan1@gmail.com");
//                        dbControlRef.child("1").child("password").setValue("vegan111");
//                        dbControlRef.child("1").child("phone").setValue("0911123456");
//                        dbControlRef.child("1").child("addr").setValue("臺中市西屯區惠來路二段101號");

//                        authControl.createUserWithEmailAndPassword(email, password)
//                                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (task.isSuccessful()) { // 帳號：登入成功
//                                            Log.d("regis", "-------- Register Successful --------");
//                                            FirebaseUser user = authControl.getCurrentUser(); // 登入成功後，將資料寫入 user裡
//                                        } else {
//                                            Log.d("regis", "-------- Register Failed --------");
//                                        }
//                                    }
//                                });
                    }
                    break;
                case R.id.button_regis_cancel: // NOTE:清除資料
                    editTextName.setText("");
                    editTextPhone.setText("");
                    editTextAddr.setText("");
                    editTextEmail.setText("");
                    editTextPass.setText("");
                    break;
            }
        }
    }
}