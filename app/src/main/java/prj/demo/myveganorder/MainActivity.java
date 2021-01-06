package prj.demo.myveganorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // TODO: 宣告區

    // 其它
    private boolean isLogin = false; // 判別是否有登入
    private String userName;
    private String userEmail;
    private String total_price;

    // Layout 物件
    private Context context;
    private ListView listViewOrder;
    private TextView textViewTotalPrice; // 總銷售金額
    private Button btnOK, btnCancel;
    private ArrayList<DataModel> dataModel; // 陣列 - 資料庫資料存放
    private int dataCount; // 陣列長度


    // 自訂 MyListAdapter 物件
    private MyListAdapter adapter; // 自訂 MyListAdapter 物件

    // Firebase 物件
    private FirebaseDatabase dbControl;
    private DatabaseReference dbControlRef;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private DataSnapshot dataSnapshot;
    private String key;
    private FirebaseAuth authControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setTitle("測試");

        // TODO: onCreate - 接收LoginActivity 傳過來的值
        Intent intent = getIntent(); // 接放login傳過來的值
        Bundle bundle = intent.getExtras();
//        Log.d("main", "Before isLogin = " + isLogin +
//                "\n\tintent = " + intent +
//                "\n\tbundle = " + bundle);
        if (intent != null && bundle != null) {
            isLogin = bundle.getBoolean("FLAG_LOGIN");
            userName = bundle.getString("USER_NAME");
            userEmail = bundle.getString("USER_EMAIL");
//            total_price = bundle.getString("TOTAL_PRICE");
            total_price = bundle.getString("ITEMTOTALPRICE");

            Log.d("main", "--------------- 接收LoginActivity 傳過來的值 ---------------" +
                    "\n\tisLogin = " + isLogin +
                    "\n\tuserName = " + userName +
                    "\n\tuserEmail = " + userEmail +
                    "\n\ttotal_price = " + total_price +
                    "\n\t------------------------------");
        }
        Log.d("main", "After isLogin = " + isLogin);


        findView(); // 宣告方法使用

        // TODO: onCreate - Firebase Authentication - 啟用
        authControl = FirebaseAuth.getInstance(); //取得 Firebase Authentication 實例(物件)

        // TODO: onCreate - Firebase Database 資料庫 - 啟用
        dbControl = FirebaseDatabase.getInstance(); // 取得FirebaseDatabase物件

        // TODO: onCreate - Firebase Storage 雲端空間  - 啟用
        storage = FirebaseStorage.getInstance(); // 取得 FirebaseStorage 物件
//        mStorageRef = storage.getReference("myVeganShop"); // 取得 Storage 參考節點

        // TODO: onCreate - 其它方法設置
//        setDBData(); // 資料庫內容 快速建立
        getProdData();
        uploadStorageToDB(); // 上傳照片
//        getProdDataChange(); // 偵測 listVew 的內容
        getMylistAdapterData(); // TEST: 測試3
    }

    // TODO: 方法陳述式 - 宣告區
    private void findView() {
        context = this; // MainActivity.this
        dataModel = new ArrayList<DataModel>();
        dataModel.clear(); // 清除陣列資料庫內容
        listViewOrder = (ListView) findViewById(R.id.listView_order);
        textViewTotalPrice = (TextView) findViewById(R.id.textView_total_price);
        btnOK = (Button) findViewById(R.id.button_ok);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        btnOK.setOnClickListener(new MyButton());
        btnCancel.setOnClickListener(new MyButton());

    }

    // TODO: 方法陳述式 - 取得商品資料庫內容
    private void getProdData() {
        dbControlRef = dbControl.getReference("product"); // 取得 Realtime Database 參考節點
        // addListenerForSingleValueEvent() 單次監聽資料庫
        // addValueEventListener() 繼續監聽引用數據中所做的更改
        dbControlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataModel.clear(); // 清除陣列資料庫內容

                dataModel = new ArrayList();
                dataCount = (int) snapshot.getChildrenCount(); // 確定資料庫 資料長度
                Log.d("main", "dataCount = " + dataCount +
                        "\n\tsnapshot.getChildren() = " + snapshot.getChildren());

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String category = (String) ds.child("category").getValue();
                    String image = (String) ds.child("image").getValue();
                    String name = (String) ds.child("name").getValue();
                    String desc = (String) ds.child("desc").getValue();
                    String price = (String) ds.child("price").getValue();

                    // - NOTE: 確定資料庫內容是否有成功讀取
//                    Log.d("main", "--------------- prod data ---------------" +
//                            "\n\tcategory = " + category +
//                            "\n\timage = " + image +
//                            "\n\tname = " + name +
//                            "\n\tdesc = " + desc +
//                            "\n\tprice = " + price +
//                            "\n\t------------------------------");
                    dataModel.add(new DataModel(category, image, name, desc, price));
                }
                // - TODO Set Adapter
