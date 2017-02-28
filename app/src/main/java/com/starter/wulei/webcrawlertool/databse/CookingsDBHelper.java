package com.starter.wulei.webcrawlertool.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.CookingItem;
import com.starter.wulei.webcrawlertool.models.CookingMaterial;
import com.starter.wulei.webcrawlertool.utilities.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingsDBHelper extends DBHelper {

    private final static String COOKINGS_TABLE_NAME = "ST_COOKINGS";

    public  CookingsDBHelper(Context context) {
        super(context);
    }

    public void insertCooking(CookingItem cooking) {
        if(null == cooking) {
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(COLUMN_COOKING_NAME, cooking.name);
            values.put(COLUMN_COOKING_URL, cooking.url);
            values.put(COLUMN_COOKING_IMG, cooking.image);
            values.put(COLUMN_COOKING_IMG_NAME, StringHelper.getImageName(cooking.image));
            values.put(COLUMN_COOKING_ID, StringHelper.getCookingId(cooking.url));
            values.put(COLUMN_COOKING_TYPE, cooking.type);
            values.put(COLUMN_COOKING_DIFF, cooking.difficulity);
            values.put(COLUMN_COOKING_MATERIALS, cooking.materials);
            db.insert(COOKINGS_TABLE_NAME, "name,url,image", values);
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

    public void updateCooking(String cooking_id, CookingMaterial material) {
        if(null == material || null == cooking_id) {
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(COLUMN_COOKING_DIFF, material.difficulty);
            values.put(COLUMN_COOKING_MATERIALS, material.materials);
            db.update(COOKINGS_TABLE_NAME,
                    values,
                    COLUMN_COOKING_ID + "=?",
                    new String[] { cooking_id });
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

    public List<String> getCookingUrls() {
        SQLiteDatabase db = null;
        ArrayList<String> result = null;
        try {
            db = getReadableDatabase();
            Cursor cursor = db.query(COOKINGS_TABLE_NAME,
                    new String[] { COLUMN_COOKING_URL },
                    null, null, null, null, null);
            if(cursor.getCount() > 0) {
                result = new ArrayList<>();
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    result.add(cursor.getString(0));
                }
            }
        }
        finally {
            if(null != db) {
                db.close();
            }
        }
        return result;
    }

    public List<String> getCookingImageUrls(int count) {
        SQLiteDatabase db = null;
        ArrayList<String> result = null;
        try {
            db = getReadableDatabase();
            Cursor cursor = db.query(COOKINGS_TABLE_NAME,
                    new String[] { COLUMN_COOKING_IMG },
                    null, null, null, null, null, count > 0 ? String.valueOf(count) : null);
            if(cursor.getCount() > 0) {
                result = new ArrayList<>();
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    result.add(cursor.getString(0));
                }
            }
        }
        finally {
            if(null != db) {
                db.close();
            }
        }
        return result;
    }
}
