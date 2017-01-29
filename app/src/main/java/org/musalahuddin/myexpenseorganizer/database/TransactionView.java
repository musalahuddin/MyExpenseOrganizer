package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class TransactionView extends Model{

    // URI
    public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.VIEW_TRANSACTIONS_URI;

    // view name
    public static final String VIEW_TRANSACTION = "view_transactions";

    // view columns
    public static final String COLUMN_ID = TransactionTable.COLUMN_ID;
    public static final String COLUMN_PRIMARY_ACCOUNT_ID = TransactionAccountTable.COLUMN_PRIMARY_ACCOUNT_ID;
    public static final String COLUMN_SECONDARY_ACCOUNT_ID = TransactionAccountTable.COLUMN_SECONDARY_ACCOUNT_ID;
    public static final String COLUMN_SECONDARY_ACCOUNT_NAME = "secondary_account_name";
    public static final String COLUMN_PRIMARY_ACCOUNT_DESCRIPTION = TransactionAccountTable.COLUMN_PRIMARY_ACCOUNT_DESCRIPTION;
    public static final String COLUMN_SECONDARY_ACCOUNT_DESCRIPTION = TransactionAccountTable.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION;
    public static final String COLUMN_AMOUNT = TransactionAccountTable.COLUMN_AMOUNT;
    public static final String COLUMN_IS_DEPOSIT = TransactionAccountTable.COLUMN_IS_DEPOSIT;
    public static final String COLUMN_TRANSACTION_CATEGORY_ID = TransactionTable.COLUMN_TRANSACTION_CATEGORY_ID;
    public static final String COLUMN_TRANSACTION_CATEGORY_NAME = "transaction_category_name";
    public static final String COLUMN_EXPENSE_CATEGORY_ID = TransactionTable.COLUMN_EXPENSE_CATEGORY_ID;
    public static final String COLUMN_EXPENSE_CATEGORY_CHILD_NAME = "expense_category_child_name";
    public static final String COLUMN_EXPENSE_CATEGORY_PARENT_NAME = "expense_category_parent_name";
    public static final String COLUMN_NOTES = TransactionTable.COLUMN_NOTES;
    public static final String COLUMN_IMAGE_PATH = TransactionTable.COLUMN_IMAGE_PATH;
    public static final String COLUMN_TRANSACTION_DATE = TransactionTable.COLUMN_TRANSACTION_DATE;
    public static final String COLUMN_DELETED = TransactionTable.COLUMN_DELETED;

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE VIEW "
            + VIEW_TRANSACTION
            + " AS SELECT "+TransactionTable.TABLE_TRANSACTION+"."+TransactionTable.COLUMN_ID
            + " AS "+COLUMN_ID+", "
            + TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_PRIMARY_ACCOUNT_ID
            + " AS "+COLUMN_PRIMARY_ACCOUNT_ID+", "
            + TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_PRIMARY_ACCOUNT_DESCRIPTION
            + " AS "+COLUMN_PRIMARY_ACCOUNT_DESCRIPTION+", "
            + TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_SECONDARY_ACCOUNT_ID
            + " AS "+COLUMN_SECONDARY_ACCOUNT_ID+", "
            + AccountTable.TABLE_ACCOUNT +"."+AccountTable.COLUMN_NAME
            + " AS "+COLUMN_SECONDARY_ACCOUNT_NAME+", "
            + TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION
            + " AS "+COLUMN_SECONDARY_ACCOUNT_DESCRIPTION+", "
            + TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_AMOUNT
            + " AS "+COLUMN_AMOUNT+", "
            + TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_IS_DEPOSIT
            + " AS "+COLUMN_IS_DEPOSIT+", "
            + TransactionTable.TABLE_TRANSACTION +"."+TransactionTable.COLUMN_TRANSACTION_CATEGORY_ID
            + " AS "+COLUMN_TRANSACTION_CATEGORY_ID+", "
            + TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY +"."+TransactionCategoryTable.COLUMN_NAME
            + " AS "+COLUMN_TRANSACTION_CATEGORY_NAME+", "
            + TransactionTable.TABLE_TRANSACTION +"."+TransactionTable.COLUMN_EXPENSE_CATEGORY_ID
            + " AS "+COLUMN_EXPENSE_CATEGORY_ID+", "
            + ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY +"."+ExpenseParentCategoryTable.COLUMN_NAME
            + " AS "+COLUMN_EXPENSE_CATEGORY_PARENT_NAME+", "
            + ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY +"."+ExpenseChildCategoryTable.COLUMN_NAME
            + " AS "+COLUMN_EXPENSE_CATEGORY_CHILD_NAME+", "
            + TransactionTable.TABLE_TRANSACTION +"."+TransactionTable.COLUMN_NOTES
            + " AS "+COLUMN_NOTES+", "
            + TransactionTable.TABLE_TRANSACTION +"."+TransactionTable.COLUMN_IMAGE_PATH
            + " AS "+COLUMN_IMAGE_PATH+", "
            + TransactionTable.TABLE_TRANSACTION +"."+TransactionTable.COLUMN_TRANSACTION_DATE
            + " AS "+COLUMN_TRANSACTION_DATE+", "
            + TransactionTable.TABLE_TRANSACTION +"."+TransactionTable.COLUMN_DELETED
            + " AS "+COLUMN_DELETED
            + " FROM "
            + TransactionTable.TABLE_TRANSACTION
            +" INNER JOIN "+TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT
            + " ON "+TransactionTable.TABLE_TRANSACTION+"."+TransactionTable.COLUMN_ID+" = "+TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_TRANSACTION_ID
            +" INNER JOIN "+AccountTable.TABLE_ACCOUNT
            + " ON "+TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT+"."+TransactionAccountTable.COLUMN_SECONDARY_ACCOUNT_ID+" = "+AccountTable.TABLE_ACCOUNT +"."+AccountTable.COLUMN_ID
            +" INNER JOIN "+TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY
            + " ON "+TransactionTable.TABLE_TRANSACTION+"."+TransactionTable.COLUMN_TRANSACTION_CATEGORY_ID+" = "+TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY +"."+TransactionCategoryTable.COLUMN_ID
            +" INNER JOIN "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY
            + " ON "+TransactionTable.TABLE_TRANSACTION+"."+TransactionTable.COLUMN_EXPENSE_CATEGORY_ID+" = "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY +"."+ExpenseChildCategoryTable.COLUMN_ID
            +" INNER JOIN "+ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY
            + " ON "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY+"."+ExpenseChildCategoryTable.COLUMN_EXPENSE_PARENT_CATEGORY_ID+" = "+ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY +"."+ExpenseParentCategoryTable.COLUMN_ID
            ;

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TransactionView.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP VIEW IF EXISTS " + VIEW_TRANSACTION);
        onCreate(database);
    }
}
