package com.starter.wulei.webcrawlertool.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.CookingStep;

import java.util.List;

/**
 * Created by kjiwu on 2017/2/23.
 */

public class CookStepsDBHelper extends DBHelper {

    private final static int VERSION = 1;
    private final static String TABLE_NAME = "TS_COOKINGSTEPS";

    private final static String COLUMN_STEP_ORDER = "step_order";
    private final static String COLUMN_STEP_NAME = "step_name";
    private final static String COLUMN_STEP_IMG_NAME = "step_img_name";
    private final static String COLUMN_STEP_IMG_PATH = "step_img_path";
    private final static String COLUMN_BOOK_ID = "book_id";

    private final static String CREATE_STEPS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "[TS_COOKINGSTEPS] (" +
            "[step_order] INT," +
            "[step_name] VARCHAR," +
            "[step_img_name] VARCHAR," +
            "[step_img_path] VARCHAR," +
            "[book_id] VARCHAR" +
            ")";

    public CookStepsDBHelper(Context context) {
        super(context, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void insertSteps(String bookId, List<CookingStep> steps) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            for (CookingStep step : steps) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_STEP_ORDER, step.getOrder());
                values.put(COLUMN_STEP_NAME, step.getName());
                values.put(COLUMN_STEP_IMG_NAME, StringHelper.getImageName(step.getImg_path()));
                values.put(COLUMN_STEP_IMG_PATH, step.getImg_path());
                values.put(COLUMN_BOOK_ID, bookId);
                db.insert(TABLE_NAME, null, values);
            }
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
