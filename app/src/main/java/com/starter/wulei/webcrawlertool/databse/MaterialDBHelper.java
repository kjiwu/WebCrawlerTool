package com.starter.wulei.webcrawlertool.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.STCookbookMaterialItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wulei on 2017/3/2.
 */

public class MaterialDBHelper extends DBHelper {

    private final static String MATERIALS_TABLE_NAME = "st_materials";

    public final static String MATERIALS_COLUMN_ID = "material_id";
    public final static String MATERIALS_COLUMN_TYPE = "material_type";
    public final static String MATERIALS_COLUMN_NAME = "material_name";
    public final static String MATERIALS_COLUMN_IMG = "material_image";

    public final static String CREATE_MATERIAL_TABLE = "CREATE TABLE IF NOT EXISTS ST_MATERIALS (" +
            "[material_id] integer primary key autoincrement," +
            "[material_type] varchar(100) not null," +
            "[material_name] varchar(100) not null," +
            "[material_image] varchar(200)" +
            ");";

    public MaterialDBHelper(Context context) {
        super(context, 4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 1:
            case 2:
            case 3:
            case 4:
                db.execSQL(CREATE_MATERIAL_TABLE);
        }
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void insert(STCookbookMaterialItem item) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(MATERIALS_COLUMN_TYPE, item.type);
            values.put(MATERIALS_COLUMN_NAME, item.name);
            values.put(MATERIALS_COLUMN_IMG, item.image);
            db.insert(MATERIALS_TABLE_NAME, null, values);
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

    public Map<String, List<STCookbookMaterialItem>> getMaterials() {
        Map<String, List<STCookbookMaterialItem>> items = null;
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select distinct material_type from st_materials", null, null);
            while (cursor.moveToNext()) {
                String type = cursor.getString(0);
                String sql = "select material_name, material_image from st_materials where material_type='" + type + "';";
                Cursor cursor_material = db.rawQuery(sql, null, null);
                List<STCookbookMaterialItem> materials = new ArrayList<>();
                items = new HashMap<>();
                while (cursor_material.moveToNext()) {
                    STCookbookMaterialItem material = new STCookbookMaterialItem();
                    material.type = type;
                    material.name = cursor_material.getString(cursor_material.getColumnIndex(MATERIALS_COLUMN_NAME));
                    material.image = cursor_material.getString(cursor_material.getColumnIndex(MATERIALS_COLUMN_IMG));
                    materials.add(material);
                }
                items.put(type, materials);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(null != db) {
                db.close();
            }
        }

        return items;
    }
}
