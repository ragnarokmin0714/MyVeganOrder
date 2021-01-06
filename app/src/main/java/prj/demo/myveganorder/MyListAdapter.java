package prj.demo.myveganorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.solver.Cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 參考資料
 * https://learnexp.tw/%E3%80%90android%E3%80%91listview-%E9%80%B2%E9%9A%8E%E7%94%A8%E6%B3%95-baseadapter/
 */
public class MyListAdapter extends BaseAdapter { // BaseAdapter
    //public class MyListAdapter extends ArrayAdapter<DataModel> {
    Context ctx;
    private static LayoutInflater inflater; // 版面加載
    private ArrayList<DataModel> dataSet; // 資料


    // TODO: BaseAdapter 版面相關設定
    // TODO: 建構方法
    public MyListAdapter(ArrayList<DataModel> dataModel, Context ctx) {
//            super(ctx, R.layout.item_layout, dataModel);
        this.ctx = ctx;
        this.dataSet = dataModel;
        this.inflater = LayoutInflater.from(ctx); // 取得版面
        /**
         * 3種code都是同樣的功能：
         * LayoutInflater inflater = getLayoutInflater();
         * LayoutInflater localinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         * LayoutInflater inflater = LayoutInflater.from(context);
         */
    }

    // TODO: 取得數量(取得Item的數量，通常用於取得資料集合的大小或數量)
    @Override
    public int getCount() {
////        return 0;
//        Log.d("mylist", "--------------- getCount ---------------" +
//                "\n\tdataSet.size() = " + dataSet.size()); //  21
        return dataSet.size();
    }

    // TODO: 取得Item(回傳Item的資料)
    @Override
    public Object getItem(int position) {
//        return null
//        Log.d("mylist", "--------------- getItem ---------------" +
//                "\n\tposition = " + position);
        return dataSet.get(position);
    }

    // TODO: 回傳Item的ID - 此範例沒有特別設計ID所以隨便回傳一個值
    @Override
    public long getItemId(int position) {
//        Log.d("mylist", "--------------- getItemId ---------------" +
//                "\n\tposition = " + position +
//                "\n\t------------------------------");
        return position;
    }

    // TODO: 回傳處理後的ListItem畫面，這個地方謹慎處理如不小心會發生很多錯誤
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int[] temp_qty = new int[getCount()]; // 暫存數量 陣列
        final DataModel dm = (DataModel) getItem(position); // 取得 db - product 相對 Key值
        final ViewHolder vh;

//        Log.d("mylist", "--------------- getView ---------------" +
//                "\n\tposition = " + position +
//                "\n\tconvertView = " + convertView +
//                "\n\tparent = " + parent +
//                "\n\tdm = " + dm +
//                "\n\t------------------------------");
        // - FIXME: 物件重覆渲染的問題
        // - TODO: listView 可見範圍設定(當ListView被拖拉時會不斷觸發getView，為了避免重複加載必須加上這個判斷)
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_layout, null);
            vh.imageViewItemPic = convertView.findViewById(R.id.imageView_item_pic);
            vh.textViewItemName = convertView.findViewById(R.id.textView_item_name);
            vh.textViewItemPrice = convertView.findViewById(R.id.textView_item_price);
            vh.textViewItemTotalPrice = convertView.findViewById(R.id.textView_item_total_price);
            vh.textViewItemDesc = convertView.findViewById(R.id.textView_item_desc);
            vh.textViewItemQty = convertView.findViewById(R.id.textView_item_qty);
            vh.buttonItemAdd = convertView.findViewById(R.id.button_item_add);
            vh.buttonItemSub = convertView.findViewById(R.id.button_item_sub);

            vh.textViewItemQty.setFocusable(false); // FIXME: 測試1_1
//            vh.qty = 0;
//            vh.itemTotalPrice = 0;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }


        // - FIXME: 圖片載入的問題
        /**
         * Cache.ICON_CACHE為ImageCache的例項,表示如果不在快取內則設定drawable為null(當然你可以可以設定為你自己的預設資源),防止顯示了之前某個行item的圖片,解決了a. 行item圖片顯示重複問題。
         * 在ImageCache的OnImageCallbackListener的onImageLoaded函式中新增
         * if(!Cache.ICON_CACHE.get(imageUrl,imageView)){ imageView.setImageDrawable(null); }}
         */
