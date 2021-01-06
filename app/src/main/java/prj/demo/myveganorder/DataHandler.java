package prj.demo.myveganorder;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class DataHandler {
    Context ctx;

    DataHandler(Context _ctx) { ctx = _ctx; }

    // TODO: 帳號比對方法
    public void getCheckUser(Map<String,Object> users) {
        ArrayList<Long> phoneNumbers = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((Long) singleUser.get("phone"));
        }
        Log.d("regis", "phoneNumbers.toString() = " + phoneNumbers.toString());
    } // END - "getCheckUser"

    // TODO: Email正則規範方法
    public Boolean toRegexEmail(String _email) {
        /**
         * \w{1,63}的意思等於[a-zA-Z0-9_]{1,63}，就是允許大小寫字母，數字和底線，至少1到63個字。
         * [a-zA-Z0-9]{2,63}的意思是允許大小寫字母和數字，至少2到63個字
         * (\.[a-zA-Z]{2,63})?表示一個.後接至少2到63個大小寫字母，而問號?的意思表示括弧內的規則可以存在0個或1個。
         */
        String pattern = "^\\w{1,63}@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,63}(\\.[a-zA-Z]{2,63})?$";
        Boolean isEmail = _email.matches(pattern);
        if (isEmail) {
            return true;
        } else {
            return false;
        }
    } // END - toRegexEmail
}
