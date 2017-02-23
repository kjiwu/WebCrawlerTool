package com.starter.wulei.webcrawlertool.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wulei on 2017/2/23.
 */

public class DBHelper extends SQLiteOpenHelper {
    protected final static String DB_NAME = "cookings.db";

    public DBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
