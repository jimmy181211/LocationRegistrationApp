package controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: this class encapsulates the basic database operations such as addition, query, deletion, and modification.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG="CourseWork:DBHelperHost";
    private static final String DB_NAME="userInfo_db.db";
    private static final int DB_VERSION=1;
    private static final String mainTableName="userinfo";//the name of the table
    private static final String[] fieldsArr={"uname","pwd","email","age","phone_no","portrait_path","warning_lv"};
    public static final Integer leastPwdLen=5;
    public static final Integer maxUnameLen=50;
    public static final Integer phone_noLen=11;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //stores the static information of all users
        String tableQuery=String.format("CREATE TABLE IF NOT EXISTS %s (uid INTEGER PRIMARY KEY AUTOINCREMENT, uname TEXT CHECK(LENGTH" +
                    "(uname) <= %s) UNIQUE, pwd TEXT CHECK(LENGTH(pwd) >= %s), email TEXT UNIQUE, " +
                        "age TEXT, phone_no TEXT CHECK(LENGTH(phone_no)==%s) UNIQUE, portrait_path TEXT DEFAULT NULL, warning_lv TEXT);",
                mainTableName, maxUnameLen.toString(), leastPwdLen.toString(),phone_noLen.toString());
        sqLiteDatabase.execSQL(tableQuery);
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
    public List<HashMap<String,String>> queryData(String[] queryCols, String[] idPair){
        SQLiteDatabase dbr= this.getReadableDatabase();
        Cursor cursor;
        if(idPair==null){
            cursor=dbr.query(mainTableName,null,null,null,null,null,null,null);
        }
        else{
            cursor=dbr.query(mainTableName,queryCols, idPair[0]+"=?", new String[]{idPair[1]},
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

    /**
     * Description: get all record infos from the table
     * @return a list of records. Each record is a HashMap
     */
    public List<HashMap<String,String>> getTable(){
        return queryData(fieldsArr,null);
    }

    /**
     * Description: this public method deletes a record according to the id
     * @param idPair is a key-value pair in a String array that identifies an unique record in the target table.
     */
    public boolean delete(String[] idPair){
        SQLiteDatabase dbw=this.getWritableDatabase();
        return dbw.delete(mainTableName,idPair[0]+"="+idPair[1],null)>0;
    }

    /**
     * Description: this method makes sure that the ContentValues object has all the fields that
     * the table has (if the the field wasn't in the object then set it to null)
     * @param vals the original ContentValues object
     * @return the formatted ContentValues object
     */
    private ContentValues formattedContentValues(ContentValues vals){
        for(int i=0;i<vals.size();i++){
            String val=(String)vals.get(fieldsArr[i]);
            vals.put(fieldsArr[i], val==null?"null":val);
        }
        return vals;
    }

    /**
     * Description: this is a public method for storing data to any give table
     * @param vals a ContentValues object, which holds multiple key-value pairs
     */
    public void store(ContentValues vals){
        SQLiteDatabase dbw= this.getWritableDatabase();
        dbw.insert(mainTableName, null, formattedContentValues(vals));
        Log.d(TAG, (String) vals.get("warning_lv"));
        dbw.close();
    }

    /**
     * modify the value stored under a field
     * @param fieldPair a string pair, fieldPair[0] is the key, and fieldPair[1] is the value
     * @param idPair this pair is to identify a record from the table
     */
    public void modify(String[] fieldPair, String[] idPair){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues vals=new ContentValues();
        vals.put(fieldPair[0],fieldPair[1]);
        db.execSQL(String.format("update %s set %s=? where %s=?",mainTableName,fieldPair[0],idPair[0]),new Object[]{fieldPair[1],idPair[1]});
        db.close();
    }
}
