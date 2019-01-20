package org.musalahuddin.myexpenseorganizer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyExpenseOrganizerDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "myexpenseorganizer.db";
    public static final int DATABASE_VERSION = 3;

    public MyExpenseOrganizerDatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON;");
        //super.onOpen(db);
        Log.i("onOpen","called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        ExpenseParentCategoryTable.onCreate(db);
        ExpenseChildCategoryTable.onCreate(db);
        AccountCategoryTable.onCreate(db);
        TransactionCategoryTable.onCreate(db);
        AccountTable.onCreate(db);
        AccountView.onCreate(db);
        TransactionTable.onCreate(db);
        TransactionAccountTable.onCreate(db);
        TransactionView.onCreate(db);
        BudgetTable.onCreate(db);
        BudgetView.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*
        ExpenseParentCategoryTable.onUpgrade(db, oldVersion, newVersion);
        ExpenseChildCategoryTable.onUpgrade(db, oldVersion, newVersion);
        AccountCategoryTable.onUpgrade(db, oldVersion, newVersion);
        TransactionCategoryTable.onUpgrade(db, oldVersion, newVersion);
        AccountTable.onUpgrade(db, oldVersion, newVersion);
        AccountView.onUpgrade(db, oldVersion, newVersion);
        TransactionTable.onUpgrade(db, oldVersion, newVersion);
        TransactionAccountTable.onUpgrade(db, oldVersion, newVersion);
        TransactionView.onUpgrade(db, oldVersion, newVersion);
        BudgetTable.onUpgrade(db, oldVersion, newVersion);
        BudgetView.onUpgrade(db, oldVersion, newVersion);
        */

        // only upgrading Account view in version 2
        AccountView.onUpgrade(db, oldVersion, newVersion);


    }

}
