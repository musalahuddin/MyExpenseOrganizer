package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BudgetTable extends Model{

    // URI
    public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.BUDGETS_URI;
    // Table columns
    public static final String TABLE_BUDGET = "budgets";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_EXPENSE_CATEGORY_ID = "expense_category_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_CREATE_DATE = "create_date";
    public static final String COLUMN_MOD_DATE = "mod_date";
    public static final String COLUMN_DELETE_DATE = "delete_date";
    public static final String COLUMN_DELETED = "deleted";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_BUDGET
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_ACCOUNT_ID + " INTEGER NOT NULL, "
            + COLUMN_EXPENSE_CATEGORY_ID + " INTEGER NOT NULL, "
            + COLUMN_AMOUNT + " INTEGER, "
            + COLUMN_NOTES + " TEXT, "
            + COLUMN_CREATE_DATE + " INTEGER, "
            + COLUMN_MOD_DATE + " INTEGER, "
            + COLUMN_DELETE_DATE + " INTEGER, "
            + COLUMN_DELETED + " INTERGER DEFAULT 0 NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_ACCOUNT_ID + ") REFERENCES "+AccountTable.TABLE_ACCOUNT+"("+AccountTable.COLUMN_ID+"), "
            + "FOREIGN KEY(" + COLUMN_EXPENSE_CATEGORY_ID + ") REFERENCES "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY+"("+ExpenseChildCategoryTable.COLUMN_ID+")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BudgetTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(database);
    }

    /**
     * Creates a new Budget
     * @param name
     * @return the row id of the newly inserted row, or -1 if something goes wrong
     */
    public static long create(
            long account_id,
            long expense_category_id,
            double amount,
            String notes
    )
    {

        //check to if the record already exists for the given expense Id and account id
        //MyExpenseOrganizerDatabaseHelper dbhelper = new MyExpenseOrganizerDatabaseHelper(MyApplication.getInstance());
        //SQLiteDatabase db = dbhelper.getWritableDatabase();

        // convert amount to integer before storing it in database
        amount = amount*100;

        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_ACCOUNT_ID, account_id);
        initialValues.put(COLUMN_EXPENSE_CATEGORY_ID, expense_category_id);
        initialValues.put(COLUMN_AMOUNT, amount);
        initialValues.put(COLUMN_NOTES, notes);

        Uri uri;
        try{
            uri = cr().insert(CONTENT_URI,initialValues);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }

        return Integer.valueOf(uri.getLastPathSegment());

    }

    /**
     * Updates Budget(s)
     * @return number of rows affected, or -1 if Account already exists
     */
    public static int update(
            long id,
            long account_id,
            long expense_category_id,
            double amount,
            String notes
    ){

        // convert amount to integer before storing it in database
        //init_balance = init_balance*100;
        amount = amount*100;

        ContentValues args = new ContentValues();
        args.put(COLUMN_ACCOUNT_ID, account_id);
        args.put(COLUMN_EXPENSE_CATEGORY_ID, expense_category_id);
        args.put(COLUMN_AMOUNT, amount);
        args.put(COLUMN_NOTES, notes);
        Uri uri;
        try{
            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(),
                    args, null, null);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }

    }

    public static int delete(Long id){

        ContentValues args = new ContentValues();
        args.put(COLUMN_DELETED, 1);

        Uri uri;
        try{
            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(), args, null, null);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }
    }

}
