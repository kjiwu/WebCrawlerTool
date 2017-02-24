package com.starter.wulei.webcrawlertool.databse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wulei on 2017/2/23.
 */

public class DBHelper extends SQLiteOpenHelper {
    protected final static String DB_NAME = "cookings.db";
    private final static int DB_VERSION = 1;

    public final static String COLUMN_COOKING_ID = "cooking_id";
    public final static String COLUMN_COOKING_TYPE = "cooking_type";
    public final static String COLUMN_COOKING_NAME = "cooking_name";
    public final static String COLUMN_COOKING_URL = "cooking_url";
    public final static String COLUMN_COOKING_IMG_NAME = "cooking_img_name";
    public final static String COLUMN_COOKING_IMG = "cooking_image";
    public final static String COLUMN_COOKING_DIFF = "cooking_difficulty";
    public final static String COLUMN_COOKING_MATERIALS = "cooking_materials";


    //菜谱列表表
    private final String CREATE_COOKINGS_TABLE = "CREATE TABLE [ST_COOKINGS](\n" +
            "    [cooking_id] VARCHAR(100) PRIMARY KEY NOT NULL, \n" +
            "    [cooking_type] INT, \n" +
            "    [cooking_name] VARCHAR(100) NOT NULL, \n" +
            "    [cooking_url] VARCHAR(100), \n" +
            "    [cooking_img_name] VARCHAR(100), \n" +
            "    [cooking_image] VARCHAR(200), \n" +
            "    [cooking_difficulty] NVARCHAR(50), \n" +
            "    [cooking_materials] VARCHAR(200));";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_COOKINGS_TABLE);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
