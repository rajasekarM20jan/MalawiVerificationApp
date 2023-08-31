package com.malawi.dmvicverification;

import static com.malawi.dmvicverification.MainActivity.base_version;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static  final String TokenTable ="tokenvalue";
    private static final String QrTable="qrTable";

    private static final String termsTable="termsTable";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "malawiVerificationApp", null, base_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TokenTable + "(tokenValue TEXT)");
            db.execSQL("CREATE TABLE " + QrTable + "(baseUrl TEXT,color TEXT,language TEXT, ImageFile TEXT)");
            db.execSQL("CREATE TABLE " + termsTable + "(termsValue TEXT)");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TokenTable);
            db.execSQL("DROP TABLE IF EXISTS " + QrTable);
            db.execSQL("DROP TABLE IF EXISTS " + termsTable);
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

    public boolean insertTerms(String terms)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("termsValue",terms);
            long result = db.insert(termsTable,null, contentValues);
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

    public Cursor getTerms()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from "+ termsTable,null);
        return res;
    }

    public void deleteTerms()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ termsTable);
        return;
    }
}
