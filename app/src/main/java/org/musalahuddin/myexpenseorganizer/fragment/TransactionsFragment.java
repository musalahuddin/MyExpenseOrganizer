package org.musalahuddin.myexpenseorganizer.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVWriter;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.activity.EditTransaction;
import org.musalahuddin.myexpenseorganizer.database.AccountCategoryTable;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.MyExpenseOrganizerDatabaseHelper;
import org.musalahuddin.myexpenseorganizer.database.TransactionTable;
import org.musalahuddin.myexpenseorganizer.database.TransactionView;
import org.musalahuddin.myexpenseorganizer.dialog.ConfirmationDialog;
import org.musalahuddin.myexpenseorganizer.permission.Storage;
import org.musalahuddin.myexpenseorganizer.serializable.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Muhammad on 12/11/2016.
 */

public class TransactionsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    //private LoaderManager mManager;
    private LoaderManager mManager, mActManager;
    private SimpleCursorAdapter mAdapter, mAccountsAdapter;

    protected boolean mShowAccounts;

    private static final int TRANSACTIONS = 1;

    private static final int ACCOUNTS = 2;

    private static final int EDIT_TRANSACTION = Menu.FIRST+1;

    private static final int DELETE_TRANSACTION = Menu.FIRST+2;

    public long mAccountId = 0L;

    public String mAccountName = "";

    private long mExpenseCatId = 0L;

    private long mStartDate = 0L;

    private long mEndDate = 0L;

    private long prevDate = 0L;

    public MyExpenseOrganizerDatabaseHelper dbhelper;

    public Context mContext;

    public Handler mHandler;

    public RelativeLayout mHeader;

    public LinearLayout mPreloader;

    public ListView lv;

    private static final int DELETE = 1;

    public TransactionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);

        //Get Argument that passed from activity in "data" key value
        mShowAccounts = getArguments().getBoolean("showAccounts");

        mHeader = (RelativeLayout) rootView.findViewById(R.id.header);

        mPreloader = (LinearLayout) rootView.findViewById(R.id.ll_preloader);

        dbhelper = new MyExpenseOrganizerDatabaseHelper(MyApplication.getInstance());
        mContext = getActivity();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                Bundle b = msg.getData();

                if (b.getInt("id") == 1) {
                    Toast.makeText(mContext,"csv location: "+ b.getString("path"),Toast.LENGTH_LONG).show();

                }
                else if (b.getInt("id") == 2) {
                    Toast.makeText(mContext,"error",Toast.LENGTH_LONG).show();

                }

                else if (b.getInt("id") == 3) {
                    Toast.makeText(mContext,"There's nothing to export",Toast.LENGTH_LONG).show();

                }
            }
        };

        if(!mShowAccounts){
            mHeader.setVisibility(View.VISIBLE);

            TextView tvAccount = (TextView) rootView.findViewById(R.id.tv_account);
            tvAccount.setVisibility(View.VISIBLE);

            Spinner spAccount = (Spinner) rootView.findViewById(R.id.sp_account);
            spAccount.setVisibility(View.GONE);

            mAccountId = getArguments().getLong("accountId");
            mAccountName = getArguments().getString("accountName");
            mExpenseCatId = getArguments().getLong("expenseCatId",0L);
            mStartDate = getArguments().getLong("startDate",0L);
            mEndDate = getArguments().getLong("endDate",0L);

            tvAccount.setText(mAccountName);
        }
        else{
            TextView tvAccount = (TextView) rootView.findViewById(R.id.tv_account);
            tvAccount.setVisibility(View.GONE);

            Spinner spAccount = (Spinner) rootView.findViewById(R.id.sp_account);
            spAccount.setVisibility(View.VISIBLE);

            spAccount.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            mAccountsAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null, new String[] {AccountTable.COLUMN_NAME}, new int[] {android.R.id.text1}, 0){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                    text.setTextColor(Color.WHITE);
                    text.setGravity(Gravity.CENTER);
                    return view;
                }

            };

            mAccountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spAccount.setAdapter(mAccountsAdapter);

            spAccount.setOnItemSelectedListener(this);

            mActManager = getLoaderManager();

            mActManager.initLoader(ACCOUNTS, null, this);

        }

        lv = (ListView) rootView.findViewById(R.id.list);


        // Create an array to specify the fields we want to display in the list
        //String[] from = new String[]{TransactionView.COLUMN_TRANSACTION_CATEGORY_NAME};
        String[] from = new String[]{};

        // and an array of the fields we want to bind those fields to
        //int[] to = new int[]{R.id.transaction_type};
        int[] to = new int[]{};
        //int[] to = new int[]{android.R.id.text1};


        // Now create a simple cursor adapter and set it to display
        mAdapter = new SimpleCursorAdapter(getActivity(),R.layout.transaction_row,null,from,to,0){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                //Toast.makeText(SelectTransaction.this, "expense_category_child_name : " + prevDate, Toast.LENGTH_LONG).show();
                //if(convertView != null) return convertView;
                if(position == 0){
                    prevDate = 0L;
                }

                if(convertView == null)
                    Log.w("getView ","converView=Null");

                Log.w("getView ", "datecheck prevDate is : "+String.valueOf(prevDate) + " position is: " + String.valueOf(position) );

               // View row = super.getView(position, convertView, parent);

                View row = super.getView(position, convertView, parent);
                Cursor c = getCursor();
                c.moveToPosition(position);

                DecimalFormat f = new DecimalFormat("0.00");
                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

                //get cursor values
                String expense_category_parent_name = c.getString(c.getColumnIndex(TransactionView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME));
                String expense_category_child_name = c.getString(c.getColumnIndex(TransactionView.COLUMN_EXPENSE_CATEGORY_CHILD_NAME));
                long transaction_date = c.getLong(c.getColumnIndex(TransactionView.COLUMN_TRANSACTION_DATE));
                long seondary_account_id = c.getLong(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_ID));
                String seondary_account_name = c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_NAME));
                String seondary_account_description = c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION));
                double transaction_amount = Math.abs(c.getDouble(c.getColumnIndex(TransactionView.COLUMN_AMOUNT)) / 100);
                int is_deposit = c.getInt(c.getColumnIndex(TransactionView.COLUMN_IS_DEPOSIT));
                String imgPath = c.getString(c.getColumnIndex(TransactionView.COLUMN_IMAGE_PATH));

                Log.i("getView ", "expense_category_parent_name: " + String.valueOf(expense_category_parent_name));

                //get views

                TextView tvTransactionAccount = (TextView) row.findViewById(R.id.transaction_account);
                TextView tvTransactionCategory = (TextView) row.findViewById(R.id.transaction_category);
                TextView tvTransactionDate = (TextView) row.findViewById(R.id.transaction_date);
                TextView tvTransactionAmount = (TextView) row.findViewById(R.id.transaction_amount);
                ImageView imTransactionAttachment = (ImageView) row.findViewById(R.id.transaction_attachment);



                 SimpleDateFormat sdf = new SimpleDateFormat("EEE, MM/dd/yyyy");
                 Calendar cal = Calendar.getInstance();
                 cal.setTimeInMillis(transaction_date);


                if(position ==0){
                    tvTransactionDate.setVisibility(View.VISIBLE);
                }
                else {
                    c.moveToPosition(position-1);
                    long prev_transaction_date = c.getLong(c.getColumnIndex(TransactionView.COLUMN_TRANSACTION_DATE));

                   // tvTransactionDate.setVisibility(View.GONE);


                    Calendar prevCal = Calendar.getInstance();
                    prevCal.setTimeInMillis(prev_transaction_date);


                    //Log.i("getView", "datecheck " + sdf.format(cal.getTime()) + " = > " + sdf.format(prevCal.getTime()));
                    if (sdf.format(cal.getTime()).equals(sdf.format(prevCal.getTime()))) {
                        tvTransactionDate.setVisibility(View.GONE);
                    } else {
                        tvTransactionDate.setVisibility(View.VISIBLE);
                    }

                }

                // set account
                if (seondary_account_id == 1L)
                    tvTransactionAccount.setText(seondary_account_description);
                else
                    tvTransactionAccount.setText(seondary_account_name);

                //set category
                tvTransactionCategory.setText(expense_category_parent_name + " : " + expense_category_child_name);
                //Toast.makeText(getActivity(), "mAccountId : " + String.valueOf(expense_category_parent_name), Toast.LENGTH_LONG).show();

                // set time
                tvTransactionDate.setText(sdf.format(cal.getTime()));

                //set amount
                if (is_deposit == 1 || transaction_amount == 0) {
                    //tvTransactionAmount.setText("$" + f.format(Math.abs(transaction_amount)));
                    tvTransactionAmount.setText(n.format(Math.abs(transaction_amount)));
                    tvTransactionAmount.setTextColor(Color.DKGRAY);
                } else {
                    //tvTransactionAmount.setText("-$"+f.format(Math.abs(transaction_amount)));
                    tvTransactionAmount.setText("-" + n.format(Math.abs(transaction_amount)));
                    //tvTransactionAmount.setTextColor(Color.RED);
                    tvTransactionAmount.setTextColor(Color.parseColor("#d30202"));
                }

                //Toast.makeText(SelectTransaction.this, "expense_category_child_name : " + expense_category_child_name, Toast.LENGTH_LONG).show();


                // attachment image
                if (imgPath.isEmpty()) {
                    imTransactionAttachment.setVisibility(View.GONE);
                } else {
                    imTransactionAttachment.setVisibility(View.VISIBLE);
                }

                prevDate = transaction_date;

                return row;

            }
       };

        mManager = getLoaderManager();

        if(!mShowAccounts){
            //Toast.makeText(getActivity(), "I am here" , Toast.LENGTH_LONG).show();
            mManager.initLoader(TRANSACTIONS, null, this);
        }

        lv.setAdapter(mAdapter);
        lv.setEmptyView(rootView.findViewById(R.id.empty));
        lv.setOnItemClickListener(this);
        registerForContextMenu(lv);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,DELETE,0,"Delete Transaction");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Cursor cursor;

        switch(item.getItemId()) {

            case DELETE:

                //cursor = mAdapter.getCursor();
                //cursor.moveToPosition(position);

                //deleteTransaction(cursor);
                confirmDelete(position);

                // Toast.makeText(getActivity(), "delete: " + position, Toast.LENGTH_LONG).show();

                return true;
        }

        return false;
    }

    public void confirmDelete(int position){
        Bundle args = new Bundle();
        args.putString(ConfirmationDialog.KEY_TITLE, "Delete transaction?");
        args.putString(ConfirmationDialog.KEY_MESSAGE, "The selected transaction will be deleted.");
        args.putString(ConfirmationDialog.KEY_NEGATIVE_BUTTON_LABEL, "CANCEL");
        args.putString(ConfirmationDialog.KEY_POSITIVE_BUTTON_LABEL, "DELETE");
        args.putInt(ConfirmationDialog.KEY_POSITION, position);

        ConfirmationDialog.newInstance(args)
                .show(getFragmentManager(), "DELETE_TRANSACTION");

    }

    public void deleteTransaction(int position){
        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        long transactionId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_ID));
        long acountId1 =  c.getLong(c.getColumnIndex(TransactionView.COLUMN_PRIMARY_ACCOUNT_ID));
        long acountId2 =  c.getLong(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_ID));

        boolean success = TransactionTable.delete(transactionId, acountId1, acountId2) != -1;

        if(!success){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {

        Log.i("onCreateLoader ", "debug--onCreateLoader "+ String.valueOf(id));

        String selection;
        String[] selectionArgs, projection;
        String sortOrder;
        CursorLoader cursorLoader = null;

        switch(id){

            case TRANSACTIONS:

                if(mExpenseCatId != 0L){
                    selection = TransactionView.COLUMN_PRIMARY_ACCOUNT_ID+"=? AND " + TransactionView.COLUMN_EXPENSE_CATEGORY_ID+"=? "
                            + " AND " + TransactionView.COLUMN_TRANSACTION_DATE + " BETWEEN  ?  AND ? ";
                    selectionArgs = new String[]{String.valueOf(mAccountId), String.valueOf(mExpenseCatId), String.valueOf(mStartDate), String.valueOf(mEndDate)};
                }
                else{
                    selection = TransactionView.COLUMN_PRIMARY_ACCOUNT_ID+"=?";
                    selectionArgs = new String[]{String.valueOf(mAccountId)};
                }
                projection = new String[]{
                        TransactionView.COLUMN_ID,
                        TransactionView.COLUMN_TRANSACTION_CATEGORY_ID,
                        TransactionView.COLUMN_TRANSACTION_CATEGORY_NAME,
                        TransactionView.COLUMN_PRIMARY_ACCOUNT_ID,
                        TransactionView.COLUMN_SECONDARY_ACCOUNT_ID,
                        TransactionView.COLUMN_SECONDARY_ACCOUNT_NAME,
                        TransactionView.COLUMN_PRIMARY_ACCOUNT_DESCRIPTION,
                        TransactionView.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION,
                        TransactionView.COLUMN_AMOUNT,
                        TransactionView.COLUMN_IS_DEPOSIT,
                        TransactionView.COLUMN_NOTES,
                        TransactionView.COLUMN_EXPENSE_CATEGORY_ID,
                        TransactionView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME,
                        TransactionView.COLUMN_EXPENSE_CATEGORY_CHILD_NAME,
                        TransactionView.COLUMN_TRANSACTION_DATE,
                        TransactionView.COLUMN_IMAGE_PATH
                };

                sortOrder = "LOWER("+TransactionView.COLUMN_TRANSACTION_DATE+")" + " DESC ";

               // Toast.makeText(getActivity(), "mAccountId : " + String.valueOf(mAccountId), Toast.LENGTH_LONG).show();

                cursorLoader = new CursorLoader(getActivity(),
                        TransactionView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

                break;

            case ACCOUNTS:

                selection = null;
                selectionArgs=null;
                projection = new String[]{
                        AccountTable.COLUMN_ID,
                        AccountTable.COLUMN_NAME
                };

                sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";

                cursorLoader = new CursorLoader(getActivity(),
                        AccountTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        int id = loader.getId();
       // Log.i("onLoadFinished ", "debug--onLoadFinished "+ String.valueOf(id));
        // TODO Auto-generated method stub
        if(id == TRANSACTIONS){
           // Toast.makeText(getActivity(), "finished loading", Toast.LENGTH_LONG).show();
            mPreloader.setVisibility(View.GONE);
            mAdapter.swapCursor(c);


        }
        else if(id == ACCOUNTS){
            mAccountsAdapter.swapCursor(c);
            //Toast.makeText(getActivity(), "Count: " + String.valueOf(c.getCount()), Toast.LENGTH_LONG).show();
            //hide header if there aren't any accounts
            if(c.getCount() > 0){
                mHeader.setVisibility(View.VISIBLE);
            }
            else{
                mHeader.setVisibility(View.GONE);
                mPreloader.setVisibility(View.GONE);
            }
        }
        //prevDate = 0L;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        int id = loader.getId();
        // TODO Auto-generated method stub
        if(id == TRANSACTIONS){
            //Toast.makeText(getActivity(), "reset", Toast.LENGTH_LONG).show();
            mAdapter.swapCursor(null);
        }
        else if(id == ACCOUNTS){
            mAccountsAdapter.swapCursor(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);

        editTransaction(cursor);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // TODO Auto-generated method stub
        switch(parent.getId()) {
            case R.id.sp_account:
                mAccountId = id;

                TextView tv_account = (TextView) view.findViewById(android.R.id.text1);
                mAccountName = tv_account.getText().toString();

                mAdapter.swapCursor(null);
                lv.setAdapter(mAdapter);
                /*
                Toast.makeText(getActivity(), "name " +
                        tv_account.getText().toString(), Toast.LENGTH_LONG).show();
                */
                //Toast.makeText(getActivity(), "mAccountId:"+ String.valueOf(mAccountId), Toast.LENGTH_LONG).show();
                mPreloader.setVisibility(View.VISIBLE);
                mManager.restartLoader(TRANSACTIONS, null, this);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void editTransaction(Cursor c){

        Transaction transaction = new Transaction();
        int isDeposit = c.getInt(c.getColumnIndex(TransactionView.COLUMN_IS_DEPOSIT));

        transaction.id = c.getLong(c.getColumnIndex(TransactionView.COLUMN_ID));
        //if(isDeposit == 0){
            transaction.primaryAccountId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_PRIMARY_ACCOUNT_ID));
            transaction.primaryAccountName = mAccountName;
            transaction.primaryAccountDescription = c.getString(c.getColumnIndex(TransactionView.COLUMN_PRIMARY_ACCOUNT_DESCRIPTION));
            transaction.secondaryAccountId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_ID));
            transaction.secondaryAccountName = c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_NAME));
            transaction.secondaryAccountDescription = c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION));
        //}
        /*
        else{
            transaction.primaryAccountId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_ID));
            transaction.primaryAccountName = c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_NAME));
            transaction.primaryAccountDescription = c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION));
            transaction.secondaryAccountId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_PRIMARY_ACCOUNT_ID));
            transaction.secondaryAccountName = mAccountName;
            transaction.secondaryAccountDescription = c.getString(c.getColumnIndex(TransactionView.COLUMN_PRIMARY_ACCOUNT_DESCRIPTION));
        }
        */
        transaction.amount = c.getDouble(c.getColumnIndex(TransactionView.COLUMN_AMOUNT));
        transaction.isDeposit = c.getInt(c.getColumnIndex(TransactionView.COLUMN_IS_DEPOSIT));
        transaction.date = c.getLong(c.getColumnIndex(TransactionView.COLUMN_TRANSACTION_DATE));
        transaction.notes=c.getString(c.getColumnIndex(TransactionView.COLUMN_NOTES));
        transaction.expenseCategoryId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_EXPENSE_CATEGORY_ID));
        transaction.expenseCategoryParentName = c.getString(c.getColumnIndex(TransactionView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME));
        transaction.expenseCategoryChildName = c.getString(c.getColumnIndex(TransactionView.COLUMN_EXPENSE_CATEGORY_CHILD_NAME));
        transaction.transactionCategoryId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_TRANSACTION_CATEGORY_ID));
        transaction.transactionCategoryName = c.getString(c.getColumnIndex(TransactionView.COLUMN_TRANSACTION_CATEGORY_NAME));
        transaction.imagePath = c.getString(c.getColumnIndex(TransactionView.COLUMN_IMAGE_PATH));

        Bundle b = new Bundle();
        b.putSerializable("mtransaction", transaction);


        Intent i = new Intent(getActivity(),EditTransaction.class);
        i.putExtras(b);
        startActivity(i);

    }

    public void deleteTransaction(Cursor c) {
        long transactionId = c.getLong(c.getColumnIndex(TransactionView.COLUMN_ID));
        long acountId1 =  c.getLong(c.getColumnIndex(TransactionView.COLUMN_PRIMARY_ACCOUNT_ID));
        long acountId2 =  c.getLong(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_ID));

        boolean success = TransactionTable.delete(transactionId, acountId1, acountId2) != -1;

        if(!success){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }


    public void exportCSV(){

        if(!Storage.permission(getActivity())){
            Toast.makeText(getActivity(), "You don't have permission to access storage. You can enable it in Application settings->permissions", Toast.LENGTH_LONG).show();
            return;
        }

        final String IMAGE_DIRECTORY_NAME = "Expense Organizer";

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase db = dbhelper.getWritableDatabase();
                        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                        DecimalFormat f = new DecimalFormat("0.00");
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        String header = "Payee, Amount, Expense Category, Transaction Method, Date";
                        String data, payee, expense_category, transaction_method, transaction_amount_str, transaction_date_str, notes;
                        int is_deposit;
                        double amount;
                        long secondary_account_id,transaction_date;
                        int count=0;

                        String selection;

                        if(mExpenseCatId != 0L){
                            selection = " AND primary_account_id = " + mAccountId + " AND expense_category_id = " + mExpenseCatId +
                                        " AND transaction_date BETWEEN " + mStartDate + " AND " + mEndDate;
                        }
                        else{
                            selection = " AND primary_account_id = " + mAccountId;

                        }

                        Cursor c = db.rawQuery(
                                "SELECT secondary_account_id, " +
                                        " secondary_account_name, " +
                                        " secondary_account_description, " +
                                        " expense_category_parent_name, " +
                                        " expense_category_child_name, " +
                                        " transaction_category_name, " +
                                        " amount, " +
                                        " is_deposit, " +
                                        " notes, " +
                                        " transaction_date " +
                                        " FROM view_transactions" +
                                        " WHERE deleted != 1" + selection +
                                        " ORDER BY transaction_date DESC", null);
                                        //" AND primary_account_id = " + mAccountId,null);

                        count = c.getCount();
                        File mediaFile = getOutputMediaFile();
                        //Log.i("export", "finish: " +String.valueOf(path.getPath()));
                        if(count > 0 && mediaFile != null) {
                            try {
                                BufferedWriter out = new BufferedWriter(new FileWriter(mediaFile));
                                CSVWriter writer = new CSVWriter(out);

                                String[] values;

                                //header
                                //values = new String[]{"Payee","Amount", "Expense Category", "Transaction Method", "Date", "Notes"};
                                values = new String[]{"Date", "Description","Amount", "Expense Category", "Transaction Method", "Notes"};
                                writer.writeNext(values);

                                c.moveToFirst();
                                while (c.getPosition() < count) {
                                    secondary_account_id = c.getLong(c.getColumnIndex("secondary_account_id"));
                                    if (secondary_account_id == 1L) {
                                        payee = c.getString(c.getColumnIndex("secondary_account_description"));
                                    } else {
                                        payee = c.getString(c.getColumnIndex("secondary_account_name"));
                                    }
                                    expense_category = c.getString(c.getColumnIndex("expense_category_parent_name")) + " : " + c.getString(c.getColumnIndex("expense_category_child_name"));
                                    transaction_method = c.getString(c.getColumnIndex("transaction_category_name"));
                                    notes = c.getString(c.getColumnIndex("notes"));
                                    is_deposit = c.getInt(c.getColumnIndex("is_deposit"));
                                    amount = Math.abs(c.getDouble(c.getColumnIndex("amount")) / 100);
                                    if (is_deposit == 1 || amount == 0) {
                                        transaction_amount_str = f.format(Math.abs(amount));
                                    } else {
                                        transaction_amount_str = "-" + f.format(Math.abs(amount));
                                    }

                                    transaction_date = c.getLong(c.getColumnIndex("transaction_date"));
                                    cal.setTimeInMillis(transaction_date);
                                    transaction_date_str = sdf.format(cal.getTime());

                                    //data = payee + "," + transaction_amount_str + "," + expense_category + "," + transaction_method + "," + transaction_date_str;
                                    //data
                                    values = new String[]{transaction_date_str,payee,transaction_amount_str,expense_category,transaction_method,notes};
                                    writer.writeNext(values);

                                    // Log.i("cursor", "going: " + c.getString(c.getColumnIndex("secondary_account_description")));
                                    //Log.i("cursor", data);
                                    c.moveToNext();
                                }

                                // out.close();
                                writer.close();
                                out.close();

                                //Toast.makeText(context,"csv location: " + mediaFile.getPath(), Toast.LENGTH_LONG).show();
                                //Toast.makeText(mContext,"csv location: ", Toast.LENGTH_LONG).show();
                                Bundle b = new Bundle();
                                b.putInt("id", 1);
                                b.putString("path", mediaFile.getPath());
                                Message m = new Message();
                                m.setData(b);
                                mHandler.sendMessage(m);

                            }
                            catch(Exception e){
                                //e.printStackTrace();
                                //Log.i("cursor erro", e.getMessage());
                                //Toast.makeText(mContext,"error", Toast.LENGTH_LONG).show();
                                Bundle b = new Bundle();
                                b.putInt("id", 2);
                                Message m = new Message();
                                m.setData(b);
                                mHandler.sendMessage(m);
                            }
                        }
                        else{

                            if (mediaFile != null){
                                Bundle b = new Bundle();
                                b.putInt("id", 3);
                                Message m = new Message();
                                m.setData(b);
                                mHandler.sendMessage(m);
                            }
                            //Toast.makeText(mContext,"sorry, can't export csv.", Toast.LENGTH_LONG).show();
                        }

                        Log.i("cursor", "finish");
                        db.close();
                        c.close();
                    }

                    public File getOutputMediaFile(){

                        // External sdcard location
                        File mediaStorageDir = new File(
                                Environment
                                        .getExternalStorageDirectory(),
                                IMAGE_DIRECTORY_NAME);

                        // Create the storage directory if it does not exist
                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                                        + IMAGE_DIRECTORY_NAME + " directory");
                                return null;
                            }
                        }

                        // Create a media file name
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());


                        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                                + "transactions_" + timeStamp + ".csv");


                        return mediaFile;
                    }


                }
        // Starts the thread by calling the run() method in its Runnable
        ).start();
    }
}
