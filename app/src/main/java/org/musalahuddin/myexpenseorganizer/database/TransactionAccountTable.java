package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class TransactionAccountTable extends Model{

    // URI
    public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.TRANSACTIONS_ACCOUNTS_URI;
    // Table columns
    public static final String TABLE_TRANSACTION_ACCOUNT = "transactions_accounts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_PRIMARY_ACCOUNT_ID = "primary_account_id";
    public static final String COLUMN_SECONDARY_ACCOUNT_ID = "secondary_account_id";
    public static final String COLUMN_PRIMARY_ACCOUNT_DESCRIPTION = "primary_account_description";
    public static final String COLUMN_SECONDARY_ACCOUNT_DESCRIPTION = "secondary_account_description";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_IS_DEPOSIT = "is_deposit";



    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TRANSACTION_ACCOUNT
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_TRANSACTION_ID + " INTEGER NOT NULL, "
            + COLUMN_PRIMARY_ACCOUNT_ID + " INTEGER NOT NULL, "
            + COLUMN_SECONDARY_ACCOUNT_ID + " INTEGER NOT NULL, "
            + COLUMN_PRIMARY_ACCOUNT_DESCRIPTION + " TEXT, "
            + COLUMN_SECONDARY_ACCOUNT_DESCRIPTION + " TEXT, "
            + COLUMN_AMOUNT + " INTEGER, "
            + COLUMN_IS_DEPOSIT + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_TRANSACTION_ID + ") REFERENCES "+TransactionTable.TABLE_TRANSACTION+"("+TransactionTable.COLUMN_ID+"), "
            + "FOREIGN KEY(" + COLUMN_PRIMARY_ACCOUNT_ID + ") REFERENCES "+AccountTable.TABLE_ACCOUNT+"("+AccountTable.COLUMN_ID+"), "
            + "FOREIGN KEY(" + COLUMN_SECONDARY_ACCOUNT_ID + ") REFERENCES "+AccountTable.TABLE_ACCOUNT+"("+AccountTable.COLUMN_ID+")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TransactionAccountTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_ACCOUNT);
        onCreate(database);
    }

    /**
     * Creates a new Transaction
     * @param name
     * @return the row id of the newly inserted row, of -1 if Transaction already exists
     */
    public static long create(
            long transaction_id,
            long primary_account_id,
            long secondary_account_id,
            String primary_account_description,
            String secondary_account_description,
            double amount,
            int is_deposit
    )
    {

        amount = amount*100;

        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_TRANSACTION_ID, transaction_id);
        initialValues.put(COLUMN_PRIMARY_ACCOUNT_ID, primary_account_id);
        initialValues.put(COLUMN_SECONDARY_ACCOUNT_ID, secondary_account_id);
        initialValues.put(COLUMN_PRIMARY_ACCOUNT_DESCRIPTION, primary_account_description);
        initialValues.put(COLUMN_SECONDARY_ACCOUNT_DESCRIPTION, secondary_account_description);
        initialValues.put(COLUMN_AMOUNT, amount);
        initialValues.put(COLUMN_IS_DEPOSIT, is_deposit);

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
     * Update TransactionAccount Table
     */
    public static int update(
            long transaction_id,
            double amount)
    {
        amount = amount*100;

        ContentValues args = new ContentValues();
        args.put(COLUMN_TRANSACTION_ID, transaction_id);
        args.put(COLUMN_AMOUNT, amount);

        Uri uri;
        try{
            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(transaction_id)).build(),
                    args, null, null);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }
    }

    /**
     * Update TransactionAccount Table
     */
    public static int update(
            long transaction_id,
            long account_id,
            double amount)
    {
        amount = amount*100;

        ContentValues args = new ContentValues();
        //args.put(COLUMN_TRANSACTION_ID, transaction_id);
        args.put(COLUMN_AMOUNT, amount);

        //Toast.makeText(MyApplication.getInstance(), String.valueOf(amount), Toast.LENGTH_LONG).show();

        Log.i("accountId",String.valueOf(account_id));
        Uri uri;
        try{

            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(transaction_id)).appendPath(String.valueOf(account_id)).build(),
                    args, null, null);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }
    }

    /**
     * Update TransactionAccount Table
     */
    public static int update(
            long transaction_id,
            long orig_account_id,
            long account_id1,
            long account_id2,
            String account_description1,
            String account_description2,
            double amount,
            int is_deposit)
    {
        amount = amount*100;

        ContentValues args = new ContentValues();
        //args.put(COLUMN_TRANSACTION_ID, transaction_id);
        //args.put(COLUMN_AMOUNT, amount);
        args.put(COLUMN_PRIMARY_ACCOUNT_ID, account_id1);
        args.put(COLUMN_SECONDARY_ACCOUNT_ID, account_id2);
        args.put(COLUMN_PRIMARY_ACCOUNT_DESCRIPTION, account_description1);
        args.put(COLUMN_SECONDARY_ACCOUNT_DESCRIPTION, account_description2);
        args.put(COLUMN_AMOUNT, amount);
        //args.put(COLUMN_IS_DEPOSIT, is_deposit);

        //Toast.makeText(MyApplication.getInstance(), String.valueOf(amount), Toast.LENGTH_LONG).show();

        Log.i("accountId",String.valueOf(account_id1));
        Uri uri;
        try{

            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(transaction_id)).appendPath(String.valueOf(account_id1)).appendPath(String.valueOf(is_deposit)).appendPath(String.valueOf(orig_account_id)).build(),
                    args, null, null);
        }
        catch (SQLiteConstraintException e){
            return -1;
        }
    }
}
