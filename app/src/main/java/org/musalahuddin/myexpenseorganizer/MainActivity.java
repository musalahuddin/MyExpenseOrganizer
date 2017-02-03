package org.musalahuddin.myexpenseorganizer;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.musalahuddin.myexpenseorganizer.activity.Preference;
import org.musalahuddin.myexpenseorganizer.database.MyExpenseOrganizerDatabaseHelper;
import org.musalahuddin.myexpenseorganizer.dialog.ConfirmationDialog;
import org.musalahuddin.myexpenseorganizer.fragment.AccountsFragment;
import org.musalahuddin.myexpenseorganizer.fragment.BudgetsFragment;
import org.musalahuddin.myexpenseorganizer.fragment.TransactionsFragment;
import org.musalahuddin.myexpenseorganizer.permission.Storage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConfirmationDialog.ConfirmationDialogListener {


    private SQLiteDatabase db;

    static final int ACCOUNT= 1;
    static final int TRANSACTION=2;
    static final int BUDGET=3;

    private Integer mCurrentFragment = ACCOUNT;

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Set the fragment initially
        //set title
        setTitle("Accounts");
        //set fragment
        fragment = new AccountsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //storage permission
        Storage.verifyStoragePermissions(this);

        //load default data
        loadDefaultData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                Bundle b;
                switch (mCurrentFragment) {
                    case ACCOUNT:
                        /*
                        Snackbar.make(view, "Replace with Account", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        */
                        i = new Intent("myexpenseorganizer.intent.add.accounts");
                        startActivity(i);
                        break;
                    case BUDGET:
                        /*
                        Snackbar.make(view, "Replace with Budget", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        */
                        b = new Bundle();
                        b.putLong("accountId", ((BudgetsFragment)fragment).mAccountId);
                        b.putString("accountName", ((BudgetsFragment)fragment).mAccountName);
                        i = new Intent("myexpenseorganizer.intent.add.budgets");
                        i.putExtras(b);
                        startActivity(i);
                        break;

                    case TRANSACTION:


                        /*
                        Snackbar.make(view, "Replace with Transaction", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        */

                        //Toast.makeText(this, "mAccountId:"+ String.valueOf(mCurrentFragment), Toast.LENGTH_LONG).show();
                       // Toast.makeText(this, "mAccountId:"+ ((TransactionsFragment)fragment).getmAccountName(), Toast.LENGTH_LONG).show();
                       // Toast.makeText(MainActivity.this, "mAccountId:"+ ((TransactionsFragment)fragment).getmAccountName(), Toast.LENGTH_LONG).show();

                        b = new Bundle();
                        b.putLong("primaryAccountId", ((TransactionsFragment)fragment).mAccountId);
                        b.putString("primaryAccountName", ((TransactionsFragment)fragment).mAccountName);
                        i = new Intent("myexpenseorganizer.intent.add.transactions");
                        i.putExtras(b);
                        startActivity(i);
                        break;
                }
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mCurrentFragment == TRANSACTION) {
            getMenuInflater().inflate(R.menu.transactions, menu);
        }
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
        }

        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i;
        if (id == R.id.nav_settings) {
            i = new Intent(this,Preference.class);
            startActivity(i);
        }
        else if(id == R.id.nav_accounts){
            mCurrentFragment = ACCOUNT;
            //set title
            setTitle("Accounts");
            //set fragment
             fragment = new AccountsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
        else if(id == R.id.nav_budgets){
            mCurrentFragment = BUDGET;
            //set title
            setTitle("Budgets");
            //set fragment
             fragment = new BudgetsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }

        else if(id == R.id.nav_transactions){
            mCurrentFragment = TRANSACTION;

            //set title
            setTitle("Transactions");
            //set fragment
             fragment = new TransactionsFragment();

            Bundle data;
            data = new Bundle();//Use bundle to pass data
            data.putBoolean("showAccounts", true);
            fragment.setArguments(data);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void loadDefaultData(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    MyExpenseOrganizerDatabaseHelper dbhelper = new MyExpenseOrganizerDatabaseHelper(MyApplication.getInstance());
                    db = dbhelper.getReadableDatabase();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void onNegative() {

    }

    @Override
    public void onPositive(int position) {
        switch (mCurrentFragment) {
            case ACCOUNT:
                ((AccountsFragment)fragment).deleteAccount(position);
                break;
            case TRANSACTION:
                ((TransactionsFragment)fragment).deleteTransaction(position);
                break;
            case BUDGET:
                ((BudgetsFragment)fragment).deleteBudget(position);
                break;
        }
        //Toast.makeText(this, String.valueOf(position), Toast.LENGTH_LONG).show();
    }
}
