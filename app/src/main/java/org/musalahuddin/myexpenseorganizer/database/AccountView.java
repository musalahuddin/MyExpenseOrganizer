package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class AccountView extends Model {

    // URI
    public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.VIEW_ACCOUNTS_URI;

    public static final String VIEW_ACCOUNT = "view_accounts";
    public static final String COLUMN_ACCOUNT_CATEGORY_NAME = "account_category_name";
    public static final String COLUMN_ACCOUNT_CATEGORY_ID = "account_category_id";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE VIEW "
            + VIEW_ACCOUNT
            + " AS SELECT " + AccountTable.TABLE_ACCOUNT +"."+AccountTable.COLUMN_ID +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_ACCOUNT_CATEGORY_ID +", "
            +  AccountCategoryTable.TABLE_ACCOUNT_CATEGORY+"."+AccountCategoryTable.COLUMN_ID + " AS " + COLUMN_ACCOUNT_CATEGORY_ID + ", "
            +  AccountCategoryTable.TABLE_ACCOUNT_CATEGORY+"."+AccountCategoryTable.COLUMN_NAME + " AS " + COLUMN_ACCOUNT_CATEGORY_NAME + ", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_NAME +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_NUMBER +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_DESCRIPTION +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_INIT_BALANCE +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_CURR_BALANCE +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_CREDIT_LIMIT +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_MONTHLY_PAYMENT +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_DUE_DATE +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_BUDGET_START_DAY +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_CREATE_DATE +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_MOD_DATE +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_DELETE_DATE +", "
            +  AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_DELETED
            + " FROM "
            + AccountTable.TABLE_ACCOUNT+" INNER JOIN "+AccountCategoryTable.TABLE_ACCOUNT_CATEGORY
            + " ON "
            + AccountTable.TABLE_ACCOUNT+"."+AccountTable.COLUMN_ACCOUNT_CATEGORY_ID+" = "+AccountCategoryTable.TABLE_ACCOUNT_CATEGORY+"."+AccountCategoryTable.COLUMN_ID;

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(AccountView.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP VIEW IF EXISTS " + VIEW_ACCOUNT);
        onCreate(database);
    }
}
