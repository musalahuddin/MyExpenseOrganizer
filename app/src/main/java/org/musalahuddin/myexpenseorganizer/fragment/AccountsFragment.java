package org.musalahuddin.myexpenseorganizer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
//import org.musalahuddin.myexpenseorganizer.activity.SelectTransaction;
import org.musalahuddin.myexpenseorganizer.activity.EditAccount;
import org.musalahuddin.myexpenseorganizer.adapter.AccountAdapter;
import org.musalahuddin.myexpenseorganizer.database.AccountCategoryTable;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.AccountView;
import org.musalahuddin.myexpenseorganizer.dialog.ConfirmationDialog;
import org.musalahuddin.myexpenseorganizer.dialog.ListDialog;
import org.musalahuddin.myexpenseorganizer.serializable.Account;
import org.w3c.dom.Text;

public class AccountsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AccountAdapter.OnItemClickListener, AccountAdapter.OnItemCreateContextMenuListener{

    private LoaderManager mManager;
    private AccountAdapter mAdapter;

    private TextView emptyView;

    private static final int EDIT= 1;
    private static final int DELETE = 2;


    public AccountsFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);

        emptyView = (TextView) rootView.findViewById(R.id.empty);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.list);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        mAdapter = new AccountAdapter(null);

        //mAdapter.setOnItemLongClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemCreateContextMenuListener(this);
        //registerForContextMenu(rv);


        mManager = getLoaderManager();

        mManager.initLoader(0, null, this);

        rv.setAdapter(mAdapter);

       // rv.setEmptyView(findViewById(R.id.empty));

        return rootView;
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = item.getOrder();
        Cursor cursor;

        switch(item.getItemId()) {

            case EDIT:

                //Toast.makeText(getActivity(), "edit: " + position, Toast.LENGTH_LONG).show();
                cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);

                editAccount(cursor);

                break;

            case DELETE:

                //cursor = mAdapter.getCursor();
                //cursor.moveToPosition(position);

                //deleteAccount(cursor);
                confirmDelete(position);
                //Toast.makeText(getActivity(), "delete: " + position, Toast.LENGTH_LONG).show();
                break;
        }

        return false;
    }

    public void confirmDelete(int position){
        Bundle args = new Bundle();
        args.putString(ConfirmationDialog.KEY_TITLE, "Delete account?");
        args.putString(ConfirmationDialog.KEY_MESSAGE, "The selected account will be deleted.");
        args.putString(ConfirmationDialog.KEY_NEGATIVE_BUTTON_LABEL, "CANCEL");
        args.putString(ConfirmationDialog.KEY_POSITIVE_BUTTON_LABEL, "DELETE");
        args.putInt(ConfirmationDialog.KEY_POSITION, position);

        ConfirmationDialog.newInstance(args)
                .show(getFragmentManager(), "DELETE_TRANSACTION");

    }

    public void deleteAccount(int position){

        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        long accountId = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));

        boolean success = AccountTable.delete(accountId) != -1;

        if(!success){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0,
                                         Bundle arg1) {

        String selection;
        String[] selectionArgs, projection;
        String sortOrder;

        selection = null;
        selectionArgs=null;
        projection = new String[]{
                AccountTable.COLUMN_ID,
                AccountTable.COLUMN_NAME,
                AccountTable.COLUMN_NUMBER,
                AccountTable.COLUMN_DESCRIPTION,
                AccountTable.COLUMN_INIT_BALANCE,
                AccountTable.COLUMN_CURR_BALANCE,
                AccountTable.COLUMN_CREDIT_LIMIT,
                AccountTable.COLUMN_MONTHLY_PAYMENT,
                AccountTable.COLUMN_DUE_DATE,
                AccountTable.COLUMN_BUDGET_START_DAY,
                AccountView.COLUMN_ACCOUNT_CATEGORY_ID,
                AccountView.COLUMN_ACCOUNT_CATEGORY_NAME,
                };

        sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";

        CursorLoader cursorLoader = new CursorLoader(MyApplication.getInstance(),
                AccountView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0,
                               Cursor c) {
        mAdapter.swapCursor(c);

        if(mAdapter.getItemCount() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }
        else{
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> c) {
        mAdapter.swapCursor(null);

    }

    @Override
    public void onItemClick(View v, int position) {
        // TODO Auto-generated method stub
        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);
        String name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
        Long id = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));

        //Toast.makeText(v.getContext(), String.valueOf(id), Toast.LENGTH_LONG).show();

        Bundle b = new Bundle();
        b.putString("accountName",name);
        b.putLong("accountId", id);
        Intent i = new Intent("myexpenseorganizer.intent.select.transactions");
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public void OnItemCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo, Integer position) {

        //Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_LONG).show();
        menu.add(0,DELETE,position,"Delete Account");//setOnMenuItemClickListener(this);
        menu.add(0,EDIT,position,"View/Edit Account");//setOnMenuItemClickListener(this);
    }

    public void editAccount(Cursor c){
        Account account = new Account();
        account.id = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));
        account.name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
        account.number = c.getInt(c.getColumnIndex(AccountTable.COLUMN_NUMBER));
        account.description = c.getString(c.getColumnIndex(AccountTable.COLUMN_DESCRIPTION));
        account.balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_CURR_BALANCE));
        account.limit = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_CREDIT_LIMIT));
        account.payment = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_MONTHLY_PAYMENT));
        account.due = c.getLong(c.getColumnIndex(AccountTable.COLUMN_DUE_DATE));
        account.budget_start_day = c.getLong(c.getColumnIndex(AccountTable.COLUMN_BUDGET_START_DAY));
        account.accoutCategoryId = c.getLong(c.getColumnIndex(AccountView.COLUMN_ACCOUNT_CATEGORY_ID));
        account.accountCategoryName = c.getString(c.getColumnIndex(AccountView.COLUMN_ACCOUNT_CATEGORY_NAME));

        Bundle b = new Bundle();
        b.putSerializable("account", account);


        Intent i = new Intent(getActivity(),EditAccount.class);
        i.putExtras(b);
        startActivity(i);
    }

    public void deleteAccount(Cursor c){

        long accountId = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));

        boolean success = AccountTable.delete(accountId) != -1;

        if(!success){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }



    /*
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        return true;

    }
    */


    /*
    @Override
    public void onItemLongClick(View v, int position) {

        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);
        String name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
        // TODO Auto-generated method stub
       // Toast.makeText(v.getContext(), "long click: " + name, Toast.LENGTH_LONG).show();


        //ListDialog.newInstance(null).show(getFragmentManager(), "CREATE_CATEGORY");
    }
    */
    /*
    void showDialog() {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = ListDialog.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }
    */

}
