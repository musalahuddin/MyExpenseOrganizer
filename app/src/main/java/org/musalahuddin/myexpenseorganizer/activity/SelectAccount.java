package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
//import org.musalahuddin.myexpenseorganizer.dialog.EditTextDialog.EditTextDialogListener;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SelectAccount extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener {

    private LoaderManager mManager;
    private SimpleCursorAdapter mAdapter;

    private Long mAccountId;

    /**
     * edit the category label
     */
    private static final int EDIT_CAT = Menu.FIRST+1;
    /**
     * delete the category after checking if
     * there are mapped transactions or subcategories
     */
    private static final int DELETE_CAT = Menu.FIRST+2;

    boolean mManageOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.accounts_list);
        Intent intent = getIntent();
        String action = intent.getAction();
        //mManageOnly = action != null && action.equals("myexpenseorganizer.intent.manage.accountcategories");

        //setTitle(mManageOnly ? R.string.pref_manage_account_categories_title : R.string.select_account_category);

        Bundle extras = getIntent().getExtras();

        mAccountId = (extras != null) ? extras.getLong("accountId") : 0L;

        //actionbar
        getSupportActionBar().setTitle("Select Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lv = (ListView) findViewById(R.id.list);

        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{AccountTable.COLUMN_NAME};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{android.R.id.text1};

        // Now create a simple cursor adapter and set it to display
        mAdapter = new SimpleCursorAdapter(getSupportActionBar().getThemedContext(),
	    		/*MyApplication.getInstance(),*/
                android.R.layout.simple_list_item_1, null, from, to,0){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };

        mManager = getLoaderManager();

        mManager.initLoader(0, null, this);

        lv.setAdapter(mAdapter);
        lv.setEmptyView(findViewById(R.id.empty));
        lv.setOnItemClickListener(this);
        registerForContextMenu(lv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection;
        String[] selectionArgs, projection;
        String sortOrder;

        selection = AccountTable.COLUMN_ID + "!=?";
        selectionArgs= new String[]{String.valueOf(mAccountId)};
        projection = new String[]{AccountTable.COLUMN_ID, AccountTable.COLUMN_NAME};
        sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";

        CursorLoader cursorLoader = new CursorLoader(MyApplication.getInstance(),
                AccountTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        mAdapter.swapCursor(c);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> c) {
        mAdapter.swapCursor(null);

    }

    /**
     * Callback from button
     * @param v
     */
    public void importCats(View v) {
        //Intent i = new Intent(this, CatImport.class);
        Intent i = new Intent("myexpenseorganizer.intent.import.accountcategories");
        startActivity(i);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        //Toast.makeText(this, "You have chosen: " + position , Toast.LENGTH_SHORT).show();

        Intent intent=new Intent();
        String name =   ((TextView) view).getText().toString();
        intent.putExtra("account_id",id);
        intent.putExtra("account_name", name);
        setResult(RESULT_OK,intent);
        finish();
    }


}
