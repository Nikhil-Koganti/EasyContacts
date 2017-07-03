package com.nikhilkoganti.easycontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vijay on 26-05-17.
 */

public class DatabaseStore {
    public static final String Name_Data = "PeopleList";
    public static final String Name_Table = "Contactlist";
    public static final int Version_Data = 1;

    public static final String Col_RowID = "id_row";
    public static final String Col_Name = "Name";
    public static final String Col_Phone = "Phone";
    public static final String Col_Email = "Email";
    public static final String Col_Spin = "Spin";

    String Create_Table = "create table Contactlist(id_row integer primary key autoincrement,Name text not null,Phone text not null,Email text not null,Spin text)";

    SQLiteDatabase db;

    DBHelper dbHelper;

    public DatabaseStore(Context context) {
        dbHelper = new DBHelper(context);
    }

    public DatabaseStore openDatabase() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    void closeDatabase() {
        dbHelper.close();
    }

    void insertvalues(String Name, String Phone, String Email,String spin) {
        ContentValues con = new ContentValues();
        con.put(Col_Name, Name);
        con.put(Col_Phone, Phone);
        con.put(Col_Email, Email);
        con.put(Col_Spin, spin);
        db.insert(Name_Table, null, con);
    }
     Cursor getSingleRow(int rowid) {
        Cursor cursor = null;
        cursor = db.query(Name_Table, new String[]{Col_RowID, Col_Name, Col_Spin, Col_Phone, Col_Email}, Col_RowID + "="+rowid, null, null, null, null);
         Log.i("Position is ", String.valueOf(cursor.getCount()));
         return cursor;
    }

        Cursor getAllValues () {
            String[] Columns = {Col_RowID, Col_Name, Col_Phone, Col_Email, Col_Spin};

            return db.query(Name_Table, Columns, null, null, null, null, null);
        }


    void deleteAllRecords() {
        db.delete(Name_Table,null,null);
    }

    void deleteOneRecord(int rowId) {
        Log.e("ststus","i am in delete");
//        db.execSQL("DELETE FROM " + Name_Table+ " WHERE "+Col_RowID+"='"+rowId+"'");
        db.delete(Name_Table, Col_RowID + "=" + rowId, null);
    }

    void editRecords(int rowId, String Name, String Phone, String Email,String spin) {
        ContentValues cont = new ContentValues();
//        cont.put(Col_RowID, rowId);
        cont.put(Col_Name, Name);
        cont.put(Col_Phone, Phone);
        cont.put(Col_Email, Email);
        cont.put(Col_Spin, spin);

        db.update(Name_Table, cont, Col_RowID + "=" +rowId, null);
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, Name_Data, null, Version_Data);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Create_Table);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
