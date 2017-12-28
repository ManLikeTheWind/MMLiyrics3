package com.dxiang.mring3.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.dxiang.mring3.utils.Commons;


public class DbHelper extends SQLiteOpenHelper
{
    public static final String CONTENT = "content";
    
    public static final String TONETYPEID = "toneTypeID";
    
    public static final String TONETYPELABEL = "toneTypeLabel";
    
    public static final String PARENTTYPEID = "parentTypeID";
    
    public static final String IFLEAFNOD = "ifLeafNod";
    
    public static final String PICURL = "picURL";

    private static DbHelper mDbHelper;
    
    public static String TABLE_RECORDS = "records";
    
    public static String TABLE_RINGS = "ringtones";

    private String DB_CREATE = "create table if not exists records"
        + "(content text primary key);";
    
    private String DB_RINGS_CREATE = "create table if not exists ringtones" + 
    "(toneTypeID text primary key, toneTypeLabel text, parentTypeID text," +
    " ifLeafNod text, picURL text);";

    public static DbHelper getInstance(Context context)
    {
        if (mDbHelper == null)
        {
            mDbHelper = new DbHelper(context, Commons.DATABASE_NAME, null, Commons.DATABASE_VERSION);
        }

        return mDbHelper;
    }

    public DbHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
    	try 
    	{
    		db.execSQL(DB_CREATE);
    		db.execSQL(DB_RINGS_CREATE);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    	
    }

}
