package org.musalahuddin.myexpenseorganizer.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.activity.Preference;
import org.musalahuddin.myexpenseorganizer.database.MyExpenseOrganizerDatabaseHelper;
import org.musalahuddin.myexpenseorganizer.dialog.ConfirmationDialog;
import org.musalahuddin.myexpenseorganizer.fragment.AccountsFragment;
import org.musalahuddin.myexpenseorganizer.fragment.BudgetsFragment;
import org.musalahuddin.myexpenseorganizer.fragment.TransactionsFragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SelectTransaction extends AppCompatActivity implements ConfirmationDialog.ConfirmationDialogListener{

    private long mAccountId = 0L;

    private String mAccountName = "";

    private long mExpenseCatId = 0L;

    private long mStartDate = 0L;

    private long mEndDate = 0L;

    public MyExpenseOrganizerDatabaseHelper dbhelper;

    public Context mContext;

    public Handler mHandler;

    public Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_transaction);

        dbhelper = new MyExpenseOrganizerDatabaseHelper(MyApplication.getInstance());
        mContext = this;

        Bundle extras = getIntent().getExtras();
        mAccountId = extras.getLong("accountId");
        mAccountName = extras.getString("accountName");
        mExpenseCatId = extras.getLong("expenseCatId",0L);
        mStartDate = extras.getLong("startDate",0L);
        mEndDate = extras.getLong("endDate",0L);
        //String action = getIntent().getAction();
        //mShowAccounts = action==null;

        //Set the fragment initially
        //set title
        setTitle("Transactions");
        //set fragment
        fragment = new TransactionsFragment();
        Bundle data;
        data = new Bundle();//Use bundle to pass data
        data.putBoolean("showAccounts", false);
        data.putLong("accountId", mAccountId );
        data.putString("accountName",mAccountName);
        data.putLong("expenseCatId", mExpenseCatId );
        data.putLong("startDate", mStartDate );
        data.putLong("endDate", mEndDate );


        fragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();



        //actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b;
                b = new Bundle();
                b.putLong("primaryAccountId", mAccountId);
                b.putString("primaryAccountName", mAccountName);
                Intent i = new Intent("myexpenseorganizer.intent.add.transactions");
                i.putExtras(b);
                startActivity(i);
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
            }
        });




        /*
        Bundle extras = getIntent().getExtras();
        mAccountId = extras.getLong("accountId");
        mAccountName = extras.getString("accountName");


        Toast.makeText(this,mAccountName, Toast.LENGTH_LONG).show();
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if(mExpenseCatId == 0L){
        getMenuInflater().inflate(R.menu.transactions, menu);
        //}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        //return super.onOptionsItemSelected(item);
        Intent i;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_export_csv:
                //Toast.makeText(this,"export csv", Toast.LENGTH_LONG).show();
                ((TransactionsFragment)fragment).exportCSV();
                break;
            case android.R.id.home:
                finish();
                return true;
        }

        return false;
    }

    @Override
    public void onNegative() {

    }

    @Override
    public void onPositive(int position) {
        //Toast.makeText(this, String.valueOf(position), Toast.LENGTH_LONG).show();
        ((TransactionsFragment)fragment).deleteTransaction(position);
    }
}
