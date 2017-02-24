package com.starter.wulei.webcrawlertool.databse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wulei on 2017/2/23.
 */

public class DBHelper extends SQLiteOpenHelper {
    protected final static String DB_NAME = "cookings.db";
    private final static int DB_VERSION = 2;

    //菜谱列表表
    private final String CREATE_COOKINGS_TABLE = "CREATE TABLE IF NOT EXISTS [ST_COOKINGS](\n" +
            "    [id] INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "    [cooking_id] VARCHAR(100) NOT NULL, \n" +
            "    [cooking_type] INT, \n" +
            "    [name] VARCHAR(100) NOT NULL, \n" +
            "    [url] VARCHAR(100), \n" +
            "    [image_name] VARCHAR(100), \n" +
            "    [image] VARCHAR(200))";

    //菜谱详情表
    private final static String CREATE_COOKBOOKS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "[ST_COOKBOOKS]" +
            "(" +
            "[book_id] VARCHAR PRIMARY KEY," +
            "[book_title] VARCHAR," +
            "[book_img_name] VARCHAR," +
            "[book_img_path] VARCHAR," +
            "[book_intro] VARCHAR," +
            "[book_tips] VARCHAR," +
            "[book_type] INT" +
            ")";

    //菜谱材料表
    private final static String CREATE_MATERIAL_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "[ST_COOKINGMATERIALS]" +
            "(" +
            "[material_id] VARCHAR PRIMARY KEY," +
            "[material_difficulty] VARCHAR," +
            "[material_time] VARCHAR," +
            "[book_id] VARCHAR" +
            ")";

    //菜谱步骤表
    private final static String CREATE_STEPS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "[ST_COOKINGSTEPS] (" +
            "[step_order] INT," +
            "[step_name] VARCHAR," +
            "[step_img_name] VARCHAR," +
            "[step_img_path] VARCHAR," +
            "[book_id] VARCHAR" +
            ")";

    //食材信息表
    private final static String CREATE_INFO_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "[ST_MATERIALINFOS] (" +
            "[info_name] VARCHAR," +
            "[info_dosage] VARCHAR," +
            "[material_id] VARCHAR" +
            ")";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_COOKINGS_TABLE);
            db.execSQL(CREATE_COOKBOOKS_TABLE);
            db.execSQL(CREATE_MATERIAL_TABLE);
            db.execSQL(CREATE_STEPS_TABLE);
            db.execSQL(CREATE_INFO_TABLE);
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
