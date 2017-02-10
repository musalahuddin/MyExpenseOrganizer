    package org.musalahuddin.myexpenseorganizer.provider;

import org.musalahuddin.myexpenseorganizer.database.*;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MyExpenseOrganizerProvider extends ContentProvider{

    // database helper
    public static MyExpenseOrganizerDatabaseHelper dbhelper;

    private static final boolean debug = false;
    static final String TAG = "MyExpenseOrganizerProvider";

    // Used for the UriMatcher
    private static final UriMatcher URI_MATCHER;
    private static final int EXPENSE_PARENT_CATEGORIES = 1;
    private static final int EXPENSE_PARENT_CATEGORIES_ID = 2;
    private static final int EXPENSE_CHILD_CATEGORIES = 3;
    private static final int EXPENSE_CHILD_CATEGORIES_ID = 4;
    private static final int ACCOUNT_CATEGORIES = 5;
    private static final int ACCOUNT_CATEGORIES_ID = 6;
    private static final int TRANSACTION_CATEGORIES = 7;
    private static final int TRANSACTION_CATEGORIES_ID = 8;
    private static final int ACCOUNTS = 9;
    private static final int ACCOUNTS_ID = 10;
    private static final int VIEW_ACCOUNTS = 11;
    private static final int TRANSACTIONS = 12;
    private static final int TRANSACTIONS_ID = 13;
    private static final int TRANSACTIONS_ACCOUNTS = 14;
    //private static final int TRANSACTIONS_ACCOUNTS_ID = 15;
    private static final int TRANSACTIONS_ACCOUNTS_ID2 = 16;
    private static final int VIEW_TRANSACTIONS = 17;
    private static final int BUDGETS = 18;
    private static final int BUDGETS_ID = 19;
    private static final int VIEW_BUDGETS_OVERALL = 20;
    private static final int VIEW_BUDGETS_INDIVIDUAL = 21;
    private static final int TRANSACTIONS_ID3 = 22;
    private static final int TRANSACTIONS_ACCOUNTS_ID3 = 23;
    private static final int TRANSACTIONS_ACCOUNTS_ID4 = 24;


    // Authority
    public static final String AUTHORITY = "org.musalahuddin.myexpenseorganizer";
    public static final Uri EXPENSE_PARENT_CATEGORIES_URI =
            Uri.parse("content://" + AUTHORITY + "/expense_parent_categories");
    public static final Uri EXPENSE_CHILD_CATEGORIES_URI =
            Uri.parse("content://" + AUTHORITY + "/expense_child_categories");
    public static final Uri ACCOUNT_CATEGORIES_URI =
            Uri.parse("content://" + AUTHORITY + "/account_categories");
    public static final Uri TRANSACTION_CATEGORIES_URI =
            Uri.parse("content://" + AUTHORITY + "/transaction_categories");
    public static final Uri ACCOUNTS_URI =
            Uri.parse("content://" + AUTHORITY + "/accounts");
    public static final Uri VIEW_ACCOUNTS_URI =
            Uri.parse("content://" + AUTHORITY + "/view_accounts");
    public static final Uri TRANSACTIONS_URI =
            Uri.parse("content://" + AUTHORITY + "/transactions");
    public static final Uri TRANSACTIONS_ACCOUNTS_URI =
            Uri.parse("content://" + AUTHORITY + "/transactions_accounts");
    public static final Uri VIEW_TRANSACTIONS_URI =
            Uri.parse("content://" + AUTHORITY + "/view_transactions");
    public static final Uri BUDGETS_URI =
            Uri.parse("content://" + AUTHORITY + "/budgets");
    public static final Uri VIEW_BUDGETS_URI =
            Uri.parse("content://" + AUTHORITY + "/view_budgets");

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, "expense_parent_categories", EXPENSE_PARENT_CATEGORIES);
        URI_MATCHER.addURI(AUTHORITY, "expense_parent_categories/#", EXPENSE_PARENT_CATEGORIES_ID);
        URI_MATCHER.addURI(AUTHORITY, "expense_child_categories", EXPENSE_CHILD_CATEGORIES);
        URI_MATCHER.addURI(AUTHORITY, "expense_child_categories/#", EXPENSE_CHILD_CATEGORIES_ID);
        URI_MATCHER.addURI(AUTHORITY, "account_categories", ACCOUNT_CATEGORIES);
        URI_MATCHER.addURI(AUTHORITY, "account_categories/#", ACCOUNT_CATEGORIES_ID);
        URI_MATCHER.addURI(AUTHORITY, "transaction_categories", TRANSACTION_CATEGORIES);
        URI_MATCHER.addURI(AUTHORITY, "transaction_categories/#", TRANSACTION_CATEGORIES_ID);
        URI_MATCHER.addURI(AUTHORITY, "accounts", ACCOUNTS);
        URI_MATCHER.addURI(AUTHORITY, "accounts/#", ACCOUNTS_ID);
        URI_MATCHER.addURI(AUTHORITY, "view_accounts", VIEW_ACCOUNTS);
        URI_MATCHER.addURI(AUTHORITY, "transactions", TRANSACTIONS);
        URI_MATCHER.addURI(AUTHORITY, "transactions/#", TRANSACTIONS_ID);
        URI_MATCHER.addURI(AUTHORITY, "transactions/#/#/#", TRANSACTIONS_ID3);
        URI_MATCHER.addURI(AUTHORITY, "transactions_accounts", TRANSACTIONS_ACCOUNTS);
        //URI_MATCHER.addURI(AUTHORITY, "transactions_accounts/#", TRANSACTIONS_ACCOUNTS_ID);
        URI_MATCHER.addURI(AUTHORITY, "transactions_accounts/#/#", TRANSACTIONS_ACCOUNTS_ID2);
        URI_MATCHER.addURI(AUTHORITY, "transactions_accounts/#/#/#", TRANSACTIONS_ACCOUNTS_ID3);
        URI_MATCHER.addURI(AUTHORITY, "transactions_accounts/#/#/#/#", TRANSACTIONS_ACCOUNTS_ID4);
        URI_MATCHER.addURI(AUTHORITY, "view_transactions", VIEW_TRANSACTIONS);
        URI_MATCHER.addURI(AUTHORITY, "budgets", BUDGETS);
        URI_MATCHER.addURI(AUTHORITY, "budgets/#", BUDGETS_ID);
        URI_MATCHER.addURI(AUTHORITY, "view_budgets", VIEW_BUDGETS_OVERALL);
        URI_MATCHER.addURI(AUTHORITY, "view_budgets/#", VIEW_BUDGETS_INDIVIDUAL);
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        //Log.i("MyExpenseOrganizerProvider", "oncreate");
        dbhelper = new MyExpenseOrganizerDatabaseHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String groupBy = null;
        String orderBy = null;
        String having = null;
        Cursor c = null;
        String sql = null;
        int uriMatcher = URI_MATCHER.match(uri);

        //switch (URI_MATCHER.match(uri)) {
        switch (uriMatcher) {

            case EXPENSE_PARENT_CATEGORIES:
                qb.setTables(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY);
                break;

            case EXPENSE_PARENT_CATEGORIES_ID:
                qb.setTables(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY);
                qb.appendWhere(ExpenseParentCategoryTable.COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;

            case EXPENSE_CHILD_CATEGORIES:
                qb.setTables(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY);
                break;

            case EXPENSE_CHILD_CATEGORIES_ID:
                qb.setTables(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY);
                qb.appendWhere(ExpenseChildCategoryTable.COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;

            case ACCOUNT_CATEGORIES:
                qb.setTables(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY);
                // example of AND
                //qb.appendWhere(AccountCategoryTable.COLUMN_ID + "!=1 AND " + AccountCategoryTable.COLUMN_ID + "=2");
                qb.appendWhere(AccountCategoryTable.COLUMN_ID + "!=1");
                break;

            case ACCOUNT_CATEGORIES_ID:
                qb.setTables(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY);
                qb.appendWhere(AccountCategoryTable.COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;

            case TRANSACTION_CATEGORIES:
                qb.setTables(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY);
                qb.appendWhere(TransactionCategoryTable.COLUMN_ID + "!=1");
                break;

            case TRANSACTION_CATEGORIES_ID:
                qb.setTables(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY);
                qb.appendWhere(TransactionCategoryTable.COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;

            case ACCOUNTS:
                qb.setTables(AccountTable.TABLE_ACCOUNT);
                qb.appendWhere(AccountTable.COLUMN_ID + "!=1 AND " + AccountTable.COLUMN_DELETED + "!=1");
                break;

            case VIEW_ACCOUNTS:
                qb.setTables(AccountView.VIEW_ACCOUNT);
               // qb.appendWhere(AccountTable.COLUMN_ID + "!=1");
                qb.appendWhere(AccountTable.COLUMN_ID + "!=1 AND " + AccountTable.COLUMN_DELETED + "!=1");
                break;

            case VIEW_TRANSACTIONS:
                qb.setTables(TransactionView.VIEW_TRANSACTION);
                qb.appendWhere(TransactionView.COLUMN_DELETED + "!=1");
                Log.i("im here", "view_trans");
                break;

            case VIEW_BUDGETS_OVERALL:


                sql = "SELECT MIN(_id) AS _id, SUM(spent) AS spent, SUM(budget) AS budget FROM ("
                        + " SELECT MIN(_id) as _id, min(SUM(spent),0) AS spent, MAX(budget) AS budget, deleted FROM ("
                        + " SELECT budgets._id AS _id, budgets.account_id AS account_id, budgets.deleted as deleted, budgets.expense_category_id AS expense_category_id, expense_parent_categories.name AS expense_category_parent_name, expense_child_categories.name AS expense_category_child_name, COALESCE(transactions_accounts.amount,0) AS spent, budgets.amount AS budget, COALESCE(transactions.transaction_date,0) AS transaction_date "
                        + " FROM budgets "
                        + " LEFT JOIN transactions ON budgets.expense_category_id = transactions.expense_category_id"
                        + " AND transactions.transaction_date BETWEEN "+selectionArgs[1]+" AND "+selectionArgs[2]
                        + " LEFT JOIN transactions_accounts ON transactions._id = transactions_accounts.transaction_id "
                        + " AND budgets.account_id = transactions_accounts.primary_account_id "
                        //+ " AND transactions_accounts.primary_account_id = "+ selectionArgs[0]
                        //+ " AND transactions_accounts.is_deposit = 0 "
                        + " AND budgets.deleted != 1"
                        + " AND transactions.deleted != 1"
                        + " INNER JOIN expense_child_categories ON budgets.expense_category_id = expense_child_categories._id "
                        + " INNER JOIN expense_parent_categories ON expense_child_categories.expense_parent_category_id = expense_parent_categories._id"
                        + " )"
                        + " WHERE account_id = " + selectionArgs[0]
                        + " AND deleted != 1"
                        + " GROUP BY account_id, expense_category_id "
                        + " )";

                c = db.rawQuery(sql, null);

                Log.i("im here", "sql--"+sql);//+" "+selectionArgs[0]+","+ selectionArgs[1]+","+selectionArgs[2]);
                break;

            case VIEW_BUDGETS_INDIVIDUAL:

                /*
                sql = " SELECT MIN(_id) as _id, account_id, expense_category_id, expense_category_parent_name, expense_category_child_name, SUM(spent) AS spent, MAX(budget) AS budget"
                        + " FROM ("
                        + " SELECT budgets._id AS _id, budgets.account_id AS account_id, budgets.expense_category_id AS expense_category_id, expense_parent_categories.name AS expense_category_parent_name, expense_child_categories.name AS expense_category_child_name, COALESCE(transactions_accounts.amount,0) AS spent, budgets.amount AS budget, COALESCE(transactions.transaction_date,0) AS transaction_date "
                        + " FROM budgets "
                        + " LEFT JOIN transactions ON budgets.expense_category_id = transactions.expense_category_id"
                        + " AND transactions.transaction_date BETWEEN "+selectionArgs[1]+" AND "+selectionArgs[2]
                        + " LEFT JOIN transactions_accounts ON transactions._id = transactions_accounts.transaction_id "
                        + " AND budgets.account_id = transactions_accounts.primary_account_id "
                        //+ " AND transactions_accounts.primary_account_id = "+ selectionArgs[0]
                        //+ " AND transactions_accounts.is_deposit = 0 "
                        + " INNER JOIN expense_child_categories ON budgets.expense_category_id = expense_child_categories._id "
                        + " INNER JOIN expense_parent_categories ON expense_child_categories.expense_parent_category_id = expense_parent_categories._id"
                        + " )"
                        + " WHERE account_id = " + selectionArgs[0]
                        + " GROUP BY account_id, expense_category_id "
                        + " ORDER BY expense_category_parent_name, expense_category_child_name";
                */

                sql = " SELECT MIN(_id) as _id, account_id, notes, deleted,  expense_category_id, expense_category_parent_name, expense_category_child_name, SUM(spent) AS spent, MAX(budget) AS budget"
                        + " FROM ("
                        + " SELECT budgets._id AS _id, budgets.account_id AS account_id, budgets.notes as notes, budgets.deleted as deleted, budgets.expense_category_id AS expense_category_id, expense_parent_categories.name AS expense_category_parent_name, expense_child_categories.name AS expense_category_child_name, COALESCE(transactions_accounts.amount,0) AS spent, budgets.amount AS budget, COALESCE(transactions.transaction_date,0) AS transaction_date "
                        + " FROM budgets "
                        + " LEFT JOIN transactions ON budgets.expense_category_id = transactions.expense_category_id"
                        + " AND transactions.transaction_date BETWEEN "+selectionArgs[1]+" AND "+selectionArgs[2]
                        + " LEFT JOIN transactions_accounts ON transactions._id = transactions_accounts.transaction_id "
                        + " AND budgets.account_id = transactions_accounts.primary_account_id "
                        //+ " AND transactions_accounts.primary_account_id = "+ selectionArgs[0]
                        //+ " AND transactions_accounts.is_deposit = 0 "
                        + " AND budgets.deleted != 1"
                        + " AND transactions.deleted != 1"
                        + " INNER JOIN expense_child_categories ON budgets.expense_category_id = expense_child_categories._id "
                        + " INNER JOIN expense_parent_categories ON expense_child_categories.expense_parent_category_id = expense_parent_categories._id"
                        + " )"
                        + " WHERE account_id = " + selectionArgs[0]
                        + " AND deleted != 1"
                        + " GROUP BY account_id, expense_category_id "
                        + " ORDER BY expense_category_parent_name, expense_category_child_name";

                c = db.rawQuery(sql, null);

                Log.i("im here", "sql--"+sql);
                break;

            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        if(uriMatcher != VIEW_BUDGETS_OVERALL && uriMatcher != VIEW_BUDGETS_INDIVIDUAL){
            if (!TextUtils.isEmpty(sortOrder)) {
                orderBy = sortOrder;
            }

            c = qb.query(db, projection, selection, selectionArgs, groupBy,
                    having, sortOrder);
            //c.setNotificationUri(getContext().getContentResolver(), uri);

            //db.close();
        }

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        int count = 0;
        switch (URI_MATCHER.match(uri)) {
            case EXPENSE_PARENT_CATEGORIES:
                count = db.delete(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY, selection, selectionArgs);
                break;
            case EXPENSE_PARENT_CATEGORIES_ID:
                selection = ExpenseParentCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                count = db.delete(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY, selection, selectionArgs);
                break;
            case EXPENSE_CHILD_CATEGORIES:
                count = db.delete(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY, selection, selectionArgs);
                break;
            case EXPENSE_CHILD_CATEGORIES_ID:
                selection = ExpenseChildCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                count = db.delete(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY, selection, selectionArgs);
                break;
            case ACCOUNT_CATEGORIES:
                count = db.delete(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, selection, selectionArgs);
                break;
            case ACCOUNT_CATEGORIES_ID:
                selection = AccountCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                count = db.delete(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, selection, selectionArgs);
                break;
            case TRANSACTION_CATEGORIES:
                count = db.delete(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, selection, selectionArgs);
                break;
            case TRANSACTION_CATEGORIES_ID:
                selection = TransactionCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                count = db.delete(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        //db.close();

        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        long id = 0;
        String newUri;
        String name;
        Long parentId;
        String selection;
        String[] selectionArgs;
        Cursor mCursor;
       // Cursor c = null;

        switch (URI_MATCHER.match(uri)) {
            case EXPENSE_PARENT_CATEGORIES:

                id = db.insertOrThrow(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY, null, values);
                newUri = EXPENSE_PARENT_CATEGORIES + "/" + id;
                break;

            case EXPENSE_CHILD_CATEGORIES:

                id = db.insertOrThrow(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY, null, values);
                newUri = EXPENSE_CHILD_CATEGORIES + "/" + id;
                break;

            case ACCOUNT_CATEGORIES:

                id = db.insertOrThrow(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, null, values);
                newUri = ACCOUNT_CATEGORIES + "/" + id;
                break;

            case TRANSACTION_CATEGORIES:

                id = db.insertOrThrow(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, null, values);
                newUri = TRANSACTION_CATEGORIES + "/" + id;
                break;

            case ACCOUNTS:
                id = db.insertOrThrow(AccountTable.TABLE_ACCOUNT, null, values);
                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_ACCOUNTS_URI, null);
                newUri = ACCOUNTS + "/" + id;
                if(id != -1){
                    transactionAccountTrigger((int) id, db);
                }
                break;

            case TRANSACTIONS:
                id = db.insertOrThrow(TransactionTable.TABLE_TRANSACTION, null, values);
                //notify the accounts view uri
                //getContext().getContentResolver().notifyChange(TRANSACTIONS_URI, null);
                newUri = TRANSACTIONS + "/" + id;
                break;

            case TRANSACTIONS_ACCOUNTS:
                id = db.insertOrThrow(TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT, null, values);
                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_TRANSACTIONS_URI, null);
                newUri = TRANSACTIONS_ACCOUNTS + "/" + id;

                /*
                if(id != -1){
                    Cursor c = db.rawQuery("Select primary_account_id from transactions_accounts Where _id=?", new String[] {String.valueOf(id)});
                    if(c.moveToNext()){
                        int accountId = c.getInt(c.getColumnIndex("primary_account_id"));
                        //Toast.makeText(getContext(), String.valueOf(total), Toast.LENGTH_LONG).show();
                        //db.rawQuery("UPDATE accounts set credit_limit = "+ total, null);
                        transactionAccountTrigger(accountId,db);
                    }
                    c.close();

                }
                */


                break;

            case BUDGETS:
                //check to if the record already exists for the given expense Id and account id
                Cursor c = db.rawQuery("SELECT _id FROM budgets WHERE deleted != 1 AND account_id = "+values.getAsLong(BudgetTable.COLUMN_ACCOUNT_ID)+" AND expense_category_id = "+values.getAsLong(BudgetTable.COLUMN_EXPENSE_CATEGORY_ID), null);

                if(c.getCount() > 0){
                    c.moveToPosition(0);
                    id = c.getLong(c.getColumnIndex(BudgetTable.COLUMN_ID));
                    /*
                    long account_id = values.getAsLong(BudgetTable.COLUMN_ACCOUNT_ID);
                    long expense_category_id = values.getAsLong(BudgetTable.COLUMN_EXPENSE_CATEGORY_ID);
                    double amount = values.getAsLong(BudgetTable.COLUMN_AMOUNT);
                    String notes = values.getAsString(BudgetTable.COLUMN_NOTES);
                    BudgetTable.update(budget_id,account_id,expense_category_id,amount,notes);
                    */
                    newUri = BUDGETS + "/" + id;
                }
                else {
                    id = db.insertOrThrow(BudgetTable.TABLE_BUDGET, null, values);
                    //notify the budget view uri
                    getContext().getContentResolver().notifyChange(VIEW_BUDGETS_URI, null);
                    newUri = BUDGETS + "/" + id;
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);

        //db.close();

        return id > 0 ? Uri.parse(newUri) : null;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        int count = 0;
        String name;
        Long parentId;
        Cursor mCursor;

        Log.i("update","decoded uri: " + uri.toString());

        switch (URI_MATCHER.match(uri)) {
            case EXPENSE_PARENT_CATEGORIES:
                // bulk update should not be supported
                count = db.update(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY, values, selection, selectionArgs);
                break;

            case EXPENSE_PARENT_CATEGORIES_ID:

                name = values.getAsString(ExpenseParentCategoryTable.COLUMN_NAME);

                selection = ExpenseParentCategoryTable.COLUMN_NAME + " = ?";
                selectionArgs = new String[]{name};

                mCursor = db.query(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY,
                        new String []{ExpenseParentCategoryTable.COLUMN_ID},
                        selection, selectionArgs, null, null, null);

                if(mCursor.getCount() != 0){
                    mCursor.close();
                    throw new SQLiteConstraintException();
                }

                mCursor.close();
                selection = ExpenseParentCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY, values, selection, selectionArgs);
                break;

            case EXPENSE_CHILD_CATEGORIES:
                // bulk update should not be supported
                count = db.update(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY, values, selection, selectionArgs);
                break;

            case EXPENSE_CHILD_CATEGORIES_ID:

                name = values.getAsString(ExpenseChildCategoryTable.COLUMN_NAME);
                parentId = values.getAsLong(ExpenseChildCategoryTable.COLUMN_EXPENSE_PARENT_CATEGORY_ID);

                selection = ExpenseChildCategoryTable.COLUMN_NAME + " = ? AND " + ExpenseChildCategoryTable.COLUMN_EXPENSE_PARENT_CATEGORY_ID + " =?";
                selectionArgs = new String[]{name,String.valueOf(parentId)};

                mCursor = db.query(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY,
                        new String []{ExpenseChildCategoryTable.COLUMN_ID},
                        selection, selectionArgs, null, null, null);

                if(mCursor.getCount() != 0){
                    mCursor.close();
                    throw new SQLiteConstraintException();
                }

                mCursor.close();
                //remove parentId .. no need to update parentId
                values.remove(ExpenseChildCategoryTable.COLUMN_EXPENSE_PARENT_CATEGORY_ID);
                selection = ExpenseChildCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(ExpenseChildCategoryTable.TABLE_EXPENSE_CHILD_CATEGORY, values, selection, selectionArgs);
                break;

            case ACCOUNT_CATEGORIES:
                // bulk update should not be supported
                count = db.update(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, values, selection, selectionArgs);
                break;

            case ACCOUNT_CATEGORIES_ID:

                name = values.getAsString(AccountCategoryTable.COLUMN_NAME);

                selection = AccountCategoryTable.COLUMN_NAME + " = ?";
                selectionArgs = new String[]{name};

                mCursor = db.query(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY,
                        new String []{AccountCategoryTable.COLUMN_ID},
                        selection, selectionArgs, null, null, null);

                if(mCursor.getCount() != 0){
                    mCursor.close();
                    throw new SQLiteConstraintException();
                }

                mCursor.close();
                selection = AccountCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, values, selection, selectionArgs);
                break;

            case TRANSACTION_CATEGORIES:
                // bulk update should not be supported
                count = db.update(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, values, selection, selectionArgs);
                break;

            case TRANSACTION_CATEGORIES_ID:

                name = values.getAsString(TransactionCategoryTable.COLUMN_NAME);

                selection = TransactionCategoryTable.COLUMN_NAME + " = ?";
                selectionArgs = new String[]{name};

                mCursor = db.query(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY,
                        new String []{TransactionCategoryTable.COLUMN_ID},
                        selection, selectionArgs, null, null, null);

                if(mCursor.getCount() != 0){
                    mCursor.close();
                    throw new SQLiteConstraintException();
                }

                mCursor.close();
                selection = TransactionCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, values, selection, selectionArgs);
                break;

            case ACCOUNTS:
                // bulk update should not be supported
                count = db.update(AccountTable.TABLE_ACCOUNT, values, selection, selectionArgs);
                break;

            case ACCOUNTS_ID:

                selection = AccountTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(AccountTable.TABLE_ACCOUNT, values, selection, selectionArgs);
                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_ACCOUNTS_URI, null);
                break;

            case BUDGETS:
                // bulk update should not be supported
                count = db.update(BudgetTable.TABLE_BUDGET, values, selection, selectionArgs);
                break;

            case BUDGETS_ID:

                selection = BudgetTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(BudgetTable.TABLE_BUDGET, values, selection, selectionArgs);
                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_BUDGETS_URI, null);
                break;

            case TRANSACTIONS:
                // bulk update should not be supported
                count = db.update(TransactionTable.TABLE_TRANSACTION, values, selection, selectionArgs);
                break;

            case TRANSACTIONS_ID:

                selection = TransactionTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(TransactionTable.TABLE_TRANSACTION, values, selection, selectionArgs);
                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_TRANSACTIONS_URI, null);

                break;

            case TRANSACTIONS_ID3:

                String account_id1 = uri.getPathSegments().get(2);
                String account_id2 = uri.getPathSegments().get(3);

                selection = TransactionTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
                selectionArgs = null;
                count = db.update(TransactionTable.TABLE_TRANSACTION, values, selection, selectionArgs);

                transactionAccountTrigger(Integer.parseInt(account_id1),db);
                transactionAccountTrigger(Integer.parseInt(account_id2),db);
                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_TRANSACTIONS_URI, null);
                //getContext().getContentResolver().notifyChange(VIEW_BUDGETS_URI, null);
                //getContext().getContentResolver().notifyChange(VIEW_ACCOUNTS_URI, null);

                break;

            case TRANSACTIONS_ACCOUNTS:
                // bulk update should not be supported
                count = db.update(TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT, values, selection, selectionArgs);
                break;

			/*
		case TRANSACTIONS_ACCOUNTS_ID:

			selection = TransactionAccountTable.COLUMN_TRANSACTION_ID + " = " +  uri.getPathSegments().get(1);
			selectionArgs = null;
			count = db.update(TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT, values, selection, selectionArgs);
			//notify the accounts view uri
			getContext().getContentResolver().notifyChange(VIEW_TRANSACTIONS_URI, null);

			break;
		*/
            case TRANSACTIONS_ACCOUNTS_ID2:

                //Toast.makeText(getContext(),"I am here", Toast.LENGTH_LONG).show();
                String transaction_id = uri.getPathSegments().get(1);
                String account_id = uri.getPathSegments().get(2);

                selection = TransactionAccountTable.COLUMN_TRANSACTION_ID + " = " +  transaction_id;
                selection += " AND " + TransactionAccountTable.COLUMN_PRIMARY_ACCOUNT_ID + " = " +  account_id;
                selectionArgs = null;

                //Toast.makeText(getContext(),String.valueOf(values.get(TransactionAccountTable.COLUMN_AMOUNT)), Toast.LENGTH_SHORT).show();

                Log.i("debug update", String.valueOf(values.get(TransactionAccountTable.COLUMN_AMOUNT)));

                count = db.update(TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT, values, selection, selectionArgs);

                transactionAccountTrigger(Integer.parseInt(account_id),db);

                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_TRANSACTIONS_URI, null);
                getContext().getContentResolver().notifyChange(VIEW_BUDGETS_URI, null);

                break;

            case TRANSACTIONS_ACCOUNTS_ID3:

                //Toast.makeText(getContext(),"I am here", Toast.LENGTH_LONG).show();
                String tran_id = uri.getPathSegments().get(1);
                String act_id = uri.getPathSegments().get(2);
                String is_deposit = uri.getPathSegments().get(3);
                //String orig_act_id = uri.getPathSegments().get(4);

                selection = TransactionAccountTable.COLUMN_TRANSACTION_ID + " = " +  tran_id;
                //selection += " AND " + TransactionAccountTable.COLUMN_PRIMARY_ACCOUNT_ID + " = " +  act_id;
                selection += " AND " + TransactionAccountTable.COLUMN_IS_DEPOSIT + " = " +  is_deposit;
                selectionArgs = null;

                //Toast.makeText(getContext(),String.valueOf(values.get(TransactionAccountTable.COLUMN_AMOUNT)), Toast.LENGTH_SHORT).show();

                Log.i("debug update", String.valueOf(values.get(TransactionAccountTable.COLUMN_AMOUNT)));

                count = db.update(TransactionAccountTable.TABLE_TRANSACTION_ACCOUNT, values, selection, selectionArgs);

                //debug **
                /*
                mCursor = db.rawQuery("SELECT COALESCE(SUM(amount),0) AS total FROM transactions_accounts "
                                       + " INNER JOIN transactions ON transactions_accounts.transaction_id = transactions._id "
                        + "WHERE transactions_accounts.primary_account_id = "+act_id+" AND transactions.deleted != 1 ", null);

                if(mCursor.moveToFirst())
                {
                    Log.w("mCursor",mCursor.getString(0));
                }
                */
                /*
                mCursor = db.rawQuery("SELECT transaction_id, primary_account_id, secondary_account_id, amount FROM transactions_accounts", null);
                String str;
               // Log.w("mCursor",String.valueOf(mCursor.getCount()));

                if (mCursor.getCount() > 0)
                {
                    mCursor.moveToFirst();
                    do {
                        str = mCursor.getString(0);
                        str += "," + mCursor.getString(1);
                        str += "," + mCursor.getString(2);
                       str += "," + mCursor.getString(3);
                        Log.w("mCursor",str);
                    } while (mCursor.moveToNext());
                }

                */
                //debug **

                /*
                Log.w("mIsDeposit",is_deposit);

                //if(is_deposit=="1") {
                    transactionAccountTrigger(Integer.parseInt(act_id), db);
                    //if(act_id != orig_act_id) transactionAccountTrigger(Integer.parseInt(orig_act_id),db);
                //}
                */

                //notify the accounts view uri
                getContext().getContentResolver().notifyChange(VIEW_TRANSACTIONS_URI, null);
                getContext().getContentResolver().notifyChange(VIEW_BUDGETS_URI, null);

                break;

            case TRANSACTIONS_ACCOUNTS_ID4:
                String orig_primary_account_id = uri.getPathSegments().get(1);
                String primary_account_id = uri.getPathSegments().get(2);
                String orig_seondary_account_id = uri.getPathSegments().get(3);
                String secondary_account_id = uri.getPathSegments().get(4);

                transactionAccountTrigger(Integer.parseInt(primary_account_id), db);
                if(primary_account_id != orig_primary_account_id) transactionAccountTrigger(Integer.parseInt(orig_primary_account_id), db);

                transactionAccountTrigger(Integer.parseInt(secondary_account_id), db);
                if(secondary_account_id != orig_seondary_account_id) transactionAccountTrigger(Integer.parseInt(orig_seondary_account_id), db);

                getContext().getContentResolver().notifyChange(VIEW_TRANSACTIONS_URI, null);
                getContext().getContentResolver().notifyChange(VIEW_BUDGETS_URI, null);

                break;

            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        //db.close();

        return count;
    }


    private void transactionAccountTrigger(int accountId, SQLiteDatabase db){

        /*
        db.execSQL(
                "UPDATE accounts SET current_balance = initial_balance +"
                        + "(SELECT COALESCE(SUM(amount),0) AS total FROM transactions_accounts "
                        + "WHERE primary_account_id = "+accountId+") "
                        + "WHERE _id = "+accountId);
        */

        String q = "UPDATE accounts SET current_balance = initial_balance +"
                + "(SELECT COALESCE(SUM(amount),0) AS total FROM transactions_accounts "
                + " INNER JOIN transactions ON transactions_accounts.transaction_id = transactions._id "
                + "WHERE transactions_accounts.primary_account_id = "+accountId+" AND transactions.deleted != 1) "
                + "WHERE _id = "+accountId;

        Log.w("transaction trigger", q);

        db.execSQL(
                "UPDATE accounts SET current_balance = initial_balance +"
                        + "(SELECT COALESCE(SUM(amount),0) AS total FROM transactions_accounts "
                        + " INNER JOIN transactions ON transactions_accounts.transaction_id = transactions._id "
                        + "WHERE transactions_accounts.primary_account_id = "+accountId+" AND transactions.deleted != 1) "
                        + "WHERE _id = "+accountId);

        getContext().getContentResolver().notifyChange(AccountTable.CONTENT_URI, null);
        getContext().getContentResolver().notifyChange(AccountView.CONTENT_URI, null);
        getContext().getContentResolver().notifyChange(VIEW_BUDGETS_URI, null);


    }


}
