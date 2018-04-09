package org.musalahuddin.myexpenseorganizer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.activity.EditAccount;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.AccountView;
import org.musalahuddin.myexpenseorganizer.dialog.ConfirmationDialog;
import org.musalahuddin.myexpenseorganizer.serializable.Account;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Muhammad on 2/12/2017.
 */

public class AccountsFragment2 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    //private LoaderManager mManager;
    private LoaderManager mManager;
    private SimpleCursorAdapter mAdapter;

    public ListView lv;

    private static final int EDIT= 1;
    private static final int DELETE = 2;


    public AccountsFragment2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.w("which fragment", "I am here onCreateView accountfragment2");

        View rootView = inflater.inflate(R.layout.fragment_accounts_v2, container, false);

        lv = (ListView) rootView.findViewById(R.id.list);

        mAdapter = new SimpleCursorAdapter(getActivity(),R.layout.account_row_v2,null,new String[]{},new int[]{},0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View row = super.getView(position, convertView, parent);
                Cursor c = getCursor();
                c.moveToPosition(position);

                String act_category = c.getString(c.getColumnIndex(AccountView.COLUMN_ACCOUNT_CATEGORY_NAME));
                String name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
                int number = c.getInt(c.getColumnIndex(AccountTable.COLUMN_NUMBER));
                double curr_balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_CURR_BALANCE))/100;
                double init_balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_INIT_BALANCE))/100;
                long due_date = c.getLong(c.getColumnIndex(AccountTable.COLUMN_DUE_DATE));

                DecimalFormat f = new DecimalFormat("0.00");
                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

                if(number > 0){
                    name = name+" (...."+number+")";
                }

                Log.w("accountName", name);

                TextView tvAccountCategory = (TextView) row.findViewById(R.id.account_category);
                TextView tvAccountName = (TextView) row.findViewById(R.id.account_name);
                TextView tvAccountCurrBalance = (TextView) row.findViewById(R.id.curr_balance);
                TextView tvAccountInitBalance = (TextView) row.findViewById(R.id.init_balance);
                TextView tvAccountDue = (TextView) row.findViewById(R.id.account_due);

                if(position ==0){
                    tvAccountCategory.setVisibility(View.VISIBLE);
                }
                else {
                    c.moveToPosition(position-1);
                    String prev_act_category = c.getString(c.getColumnIndex(AccountView.COLUMN_ACCOUNT_CATEGORY_NAME));

                    //Log.i("getView", "datecheck " + sdf.format(cal.getTime()) + " = > " + sdf.format(prevCal.getTime()));
                    if ((act_category).equals(prev_act_category)) {
                        tvAccountCategory.setVisibility(View.GONE);
                    } else {
                        tvAccountCategory.setVisibility(View.VISIBLE);
                    }
                }

                // set category
                tvAccountCategory.setText(act_category);

                // set name
                tvAccountName.setText(name);

                // set curr balance
                if(curr_balance < 0){
                    tvAccountCurrBalance.setText("-"+n.format(Math.abs(curr_balance)));
                    tvAccountCurrBalance.setTextColor(Color.parseColor("#d30202"));
                }
                else{

                    tvAccountCurrBalance.setText(n.format(Math.abs(curr_balance)));
                    tvAccountCurrBalance.setTextColor(Color.DKGRAY);
                }

                //set opening balance
                if(init_balance < 0){
                    tvAccountInitBalance.setText("opening balance (-"+n.format(Math.abs(init_balance))+")");
                }
                else{
                    tvAccountInitBalance.setText("opening balance ("+n.format(Math.abs(init_balance))+")");
                }

                //set due date
                if(due_date != 0L){
                    java.text.DateFormat mTitleDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM);
                    tvAccountDue.setVisibility(View.VISIBLE);
                    if(due_date < 32){
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis());
                        //cal.set(Calendar.DAY_OF_MONTH, (int) due_date);

                        cal.set(Calendar.DAY_OF_MONTH, Math.min((int) due_date, cal.getActualMaximum(Calendar.DAY_OF_MONTH)));

                        due_date = cal.getTimeInMillis();
                    }
                    if (due_date < System.currentTimeMillis()) {
                        Calendar cal = Calendar.getInstance();
                        Calendar curr = Calendar.getInstance();

                        cal.setTimeInMillis(due_date);
                        curr.setTimeInMillis(System.currentTimeMillis());

                        //curr.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));

                        curr.set(Calendar.DAY_OF_MONTH, Math.min(cal.get(Calendar.DAY_OF_MONTH), curr.getActualMaximum(Calendar.DAY_OF_MONTH)));

                        if (curr.getTimeInMillis() < System.currentTimeMillis()) {
                            curr.add(Calendar.MONTH, 1);
                        }

                        due_date = curr.getTimeInMillis();
                    }

                    tvAccountDue.setText("Due: " + mTitleDateFormat.format(due_date));
                }
                else{
                    tvAccountDue.setVisibility(View.GONE);
                    tvAccountDue.setText("");
                }

                return row;
            }
        };

        mManager = getLoaderManager();
        mManager.initLoader(0, null, this);
        lv.setAdapter(mAdapter);
        lv.setEmptyView(rootView.findViewById(R.id.empty));
        lv.setOnItemClickListener(this);
        registerForContextMenu(lv);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.w("which loader", "I am here onCreateLoader");

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

        sortOrder = "LOWER("+AccountView.COLUMN_ACCOUNT_CATEGORY_NAME+"), LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";
        //sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";

        CursorLoader cursorLoader = new CursorLoader(MyApplication.getInstance(),
                AccountView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        mAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,DELETE,0,"Delete Account");//setOnMenuItemClickListener(this);
        menu.add(0,EDIT,0,"View/Edit Account");//setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
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

    public void editAccount(Cursor c){
        Account account = new Account();
        account.id = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));
        account.name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
        account.number = c.getInt(c.getColumnIndex(AccountTable.COLUMN_NUMBER));
        account.description = c.getString(c.getColumnIndex(AccountTable.COLUMN_DESCRIPTION));
        account.balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_INIT_BALANCE));
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

    public void deleteAccount(int position){

        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        long accountId = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));

        boolean success = AccountTable.delete(accountId) != -1;

        if(!success){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }
}
