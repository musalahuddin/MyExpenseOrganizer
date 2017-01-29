package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;
import org.musalahuddin.myexpenseorganizer.util.CatImporter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ExpenseChildCategoryTable extends Model{

    // URI
    public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.EXPENSE_CHILD_CATEGORIES_URI;

    // Table columns
    public static final String TABLE_EXPENSE_CHILD_CATEGORY = "expense_child_categories";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXPENSE_PARENT_CATEGORY_ID = "expense_parent_category_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_CREATE_DATE = "create_date";
    public static final String COLUMN_MOD_DATE = "mod_date";
    public static final String COLUMN_DELETE_DATE = "delete_date";
    public static final String COLUMN_DELETED = "deleted";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_EXPENSE_CHILD_CATEGORY
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_EXPENSE_PARENT_CATEGORY_ID + " INTEGER NOT NULL, "
            + COLUMN_NAME + " TEXT,"
            + COLUMN_NOTES + " TEXT, "
            + COLUMN_CREATE_DATE + " INTEGER, "
            + COLUMN_MOD_DATE + " INTEGER, "
            + COLUMN_DELETE_DATE + " INTEGER, "
            + COLUMN_DELETED + " INTERGER DEFAULT 0 NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_EXPENSE_PARENT_CATEGORY_ID + ") REFERENCES "+ExpenseParentCategoryTable.TABLE_EXPENSE_PARENT_CATEGORY+"("+ExpenseParentCategoryTable.COLUMN_ID+")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        CatImporter.importExpenseCategories();
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ExpenseChildCategoryTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE_CHILD_CATEGORY);
        onCreate(database);
    }

    public static boolean delete(Long id){
        try{
            return cr().delete(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(),
                    null, null) > 0;
        }
        catch (SQLiteConstraintException e){
            Log.i("delete",e.getMessage());
            return false;
        }
    }

    /**
     * Creates a new Sub Category for a giver Parent Category
     * @param name
     * @param parentId
     * @return the row id of the newly inserted row, of -1 if category already exists
     */
    public static long create(String name, Long parentId){
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_NAME, name);
        initialValues.put(COLUMN_EXPENSE_PARENT_CATEGORY_ID, parentId);
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
     * Updates the name for category
     * @param name
     * @param id
     * @return number of rows affected, or -1 if unique constraint is violated
     */
    public static int update(String name, Long id, Long parentId) {
        ContentValues args = new ContentValues();
        args.put(COLUMN_NAME, name);
        args.put(COLUMN_EXPENSE_PARENT_CATEGORY_ID, parentId);
        try {
            return cr().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(),
                    args, null, null);
        } catch (SQLiteConstraintException e) {
            // TODO Auto-generated catch block
            return -1;
        }
    }

    /**
     * Looks for a cat with a name and a parent id
     * @param name
     * @param parentId
     * @return id of -1 if not found
     */
    public static long find(String name, Long parentId){
        String selection;
        String[] selectionArgs;

        selection = COLUMN_NAME + " = ? AND " + COLUMN_EXPENSE_PARENT_CATEGORY_ID + " = ?";
        selectionArgs = new String[]{name,String.valueOf(parentId)};

        Cursor mCursor = cr().query(CONTENT_URI, new String[] {COLUMN_ID}, selection, selectionArgs, null);

        if(mCursor.getCount() == 0){
            mCursor.close();
            return -1;
        }
        else{
            mCursor.moveToFirst();
            long result = mCursor.getLong(0);
            mCursor.close();
            return result;
        }
    }

    /**
     * Count How many subcategories under a given parent?
     * @param parentId
     * return number of subcategories
     */

    public static long countSub(Long parentId){
        String selection;
        String[] selectionArgs;

        selection = COLUMN_EXPENSE_PARENT_CATEGORY_ID + " = ?";
        selectionArgs = new String[]{String.valueOf(parentId)};

        Cursor mCursor = cr().query(CONTENT_URI, new String[] {"COUNT(*)"}, selection, selectionArgs, null);

        if(mCursor.getCount() == 0){
            mCursor.close();
            return 0;
        }
        else{
            mCursor.moveToFirst();
            long result = mCursor.getInt(0);
            mCursor.close();
            return result;
        }
    }


}
