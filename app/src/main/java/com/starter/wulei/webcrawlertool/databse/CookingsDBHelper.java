package com.starter.wulei.webcrawlertool.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.starter.wulei.webcrawlertool.models.CookingItem;
import com.starter.wulei.webcrawlertool.utilities.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingsDBHelper extends DBHelper {

    private final static String COOKINGS_TABLE_NAME = "ST_COOKINGS";

    private final static String COLUMN_COOKING_ID = "cooking_id";
    private final static String COLUMN_COOKING_TYPE = "cooking_type";
    private final static String COLUMN_COOKING_NAME = "name";
    private final static String COLUMN_COOKING_URL = "url";
    private final static String COLUMN_COOKING_IMG_NAME = "image_name";
    private final static String COLUMN_COOKING_IMG = "image";

    public  CookingsDBHelper(Context context) {
        super(context);
    }

    public void insertCooking(List<CookingItem> cookings) {
        SQLiteDatabase db = null;
        try {
            if(null == cookings || cookings.size() == 0) {
                return;
            }

            db = getWritableDatabase();
            db.beginTransaction();
            for (CookingItem cooking : cookings) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_COOKING_NAME, cooking.name);
                values.put(COLUMN_COOKING_URL, cooking.url);
                values.put(COLUMN_COOKING_IMG, cooking.image);
                values.put(COLUMN_COOKING_IMG_NAME, StringHelper.getImageName(cooking.image));
                values.put(COLUMN_COOKING_ID, StringHelper.getCookingId(cooking.url));
                values.put(COLUMN_COOKING_TYPE, cooking.type);
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
}
