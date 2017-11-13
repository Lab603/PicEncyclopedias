package com.lab603.picencyclopedias.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by FutureApe on 2017/10/1.
 */

public class CollectionDataManager {

    //一些宏定义和声明
    private static final String TAG = "CollectionDataManager";
    private static final String DB_NAME = "collection_database";
    private static final String TABLE_NAME = "collection";
    public static final String URI = "uri";
    public static final String RESULT = "result";
    public static final String SAVE = "save";
    public static final String INDEX = "index";
    private static final int DB_VERSION = 2;
    private Context mContext = null;

    //创建表
    private static final String DB_CREATE = "create table collection(" +
            "id integer primary key autoincrement, " +
            "uri text, " +
            "result text, " +
            "save text)";

    private static final String TB_CREATE = "create table collection_save(" +
            "id integer primary key, " +
            "uri text, " +
            "result text)";
    private SQLiteDatabase mSQLiteDatabase = null;
    private DataBaseManagementHelper mDatabaseHelper = null;

    //DataBaseManagementHelper继承自SQLiteOpenHelper
    private static class DataBaseManagementHelper extends SQLiteOpenHelper {

        DataBaseManagementHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        /**
         * 创建表
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG,"db.getVersion()="+db.getVersion());
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            db.execSQL(DB_CREATE);
            db.execSQL(TB_CREATE);
            Log.i(TAG, "db.execSQL(DB_CREATE)");
            Log.e(TAG, DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "DataBaseManagementHelper onUpgrade");
            onCreate(db);
        }
    }

    public CollectionDataManager(Context context) {
        mContext = context;
        Log.i(TAG, "UserDataManager construction!");
    }
    //打开数据库
    public void openDataBase() throws SQLException {
        mDatabaseHelper = new DataBaseManagementHelper(mContext);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }
    //关闭数据库
    public void closeDataBase() throws SQLException {
        mDatabaseHelper.close();
    }
    //添加新记录
    public void insertCollectionData(CollectionData collectionData) {
        String uri=collectionData.getUri();
        String result=collectionData.getResult();
        String save = collectionData.getSave().toString();
        Log.e("uri",uri);
        Log.e("result",result);
        Log.e("save",save);
        ContentValues values = new ContentValues();
        values.put(SAVE, save);
        values.put(RESULT, result);
        values.put(URI, uri);
        mSQLiteDatabase.insert(TABLE_NAME, null, values);
//        mSQLiteDatabase.close();
    }
    //添加一条新的记录
    public void insertTOSave(String uri, String result){
        ContentValues values = new ContentValues();
        values.put("uri", uri);
        values.put("result", result);
        mSQLiteDatabase.insert("collection_save", null, values);
    }
    //读取
    public ArrayList<CollectionData> queryToSave(){
        Cursor cursor = mSQLiteDatabase.query("collection_save", null, null, null, null, null, null);
        ArrayList<CollectionData> collectionList = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                CollectionData collectionData = new CollectionData();
                String uri = cursor.getString(cursor.getColumnIndex("uri"));
                String result = cursor.getString(cursor.getColumnIndex("result"));
//                String save_cr = cursor.getString(cursor.getColumnIndex("save"));
//                Log.e("cursor查询结果save",save_cr);
//                Boolean save;
//                if (save_cr.equals("true")) {
//                    save = true;
//                } else {
//                    if (save_cr.equals("false")) {
//                        save = false;
//                    } else
//                        continue;
//                }
                collectionData.setUri(uri);
                collectionData.setResult(result);
//                collectionData.setSave(save);
                collectionList.add(collectionData);
            } while (cursor.moveToPrevious());
            cursor.close();
            return collectionList;
        }
        return null;
    }
    //更新
    public boolean updateCollectionData(CollectionData collectionData) {
        String uri=collectionData.getUri();
        String result=collectionData.getResult();
        Boolean save = collectionData.getSave();
        ContentValues values = new ContentValues();
        values.put(SAVE,save);
        values.put(RESULT, result);
        values.put(URI, uri);
        return mSQLiteDatabase.update(TABLE_NAME, values, URI + "=" + uri, null) > 0;
        //return mSQLiteDatabase.update(TABLE_NAME, values, ID + "=" + id, null) > 0;
    }

    //根据uri删除
    public boolean deleteCollectionData(String uri) {
        return mSQLiteDatabase.delete(TABLE_NAME, "uri = ?" , new String[]{uri}) > 0;
    }


    //所有记录的条数
    public int getCount(){
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        int size = cursor.getCount();
        cursor.close();
        return size;
    }

    public int getSVCount(){
        Cursor cursor = mSQLiteDatabase.query("collection_save", null, null, null, null, null, null);
        int size = cursor.getCount();
        cursor.close();
        return size;
    }

    //查询所有的记录
    public ArrayList<CollectionData> queryAll() {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        ArrayList<CollectionData> collectionList = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                CollectionData collectionData = new CollectionData();
                String uri = cursor.getString(cursor.getColumnIndex("uri"));
                String result = cursor.getString(cursor.getColumnIndex("result"));
                String save_cr = cursor.getString(cursor.getColumnIndex("save"));
//                Log.e("cursor查询结果save",save_cr);
                Boolean save;
                if (save_cr.equals("true")) {
                    save = true;
                } else {
                    if (save_cr.equals("false")) {
                        save = false;
                    } else
                        continue;
                }
                collectionData.setUri(uri);
                collectionData.setResult(result);
                collectionData.setSave(save);
                collectionList.add(collectionData);
//                collectionData = null;
            } while (cursor.moveToPrevious());
            cursor.close();
            return collectionList;
        }
        return null;
    }

    //查询收藏了的记录
    public ArrayList<CollectionData> querySaved() {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME,null,SAVE + "=" + true,null,null,null,null);
        CollectionData collectionData = new CollectionData();
        ArrayList<CollectionData> collectionDatas = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                String uri = cursor.getString(cursor.getColumnIndex("uri"));
                String result = cursor.getString(cursor.getColumnIndex("result"));
                String save_cr = cursor.getString(cursor.getColumnIndex("save"));
                Boolean save = null;
                if (save_cr.equals("true")){
                    save=true;
                }else {
                    if (save_cr.equals("false")){
                        save = false;
                    }
                    else
                        continue;
                }
                collectionData.setUri(uri);
                collectionData.setResult(result);
                collectionData.setSave(save);
                collectionDatas.add(collectionData);
            }while (cursor.moveToNext());
            cursor.close();
            return collectionDatas;
        }
        return null;
    }

    //删除所有
    public boolean deleteAllDatas() {
        return mSQLiteDatabase.delete(TABLE_NAME, null, null) > 0;
    }

    //根据uri查找，可以判断该记录是否已经存在
    public int findCollectionByUri(String uri){
        Log.i(TAG,"findUserByName , userName="+uri);
        int result=0;
        Cursor mCursor=mSQLiteDatabase.query(TABLE_NAME, null, URI+"="+uri, null, null, null, null);
        if(mCursor!=null){
            result=mCursor.getCount();
            mCursor.close();
            Log.i(TAG,"findUserByName , result="+result);
        }
        return result;
    }
}
