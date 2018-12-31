package org.musalahuddin.myexpenseorganizer.activity;


import java.text.DecimalFormat;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.BudgetTable;
import org.musalahuddin.myexpenseorganizer.serializable.Budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class EditBudget extends AppCompatActivity implements OnClickListener{

    boolean mAddOnly;

    private long mAccountId = 0L;
    private String mAccountName = "";
    private long mBudgetId = 0L;

    private double mBudgetAmount = 0;
    private String mBudgetNotes = "";
    private Long mExpenseCatId = 0L;
    private String mExpenseCatParentName = "";
    private String mExpenseCatChildName = "";

    private static final int SELECT_EXPENSE_CATEGORY_REQUEST = 1;
    private static final int SELECT_ACCOUNT_REQUEST = 2;

    private EditText mBtnBudgetAccount;
    private EditText mEtBudgetAmount;
    private EditText mEtBudgetNotes;
    private EditText mBtnBudgetCategory;
    private ScrollView mContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_budget);

        Intent intent = getIntent();
        String action = intent.getAction();
        mAddOnly = action != null && action.equals("myexpenseorganizer.intent.add.budgets");

        //actionbar
        //getSupportActionBar().setTitle(mAddOnly ? R.string.budget_new_title : R.string.budget_edit_title);
        getSupportActionBar().setTitle(mAddOnly ? "New Budget" : "Edit Budget");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(EditTransaction.this, " Account :"+String.valueOf(mPrimaryAccountId), Toast.LENGTH_LONG).show();
                if(saveState()){
                    //Toast.makeText(this, "Success!!", Toast.LENGTH_LONG).show();
                    save();
                }
            }
        });

        mBtnBudgetAccount = (EditText) findViewById(R.id.in_budget_account);
        mEtBudgetAmount = (EditText) findViewById(R.id.in_budget_amount);
        mEtBudgetNotes = (EditText) findViewById(R.id.in_budget_notes);
        mBtnBudgetCategory = (EditText) findViewById(R.id.in_budget_category);
        mContainer = (ScrollView) findViewById(R.id.container_edit_budget);

        mContainer.requestFocus();

        setup();
    }

    /**
     * set default values
     */
    public void setup(){

        Bundle extras = getIntent().getExtras();

        mBtnBudgetCategory.setOnClickListener(this);
        mBtnBudgetAccount.setOnClickListener(this);

        if(extras != null){

            Budget budget = (Budget) extras.getSerializable("budget");

            if(budget != null){
                mBudgetId = budget.id;
                mAccountId = budget.accountId;
                mAccountName = budget.accountName;
                mBudgetAmount = budget.amount;
                mBudgetNotes = budget.notes;
                mExpenseCatId = budget.expenseCategoryId;
                mExpenseCatParentName = budget.expenseCategoryParentName;
                mExpenseCatChildName = budget.expenseCategoryChildName;
            }
            else{
                mAccountId = extras.getLong("accountId");
                mAccountName = extras.getString("accountName");


            }

            mBtnBudgetAccount.setText(mAccountName);

        }

        if(mBudgetId != 0L){
            DecimalFormat f = new DecimalFormat("0.00");

            mEtBudgetAmount.setText(f.format(mBudgetAmount/100).toString());
            mEtBudgetNotes.setText(mBudgetNotes);
            mBtnBudgetCategory.setText(mExpenseCatParentName + " : " + mExpenseCatChildName);

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.in_budget_category:
                startSelectExpenseCategory();
                break;

            case R.id.in_budget_account:
                //Toast.makeText(this, "From Account", Toast.LENGTH_SHORT).show();
                startSelectAccount();
                break;
        }
    }

    /**
     * calls the activity for selecting accounts
     */
    private void startSelectAccount(){
        Intent i = new Intent(this, SelectAccount.class);
        startActivityForResult(i,SELECT_ACCOUNT_REQUEST);
    }

    /**
     * calls the activity for selecting (and managing) expense categories
     */
    private void startSelectExpenseCategory(){
        Intent i = new Intent(this, SelectExpenseCategory.class);
        startActivityForResult(i,SELECT_EXPENSE_CATEGORY_REQUEST);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        //return super.onOptionsItemSelected(item);
        Intent i;

        switch (item.getItemId()) {


            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

        }

        return false;
    }

    /*
	 * validate the form
	 */
    protected boolean saveState(){

        String budgetAmount = mEtBudgetAmount.getText().toString();

        if(mAccountId == 0L){
            //Toast.makeText(this, "Please select Account", Toast.LENGTH_LONG).show();
            mBtnBudgetAccount.setError("Please select Account");
            return false;
        }

        if(TextUtils.isEmpty(budgetAmount.trim())){
            //Toast.makeText(this, "Please enter amount", Toast.LENGTH_LONG).show();
            mEtBudgetAmount.setError("Please enter amount");
            return false;
        }



        if(mExpenseCatId == 0L){
            //Toast.makeText(this, "Please select category", Toast.LENGTH_LONG).show();
            mBtnBudgetCategory.setError("Please select category");
            return false;
        }



        return true;
    }

    /*
     * save the data
     */
    protected void save(){
        //Toast.makeText(EditTransaction.this,String.valueOf(mPrimaryAccountId), Toast.LENGTH_LONG).show();

        long budgetId = 0L;
        boolean success;

        double budgetAmount = Math.abs(parseDouble(mEtBudgetAmount.getText().toString()));
        String budgetNotes = mEtBudgetNotes.getText().toString();

        if(mBudgetId != 0L){
            success = BudgetTable.update(mBudgetId,mAccountId,mExpenseCatId,budgetAmount,budgetNotes) != -1;
        }
        else{
            success = BudgetTable.create(mAccountId,mExpenseCatId,budgetAmount,budgetNotes) != -1;
            //success = budgetId != -1;
        }

        if(!success){
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }

        finish();

    }

    protected double parseDouble(String str){
        double number;
        try{
            number =  Double.parseDouble(str);
        }catch(NumberFormatException e){
            number = 0;
        }
        return number;
    }

    protected int parseInt(String str){
        int number;
        try{
            number =  Integer.parseInt(str);
        }catch(NumberFormatException e){
            number = 0;
        }
        return number;
    }

    protected long parseLong(String str){
        long number;
        try{
            number =  Long.parseLong(str);
        }catch(NumberFormatException e){
            number = 0;
        }
        return number;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == SELECT_EXPENSE_CATEGORY_REQUEST){
            if (intent != null) {
                mExpenseCatId = intent.getLongExtra("exp_cat_id",0);
                mBtnBudgetCategory.setText(intent.getStringExtra("exp_cat_name"));
            }
        }
        else if(requestCode == SELECT_ACCOUNT_REQUEST){

            if (intent != null) {
                //Toast.makeText(this, intent.getStringExtra("account_name") , Toast.LENGTH_SHORT).show();
                mAccountId = intent.getLongExtra("account_id",0);
                mAccountName = intent.getStringExtra("account_name");
                mBtnBudgetAccount.setText(mAccountName);
            }
        }
    }



}
