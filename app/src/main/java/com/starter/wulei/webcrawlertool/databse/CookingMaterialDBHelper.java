package com.starter.wulei.webcrawlertool.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.CookingMaterial;

/**
 * Created by kjiwu on 2017/2/23.
 */

public class CookingMaterialDBHelper extends DBHelper {

    private final static String TABLE_NAME = "ST_COOKINGMATERIALS";

    private final static String COLUMN_ID = "material_id";
    private final static String COLUMN_DIFFICULTY = "material_difficulty";
    private final static String COLUMN_TIME = "material_time";
    private final static String COLUMN_BOOK_ID = "book_id";


    public CookingMaterialDBHelper(Context context) {
        super(context);
    }

    public void insertCookingMaterial(String bookId, CookingMaterial material) {
        SQLiteDatabase db = null;
        try {
            if(null == material || null == bookId) {
                return;
            }

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