//        Log.d("mylist", "dm.getImage() = " + dm.getImage());
//        Uri imgUri = Uri.parse(dm.getImage());
//        vh.imageViewPic.setImageURI(null);
//        vh.imageViewPic.setImageURI(imgUri);
//        new AsyncUploadImage(vh.imageViewPic).execute(dm.getImage()); // 圖片有出來但會亂

        vh.imageViewItemPic.setVisibility(View.GONE);

        // - TODO: Set Tag
        vh.textViewItemName.setText(dm.getName());
        vh.textViewItemPrice.setText(dm.getPrice());
        vh.textViewItemDesc.setText(dm.getDesc()); // 描述 暫時沒打開
        vh.imageViewItemPic.setTag(position + "imageViewItemPic");
        vh.textViewItemName.setTag(position + "textViewItemName");
        vh.textViewItemPrice.setTag(position + "textViewItemPrice");
        vh.textViewItemTotalPrice.setTag(position + "textViewItemTotalPrice");
        vh.textViewItemDesc.setTag(position + "textViewItemDesc");
        vh.textViewItemQty.setTag(position + "textViewItemQty");
        vh.buttonItemAdd.setTag(position + "buttonItemAdd");
        vh.buttonItemSub.setTag(position + "buttonItemSub");
        vh.price = Integer.parseInt(vh.textViewItemPrice.getText().toString());



        Log.d("mylist", "--------------- Tag ---------------" +
                "\n\tvh.itemTotalPrice = " + vh.itemTotalPrice +
                "\n\tvh.textViewItemTotalPrice.getText().toString() = " + vh.textViewItemTotalPrice.getText().toString() +
                "\n\t------------------------------");

        // - TODO: 方法 - 增加數量
        vh.buttonItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.textViewItemQty.setFocusable(true); // FIXME: 測試1_2
                if (vh.qty >= 0) {
                    vh.qty++;
                    vh.itemTotalPrice = vh.qty * vh.price;
                    vh.textViewItemQty.setText(vh.qty+""); // 整數 轉字串呈現
                    vh.textViewItemTotalPrice.setText(vh.itemTotalPrice+"");
                }
//                notifyDataSetChanged(); // 每次點擊都會即時更新 ListView 的畫面
//                Log.d("mylist", "--------------- add ---------------" +
//                        "\n\ttemp_qty[position] = " + temp_qty[position] +
//                        "\n\tvh.qty = " + vh.qty +
//                        "\n\tvh.price = " + vh.price +
//                        "\n\tvh.itemTotalItemPrice = " + vh.itemTotalItemPrice +
//                        "\n\tvh.textViewItemTotalPrice.getText().toString() = " + vh.textViewItemTotalPrice.getText().toString() +
//                        "\n\t------------------------------");
            }
        });

        // - TODO: 方法 - 減少數量
        vh.buttonItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.itemTotalPrice = 0;
                vh.textViewItemQty.setFocusable(true); // FIXME: 測試1_3
                if (vh.qty <= 0) {
                    vh.textViewItemQty.setText(0+""); // 整數 轉字串呈現

                } else {
                    vh.qty--;
                    vh.itemTotalPrice = vh.qty * vh.price;
                    vh.textViewItemQty.setText(vh.qty+""); // 整數 轉字串呈現
                    vh.textViewItemTotalPrice.setText(vh.itemTotalPrice+"");
                }
//                notifyDataSetChanged(); // 每次點擊都會即時更新 ListView 的畫面
                Log.d("mylist", "--------------- add ---------------" +
                        "\n\ttemp_qty[position] = " + temp_qty[position] +
                        "\n\tvh.qty = " + vh.qty +
                        "\n\tvh.price = " + vh.price +
                        "\n\tvh.itemTotalPrice = " + vh.itemTotalPrice +
                        "\n\tvh.textViewTotalPrice.getText().toString() = " + vh.textViewItemTotalPrice.getText().toString() +
                        "\n\t------------------------------");
            }
        });

        vh.textViewItemQty.setText(temp_qty[position] + ""); // 整數 轉字串呈現
        vh.itemTotalPrice = temp_qty[position] * vh.price;
        vh.textViewItemTotalPrice.setText(vh.itemTotalPrice + "");

        return convertView;
    }

    // TODO: --------------- 個人相關設定 ---------------

    // TODO: 宣告在 getView 會動到的 Item 元件 (優化Listview 避免重新加載)
    private class ViewHolder {
        /**
         * mageView does not has a setImageUrl method you need to either cast giftPicture as
         * NetworkImageView while calling Uri or change
         */
        ImageView imageViewItemPic; // imageView_item_pic
        TextView textViewItemName; // textView_item_name
        TextView textViewItemPrice; // textView_item_price
        TextView textViewItemTotalPrice; // textView_item_total_price
        TextView textViewItemDesc; // textView_item_desc
        TextView textViewItemQty; // textView_item_qty
        Button buttonItemAdd; // button_item_add
        Button buttonItemSub; // button_item_sub
        int qty;
        int price;
        int itemTotalPrice;
    }


}
