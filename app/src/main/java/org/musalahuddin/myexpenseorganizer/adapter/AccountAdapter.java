package org.musalahuddin.myexpenseorganizer.adapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountAdapter extends CursorRecyclerAdapter<AccountAdapter.AccountViewHolder> {

    public static OnItemClickListener mItemClickListener;
    public static OnItemLongClickListener mItemLongClickListener;
    public static OnItemCreateContextMenuListener mItemCreateContextMenuListener;

    private static final int EDIT= 1;
    private static final int DELETE = 2;


    private final java.text.DateFormat mTitleDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM);

    public AccountAdapter(Cursor cursor) {
        super(cursor);
        // TODO Auto-generated constructor stub
    }


    public static interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public static interface OnItemLongClickListener {
        public void onItemLongClick(View view , int position);
    }

    public static interface OnItemCreateContextMenuListener {
        public void OnItemCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo, Integer position);
    }

    public static interface OnContextMenuItemClickListener {
        public void OnContextMenuItemClick(MenuItem menuItem);
    }


    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        AccountAdapter.mItemClickListener = mItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mItemLongClickListener) {
        AccountAdapter.mItemLongClickListener = mItemLongClickListener;
    }

    public void setOnItemCreateContextMenuListener(OnItemCreateContextMenuListener mItemCreateContextMenuListener){
        AccountAdapter.mItemCreateContextMenuListener = mItemCreateContextMenuListener;
    }



    public static class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{



        protected TextView tvAccountName;
        protected TextView tvAccountBalance;
        protected TextView tvAccountInitBalance;
        protected TextView tvAccountPayment;
        protected TextView tvAccountLimit;
        protected TextView tvAccountDue;
        protected RelativeLayout rowAccountDue;


        public AccountViewHolder(View v) {
            super(v);
            tvAccountName =  (TextView) v.findViewById(R.id.account_name);
            tvAccountBalance = (TextView)  v.findViewById(R.id.account_balance);
            tvAccountDue = (TextView)  v.findViewById(R.id.account_due);
            tvAccountInitBalance = (TextView) v.findViewById(R.id.account_init_balance);
            rowAccountDue = (RelativeLayout) v.findViewById(R.id.row_account_due);

            v.setOnClickListener(this);
           // v.setOnLongClickListener(this);
            v.setOnCreateContextMenuListener(this);
           // v.setOnMenuItemClickListener(this);

        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }

            //Log.i("i clicked","hurray");

            //Toast.makeText(v.getContext(), "im clicked "+getAdapterPosition(), Toast.LENGTH_LONG).show();

        }

        /*
        @Override
        public boolean onLongClick(View v) {
            // TODO Auto-generated method stub
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return false;
        }
        */

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            if (mItemCreateContextMenuListener != null) {
                mItemCreateContextMenuListener.OnItemCreateContextMenu(menu, view, contextMenuInfo, getAdapterPosition());
            }
        }

    }

    @Override
    public void onBindViewHolderCursor(AccountViewHolder holder, Cursor c) {
        // TODO Auto-generated method stub

        DecimalFormat f = new DecimalFormat("0.00");
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

        //get cursor values
        String name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
        int number = c.getInt(c.getColumnIndex(AccountTable.COLUMN_NUMBER));
        double balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_CURR_BALANCE))/100;
        double init_balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_INIT_BALANCE))/100;
        long due_date = c.getLong(c.getColumnIndex(AccountTable.COLUMN_DUE_DATE));

        //set name

        if(number > 0){
            name = name+" (...."+number+")";
        }

        holder.tvAccountName.setText(name);

        //set current balance
        if(init_balance < 0){
            //holder.tvAccountBalance.setText("-$"+f.format(Math.abs(balance)));
            holder.tvAccountInitBalance.setText("-"+n.format(Math.abs(init_balance)));
            //holder.tvAccountBalance.setTextColor(Color.RED);
            //holder.tvAccountInitBalance.setTextColor(Color.parseColor("#d30202"));
        }
        else{
            //holder.tvAccountBalance.setText("$"+f.format(Math.abs(balance)));
            holder.tvAccountInitBalance.setText(n.format(Math.abs(init_balance)));
            //holder.tvAccountInitBalance.setTextColor(Color.DKGRAY);
        }

        //set balance
        if(balance < 0){
            //holder.tvAccountBalance.setText("-$"+f.format(Math.abs(balance)));
            holder.tvAccountBalance.setText("-"+n.format(Math.abs(balance)));
            //holder.tvAccountBalance.setTextColor(Color.RED);
            holder.tvAccountBalance.setTextColor(Color.parseColor("#d30202"));
        }
        else{
            //holder.tvAccountBalance.setText("$"+f.format(Math.abs(balance)));
            holder.tvAccountBalance.setText(n.format(Math.abs(balance)));
            holder.tvAccountBalance.setTextColor(Color.DKGRAY);
        }
        /*
        else if(balance > 0){
            holder.tvAccountBalance.setTextColor(Color.GREEN);
        }
        */
        //set due date
        /*
        if(due_date != 0L){
            if(due_date < System.currentTimeMillis()){
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(due_date);
                cal.add(Calendar.MONTH, 1);
                due_date = cal.getTimeInMillis();
            }
            holder.tvAccountDue.setText(mTitleDateFormat.format(due_date));
        }
        else{
            holder.tvAccountDue.setText("");
        }
        */

        //set due date
        if(due_date != 0L){
            holder.rowAccountDue.setVisibility(View.VISIBLE);
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
                /*
                cal.setTimeInMillis(due_date);
                cal.add(Calendar.MONTH, 1);
                due_date = cal.getTimeInMillis();
                */
            }

            holder.tvAccountDue.setText("Due: " + mTitleDateFormat.format(due_date));
        }
        else{
            holder.rowAccountDue.setVisibility(View.GONE);
            holder.tvAccountDue.setText("");
        }

    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.account_row, viewGroup, false);

        return new AccountViewHolder(itemView);
    }
}
