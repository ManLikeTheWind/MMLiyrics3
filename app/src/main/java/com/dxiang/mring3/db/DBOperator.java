package com.dxiang.mring3.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dxiang.mring3.bean.ToneTypeAPPInfo;

public class DBOperator
{
    public static boolean insertData(SQLiteDatabase db, String content)
    {
        ContentValues cv = new ContentValues();
        
        cv.put(DbHelper.CONTENT, content);

        long result = db.insert(DbHelper.TABLE_RECORDS, DbHelper.CONTENT, cv);
        boolean flag = true;
        if (result == -1)
        {
            flag = false;
        }
        return flag;
    }

    public static boolean update(SQLiteDatabase db, String oldCon, String newCon)
    {
        boolean flag = true;
        try
        {
            // 			ContentValues cv = new ContentValues();
            // 	    	  cv.put(DbHelper.ID, p.getId());
            // 	    	  cv.put(DbHelper.TIME, p.getTime());
            // 	    	  cv.put(DbHelper.MOOD, p.getMood());
            // 	    	  cv.put(DbHelper.TITLE, p.getTitle());
            // 	    	  cv.put(DbHelper.CONTENT, p.getContent());
            // 	    	 db.update("notes", cv, " id =?", new String[]{
            // 	    			 String.valueOf(p.getId())});
            db.execSQL(
                "update records set content = ? where content = ?",
                new Object[]{newCon, oldCon});
        }
        catch (Exception e)
        {
            flag = false;
        }
        return flag;
        // 		ContentValues values = new ContentValues();
        // 		values.put("name", p.getName());
        // 		values.put("phone", p.getPhone());
        // 		values.put("amount", p.getAmount());
        // 		db.update("person", values, "personid=?", new String[]{p.getId().toString()});
    }

    public static boolean delete(SQLiteDatabase db, String id)
    {
        String sql = "delete from " + DbHelper.TABLE_RECORDS + " where "+DbHelper.CONTENT+" = '" + id + "'";

        boolean flag = true;
        try
        {
            db.execSQL(sql);
        }
        catch (SQLException e)
        {
            flag = false;
        }
        finally
        {
            return flag;
        }
    }
    
    public static boolean deleteAll(SQLiteDatabase db)
    {
        String sql = "delete from " + DbHelper.TABLE_RECORDS;

        boolean flag = true;
        try
        {
            db.execSQL(sql);
        }
        catch (SQLException e)
        {
            flag = false;
        }
        finally
        {
            return flag;
        }
    }
    
    public static final boolean hasRecord(SQLiteDatabase db, String record)
    {
    	String sql = "select * from " + DbHelper.TABLE_RECORDS + " where content = '" + record + "'";
    	boolean flag = true;
        try
        {
        	Cursor cursor = db.rawQuery(sql, null);
        	if(cursor == null || cursor.getCount() == 0)
        	{
        		return false;
        	}
        	
        	return true;
        }
        catch (SQLException e)
        {
            flag = false;
        }
        finally
        {
            return flag;
        }
    }

    public static Cursor queryAll(SQLiteDatabase db)
    {
        Cursor cr = db.query(DbHelper.TABLE_RECORDS, new String[]{DbHelper.CONTENT}, null, null, null, null, null);
        return cr;
    }
    
    //查询Home页面 所有的广告数据    
    public static Cursor queryAllRings(SQLiteDatabase db)
    {
        Cursor cr = db.query(DbHelper.TABLE_RINGS, new String[]{DbHelper.TONETYPEID, DbHelper.IFLEAFNOD, DbHelper.PARENTTYPEID
        		, DbHelper.PICURL, DbHelper.TONETYPELABEL}, null, null, null, null, null);
        return cr;
    }
    
    //添加Home页面广告记录到数据库
    public static boolean insertData(SQLiteDatabase db, ToneTypeAPPInfo info)
    {
    	synchronized (db) {
    		 ContentValues cv = new ContentValues();
    	        cv.put(DbHelper.IFLEAFNOD, info.getIfLeafNod());
    	        cv.put(DbHelper.PARENTTYPEID, info.getParentTypeID());
    	        cv.put(DbHelper.PICURL, info.getPicURL());
    	        cv.put(DbHelper.TONETYPEID, info.getToneTypeID());
    	        cv.put(DbHelper.TONETYPELABEL, info.getToneTypeLabel());

    	        long result = db.insert(DbHelper.TABLE_RINGS, DbHelper.TONETYPEID, cv);
    	        boolean flag = true;
    	        if (result == -1)
    	        {
    	            flag = false;
    	        }
    	        return flag;
		}
       
    }
    
    //删除Home页面的所有一级数据
    public static boolean deleteRings(SQLiteDatabase db)
    {
    	synchronized (db) {
    		Cursor cursor = queryAllRings(db);
        	if(cursor == null || cursor.getCount() == 0)
        	{
        		return true;
        	}
        	boolean flag = true;
        	
        	for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
        		int parenttypeid = cursor.getColumnIndex(DbHelper.TONETYPEID);
        		int parentid = cursor.getInt(parenttypeid);
        		String sql = "delete from " + DbHelper.TABLE_RINGS + " where "+DbHelper.TONETYPEID+" = '" + parentid + "'";

                try
                {
                    db.execSQL(sql);
                }
                catch (SQLException e)
                {
                    flag = false;
                }
               
        	}
        	return flag;
		}
    	
        
    }

}
