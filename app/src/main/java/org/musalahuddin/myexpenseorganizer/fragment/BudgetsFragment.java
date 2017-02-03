package org.musalahuddin.myexpenseorganizer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.activity.EditBudget;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.BudgetTable;
import org.musalahuddin.myexpenseorganizer.database.BudgetView;
import org.musalahuddin.myexpenseorganizer.database.TransactionView;
import org.musalahuddin.myexpenseorganizer.dialog.ConfirmationDialog;
import org.musalahuddin.myexpenseorganizer.serializable.Budget;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Muhammad on 12/11/2016.
 */

public class BudgetsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    //private LoaderManager mManager;
    private LoaderManager  mLoaderManager;
    private SimpleCursorAdapter mAccountsAdapter, mBgtIndividualAdapter;

    public long mAccountId = 0L;
    public String mAccountName = "";

    private int mStartDay = 0;

    private String prevCat = "";

    private static final int ACCOUNTS = 1;
    private static final int BUDGETS_OVERALL = 2;
    private static final int BUDGETS_INDIVIDUAL = 3;

    private Calendar currCal = Calendar.getInstance();
    private Calendar nextCal = Calendar.getInstance();

    private TextView mTvBudgetPeriod;
    private ProgressBar mPbBudgetProgress;
    private TextView mTvBudgetInfo;

    public RelativeLayout mHeader;

    private static final int EDIT= 1;
    private static final int DELETE = 2;

    public ListView lv;

    public BudgetsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budgets, container, false);

        Spinner spAccount = (Spinner) rootView.findViewById(R.id.sp_account);
        spAccount.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        mHeader = (RelativeLayout) rootView.findViewById(R.id.header);

        mTvBudgetPeriod = (TextView) rootView.findViewById(R.id.tv_budget_period);
        mPbBudgetProgress = (ProgressBar) rootView.findViewById(R.id.pb_budget_progress);
        mTvBudgetInfo= (TextView) rootView.findViewById(R.id.tv_budget_info);

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

        mLoaderManager = getLoaderManager();

        mLoaderManager.initLoader(ACCOUNTS, null, this);

        lv = (ListView) rootView.findViewById(R.id.list);

        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{BudgetView.COLUMN_SPENT};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{R.id.tv_budget_info};

        // Now create a simple cursor adapter for individual budget list
        mBgtIndividualAdapter = new SimpleCursorAdapter(getActivity(),R.layout.budget_row,null,from,to,0){
            @SuppressWarnings("deprecation")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(position == 0){
                    prevCat = "";
                }

                View row = super.getView(position, convertView, parent);
                Cursor c = getCursor();
                c.moveToPosition(position);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                DecimalFormat f = new DecimalFormat("0.00");
                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

                //double spent = Math.abs(c.getDouble(c.getColumnIndex(BudgetView.COLUMN_SPENT))/100);
                double spent = (c.getDouble(c.getColumnIndex(BudgetView.COLUMN_SPENT))/100);
                double total = Math.abs(c.getDouble(c.getColumnIndex(BudgetView.COLUMN_BUDGET))/100);
                String parentCategory = c.getString(c.getColumnIndex(BudgetView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME));
                String childCategory = c.getString(c.getColumnIndex(BudgetView.COLUMN_EXPENSE_CATEGORY_CHILD_NAME));

                // reset spent to 0 if it's greater than 0
                if(spent > 0) spent = 0;

                spent = Math.abs(spent);

                double progress = (spent/total)*100;

                Log.i("onLoadFinished", "sql--spent: "+String.valueOf(spent));
                Log.i("onLoadFinished", "sql--total: "+String.valueOf(total));
                Log.i("onLoadFinished", "sql--progress: "+String.valueOf((int)progress));



                //get views
                TextView tvParentCategory = (TextView) row.findViewById(R.id.tv_parent_category);
                TextView tvChildCategory = (TextView) row.findViewById(R.id.tv_child_category);
                TextView tvbudgetInfo = (TextView) row.findViewById(R.id.tv_budget_info);
                ProgressBar pbBudgetProgress = (ProgressBar) row.findViewById(R.id.pb_budget_progress);

                // reset
                //tvParentCategory.setVisibility(View.VISIBLE);

                Log.i("onLoadFinished", "sql--parentCat: "+ parentCategory);
                Log.i("onLoadFinished", "sql--prevCat: "+ prevCat);

                /*
                if(parentCategory.equals(prevCat)){
                    Log.i("onLoadFinished", "sql--equal");
                    tvParentCategory.setVisibility(View.GONE);
                }
                */

                if(position ==0){
                    tvParentCategory.setVisibility(View.VISIBLE);
                }
                else{
                    c.moveToPosition(position-1);
                    String prev_parent_cat = c.getString(c.getColumnIndex(BudgetView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME));
                    if(parentCategory.equals(prev_parent_cat)) {
                        tvParentCategory.setVisibility(View.GONE);
                    }
                    else{
                        tvParentCategory.setVisibility(View.VISIBLE);
                    }
                }

                //setting values
                if(spent <= total){
                    pbBudgetProgress.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress));
                }
                else{
                    pbBudgetProgress.setProgressDrawable(getResources().getDrawable(R.drawable.red_progress));
                }
                pbBudgetProgress.setProgress((int)progress);
                tvParentCategory.setText(parentCategory);
                tvChildCategory.setText(childCategory);
                //tvbudgetInfo.setText("$"+f.format(Math.abs(spent))+" of "+ "$"+f.format(Math.abs(total)));
                tvbudgetInfo.setText(n.format(Math.abs(spent))+" of "+ n.format(Math.abs(total)));

                prevCat = parentCategory;

                return row;
            }
        };

        lv.setAdapter(mBgtIndividualAdapter);
        lv.setEmptyView(rootView.findViewById(R.id.empty));

        lv.setOnItemClickListener(this);

        registerForContextMenu(lv);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void initBudgets(){

       // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat d_sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM. d, yyyy");

        Calendar currCalendar = Calendar.getInstance();
        currCalendar.setTimeInMillis(System.currentTimeMillis());

        // get last day for a given month
        int currMaxDay = currCalendar.getActualMaximum(currCalendar.DAY_OF_MONTH);

        //currCal = Calendar.getInstance();
        //nextCal = Calendar.getInstance();

        if(mStartDay > currMaxDay){
            mStartDay = currMaxDay;
        }

        // compute current and next dates
        currCal.set(currCalendar.get(currCalendar.YEAR), currCalendar.get(currCalendar.MONTH), mStartDay, 0, 0, 0);
        nextCal.setTimeInMillis(currCal.getTimeInMillis());
        nextCal.add(Calendar.MONTH, 1);
        nextCal.add(Calendar.SECOND, -1);


        Log.w("date","date--currFormat: " + d_sdf.format(currCal.getTime()));
        Log.w("date","date--nextFormat: " + d_sdf.format(nextCal.getTime()));

        mTvBudgetPeriod.setText(sdf.format(currCal.getTime())+" - "+sdf.format(nextCal.getTime()));

        //Log.w("date","date--currFormat: " + currCal.toString());
        //Log.w("date","date--nextFormat: " + nextCal.toString());

        mLoaderManager.restartLoader(BUDGETS_OVERALL, null, this);
        mLoaderManager.restartLoader(BUDGETS_INDIVIDUAL, null, this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        switch(parent.getId()) {
            case R.id.sp_account:

                Cursor cursor = mAccountsAdapter.getCursor();
                cursor.moveToPosition(position);

                mBgtIndividualAdapter.swapCursor(null);
                lv.setAdapter(mBgtIndividualAdapter);

                mAccountId = id;
               // Toast.makeText(getActivity(), "selected", Toast.LENGTH_LONG).show();

                TextView tv_account = (TextView) view.findViewById(android.R.id.text1);
                mAccountName = tv_account.getText().toString();

                mStartDay = cursor.getInt(cursor.getColumnIndex(AccountTable.COLUMN_BUDGET_START_DAY));

                //mLoaderManager.restartLoader(BUDGETS_OVERALL, null, this);
                initBudgets();

                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,DELETE,0,"Delete Budget");//setOnMenuItemClickListener(this);
        menu.add(0,EDIT,0,"View/Edit Budget");//setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Cursor cursor;

        switch(item.getItemId()) {

            case EDIT:

                //Toast.makeText(getActivity(), "edit: " + position, Toast.LENGTH_LONG).show();
                cursor = mBgtIndividualAdapter.getCursor();
                cursor.moveToPosition(position);

                editBudget(cursor);

                return true;

            case DELETE:

                //cursor = mBgtIndividualAdapter.getCursor();
                //cursor.moveToPosition(position);

                //deleteBudget(cursor);
                confirmDelete(position);

               // Toast.makeText(getActivity(), "delete: " + position, Toast.LENGTH_LONG).show();

                return true;
        }

        return false;
    }

    public void confirmDelete(int position){
        Bundle args = new Bundle();
        args.putString(ConfirmationDialog.KEY_TITLE, "Delete budget?");
        args.putString(ConfirmationDialog.KEY_MESSAGE, "The selected budget will be deleted.");
        args.putString(ConfirmationDialog.KEY_NEGATIVE_BUTTON_LABEL, "CANCEL");
        args.putString(ConfirmationDialog.KEY_POSITIVE_BUTTON_LABEL, "DELETE");
        args.putInt(ConfirmationDialog.KEY_POSITION, position);

        ConfirmationDialog.newInstance(args)
                .show(getFragmentManager(), "DELETE_TRANSACTION");

    }

    public void deleteBudget(int position) {

        Cursor c = mBgtIndividualAdapter.getCursor();
        c.moveToPosition(position);

        long budgetId = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));

        boolean success = BudgetTable.delete(budgetId) != -1;

        if(!success){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Cursor c = mBgtIndividualAdapter.getCursor();
        c.moveToPosition(position);

        //String name = c.getString(c.getColumnIndex("expense_category_parent_name")) + " : " + c.getString(c.getColumnIndex("expense_category_child_name")) ;
        //Long account_id = c.getLong(c.getColumnIndex("account_id"));
        Long expense_category_id = c.getLong(c.getColumnIndex("expense_category_id"));
        long startDate = currCal.getTimeInMillis();
        long endDate = nextCal.getTimeInMillis();

        Bundle b = new Bundle();
        b.putLong("accountId", mAccountId);
        b.putString("accountName", mAccountName);

        b.putLong("expenseCatId", expense_category_id);
        b.putLong("startDate", startDate);
        b.putLong("endDate", endDate);

        Intent i = new Intent("myexpenseorganizer.intent.select.transactions");
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {

        Log.i("onCreateLoader ", "debug--onCreateLoader "+ String.valueOf(id));

        String selection;
        String[] selectionArgs=null, projection=null;
        String sortOrder=null;
        CursorLoader cursorLoader = null;
        long startDate;
        long endDate;

        switch(id){

            case ACCOUNTS:

                selection = null;
                selectionArgs=null;
                projection = new String[]{
                        AccountTable.COLUMN_ID,
                        AccountTable.COLUMN_NAME,
                        AccountTable.COLUMN_BUDGET_START_DAY
                };

                sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";

                cursorLoader = new CursorLoader(getActivity(),
                        AccountTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

                break;

            case BUDGETS_OVERALL:
                startDate = currCal.getTimeInMillis();
                endDate = nextCal.getTimeInMillis();
                selection = BudgetView.COLUMN_ACCOUNT_ID+"="+String.valueOf(mAccountId)+" AND ("+BudgetView.COLUMN_TRANSACTION_DATE+"=0 OR "+BudgetView.COLUMN_TRANSACTION_DATE+" BETWEEN "+String.valueOf(startDate)+" AND "+String.valueOf(endDate)+") ";
                selectionArgs = new String[]{String.valueOf(mAccountId), String.valueOf(startDate), String.valueOf(endDate)};
                cursorLoader = new CursorLoader(getActivity(),
                        BudgetView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
                break;

            case BUDGETS_INDIVIDUAL:
                startDate = currCal.getTimeInMillis();
                endDate = nextCal.getTimeInMillis();
                selection = BudgetView.COLUMN_ACCOUNT_ID+"="+String.valueOf(mAccountId)+" AND ("+BudgetView.COLUMN_TRANSACTION_DATE+"=0 OR "+BudgetView.COLUMN_TRANSACTION_DATE+" BETWEEN "+String.valueOf(startDate)+" AND "+String.valueOf(endDate)+") ";
                selectionArgs = new String[]{String.valueOf(mAccountId), String.valueOf(startDate), String.valueOf(endDate)};
                cursorLoader = new CursorLoader(getActivity(),
                        BudgetView.CONTENT_URI.buildUpon().appendPath("1").build(), projection, selection, selectionArgs, sortOrder);
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        int id = loader.getId();

        if(id == ACCOUNTS){
            mAccountsAdapter.swapCursor(c);

            if(c.getCount() > 0){
                mHeader.setVisibility(View.VISIBLE);
            }
        }
        else if(id == BUDGETS_OVERALL){

            updateBudgetInfo(c);
        }
        else if(id == BUDGETS_INDIVIDUAL){

            mBgtIndividualAdapter.swapCursor(c);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        int id = loader.getId();

        if(id == ACCOUNTS){
            mAccountsAdapter.swapCursor(null);
        }
        else if(id == BUDGETS_OVERALL){

           // updateBudgetInfo(null);
        }
        else if(id == BUDGETS_OVERALL){

            mBgtIndividualAdapter.swapCursor(null);
        }
    }

    public void editBudget(Cursor c){
        Budget budget = new Budget();
        budget.id = c.getLong(c.getColumnIndex(BudgetView.COLUMN_ID));
        budget.accountId = mAccountId;
        budget.accountName = mAccountName;
        budget.amount = c.getDouble(c.getColumnIndex(BudgetView.COLUMN_BUDGET));
        budget.notes = c.getString(c.getColumnIndex(BudgetView.COLUMN_NOTES));
        budget.expenseCategoryId = c.getLong(c.getColumnIndex(BudgetView.COLUMN_EXPENSE_CATEGORY_ID));
        budget.expenseCategoryParentName = c.getString(c.getColumnIndex(BudgetView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME));
        budget.expenseCategoryChildName = c.getString(c.getColumnIndex(BudgetView.COLUMN_EXPENSE_CATEGORY_CHILD_NAME));

        Bundle b = new Bundle();
        b.putSerializable("budget", budget);


        Intent i = new Intent(getActivity(),EditBudget.class);
        i.putExtras(b);
        startActivity(i);
    }

    public void deleteBudget(Cursor c) {

        long budgetId = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));

        boolean success = BudgetTable.delete(budgetId) != -1;

        if(!success){
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
        }
    }


    public void updateBudgetInfo(Cursor c){
       // Toast.makeText(getActivity(), "I am here", Toast.LENGTH_LONG).show();


        c.moveToPosition(0);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        DecimalFormat f = new DecimalFormat("0.00");
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

        double spent = Math.abs(c.getDouble(c.getColumnIndex(BudgetView.COLUMN_SPENT))/100);
        double total = Math.abs(c.getDouble(c.getColumnIndex(BudgetView.COLUMN_BUDGET))/100);

        double progress = (spent/total)*100;

        //setting progress
        //setting values
        if(spent <= total){
            mPbBudgetProgress.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress));
        }
        else{
            mPbBudgetProgress.setProgressDrawable(getResources().getDrawable(R.drawable.red_progress));
        }
        mPbBudgetProgress.setProgress((int)progress);

        //mTvBudgetInfo.setText("$"+f.format(Math.abs(spent))+" of "+ "$"+f.format(Math.abs(total)));
        mTvBudgetInfo.setText(n.format(Math.abs(spent))+" of "+ n.format(Math.abs(total)));

       // Toast.makeText(getActivity(), String.valueOf(progress), Toast.LENGTH_LONG).show();

    }
}
