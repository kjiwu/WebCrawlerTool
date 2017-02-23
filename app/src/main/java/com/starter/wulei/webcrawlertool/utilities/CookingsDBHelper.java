package com.starter.wulei.webcrawlertool.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.CookingItem;

import java.util.List;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingsDBHelper extends DBHelper {

    private final static String COOKINGS_TABLE_NAME = "ST_COOKINGS";
    private final static int VERSION = 1;

    private final String CREATE_COOKINGS_TABLE = "CREATE TABLE IF NOT EXISTS [ST_COOKINGS](\n" +
            "    [id] INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "    [cooking_id] VARCHAR(100) NOT NULL, \n" +
            "    [cooking_type] INT, \n" +
            "    [name] VARCHAR(100) NOT NULL, \n" +
            "    [url] VARCHAR(100), \n" +
            "    [image_name] VARCHAR(100), \n" +
            "    [image] VARCHAR(200))";

    public  CookingsDBHelper(Context context) {
        super(context, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COOKINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertCooking(List<CookingItem> cookings) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            for (CookingItem cooking : cookings) {
                ContentValues values = new ContentValues();
                values.put("name", cooking.name);
                values.put("url", cooking.url);
                values.put("image", cooking.image);
                db.insert(COOKINGS_TABLE_NAME, "name,url,image", values);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(null != db) {
                db.endTransaction();
                db.close();
            }
        }
    }
}
