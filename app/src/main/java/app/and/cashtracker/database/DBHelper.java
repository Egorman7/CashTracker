package app.and.cashtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.and.cashtracker.models.RecordModel;

/**
 * Created by Egorman on 04.02.2018.
 */

public class DBHelper extends SQLiteOpenHelper{

    private static DBHelper mInstance = null;
    private Context context;

    public static final String DB_NAME = "cashtrackerdb";
    private static final int DB_VERSION = 4;
    // table names
    public static final String TABLE_CATEGORY = "categories",
    TABLE_RECORDS = "records", TABLE_CURRENT = "current";
    // field names
    public static final String CAT_ID="_id", CAT_NAME="name", CAT_COLOR="color", CAT_INCOME = "income",
    REC_ID="_id", REC_VALUE="value", REC_DATE="date", REC_INCOME="income", REC_CAT="cat", REC_DESC = "desc",
    CUR_ID="_id", CUR_VALUE="value", CUR_PIN = "pin", CUR_NOTF = "notf", CUR_PERIOD = "period";

    public static SimpleDateFormat SDF = new SimpleDateFormat(DBHelper.DATE_FORMAT);

    // create tables queries
    private static final String CAT_CREATE_TABLE = "create table " +
            TABLE_CATEGORY+" (" +
            CAT_ID + " integer primary key autoincrement, " +
            CAT_NAME + " text not null, " +
            CAT_COLOR + " integer, " +
            CAT_INCOME + " boolean);";
    private static final String REC_CREATE_TABLE = "create table " +
            TABLE_RECORDS+" (" +
            REC_ID + " integer primary key autoincrement, " +
            REC_VALUE + " decimal, " +
            REC_DATE + " date, " +
            REC_INCOME + " boolean, " +
            REC_CAT + " integer references "+TABLE_CATEGORY+"("+CAT_ID+"), " +
            REC_DESC + "text);";
    private static final String CUR_CREATE_TABLE = "create table " +
            TABLE_CURRENT+" (" +
            CUR_ID + " integer primary key autoincrement, " +
            CUR_VALUE + " decimal, " +
            CUR_PIN + " text, " +
            CUR_NOTF + "boolean, " +
            CUR_PERIOD + "integer);";

    // fill default data
    private static final String[] CAT_COLORS = {
            "#777474", "#E4B312", "#6ACB2A", "#2A80CB", "#09940E"
    };

    private static final String CAT_FILL_DEFAULT_START = "insert into " +
            TABLE_CATEGORY + " ("+CAT_NAME+", " + CAT_COLOR + ", " + CAT_INCOME + ") values(", CAT_FILL_DEFAULT_END = ");";
    private static final String[] CAT_FILL_DATA = {
            "'Прочее'", "'Продукты'", "'Развлечения'", "'Транспорт'", "'Прочее'", "'Зарплата'"
    };
    private static final int[] CAT_COLOR_DATA={
            Color.parseColor(CAT_COLORS[0]),
            Color.parseColor(CAT_COLORS[1]),
            Color.parseColor(CAT_COLORS[2]),
            Color.parseColor(CAT_COLORS[3]),
            Color.parseColor(CAT_COLORS[0]),
            Color.parseColor(CAT_COLORS[4])
    };
    private static final int[] CAT_INCOME_DATA={
            0,0,0,0,1,1
    };
    private static final String CUR_FILL_DEFAULT = "insert into " +
            TABLE_CURRENT + " ("+CUR_VALUE+") values(0);";


    // date format
    public static final String DATE_FORMAT = "yyyy-MM-dd";



