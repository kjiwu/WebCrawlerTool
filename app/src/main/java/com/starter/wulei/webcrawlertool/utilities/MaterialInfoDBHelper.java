package com.starter.wulei.webcrawlertool.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.MaterialInfo;

import java.util.List;

/**
 * Created by kjiwu on 2017/2/23.
 */

public class MaterialInfoDBHelper extends DBHelper {

    private final static int VERSION = 1;

    private final static String TABLE_NAME = "TS_MATERIALINFOS";
    private final static String COLUMN_INFO_NAME = "info_name";
    private final static String COLUMN_INFO_DOSAGE = "info_dosage";
    private final static String COLUMN_MATERIAL_ID = "material_id";


    private final static String CREATE_INFO_TABLE = "CREATE TABLE IF NOT EXITS " +
            "[TS_MATERIALINFOS] (" +
            "[info_name] VARCHAR," +
            "[info_dosage] VARCHAR," +
            "[material_id] VARCHAR" +
            ")";

    public MaterialInfoDBHelper(Context context) {
        super(context, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void insertMaterialInfos(String materialId, List<MaterialInfo> infos) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            for (MaterialInfo info : infos) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_INFO_NAME, info.getName());
                values.put(COLUMN_INFO_DOSAGE, info.getDosage());
                values.put(COLUMN_MATERIAL_ID, materialId);
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            if(null != db) {
                db.endTransaction();
                db.close();
            }
        }
    }
}
