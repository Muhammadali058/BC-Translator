package com.braincoder.bctranslator.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.braincoder.bctranslator.Models.Languages;
import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB";
    private static final String TABLE_LANGUAGES = "languages";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_LANGUAGES +
                "(id integer primary key autoincrement," +
                "language text," +
                "languageCode text)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGES);

        // Create tables again
        onCreate(db);
    }

    @SuppressLint("Range")
    public List<Languages> getAllLanguages(){
        SQLiteDatabase db = getReadableDatabase();
        List<Languages> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_LANGUAGES, null);
        if(cursor != null){
            while (cursor.moveToNext()){
                Languages language = new Languages();

                language.setId(cursor.getInt(cursor.getColumnIndex("id")));
                language.setLanguage(cursor.getString(cursor.getColumnIndex("language")));
                language.setLanguageCode(cursor.getString(cursor.getColumnIndex("languageCode")));

                list.add(language);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    public void addLanguage(Languages language){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("language", language.getLanguage());
        values.put("languageCode", language.getLanguageCode());

        int id = (int) db.insert(TABLE_LANGUAGES, null, values);
        db.close();
    }

    public void updateLanguage(Languages language){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("language", language.getLanguage());
        values.put("languageCode", language.getLanguageCode());

        db.update(TABLE_LANGUAGES, values, "id=?", new String[]{String.valueOf(language.getId())});
        db.close();
    }

    public void deleteLanguage(int id){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_LANGUAGES, "id=?", new String[]{String.valueOf(id)});

        db.close();
    }

}