    public static DBHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CAT_CREATE_TABLE);
        sqLiteDatabase.execSQL(REC_CREATE_TABLE);
        sqLiteDatabase.execSQL(CUR_CREATE_TABLE);

        for(int i=0; i<CAT_FILL_DATA.length; i++){
            sqLiteDatabase.execSQL(CAT_FILL_DEFAULT_START+CAT_FILL_DATA[i]+", "+CAT_COLOR_DATA[i]+", "+CAT_INCOME_DATA[i]+CAT_FILL_DEFAULT_END);
        }
        sqLiteDatabase.execSQL(CUR_FILL_DEFAULT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        switch (oldV){
            case 1:
                sqLiteDatabase.execSQL("alter table " + TABLE_RECORDS + " add column '" + REC_DESC + "' text default ''");
                Log.d("DB_UPGRADE", "Upgrading from 1 to 2");
            case 2:
                sqLiteDatabase.execSQL("alter table " + TABLE_CURRENT + " add column '" + CUR_PIN + "' text default ''");
                sqLiteDatabase.execSQL("alter table " + TABLE_CURRENT + " add column '" + CUR_NOTF + "' boolean default 1");
                sqLiteDatabase.execSQL("alter table " + TABLE_CURRENT + " add column '" + CUR_PERIOD + "' integer default 30");
                Log.d("DB_UPGRADE", "Upgrading from 2 to 3");
            case 3:
                Log.d("DB_UPGRADE", "Upgrading from 3 to 4");
        }
    }

    // get data methods
    public static String[] getCategories(DBHelper dbHelper, boolean income){
        String inc = income ? "1" : "0";
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_CATEGORY + " where " + CAT_INCOME + " = " + inc, null);
        if(cursor.moveToFirst()){
            do{
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(CAT_NAME)));
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return categories.toArray(new String[categories.size()]);
    }
    public static int getCategoryIdByName(DBHelper dbHelper, String name, boolean income){
        String inc = income ? "1" : "0";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + CAT_ID + " from " + TABLE_CATEGORY + " where " + CAT_NAME + " = '" + name + "' and " + CAT_INCOME +" = " + inc + ";",null);
        int res = -1;
        if(cursor.moveToFirst()){
            res = cursor.getInt(cursor.getColumnIndexOrThrow(CAT_ID));
        }
        database.close();
        cursor.close();
        return res;
    }
    public static String getCategoryById(DBHelper dbHelper, int id){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String res="";
        Cursor cursor = database.rawQuery("select " + CAT_NAME + " from " + TABLE_CATEGORY + " where " + CAT_ID +" = " + id, null);
        if(cursor.moveToFirst()){
            res = cursor.getString(cursor.getColumnIndexOrThrow(CAT_NAME));
        }
        database.close();
        cursor.close();
        return res;
    }
    public static Map<Integer, String> getCategoriesList(DBHelper dbHelper, boolean income){
        String inc = income ? "1" : "0";
        Map<Integer, String> map = new HashMap<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + CAT_NAME +", "+CAT_ID+" from " + TABLE_CATEGORY + " where " + CAT_INCOME + " = " + inc, null);
        if(cursor.moveToFirst()){
            do{
                map.put(cursor.getInt(cursor.getColumnIndexOrThrow(CAT_ID)),cursor.getString(cursor.getColumnIndexOrThrow(CAT_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return map;
    }
    public static double getValuesSumForDates(DBHelper dbHelper, String dateStart, String dateEnd, boolean income){
        String inc = ((income) ? "1" : "0");
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        double res = 0;
        Cursor cursor = database.rawQuery("select " + REC_VALUE + " from " + TABLE_RECORDS + " where " + REC_INCOME + " = " + inc + " and " +
                REC_DATE + " >= '" + dateStart + "' and " + REC_DATE + " <= '" + dateEnd + "';",null);
        if(cursor.moveToFirst()){
            do{
                res += cursor.getDouble(cursor.getColumnIndexOrThrow(REC_VALUE));
            }while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
        return res;
    }
    public static double getBalance(DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_CURRENT, null);
        double res = 0;
        if(cursor.moveToFirst()){
            res = cursor.getDouble(cursor.getColumnIndexOrThrow(CUR_VALUE));
        }
        database.close();
        cursor.close();
        return res;
    }

    public static Map<String, Float> getValuesForCat(DBHelper dbHelper, String dateStart, String dateEnd, boolean income){
        String inc = income ? "1" : "0";
        Map<Integer, String> categories = getCategoriesList(dbHelper,income);
        Map<String, Float> map = new HashMap<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + REC_CAT + ", sum("+REC_VALUE+") as sum from " + TABLE_RECORDS + " where " +
            REC_INCOME + " = " + inc + " and " + REC_DATE + " >= '" + dateStart + "' and " + REC_DATE + " <= '" + dateEnd + "' group by " + REC_CAT + " order by sum asc;", null);
        if(cursor.moveToFirst()){
            do{
                float value = (float)cursor.getDouble(cursor.getColumnIndexOrThrow("sum"));
                map.put(categories.get(cursor.getInt(cursor.getColumnIndexOrThrow(REC_CAT))),value);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return map;
    }

    public static Map<String, Float> getValuesForCategoriesByDates(DBHelper dbHelper, String dateStart, String dateEnd, boolean income){
        String inc = income ? "1" : "0";
        Map<String, Float> map = new HashMap<>();
        String[] categories = getCategories(dbHelper, income);
        for(String cat : categories){
            int catId = getCategoryIdByName(dbHelper,cat,income);
            Float res = 0.0f;
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from " + TABLE_RECORDS + " where " + REC_CAT + " = " + catId + " and " + REC_INCOME + " = " + inc +
                    " and " + REC_DATE + " >= '" + dateStart + "' and " + REC_DATE + " <= '" + dateEnd + "' order by " + REC_VALUE + " asc;", null);
            if(cursor.moveToFirst()){
                do{
                    float value = (float)cursor.getDouble(cursor.getColumnIndexOrThrow(REC_VALUE));
                    res+=value;
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
            map.put(cat,res);
        }
        return map;
    }
    public static int getCategoryColor(DBHelper dbHelper, String category, boolean income){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String inc = income ? "1" : "0";
        Cursor cursor = database.rawQuery("select " + CAT_COLOR + " from " + TABLE_CATEGORY + " where " + CAT_NAME + " = '" + category +"' and "+ CAT_INCOME + " = " + inc+";",null);
        int color=0;
        if(cursor.moveToFirst()){
            color = cursor.getInt(cursor.getColumnIndexOrThrow(CAT_COLOR));
        }
        database.close();
        cursor.close();
        return color;
    }
    private static double getRecordValueById(DBHelper dbHelper, int id){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + REC_VALUE + " from " + TABLE_RECORDS + " where " + REC_ID + " = " + id, null);
        double res = 0;
        if(cursor.moveToFirst()){
            res = cursor.getDouble(cursor.getColumnIndexOrThrow(REC_VALUE));
        }
        database.close();
        cursor.close();
        return res;
    }
    private static boolean getRecordTypeById(DBHelper dbHelper, int id){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + REC_INCOME + " from " + TABLE_RECORDS + " where " + REC_ID +" = " + id, null);
        boolean f = false;
        if(cursor.moveToFirst()) f = cursor.getInt(cursor.getColumnIndexOrThrow(REC_INCOME)) > 0;
        database.close();
        cursor.close();
        return f;
    }
    public static Map<String, Double> getDataForChart(Context context, String category, String dateStart, String dateEnd, boolean income){
        int id = getCategoryIdByName(DBHelper.getInstance(context),category, income);
        //String inc = income ? "1" : "0";
        Map<String, Double> data = new HashMap<>();
        SQLiteDatabase database = DBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + REC_DATE + ", sum(" + REC_VALUE +") as sum from " + TABLE_RECORDS +
            " where " + REC_DATE + " >= '" + dateStart + "' and " + REC_DATE +" <= '" + dateEnd + "' and " + REC_CAT + " = " + id +
            " group by " + REC_DATE + " order by " + REC_DATE + " desc;", null);
        if(cursor.moveToFirst()){
            do{
                data.put(cursor.getString(cursor.getColumnIndexOrThrow(REC_DATE)), cursor.getDouble(cursor.getColumnIndexOrThrow("sum")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return data;
    }
    public static Map<String, Double> getAllData(Context context, String dateStart, String dateEnd, boolean income){
        Map<String, Double> data = new HashMap<>();
        String inc = income ? "1" : "0";
        SQLiteDatabase database = DBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + REC_DATE + ", sum(" + REC_VALUE +") as sum from " + TABLE_RECORDS +
                " where " + REC_DATE + " <= '" + dateEnd + "' and " + REC_DATE + " >= '" + dateStart +"' and " + REC_INCOME + " = " + inc +
                " group by " + REC_DATE + " order by " + REC_DATE + " desc;", null);
        if(cursor.moveToFirst()){
            do{
                data.put(cursor.getString(cursor.getColumnIndexOrThrow(REC_DATE)), cursor.getDouble(cursor.getColumnIndexOrThrow("sum")));
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return data;
    }
    public static String getPin(Context context){
        SQLiteDatabase database = DBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + CUR_PIN +" from " + TABLE_CURRENT, null);
        String res = "";
        if(cursor.moveToFirst()) res = cursor.getString(cursor.getColumnIndexOrThrow(CUR_PIN));
        cursor.close();
        database.close();
        return res;
    }



    // get cursors
    public static Cursor getCategoriesCursor(DBHelper dbHelper, boolean income){
        String inc = income ? "1" : "0";
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_CATEGORY + " where " + CAT_INCOME + " = " + inc, null);
        //database.close();
        return cursor;
    }
    public static Cursor getRecordsCursor(DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_RECORDS, null);
        return cursor;
    }
    public static Cursor getRecordsCursorByDates(DBHelper dbHelper, String dateStart, String dateEnd){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_RECORDS + " where " + REC_DATE + " >= '" + dateStart +
                "' and " + REC_DATE + " <= '" + dateEnd + "' order by " + REC_DATE + " desc, " + REC_ID + " desc;",null);
        return cursor;
    }
    public static Cursor getMoreCursor(DBHelper dbHelper, String dateStart, String dateEnd, boolean income, String category){
        int id = getCategoryIdByName(dbHelper, category, income);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + REC_DATE +", "+REC_ID+", sum("+REC_VALUE+") as sum from " + TABLE_RECORDS +" where "+
                REC_DATE +" >= '" + dateStart + "' and " + REC_DATE + " <= '" + dateEnd + "' and "+
                REC_CAT + " = "+ id + " group by " + REC_DATE + " order by " + REC_DATE + " desc;", null);
        return cursor;
    }

    // update data
    private static void updateRecordsCategory(DBHelper dbHelper, int oldId, int newId){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select " + REC_CAT + ", " + REC_ID + " from " + TABLE_RECORDS,null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getInt(cursor.getColumnIndexOrThrow(REC_CAT))==oldId){
                    ContentValues cv = new ContentValues();
                    cv.put(REC_CAT, newId);
                    database.update(TABLE_RECORDS,cv,REC_ID +" = " + cursor.getInt(cursor.getColumnIndexOrThrow(REC_ID)),null);
                }
            }while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
    }
    public static boolean updateRecord(DBHelper dbHelper, RecordModel rm){
        int catId = getCategoryIdByName(dbHelper,rm.getCategory(),rm.isIncome());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(REC_VALUE, rm.getValue());
        cv.put(REC_DATE, SDF.format(rm.getDate()));
        cv.put(REC_CAT, catId);
        cv.put(REC_INCOME, rm.isIncome() ? 1 : 0);
        cv.put(REC_DESC, rm.getDesc());
        if(database.update(TABLE_RECORDS,cv,REC_ID + " = " + rm.getId(),null) > 0){
            database.close();
            return true;
        }
        database.close();
        return false;
    }
    public static boolean updateCurrentValue(DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select "+ REC_VALUE + ", " + REC_INCOME +" from " + TABLE_RECORDS, null);
        double res = 0;
        if(cursor.moveToFirst()){
            do{
                double d = cursor.getDouble(cursor.getColumnIndexOrThrow(REC_VALUE));
                if(cursor.getInt(cursor.getColumnIndexOrThrow(REC_INCOME))>0){
                    res+=d;
                } else res-=d;
            } while (cursor.moveToNext());
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put(CUR_VALUE, res);
        if(database.update(TABLE_CURRENT,cv,CUR_ID + " = 1", null) >0)
        {
            database.close();
            return true;
        }
        database.close();
        return false;
    }

    // remove data
    public static boolean removeCategoryById(DBHelper dbHelper, int id, boolean income){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        if(database.delete(TABLE_CATEGORY,CAT_ID + " = " + id, null) > 0){
            database.close();
            updateRecordsCategory(dbHelper,id,income ? 5 : 1);
            return true;
        }
        database.close();
        return false;
    }
    public static boolean removeRecordById(DBHelper dbHelper, int id){
        double value = getRecordValueById(dbHelper, id);
        boolean isIncome = getRecordTypeById(dbHelper,id);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        if(database.delete(TABLE_RECORDS,REC_ID + " = " + id, null) > 0){
            database.close();
            addBalance(dbHelper, isIncome ? -value : value);
            return true;
        }
        database.close();
        return false;
    }

    // add data
    public static boolean addCategory(DBHelper dbHelper, String name, int color, boolean income){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CAT_NAME,name);
        cv.put(CAT_COLOR,color);
        cv.put(CAT_INCOME, income ? 1 : 0);
        if(database.insert(TABLE_CATEGORY,null,cv) !=0){
            database.close();
            return true;
        }
        database.close();
        return false;
    }
    public static boolean addRecord(DBHelper dbHelper, RecordModel recordModel, boolean income){
        int catId = getCategoryIdByName(dbHelper, recordModel.getCategory(), income);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(REC_VALUE, recordModel.getValue());
        cv.put(REC_DATE, SDF.format(recordModel.getDate()));
        cv.put(REC_INCOME, recordModel.isIncome());
        cv.put(REC_CAT, catId);
        cv.put(REC_DESC, recordModel.getDesc());
        if(database.insert(TABLE_RECORDS,null,cv)!=0){
            database.close();
            return true;
        }
        database.close();
        return false;
    }
    public static boolean addBalance(DBHelper dbHelper, double value){
        try {
            double currentBalance = getBalance(dbHelper);
            Log.d("[ADD_BALANCE]","Current balance = " + currentBalance);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(CUR_VALUE, currentBalance + value);
            Log.d("[ADD_BALANCE]", "Content put: " + CUR_VALUE + " : " + (currentBalance+value));
            boolean flag = database.update(TABLE_CURRENT, cv, CUR_ID + " = 1", null) > 0;
            Log.d("[ADD_BALANCE]", "DB updated! Flag = " + flag);
            database.close();
            return flag;
        } catch (Exception ex){ex.printStackTrace();}
        return false;
    }
    public static boolean setPin(Context context, String pin){
        try{
            SQLiteDatabase database = getInstance(context).getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(CUR_PIN, pin);
            boolean flag = database.update(TABLE_CURRENT, cv, CUR_ID + " = 1", null) > 0;
            database.close();
            return flag;
        } catch (Exception ex) {ex.printStackTrace();}
        return false;
    }
}
