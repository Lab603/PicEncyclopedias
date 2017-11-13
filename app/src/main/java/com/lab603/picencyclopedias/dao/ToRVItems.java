package com.lab603.picencyclopedias.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import com.lab603.picencyclopedias.MenuFragment;
import com.lab603.picencyclopedias.bean.RvItem;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by FutureApe on 2017/10/7.
 */

public class ToRVItems {

    private static final int INPUT_SIZE = 224;

    public static RvItem toRvItem(Context context,CollectionData collectionData){
        RvItem rvItem = new RvItem();
        String uriStr = collectionData.getUri();
        Uri uri = Uri.parse(uriStr);
        ContentResolver cr = context.getContentResolver();
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.e("Exception", e.getMessage(), e);
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) INPUT_SIZE) / width;
        float scaleHeight = ((float) INPUT_SIZE) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        rvItem.setBitmap(bitmap);
        rvItem.setResult_str(collectionData.getResult());
        rvItem.setSave(collectionData.getSave());
        return rvItem;
    }

    public ArrayList<RvItem> querySave(Context context,CollectionDataManager manager){
        ArrayList<CollectionData> collectionDatas;
        ArrayList<RvItem> rvItemArrayList;

        collectionDatas = manager.queryToSave();
        rvItemArrayList = toRvItemTool(context,collectionDatas);
        return rvItemArrayList;
    }
    //转换所有记录
    public ArrayList<RvItem> queryAll(Context context,CollectionDataManager manager){
        ArrayList<CollectionData> collectionDatas;
        ArrayList<RvItem> rvItemArrayList ;

        collectionDatas = manager.queryAll();
        rvItemArrayList = toRvItemTool(context,collectionDatas);
        return rvItemArrayList;
    }

    //转换存储的记录
    public ArrayList<RvItem> querySaved(Context context,CollectionDataManager manager){
        ArrayList<CollectionData> collectionDatas;
        ArrayList<RvItem> rvItemArrayList;
        collectionDatas = manager.querySaved();
        rvItemArrayList = toRvItemTool(context,collectionDatas);
        return rvItemArrayList;
    }

    private ArrayList<RvItem> toRvItemTool(Context context ,ArrayList<CollectionData> collectionDatas){
        ArrayList<RvItem> rvItemArrayList = new ArrayList<>();
//        collectionDatas = collectionDataManager.queryAll();

        if (collectionDatas == null){
            return null;
        }else{
            for (int i = 0; i < collectionDatas.size();i++){
                RvItem rvItem = new RvItem();
                String uriStr = collectionDatas.get(i).getUri();
                Uri uri = Uri.parse(uriStr);
                ContentResolver cr = context.getContentResolver();
                Bitmap bitmap = null;
                try{
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scaleWidth = ((float) INPUT_SIZE) / width;
                float scaleHeight = ((float) INPUT_SIZE) / height;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                rvItem.setBitmap(bitmap);
                rvItem.setResult_str(collectionDatas.get(i).getResult());
                rvItem.setSave(collectionDatas.get(i).getSave());
                rvItem.setUri(collectionDatas.get(i).getUri());
                rvItemArrayList.add(rvItem);
            }
            Log.e("查询结果",rvItemArrayList.size()+"");
            return rvItemArrayList;
        }
    }


}
