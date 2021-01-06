package prj.demo.myveganorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    private EditText editTextEmail, editTextPass;
    private Switch switchPass;
    private Button buttonCancel, buttonLogin, buttonRegister;
    private DataHandler dh;

    // Firebase 物件
    private FirebaseAuth authControl;
    private FirebaseUser currentUser;
    private FirebaseDatabase dbControl;
    private DatabaseReference dbControlRef;

    // 登入成功與否
    private boolean flagLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // TODO: Firebase Authentication - 啟用
        authControl = FirebaseAuth.getInstance();

        // TODO: Firebase Database 資料庫 - 啟用
        dbControl = FirebaseDatabase.getInstance(); // 取得FirebaseDatabase物件

        // TODO: 在左上角 設置 返回圖標
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        findView();
    }

    // TODO: 方法陳述式 - 宣告區
    private void findView() {
        context = this;
        dh = new DataHandler(context);

        editTextEmail = (EditText) findViewById(R.id.editText_login_email);
        editTextPass = (EditText) findViewById(R.id.editText_login_password);

        switchPass = (Switch) findViewById(R.id.switch_login_pass);
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

        buttonCancel = (Button) findViewById(R.id.button_login_cancel);
        buttonLogin = (Button) findViewById(R.id.button_login_ok);
        buttonRegister = (Button) findViewById(R.id.button_login_register);
        buttonCancel.setOnClickListener(new MyButton());
        buttonLogin.setOnClickListener(new MyButton());
        buttonRegister.setOnClickListener(new MyButton());
    }

    // TODO: 方法陳述式 - 選單版面內容設定
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // NOTE:返回圖標 回前一頁
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: 方法陳述式 - Mybutton 按鈕事件設定
    private class MyButton implements View.OnClickListener {
        private String email, password;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_login_cancel: // NOTE:清除資料
                    editTextEmail.setText("");
                    editTextPass.setText("");
                    break;
                case R.id.button_login_ok: // NOTE:登入帳號
                    if (editTextEmail.length() == 0 || editTextPass.length() == 0) { // 帳號或密碼 沒有輸入
                        Toast.makeText(context, "Please input your email & password", Toast.LENGTH_SHORT).show();
                        Log.d("login", "-------- Please input your email & password --------");
                        break;
                    } else {
                        Log.d("login", "-------- Successful: --------");
                        email = editTextEmail.getText().toString();
                        password = editTextPass.getText().toString();

                        dbControlRef = dbControl.getReference("member"); // 取得 Realtime Database 參考節點
                        dbControlRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                dh.getCheckUser((Map<String,Object>) dataSnapshot.getValue());
                                ArrayList<HashMap<String, Object>> users = (ArrayList<HashMap<String, Object>>) dataSnapshot.getValue();

                                Log.d("login", "users = " + users +
                                        "\n\tusers.size() = " + users.size());
                                for (int i = 1; i < users.size(); i++) { // 要避開0 在資料庫是null 跑起來會當機
                                    HashMap<String, Object> user = users.get(i);
//                                    Log.d("login", "users.get(" + i + ") = " + users.get(i) +
//                                            "\n\tuser.get(\"password\") = " + user.get("password") +
//                                            "\n\tuser.get(\"email\") = " + user.get("email") +
//                                            "\n\temail = " + email +
//                                            "\n\tpassword = " + password +
//                                            "\n\temail.equals(user.get(\"email\")) = " + email.equals(user.get("email")) +
//                                            "\n\tpassword.equals(user.get(\"password\")) = " + password.equals(user.get("password")));

                                    if (email.equals(user.get("email")) && password.equals(user.get("password"))) {
                                        Toast.makeText(context, getResources().getString(R.string.login_right_acct_and_pwd), Toast.LENGTH_SHORT).show();
                                        // TODO: 登入成功後將判斷值送回首頁
                                        flagLogin = true;
                                        String userName = user.get("name").toString();
                                        String userEmail = user.get("email").toString();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("FLAG_LOGIN", flagLogin);
                                        bundle.putString("USER_NAME", userName);
                                        bundle.putString("USER_EMAIL", userEmail);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        return;
                                    } else {
//                                        Toast.makeText(context, "帳密不對", Toast.LENGTH_SHORT).show();
                                        flagLogin = false;
                                    }

////                                    Object data = dataSnapshot.getValue();
////                                    Log.i("我得到的東西", data.toString());
////                                    ArrayList real = (ArrayList) data;
////                                    HashMap tom_data = (HashMap) real.get(1);
////                                    String tom_name = (String) tom_data.get("name");
////                                    Log.i("我得到的東西", "name:" + tom_name);
                                }


//                                Log.d("login", "dataSnapshot = " + dataSnapshot +
//                                        "\n\tusers = " + users +
//                                        "\n\tusers.size() = " + users.size() +
//                                        "\n\tusers.get(1) = " + users.get(1) +
//                                        "\n\tdataSnapshot.getKey() = " + dataSnapshot.getKey() +
//                                        "\n\tdataSnapshot.getChildrenCount() = " + dataSnapshot.getChildrenCount() +
//                                        "\n\tdataSnapshot.getValue() = " + dataSnapshot.getValue());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });


//                        // TODO: FirebaseAuth
//                        currentUser = authControl.getCurrentUser();
//                        Log.d("login", "-------- currentUser2 --------" +
//                                "\n\tcurrentUser = " + currentUser +
//                                "\n\temail = " + email +
//                                "\n\tpassword = " + password +
//                                "\n------------------------");
//                        if (currentUser != null) {
//                            authControl.signOut(); // 強制登出
//                        }
//                        // - TODO: 帳密判定
//                        authControl.signInWithEmailAndPassword(email, password) // 註冊輸入 帳號、密碼
//                                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() { // 輸入完後 偵測其內容
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        /**
//                                         * https://console.firebase.google.com/project/fir-data-e71c0/authentication/users
//                                         * 帳號密碼 相關資料
//                                         * 請參閱 Firebase - Authentication(Users)
//                                         * jane@test.com
//                                         * 123456
//                                         */
//                                        flagLogin = false;
//                                        if (task.isSuccessful()) { // 帳號：登入成功
//                                            Log.d("login", "-------- Login Successful --------");
//                                            FirebaseUser user = authControl.getCurrentUser(); // NOTE:登入成功後，將資料寫入 user 裡
//
//                                            // TODO: 登入成功後將判斷值送回首頁
//                                            flagLogin = true;
//                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                            Bundle bundle = new Bundle();
//                                            bundle.putBoolean("FLAG_LOGIN", flagLogin);
//                                            intent.putExtras(bundle);
//                                            startActivity(intent);
//                                        } else {
//                                            flagLogin = false;
//                                            Log.d("login", "-------- Login Failed --------");
//                                        }
//                                    }
//                                });
                    } // END - else
                    break;

                case R.id.button_login_register: // NOTE:新增帳號
                    Intent intent = new Intent(context, RegisterActivity.class);
                    startActivity(intent);
                    break;
            }
        }

//        // TODO: 方法 - DisplayUser(登入者資料顯示)
//        private void DisplayUser(FirebaseUser user) {
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            String uid = user.getUid();
//            textViewResult.setText("Login Successful");
//            textViewResult.setText("name = " + name + "\n");
//            textViewResult.append("email = " + email + "\n");
//            textViewResult.append("UID = " + uid + "\n");
//        }
    }
}