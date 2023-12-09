package com.cmtech.amslogbook.global

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DB(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    var database:SQLiteDatabase?=null

    override fun onCreate(db: SQLiteDatabase) {
        //val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $NAME TEXT,$COMPLETED TEXT, $DESC2 TEXT);"
        db.execSQL(SqlTable.addvehicale)
        db.execSQL(SqlTable.addtrip)
        db.execSQL(SqlTable.add_fuel)
        db.execSQL(SqlTable.admin)
        db.execSQL(SqlTable.register)
        db.execSQL(SqlTable.places)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //val DROP_TABLE = "DROP TABLE IF EXISTS "+SqlTable.login+"";
        db.execSQL("DROP TABLE IF EXISTS "+SqlTable.addvehicale+"")
        db.execSQL("DROP TABLE IF EXISTS "+SqlTable.addtrip+"")
        db.execSQL("DROP TABLE IF EXISTS "+SqlTable.add_fuel+"")
        db.execSQL("DROP TABLE IF EXISTS "+SqlTable.admin+"")
        db.execSQL("DROP TABLE IF EXISTS "+SqlTable.register+"")
        db.execSQL("DROP TABLE IF EXISTS"+SqlTable.places+"")
        onCreate(db)
    }

    fun fireQuery1(query:String):Cursor?{
        var tempCursor:Cursor?=null
        try {
            if(database==null){
                synchronized(DB::class.java){
                    if (database==null){
                        database =this.writableDatabase
                    }
                }
            }
            tempCursor= database?.rawQuery(query,null)
            if(tempCursor!=null && tempCursor.count>0){
                if(tempCursor.moveToFirst()){
                    return tempCursor
                }

            }

        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            // db.close()
        }
        return tempCursor
    }


    @Throws(SQLException::class)
    fun FireQuery(query:String): Cursor? {
        var TempCursor: Cursor? = null
        try
        {
            val database = this.writableDatabase
            TempCursor = database.rawQuery(query, null)
            if (TempCursor != null && TempCursor.getCount() > 0)
            {
                if (TempCursor.moveToFirst())
                {
                    return TempCursor
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        finally {
            close()
        }
        return TempCursor
    }

    fun executeQuery1(query:String):Boolean{
        try {
            if(database==null){
                synchronized(DB::class.java){
                    if(database==null){
                        database= this.writableDatabase
                    }
                }
            }
            database?.execSQL(query)
        }catch (e:Exception){
            e.printStackTrace()
            return false
        }finally {
            // db.close()
        }
        return true
    }


    fun ExecuteQuery(sql:String):Boolean {
        var b:Boolean?=true
        try{
            val database = this.writableDatabase
            database.execSQL(sql)
            //return true
        }
        catch (e:Exception) {
            e.printStackTrace()
            b= false
        }
        finally {
            close()
        }
        return b!!
    }

    fun insertOrReplace(tableName:String,contentValues: ContentValues){
        try {
            if(database==null){
                synchronized(DB::class.java){
                    if(database==null){
                        database=this.writableDatabase
                    }
                }
            }
            database?.replace(tableName,null,contentValues)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "Logbook.DB"

    }
}