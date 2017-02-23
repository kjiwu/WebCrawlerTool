package com.starter.wulei.webcrawlertool.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.CookingMaterial;

/**
 * Created by kjiwu on 2017/2/23.
 */

public class CookingMaterialDBHelper extends DBHelper {

    private final static int VERSION = 1;
    private final static String TABLE_NAME = "TS_COOKINGMATERIALS";

    private final static String COLUMN_ID = "material_id";
    private final static String COLUMN_DIFFICULTY = "material_difficulty";
    private final static String COLUMN_TIME = "material_time";
    private final static String COLUMN_BOOK_ID = "book_id";


    private final static String CREATE_MATERIAL_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "[TS_COOKINGMATERIALS]" +
            "(" +
            "[material_id] VARCHAR PRIMARY KEY," +
            "[material_difficulty] VARCHAR," +
            "[material_time] VARCHAR," +
            "[book_id] VARCHAR" +
            ")";

    public CookingMaterialDBHelper(Context context) {
        super(context, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MATERIAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void insertCookingMaterial(String bookId, CookingMaterial material) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, material.getUuid().toString());
            values.put(COLUMN_DIFFICULTY, material.getDifficulty());
            values.put(COLUMN_TIME, material.getCookingTime());
            values.put(COLUMN_BOOK_ID, bookId);
            db.insert(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        }
        finally {
            if (null != db) {
                db.endTransaction();
                db.close();
            }
        }
    }
}
