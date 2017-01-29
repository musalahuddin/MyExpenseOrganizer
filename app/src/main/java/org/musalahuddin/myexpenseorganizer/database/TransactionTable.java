package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class TransactionTable extends Model{

    // URI
    public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.TRANSACTIONS_URI;
    // Table columns
    public static final String TABLE_TRANSACTION = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRANSACTION_CATEGORY_ID = "transaction_category_id";
    public static final String COLUMN_EXPENSE_CATEGORY_ID = "expense_category_id";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_TRANSACTION_DATE = "transaction_date";
    public static final String COLUMN_CREATE_DATE = "create_date";
    public static final String COLUMN_MOD_DATE = "mod_date";
    public static final String COLUMN_DELETE_DATE = "delete_date";
    public static final String COLUMN_DELETED = "deleted";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TRANSACTION
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_TRANSACTION_CATEGORY_ID + " INTEGER NOT NULL, "
            + COLUMN_EXPENSE_CATEGORY_ID + " INTEGER NOT NULL, "
            + COLUMN_NOTES + " TEXT, "
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_TRANSACTION_DATE + " INTEGER, "
            + COLUMN_CREATE_DATE + " INTEGER, "
            + COLUMN_MOD_DATE + " INTEGER, "
            + COLUMN_DELETE_DATE + " INTEGER, "
            + COLUMN_DELETED + " INTERGER DEFAULT 0 NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_TRANSACTION_CATEGORY_ID + ") REFERENCES "+TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY+"("+TransactionCategoryTable.COLUMN_ID+"), "
            + "FOREIGN KEY(" + COLUMN_EXPENSE_CATEGORY_ID + ") REFERENCES "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY+"("+ExpenseChildCategoryTable.COLUMN_ID+")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TransactionTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(database);
    }

    /**
     * Creates a new Transaction Account
     * @param name
     * @return the row id of the newly inserted row, or -1 if something goes wrong
     */
    public static long create(
            long transaction_category_id,
            long expense_category_id,
            String description,
            String notes,
            String image_path,
            long transaction_date
    )
    {


        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_TRANSACTION_CATEGORY_ID, transaction_category_id);
        initialValues.put(COLUMN_EXPENSE_CATEGORY_ID, expense_category_id);
        initialValues.put(COLUMN_NOTES, notes);
        initialValues.put(COLUMN_IMAGE_PATH, image_path);
        initialValues.put(COLUMN_TRANSACTION_DATE, transaction_date);

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
     * update Transactions
     */
    public static int update(
            Long id,
            long transaction_category_id,
            long expense_category_id,
            String description,
            String notes,
            String image_path,
            long transaction_date){

        ContentValues args = new ContentValues();
        args.put(COLUMN_TRANSACTION_CATEGORY_ID, transaction_category_id);
        args.put(COLUMN_EXPENSE_CATEGORY_ID, expense_category_id);
        args.put(COLUMN_NOTES, notes);
        args.put(COLUMN_IMAGE_PATH, image_path);
        args.put(COLUMN_TRANSACTION_DATE, transaction_date);

        Uri uri;
        try{
            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(),
                    args, null, null);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }
    }

    public static int delete(Long id, Long account_id1, Long account_id2){

        ContentValues args = new ContentValues();
        args.put(COLUMN_DELETED, 1);

        Uri uri;
        try{
            //return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(), args, null, null);
            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).appendPath(String.valueOf(account_id1)).appendPath(String.valueOf(account_id2)).build(), args, null, null);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }
    }
}
