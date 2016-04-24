package com.zjy.trafficassist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by ZJY on 2016/4/14.
 */
public class DatabaseManager {

    private DatabaseHelper helper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        helper = new DatabaseHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        database = helper.getWritableDatabase();

    }

    public void Register(User user) {
        database.beginTransaction();  //开始事务
        try {
            database.execSQL("INSERT INTO user VALUES(null, ?, ?)", new Object[]{user.getUsername(), user.getPassword()});
            database.setTransactionSuccessful();  //设置事务成功完成
            System.out.println("写入成功");
        } finally {
            database.endTransaction();    //结束事务
        }
        //database.close();
    }

    public boolean Login(User user) {
        String sql="select * from user where username=? and password=?";
        Cursor cursor=database.rawQuery(sql, new String[]{user.getUsername(),user.getPassword()});
        //Cursor cursor=database.rawQuery(sql, new String[]{username,password});
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        return false;
    }

    public long getUserCount(){
        //获取数据总数
        Cursor cursor =database.rawQuery("select count(*) from user", null);
        cursor.moveToFirst();
        long reslut=cursor.getLong(0);
        return reslut;
    }
}
