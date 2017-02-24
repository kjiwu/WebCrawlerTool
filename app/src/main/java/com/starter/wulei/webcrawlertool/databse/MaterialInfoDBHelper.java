package com.starter.wulei.webcrawlertool.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.MaterialInfo;

import java.util.List;

/**
 * Created by kjiwu on 2017/2/23.
 */

public class MaterialInfoDBHelper extends DBHelper {

    private final static String TABLE_NAME = "ST_MATERIALINFOS";
    private final static String COLUMN_INFO_NAME = "info_name";
    private final static String COLUMN_INFO_DOSAGE = "info_dosage";
    private final static String COLUMN_MATERIAL_ID = "material_id";


    public MaterialInfoDBHelper(Context context) {
        super(context);
    }

    public void insertMaterialInfos(String materialId, List<MaterialInfo> infos) {
        SQLiteDatabase db = null;
        try {
            if(null == infos || infos.size() == 0) {
                return;
            }

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
