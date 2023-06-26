package com.malawi.dmvicverification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static  final String TokenTable ="tokenvalue";
    private static final String QrTable="qrTable";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "malawiVerificationApp", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TokenTable + "(tokenValue TEXT)");
            db.execSQL("CREATE TABLE " + QrTable + "(baseUrl TEXT,color TEXT,language TEXT, ImageFile TEXT)");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TokenTable);
            db.execSQL("DROP TABLE IF EXISTS " + QrTable);
            onCreate(db);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean insertToken(String token)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("TokenValue",token);
            long result = db.insert(TokenTable,null, contentValues);
            if(result == -1)
            {
                return  false;
            }
            else
            {
                return  true;
            }
        }
        catch (Exception ex)
        {
            ex.getStackTrace();
            return false;
        }

    }

    public Cursor getTokenDetails()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from "+ TokenTable,null);
        return res;
    }

    public void deleteTokenData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TokenTable);
        return;
    }

    public boolean insertQrData(String url, String colorCode,String lang,String image)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("baseUrl",url);
            contentValues.put("color",colorCode);
            contentValues.put("language",lang);
            contentValues.put("ImageFile", image);
            long result = db.insert(QrTable,null, contentValues);
            if(result == -1)
            {
                return  false;
            }
            else
            {
                return  true;
            }
        }
        catch (Exception ex)
        {
            ex.getStackTrace();
            return false;
        }

    }

    public Cursor getQrData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from "+ QrTable,null);
        return res;
    }

    public void deleteQrData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ QrTable);
        return;
    }

}
