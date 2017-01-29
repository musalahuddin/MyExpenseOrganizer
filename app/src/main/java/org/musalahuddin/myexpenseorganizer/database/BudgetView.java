package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BudgetView extends Model{

    // URI
    public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.VIEW_BUDGETS_URI;

    // view name
    public static final String VIEW_BUDGET = "view_budgets";

    // view columns
    public static final String COLUMN_ID = BudgetTable.COLUMN_ID;
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_EXPENSE_CATEGORY_ID = TransactionTable.COLUMN_EXPENSE_CATEGORY_ID;
    public static final String COLUMN_EXPENSE_CATEGORY_CHILD_NAME = "expense_category_child_name";
    public static final String COLUMN_EXPENSE_CATEGORY_PARENT_NAME = "expense_category_parent_name";
    public static final String COLUMN_SPENT = "spent";
    public static final String COLUMN_BUDGET = "budget";
    public static final String COLUMN_TRANSACTION_DATE = TransactionTable.COLUMN_TRANSACTION_DATE;

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE VIEW "
            + VIEW_BUDGET
            + " AS SELECT "+BudgetTable.TABLE_BUDGET+"."+BudgetTable.COLUMN_ID
            + " AS "+COLUMN_ID+", "
            + BudgetTable.TABLE_BUDGET +"."+BudgetTable.COLUMN_ACCOUNT_ID
            + " AS "+COLUMN_ACCOUNT_ID+", "
            + BudgetTable.TABLE_BUDGET +"."+BudgetTable.COLUMN_EXPENSE_CATEGORY_ID
            + " AS "+COLUMN_EXPENSE_CATEGORY_ID+", "
            + ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY +"."+ExpenseParentCategoryTable.COLUMN_NAME
            + " AS "+COLUMN_EXPENSE_CATEGORY_PARENT_NAME+", "
            + ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY +"."+ExpenseChildCategoryTable.COLUMN_NAME
            + " AS "+COLUMN_EXPENSE_CATEGORY_CHILD_NAME+", "
            + "COALESCE("+TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_AMOUNT+",0)"
            + " AS "+COLUMN_SPENT+", "
            + BudgetTable.TABLE_BUDGET+"."+BudgetTable.COLUMN_AMOUNT
            + " AS "+COLUMN_BUDGET+", "
            + "COALESCE("+TransactionTable.TABLE_TRANSACTION +"."+TransactionTable.COLUMN_TRANSACTION_DATE+",0)"
            + " AS "+COLUMN_TRANSACTION_DATE
            + " FROM "
            + BudgetTable.TABLE_BUDGET
            + " LEFT JOIN "+TransactionTable.TABLE_TRANSACTION
            + " ON "+ BudgetTable.TABLE_BUDGET+"."+BudgetTable.COLUMN_EXPENSE_CATEGORY_ID+ " = " +TransactionTable.TABLE_TRANSACTION+"."+TransactionTable.COLUMN_EXPENSE_CATEGORY_ID
            + " LEFT JOIN "+TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT
            + " ON "+TransactionTable.TABLE_TRANSACTION+"."+TransactionTable.COLUMN_ID+" = "+TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT +"."+TransactionAccountTable.COLUMN_TRANSACTION_ID
            + " AND "+BudgetTable.TABLE_BUDGET+"."+BudgetTable.COLUMN_ACCOUNT_ID+ " = " +TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT+"."+TransactionAccountTable.COLUMN_PRIMARY_ACCOUNT_ID
            + " AND "+TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT+"."+TransactionAccountTable.COLUMN_IS_DEPOSIT+" = 0"
            +" INNER JOIN "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY
            + " ON "+BudgetTable.TABLE_BUDGET+"."+BudgetTable.COLUMN_EXPENSE_CATEGORY_ID+" = "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY +"."+ExpenseChildCategoryTable.COLUMN_ID
            +" INNER JOIN "+ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY
            + " ON "+ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY+"."+ExpenseChildCategoryTable.COLUMN_EXPENSE_PARENT_CATEGORY_ID+" = "+ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY +"."+ExpenseParentCategoryTable.COLUMN_ID
            ;

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BudgetView.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP VIEW IF EXISTS " + VIEW_BUDGET);
        onCreate(database);
    }






}