//                adapter = new MyListAdapter(dataModel, getApplicationContext()); //keep alive
                adapter = new MyListAdapter(dataModel, context); //keep alive
                listViewOrder.setAdapter(adapter);
//                adapter.notifyDataSetChanged(); // 通知 Adapter重新產生一次畫面 刷新 ListView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // TODO: 方法陳述式 - 取得 MylistAdapter 變動資料內容
    // TEST: 測試2
    private void getMylistAdapterData() {
//        Log.d("main", "Active getMylistAdapterData: ");
////        listViewOrder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
////            @Override
////            public void onFocusChange(View v, boolean hasFocus) {
////                Log.d("main", "--------------- getMylistAdapterData ---------------" +
////                        "\n\tv = " + v +
////                        "\n\thasFocus = " + hasFocus +
////                        "\n\t------------------------------");
////            }
////        });
//
////        adapter.registerAdapterDataObserver()
//        adapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//
//            }
//        });
    }

//    private void getProdDataChange() {
//        int count = 0;
//        for (int i = 0; i < listViewOrder.getCount() - 1; i++) {
//            TextView textViewPrice = ((RelativeLayout) getViewByPosition(i, listViewOrder)).findViewWithTag(i + "textViewItemPrice");
//            TextView textViewTotalPrice = ((RelativeLayout) getViewByPosition(i, listViewOrder)).findViewWithTag(i + "textViewItemTotalPrice");
//            Button btn_inc = ((RelativeLayout) getViewByPosition(i, listViewOrder)).findViewWithTag(i + "buttonItemAdd");
//            Button btn_dsc = ((RelativeLayout) getViewByPosition(i, listViewOrder)).findViewWithTag(i + "buttonItemSub");
//
//            int current_item_price = Integer.parseInt(textViewTotalPrice.getText().toString());
//            Log.d("main", "current_item_price = " + current_item_price +
//                    "btn_inc.isFocusable() = " + btn_inc.isFocusable());
//            if (btn_inc.isFocusable()) {
//                count += current_item_price;
//            } else if (btn_dsc.isFocusable()) {
//                count -= current_item_price;
//            }
//            textViewTotalPrice.setText(String.valueOf(count));
//        }
//    }
//
//    public View getViewByPosition(int pos, ListView listView) {
//        final int firstListItemPosition = listView.getFirstVisiblePosition();
//        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
//
//        if (pos < firstListItemPosition || pos > lastListItemPosition) {
//            return listView.getAdapter().getView(pos, null, listView);
//        } else {
//            final int childIndex = pos - firstListItemPosition;
//            return listView.getChildAt(childIndex);
//        }
//    }


    // TODO: 方法陳述式 - 創建資料庫內容
    private void setDBData() {
//        dbControlRef = dbControl.getReference("product"); // 取得 Realtime Database 參考節點
//        ...
    }

    // TODO: 方法陳述式 - 取得 Storage 圖片
    private void uploadStorageToDB() {
        dbControlRef = dbControl.getReference("product"); // 取得 Realtime Database 參考節點
        mStorageRef = storage.getReference("myVeganShop"); // 取得 Storage 參考節點
        mStorageRef.child("product").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
//                Log.d("main", "listResult.getItems().size() = " + listResult.getItems().size());
//                for (StorageReference fileRef : listResult.getItems()) {
//                    Log.d("main", "fileRef.getDownloadUrl() = " + fileRef.getDownloadUrl().toString());
                for (int i = 0; i < listResult.getItems().size(); i++) {
                    final int sizeI = i + 1;
//                    Log.d("main", "sizeI = " + sizeI);
                    listResult.getItems().get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                            Log.d("main", "fileRef.getDownloadUrl() = " + uri); // 取得 storage 的圖片位址
                            dbControlRef.child(sizeI + "").child("image").setValue(uri.toString());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle any errors
                Log.d("main:", "錯誤訊息 = " + exception);
            }
        });
    }

    // TODO: 方法陳述式 - 選單版面_配置啟用
    // TODO: onCreateOptionsMenu - 建立option menu_MENU顯示之前，去做一次！！！之後就不會再去呼叫！
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 設置要用哪個menu檔做為選單
        getMenuInflater().inflate(R.menu.setup, menu);
        return true;
    }

    // TODO: 方法陳述式 - 選單版面_動態設定
    // TODO: onPrepareOptionsMenu - 建立option menu，每次在display menu之前，都會去呼叫！
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d("main", "--------------- onPrepareOptionsMenu ---------------");
        MenuItem userPic = menu.findItem(R.id.user_pic);
        MenuItem login = menu.findItem(R.id.login);
        MenuItem logout = menu.findItem(R.id.logout);
        if (isLogin) {
            userPic.setTitle("歡迎光臨 " + userName);
            login.setVisible(false);
            logout.setVisible(true);
            logout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//            logout.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            userPic.setTitle("尚未登入");
            login.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//            login.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            login.setVisible(true);
            logout.setVisible(false);
        }

        // do smth with menu item
        return true;
    }


    // TODO: 方法陳述式 - 選單版面_內容設定
    // TODO: onOptionsItemSelected - 當menu option被點選以後的反應
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("main", "--------------- onOptionsItemSelected ---------------" +
                "\n\tisLogin = " + isLogin +
                "\n\t------------------------------");
        switch (item.getItemId()) {
            case R.id.user_pic:
//                Log.d("main", "user_pic active");
                break;
            case R.id.login:
//                Log.d("main", "login active");
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
//                Log.d("main", "logout  active");
                isLogin = false;
                invalidateOptionsMenu(); // 強制執行 OptionsMenu
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: 方法陳述式 - 按鈕事件_功能配置
    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_ok:
                    // FIXME: TEST區
//                    Log.d("main", "--------------- getProdDataChange ---------------" +
//                            "\n\tlistViewOrder = " + listViewOrder +
//                            "\n\t取得目前螢幕上第一個listView item的陣列index: listViewOrder.getFirstVisiblePosition() = " + listViewOrder.getFirstVisiblePosition() +
//                            "\n\t取得目前螢幕上listView item的陣列長度(非全部): listViewOrder.getChildCount() = " + listViewOrder.getChildCount() +
//                            "\n\t取得目前螢幕上最後一個listView item的陣列index: listViewOrder.getChildCount()-listViewOrder.getFirstVisiblePosition()-1 = " + (listViewOrder.getChildCount() - listViewOrder.getFirstVisiblePosition() - 1) +
//                            "\n\tlistViewOrder.getFooterViewsCount() = " + listViewOrder.getFooterViewsCount() +
//                            "\n\tlistViewOrder.getHeaderViewsCount() = " + listViewOrder.getHeaderViewsCount() +
//                            "\n\tlistViewOrder.getCount() = " + listViewOrder.getCount() + // 21 減一可為陣列數
//                            "\n\tlistViewOrder.getChildAt(1) = " + listViewOrder.getChildAt(1) +
//                            "\n\tlistViewOrder.getChildAt(1).getTag() = " + listViewOrder.getChildAt(1).getTag() +
//                            "\n\tlistViewOrder.getChildAt(1).getTag().toString() = " + listViewOrder.getChildAt(1).getTag().toString() +
//                            "\n\t------------------------------");
//
//                    TextView price0 = (TextView) listViewOrder.findViewWithTag(0 + "textViewPrice");
//                    TextView name0 = (TextView) listViewOrder.findViewWithTag(0 + "textViewName");
//                    TextView qty0 = (TextView) listViewOrder.findViewWithTag(0 + "textViewQty");
//                    TextView totalPrice0 = (TextView) listViewOrder.findViewWithTag(0 + "textViewTotalPrice");
//                    TextView price1 = (TextView) listViewOrder.findViewWithTag(1 + "textViewPrice");
//                    TextView name1 = (TextView) listViewOrder.findViewWithTag(1 + "textViewName");
//                    TextView qty1 = (TextView) listViewOrder.findViewWithTag(1 + "textViewQty");
//                    TextView totalPrice1 = (TextView) listViewOrder.findViewWithTag(1 + "textViewTotalPrice");
//
//                    TextView price20 = (TextView) listViewOrder.findViewWithTag(20 + "textViewPrice");
//                    TextView totalPrice20_1 = (TextView) listViewOrder.findViewWithTag(20 + "textViewTotalPrice");
////                    if (price0 != null && price1 != null) {
////                    if (price0 != null && price20 != null) {
//                        String price_str0 = price0.getText().toString();
//                        String name_str0 = name0.getText().toString();
//                        String qty_str0 = qty0.getText().toString();
//                        String totalPrice_str0 = totalPrice0.getText().toString();
//                    String price_str1 = price1.getText().toString();
//                    String name_str1 = name1.getText().toString();
//                    String qty_str1 = qty1.getText().toString();
//                    String totalPrice_str1 = totalPrice1.getText().toString();
//
//
////                        String price20_str = price20.getText().toString();
////                        String totalPrice_str20 = totalPrice20_1.getText().toString();
//                        Log.d("main", "--------------- getProdDataChange ---------------" +
//                                "\n\tprice_str0 = " + price_str0 +
//                                "\n\tname_str0 = " + name_str0 +
//                                "\n\tqty_str0 = " + qty_str0 +
//                                "\n\ttotalPrice_str0 = " + totalPrice_str0 +
//                                "\n\tprice_str1 = " + price_str1 +
//                                "\n\tname_str1 = " + name_str1 +
//                                "\n\tqty_str1 = " + qty_str1 +
//                                "\n\ttotalPrice_str1 = " + totalPrice_str1 +
//                                "\n\t------------------------------");
////                    }


                    // NOTE: 取tag值
//                    int totalPrice = 0, itemTotalPrice = 0;
                    ArrayList<Map<String, String>> getTagData = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < listViewOrder.getCount() - 1; i++) {
                        Map<String, String> getTagDataMap = new HashMap<String, String>();
                        if (listViewOrder.findViewWithTag(i + "textViewItemQty") != null) {
//                            Log.d("main", "listViewOrder.findViewWithTag(" + i + " + \"textViewQty\") = " + listViewOrder.findViewWithTag(i + "textViewQty"));
                            getTagDataMap.put("PRICE", ((TextView) listViewOrder.findViewWithTag(i + "textViewItemPrice")).getText().toString());
                            getTagDataMap.put("NAME", ((TextView) listViewOrder.findViewWithTag(i + "textViewItemName")).getText().toString());
                            getTagDataMap.put("QTY", ((TextView) listViewOrder.findViewWithTag(i + "textViewItemQty")).getText().toString());
                            getTagDataMap.put("TOTALPRICE", ((TextView) listViewOrder.findViewWithTag(i + "textViewItemTotalPrice")).getText().toString());
//                            Log.d("main", "getTagDataMap = " + getTagDataMap);

//                            // NOTE: 總價格變動 textViewTotalPrice
//                            itemTotalPrice = Integer.parseInt(((TextView) listViewOrder.findViewWithTag(i + "textViewItemTotalPrice")).getText().toString());
//                            totalPrice += itemTotalPrice;
//                            textViewTotalPrice.setText(totalPrice+"");

                            if (!(((TextView) listViewOrder.findViewWithTag(i + "textViewItemQty")).getText().toString()).equals("0")) {
                                getTagData.add(getTagDataMap);
                            }
//                            Log.d("main", "getTagData = " + getTagData);
                        }
                    }

//                    Log.d("main", "getTagData = " + getTagData);
//                    Log.d("main", "totalPrice = " + totalPrice);


                    // NOTE: 傳送資料
                    if (isLogin) {
                        Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
                        Bundle bundle = new Bundle();
//                    bundle.putParcelableArrayList("USER_ORDER", ((ArrayList<? extends Parcelable>) getTagData));
                        bundle.putSerializable("USER_ORDER", getTagData);
                        bundle.putString("USER_EMAIL", userEmail);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.login_isOrNot), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.button_cancel:

                    break;
                default:

            }

        }
    }

    // TODO: 在生命週期強制登出
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        authControl.signOut(); // 強制登出
        isLogin = false; // 強制登出
    }


}