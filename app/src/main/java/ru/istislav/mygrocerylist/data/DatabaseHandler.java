package ru.istislav.mygrocerylist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.istislav.mygrocerylist.activities.MainActivity;
import ru.istislav.mygrocerylist.model.Grocery;
import ru.istislav.mygrocerylist.util.Constants;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctx;

    public DatabaseHandler(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Constants.DB_NAME, factory, Constants.DB_VERSION);
        this.ctx = context;
    }

    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GROCERY_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "(" +
                Constants.KEY_ID + " INTEGER PRIMARY KEY, " +
                Constants.KEY_GROCERY_ITEM + " TEXT, " +
                Constants.KEY_QTY_NUMBER + " TEXT, " +
                Constants.KEY_DATE_NAME+ " LONG);";

        db.execSQL(CREATE_GROCERY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(db);
    }

    /**
     * CRUD OPERATIONS: Create, Read, Update, Delete Methods
     */

    public void addGrocery(Grocery grocery) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = valuesFromGrocery(grocery);
        db.insert(Constants.TABLE_NAME, null, values);
        Log.d("Saved!", "Saved the grocery to DB");
    }

    private ContentValues valuesFromGrocery(Grocery grocery) {
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_QTY_NUMBER, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis()); // system time in milliseconds

        return values;
    }

    public Grocery getGrocery(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {
                Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_QTY_NUMBER,
                Constants.KEY_DATE_NAME
        }, Constants.KEY_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);

        if(cursor == null) return null;

        cursor.moveToFirst();
        Grocery grocery = groceryFromCursor(cursor);

        return grocery;
    }

    private Grocery groceryFromCursor(Cursor cursor) {
        Grocery grocery = new Grocery();
        grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
        grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
        grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QTY_NUMBER)));

        //convert timestamp to something readable
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());

        grocery.setDateItemAdded(formattedDate);
        return grocery;
    }


    public List<Grocery> getAllGroceries() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Grocery> groceryList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {
                Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_QTY_NUMBER,
                Constants.KEY_DATE_NAME
        }, null, null, null, null, Constants.KEY_DATE_NAME + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Grocery grocery = groceryFromCursor(cursor);
                groceryList.add(grocery);
            } while (cursor.moveToNext());
        }

        return groceryList;
    }

    public int updateGrocery(Grocery grocery) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = valuesFromGrocery(grocery);

        return db.update(Constants.TABLE_NAME, values,
            Constants.KEY_ID + "=?", new String[] { String.valueOf(grocery.getId())});
    }

    public void deleteGrocery(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(Constants.TABLE_NAME,
                Constants.KEY_ID + "=?", new String[] { String.valueOf(id) });
        db.close();
    }

    public int getGroceryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
