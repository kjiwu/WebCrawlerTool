package com.starter.wulei.webcrawlertool.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

import com.starter.wulei.webcrawlertool.models.CookBook;
import com.starter.wulei.webcrawlertool.models.CookingMaterial;
import com.starter.wulei.webcrawlertool.models.CookingStep;
import com.starter.wulei.webcrawlertool.models.MaterialInfo;
import com.starter.wulei.webcrawlertool.utilities.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjiwu on 2017/2/23.
 */

public class CookBookDBHelper extends DBHelper {

    private final static String TABLE_NAME = "ST_COOKBOOKS";
    private final static String COLUMN_ID = "book_id";
    private final static String COLUMN_TITLE = "book_title";
    private final static String COLUMN_IMG_NAME = "book_img_name";
    private final static String COLUMN_IMG_PATH = "book_img_path";
    private final static String COLUMN_INTRO = "book_intro";
    private final static String COLUMN_TIPS = "book_tips";
    private final static String COLUMN_TYPE = "book_type";

    private final static String TABLE_CM_NAME = "ST_COOKINGMATERIALS";
    private final static String COLUMN_CM_ID = "material_id";
    private final static String COLUMN_CM_DIFFICULTY = "material_difficulty";
    private final static String COLUMN_CM_TIME = "material_time";
    private final static String COLUMN_BOOK_ID = "book_id";

    private final static String TABLE_MI_NAME = "ST_MATERIALINFOS";
    private final static String COLUMN_MI_NAME = "info_name";
    private final static String COLUMN_MI_DOSAGE = "info_dosage";
    private final static String COLUMN_MATERIAL_ID = "material_id";

    private final static String TABLE_STEP_NAME = "ST_COOKINGSTEPS";

    private final static String COLUMN_STEP_ORDER = "step_order";
    private final static String COLUMN_STEP_NAME = "step_name";
    private final static String COLUMN_STEP_IMG_NAME = "step_img_name";
    private final static String COLUMN_STEP_IMG_PATH = "step_img_path";

    public CookBookDBHelper(Context context) {
        super(context);
    }

    public void insertCookBook(CookBook book) {
        SQLiteDatabase db = null;
        try {
            if(null == book) {
                return;
            }

            db = getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, book.getId());
            values.put(COLUMN_TITLE, book.getTitle());
            values.put(COLUMN_IMG_NAME, StringHelper.getImageName(book.getPic_path()));
            values.put(COLUMN_IMG_PATH, book.getPic_path());
            values.put(COLUMN_INTRO, book.getIntro());
            values.put(COLUMN_TYPE, book.getType());
            values.put(COLUMN_TIPS, StringHelper.getTipsString(book.getTips()));
            db.insert(TABLE_NAME, null, values);

            ContentValues values_cm = new ContentValues();
            CookingMaterial material = book.getMaterial();
            if(null != material) {
                values_cm.put(COLUMN_CM_ID, material.getUuid().toString());
                values_cm.put(COLUMN_CM_DIFFICULTY, material.getDifficulty());
                values_cm.put(COLUMN_CM_TIME, material.getCookingTime());
                values_cm.put(COLUMN_BOOK_ID, book.getId());
                db.insert(TABLE_CM_NAME, null, values_cm);

                List<MaterialInfo> infos_m = material.getMainMaterials();
                List<MaterialInfo> infos_i = material.getIngredients();
                ArrayList<MaterialInfo> infos = new ArrayList<>();
                infos.addAll(infos_m);
                infos.addAll(infos_i);
                for (MaterialInfo info : infos) {
                    ContentValues values_mi = new ContentValues();
                    values.put(COLUMN_MI_NAME, info.getName());
                    values.put(COLUMN_MI_DOSAGE, info.getDosage());
                    values.put(COLUMN_MATERIAL_ID, material.getUuid().toString());
                    db.insert(TABLE_MI_NAME, null, values_mi);
                }
            }

            List<CookingStep> steps = book.getSteps();
            if(null != steps && steps.size() > 0) {
                for (CookingStep step : steps) {
                    ContentValues values_st = new ContentValues();
                    values.put(COLUMN_STEP_ORDER, step.getOrder());
                    values.put(COLUMN_STEP_NAME, step.getName());
                    values.put(COLUMN_STEP_IMG_NAME, StringHelper.getImageName(step.getImg_path()));
                    values.put(COLUMN_STEP_IMG_PATH, step.getImg_path());
                    values.put(COLUMN_BOOK_ID, book.getId());
                    db.insert(TABLE_STEP_NAME, null, values_st);
                }
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

}
