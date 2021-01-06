package prj.demo.myveganorder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncUploadImage extends AsyncTask<Object, Object, Object> {
    private static final String TAG = "AsyncUploadImage ";
    ImageView iv;
    private HttpURLConnection connection;
    private InputStream is;
    private Bitmap bitmap;
    public AsyncUploadImage(ImageView mImageView) {
        iv = mImageView;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        URL url;
        try {
            url = new URL((String) objects[0]);
            Log.d("async", "url = " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if (null != result) {
            iv.setImageBitmap((Bitmap) result);
            Log.i(TAG, "image download ok！！！");
        }else {
//            iv.setBackgroundResource(R.drawable.shuben1);
            iv.setBackgroundResource(R.drawable.food);
            Log.i(TAG, "image download false！！！");
        }
    }

}
