package com.starter.wulei.webcrawlertool.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.starter.wulei.webcrawlertool.models.CookBook;

/**
 * Created by kjiwu on 2017/2/23.
 */

public class CookBookDBHelper extends DBHelper {

    private final static int VERSION = 1;
    private final static String TABLE_NAME = "TS_COOKBOOKS";

    private final static String COLUMN_ID = "book_id";
    private final static String COLUMN_TITLE = "book_title";
    private final static String COLUMN_IMG_NAME = "book_img_name";
    private final static String COLUMN_IMG_PATH = "book_img_path";
    private final static String COLUMN_INTRO = "book_intro";
    private final static String COLUMN_TIPS = "book_tips";


    private final static String CREATE_COOKBOOKS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "[TS_COOKBOOKS]" +
            "(" +
            "[book_id] VARCHAR PRIMARY KEY," +
            "[book_title] VARCHAR," +
            "[book_img_name] VARCHAR," +
            "[book_img_path] VARCHAR," +
            "[book_intro] VARCHAR," +
            "[book_tips] VARCHAR" +
            ")";

    public CookBookDBHelper(Context context) {
        super(context, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COOKBOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
            values.put(COLUMN_TIPS, StringHelper.getTipsString(book.getTips()));
            db.insert(TABLE_NAME, null, values);
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
