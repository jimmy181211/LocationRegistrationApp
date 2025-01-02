package com.example.myadlib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: this class enables the programmer to interact with the sqLite database, through
 * the basic operations it supports: query, insertion. It creates tables depending on the tableType
 * argument. There are two types of table: static and dynamic
 * @version 3.0
 */
public class DBHelper extends SQLiteOpenHelper {
    public enum TableType{
        USERS_TABLE, SINGLE_USER_TABLE
    }
    private static final String TAG="CourseWork:DBHelperAd";
    private static final String DB_NAME="myAdAppInfo.db";
    private static final int DB_VERSION=1;
    private TableType tableType;

    //name of tables
    public String APP_TABLE;
    public String LOCATION_TABLE;
    public String USERS_TABLE="userinfo";
    //a table that maps: name of the table -> fields in array
    private static HashMap<String,String[]> tableMapArr=new HashMap<>();

    private static String[] aTable_fields={"app_name", "type", "installed_date"};
    private static String[] lTable_fields={"time", "latitude", "longitude"};
    private static String[] uTable_fields={"username", "IMEI", "ad_id"};


    public DBHelper(Context ctx, TableType tableType, String uname){
        super(ctx,DB_NAME,null,DB_VERSION);
        this.tableType=tableType;
        String tableName= uname==null ? "none" : uname+"_data";

        //set table names
        APP_TABLE=tableName+"_apps";
        LOCATION_TABLE=tableName;

        //create arrays facilitating the iteration
        String[] table_names={APP_TABLE, LOCATION_TABLE, USERS_TABLE};
        String[][] table_fields={aTable_fields, lTable_fields, uTable_fields};
        //create mapping between the fields and the table name
        for(int i=0;i<table_names.length;i++){
            tableMapArr.put(table_names[i],table_fields[i]);
        }
    }

    /**
     * Description: the method creates tables based on 'tableType'
     * @param sqLiteDB the database that the data will be stored in
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDB) {
        List<String> createTableQueries=new ArrayList<>();
        if(tableType.equals(TableType.USERS_TABLE)){
            createTableQueries.add(String.format("CREATE TABLE IF NOT EXISTS %s (username TEXT " +
                    "PRIMARY KEY CHECK(LENGTH(uname)<=50), IMEI TEXT, ad_id TEXT",USERS_TABLE));
        }
        else if(tableType.equals(TableType.SINGLE_USER_TABLE)){
            //stores the dynamically monitored/ changed data
            createTableQueries.add(String.format("CREATE TABLE IF NOT EXISTS %s (time TEXT PRIMARY " +
                    "KEY, latitude TEXT, longitude TEXT);", LOCATION_TABLE));
            //stores the information of installed apps in the device
            createTableQueries.add(String.format("CREATE TABLE IF NOT EXISTS %s (app_name TEXT, " +
                    "type TEXT CHECK(type IN ('art_music', 'sport', 'social media', " +
                    "'food', 'learning', 'efficiency_tool', 'shopping', 'game', 'search', 'video', " +
                    "'none')), installed_date TEXT);", APP_TABLE));
        }
        //if the user didn't enter a valid tableType
        else{
            Log.e(TAG,"tableType should be correctly set!");
            try {
                throw new Exception("tableType entered in the constructor is unidentifiable");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //execute the queries added above
        for(int i=0;i<createTableQueries.size();i++){
            sqLiteDB.execSQL(createTableQueries.get(i));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     * Description: this method queries data from the table 'userinfo'. It maps data according to
     * the username. It will log the queried data onto logcat.
     * @param queryCols it is a string array that contains the fields information to be queried
     * @param idPair is a key-value pair in a String array that identifies an unique record in the target table.
     */
    public List<HashMap<String,String>> queryData(String tableName,String[] queryCols, String[] idPair){
        SQLiteDatabase dbr= this.getReadableDatabase();
        Cursor cursor;
        if(idPair==null){
            cursor=dbr.query(tableName,null,null,null,null,null,null,null);
        }
        else{
            cursor=dbr.query(tableName,queryCols, idPair[0]+"=?", new String[]{idPair[1]},
                    null,null,null);
        }

        //create a container to store the queried data and return it
        List<HashMap<String,String>> dataList=new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                HashMap<String,String> map=new HashMap<>();
                for(String field:queryCols){
                    int idx=cursor.getColumnIndex(field);
                    String data=idx>=0?cursor.getString(idx):"none";
                    map.put(field,data);
                }
                dataList.add(map);
            }while(cursor.moveToNext());
        }
        cursor.close();
        dbr.close();
        return dataList;
    }

    public List<HashMap<String,String>> getTable(String tableName){
        return queryData(tableName,tableMapArr.get(tableName),null);
    }

    /**
     * Description: this public methods delete a record according to the id
     * @param idPair is a key-value pair in a String array that identifies an unique record in the target table.
     */
    public boolean delete(String tableName, String[] idPair){
        SQLiteDatabase dbw=this.getWritableDatabase();
        return dbw.delete(tableName,idPair[0]+"="+idPair[1],null)>0;
    }

    /**
     * Description: this method makes sure that the ContentValues object has all the fields that
     * the table has (if the the field wasn't in the object then set it to null)
     * @param vals the original ContentValues object
     * @param tableName the ContentValues object is formatted according to the fields that the table has
     * @return the formatted ContentValues object
     */
    private ContentValues formattedContentValues(ContentValues vals, String tableName){
        String[] fields=tableMapArr.get(tableName);
        assert fields != null;
        for(int i=0;i<vals.size();i++){
            String val=(String)vals.get(fields[i]);
            vals.put(fields[i], val);
        }
        return vals;
    }

    /**
     * Description: this is a public method for storing data to any give table
     * @param vals a ContentValues object, which holds multiple key-value pairs
     * @param tableName the name of the table which data record is inserted into
     */
    public void store(ContentValues vals, String tableName){
        SQLiteDatabase dbw= this.getWritableDatabase();
        dbw.insert(tableName, null, formattedContentValues(vals,tableName));
        dbw.close();
    }
}
