package org.musalahuddin.myexpenseorganizer.activity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.AccountView;
//import org.musalahuddin.myexpenseorganizer.dialog.MultipleChoiceDialog;
import org.musalahuddin.myexpenseorganizer.dialog.SingleChoiceDialog;
import org.musalahuddin.myexpenseorganizer.serializable.Account;
import org.musalahuddin.myexpenseorganizer.util.Utils;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;


public class EditAccount extends AppCompatActivity implements View.OnClickListener,SingleChoiceDialog.SingleChoiceDialogListener {

    private static final int SELECT_CATEGORY_REQUEST = 1;
    private static final int SELECT_FIELD_REQUEST = 2;

    private static final int BUDGET= 3;
    private static final int DUE = 4;

    private int mDialogCode;

    private long mAccountId = 0L;

    private Long mAccountCatId = 0L;
    private long mAccountDueDate = 0L;
    private String mAccountCatName;
    private Calendar mCalendar = Calendar.getInstance();


    private Long mBudgetStartDay = 1L;

    //fields
    private TextView mAccountNameText;
    private TextView mAccountNumText;
    private TextView mAccountDescText;
    private TextView mAccountBalText;
    private TextView mAccountLimitText;
    private TextView mAccountPayText;
    private TextView mAccountDueButton;
    private TextView mAccountCatButton;
    private Button mAddFieldButton;
    private ScrollView mContainer;
    private Switch mSwitch;
    private EditText mETAccountBudgetDay;

    //rows
    private TableRow mAccountNameRow;
    private TableRow mAccountNumRow;
    private TableRow mAccountDescRow;
    private TableRow mAccountBalRow;
    private TableRow mAccountLimitRow;
    private TableRow mAccountPayRow;
    private TableRow mAccountDueRow;
    private TableRow mAccountCatRow;


    static final int DIALOG_DATE = 0;
    static final int DIALOG_FIELDS = 1;

    private ArrayList<Field> fields = new ArrayList<Field>();
    private Integer[] fieldIds;

    private final java.text.DateFormat mTitleDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);

    boolean mAddOnly;

    protected class Field{
        int fieldId;
        String fieldLabel;

        public Field(int id, String label){
            this.fieldId = id;
            this.fieldLabel = label;
        }

        public int getFieldId(){
            return fieldId;
        }

        public String getFieldLabel(){
            return fieldLabel;
        }
    }

    /**
     * calls the activity for selecting (and managing) account categories
     */
    private void startSelectAccountCategory(){
        Intent i = new Intent(this, SelectAccountCategory.class);
        startActivityForResult(i,SELECT_CATEGORY_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account);

        Intent intent = getIntent();
        String action = intent.getAction();
        mAddOnly = action != null && action.equals("myexpenseorganizer.intent.add.accounts");

        //setTitle(mAddOnly ? R.string.account_new_title : R.string.account_edit_title);
        //Log.i("time: " , String.valueOf(System.currentTimeMillis()));

        //actionbar
        //getSupportActionBar().setTitle(mAddOnly ? R.string.account_new_title : R.string.account_edit_title);
        getSupportActionBar().setTitle(mAddOnly ? "New Account" : "Edit Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(saveState()){
                    //Toast.makeText(this, "Success!!", Toast.LENGTH_LONG).show();
                    save();
                }
            }
        });



        mAccountNameText = (EditText) findViewById(R.id.in_account_name);
        mAccountNumText = (EditText) findViewById(R.id.in_account_number);
        mAccountDescText= (EditText) findViewById(R.id.in_account_description);
        mAccountBalText= (EditText) findViewById(R.id.in_account_balance);
        mAccountLimitText= (EditText) findViewById(R.id.in_account_limit);
        mAccountPayText= (EditText) findViewById(R.id.in_account_payment);
        mAccountCatButton = (EditText) findViewById(R.id.in_account_category);
        mAccountDueButton = (EditText) findViewById(R.id.in_account_due);
        //mAddFieldButton = (Button) findViewById(R.id.add_field);
        mContainer = (ScrollView) findViewById(R.id.container_edit_account);
        mSwitch = (Switch) findViewById(R.id.mySwitch);
        mETAccountBudgetDay = (EditText) findViewById(R.id.in_account_budget_day);


        mAccountCatButton.setOnClickListener(this);
        mAccountDueButton.setOnClickListener(this);
        mETAccountBudgetDay.setOnClickListener(this);
       // mAddFieldButton.setOnClickListener(this);


        mContainer.requestFocus();

        populateFields();

    }

    private void populateFields(){

        Bundle extras = getIntent().getExtras();
       // mAccountId = extras != null ? extras.getLong(AccountTable.COLUMN_ID):0L;
        DecimalFormat f = new DecimalFormat("0.00");

        if(extras != null){
            Account account = (Account) extras.getSerializable("account");
            mAccountId = account.id;
            mAccountCatId = account.accoutCategoryId;
            mAccountDueDate = account.due;
            if(mAccountDueDate != 0L && mAccountDueDate > 31){
                mCalendar.setTimeInMillis(mAccountDueDate);
                mAccountDueDate = mCalendar.get(Calendar.DAY_OF_MONTH);
            }

            mBudgetStartDay = account.budget_start_day;

            //populate fields
            mAccountNameText.setText(account.name);
            if(account.number != 0) mAccountNumText.setText(String.valueOf(account.number));
            if(account.description != "") mAccountDescText.setText(account.description);
            mAccountBalText.setText(f.format(account.balance/100).toString());
            if(account.limit != 0) mAccountLimitText.setText(f.format(account.limit/100).toString());
            if(account.payment != 0) mAccountPayText.setText(f.format(account.payment/100).toString());
            mAccountCatButton.setText(account.accountCategoryName);
            if(mAccountDueDate != 0L) {
                //mAccountDueButton.setText(mTitleDateFormat.format(mCalendar.getTime()));
                mAccountDueButton.setText(String.valueOf(mAccountDueDate)+suffix(mAccountDueDate)+" of the Month");
            }



            //hide balance field when editing account
            LinearLayout balance_row = (LinearLayout) findViewById(R.id.row_account_balance);
            balance_row.setVisibility(View.GONE);
        }


        mETAccountBudgetDay.setText(String.valueOf(mBudgetStartDay)+suffix(mBudgetStartDay)+" of the Month");
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.edit, menu);
        //return true;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
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
            /*
            case R.id.SAVE_COMMAND:
                if(saveState()){
                    //Toast.makeText(this, "Success!!", Toast.LENGTH_LONG).show();
                    save();
                }
                break;
            */
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                //return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()){
            case R.id.in_account_category:
                startSelectAccountCategory();
                break;
            case R.id.in_account_due:
                //showDialog(DIALOG_DATE);
                startSelectBudgetDay(DUE);
                break;
            case R.id.in_account_budget_day:
                startSelectBudgetDay(BUDGET);
                break;
            /*
            case R.id.add_field:
                selectFields();
                break;
            */
        }
    }

    /**
     * listens on changes in the date dialog and sets the date on the button
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mCalendar.set(year, monthOfYear, dayOfMonth);
                    setDate();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        switch (id) {
            case DIALOG_DATE:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)
                );
        }
        return null;
    }

    protected void startSelectBudgetDay(int code){
        mDialogCode = code;
        String tag;
        //Toast.makeText(this, String.valueOf(mBudgetStartDay), Toast.LENGTH_LONG).show();
        Bundle args = new Bundle();
        args.putString(SingleChoiceDialog.KEY_TITLE, "Select a day of the Month");
        args.putString(SingleChoiceDialog.KEY_NEGATIVE_BUTTON_LABEL, "CANCEL");
        args.putString(SingleChoiceDialog.KEY_POSITIVE_BUTTON_LABEL, "OK");
        //args.putInt(SingleChoiceDialog.KEY_CHOICES, R.array.days_of_month);
        if(code == BUDGET) {
            args.putLong(SingleChoiceDialog.KEY_DAY, mBudgetStartDay);
            tag = "DUE_DAY";
        }
        else {
            args.putLong(SingleChoiceDialog.KEY_DAY, mAccountDueDate);
            tag = "BUDGET_DAY";
        }
        //args.putInt(SingleChoiceDialog.KEY_DAY, 6);

        SingleChoiceDialog.newInstance(args)
                .show(getSupportFragmentManager(), tag);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode){
            case SELECT_CATEGORY_REQUEST:
                if (intent != null) {
                    mAccountCatId = intent.getLongExtra("cat_id",0);
                    mAccountCatName = intent.getStringExtra("cat_name");
                    mAccountCatButton.setText(mAccountCatName);
                }
                break;
            case SELECT_FIELD_REQUEST:
                if (intent != null) {
                    Bundle b = intent.getExtras();
                    displayFields(b);
                    checkFields();
                }
                break;
        }

    }

    /**
     * sets date on date button
     */
    private void setDate() {
        mAccountDueDate = mCalendar.getTimeInMillis();
		/*
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		String day = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.US);
		String month = mCalendar.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.US);
		int year = mCalendar.get(Calendar.YEAR);
		//mAccountDueDate = day;

		String suffix = getDateSuffix(dayOfMonth);

		mAccountDueButton.setText(day+ ", "+month+", "+String.valueOf(dayOfMonth)+suffix+", "+String.valueOf(year));
		*/
        mAccountDueButton.setText(mTitleDateFormat.format(mCalendar.getTime()));
    }

    /**
     * return suffix based on the day of the month
     */
    protected String getDateSuffix(int day) {
        switch (day) {
            case 1: case 21: case 31:
                return ("st");

            case 2: case 22:
                return ("nd");

            case 3: case 23:
                return ("rd");

            default:
                return ("th");
        }
    }

    /*
     * validate the form
     */
    protected boolean saveState(){
        String name = mAccountNameText.getText().toString();
        String balance = mAccountBalText.getText().toString();
        String limit = mAccountLimitText.getText().toString();
        String pay = mAccountPayText.getText().toString();
        long catId  = mAccountCatId;


        //if(name.equals("")){
        if(TextUtils.isEmpty(name.trim())){
            //Toast.makeText(this, "Please enter account name", Toast.LENGTH_LONG).show();
            mAccountNameText.setError("Please enter account name");
            return false;
        }
        if(balance.equals(".") || limit.equals(".") || pay.equals(".")){
            Toast.makeText(this, "Invalid number entered", Toast.LENGTH_LONG).show();
            return false;
        }

        if(catId == 0L){
            //Toast.makeText(this, "Please select account category", Toast.LENGTH_LONG).show();
            mAccountCatButton.setError("Please select account category");
            return false;
        }

        return true;
    }

    /*
     * save the data
     */
    protected void save(){
        boolean success;
        String name = mAccountNameText.getText().toString();
        int number = parseInt(mAccountNumText.getText().toString());
        String description = mAccountDescText.getText().toString();
        double balance = parseDouble(mAccountBalText.getText().toString());
        double limit = parseDouble(mAccountLimitText.getText().toString());
        double pay = parseDouble(mAccountPayText.getText().toString());
        long due = mAccountDueDate;
        long catId  = mAccountCatId;
        long bstartDay = mBudgetStartDay;

        if(!mSwitch.isChecked()){
            balance = balance * -1;
        }

        Log.i("credit limit is ",String.valueOf(limit));
        Log.i("due date is ",String.valueOf(due));

        if(mAccountId != 0L){
            success = AccountTable.update(mAccountId, name, number, description, limit, pay, due, bstartDay, catId) != -1;
        }
        else{
            success = AccountTable.create(name, number, description, balance, limit, pay, due, bstartDay, catId) != -1;
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

    protected void displayFields(Bundle bundle){
        boolean[] fieldChecks = bundle.getBooleanArray("fieldChecks");
        TableRow row;
        for(int i=0; i<fieldChecks.length; i++){
            //System.out.println("check is: " + fieldChecks[i] );
            if(fieldChecks[i]){
                row = (TableRow) findViewById(fieldIds[i]);
                row.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void checkFields(){

        boolean hiddenField = false;
        TableRow row;

        for(Field field: fields){
            row = (TableRow) findViewById(field.getFieldId());
            if(row.getVisibility() == View.GONE){
                hiddenField = true;
                break;
            }
        }

        /*
        // if all fields are visible then hide add file button
        if(!hiddenField){
            //hide add field button
            row = (TableRow) findViewById(R.id.row_add_field);
            row.setVisibility(View.GONE);
        }
        */
    }



    private void hideKeyboard() {
        //Utils.hideSoftInputForViews(this, mAccountNameText);
        //mAccountNameText.setCursorVisible(false);
        //mAccountNameText.setFocusable(false);
        mContainer.requestFocus();
    }

	/*
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		//mAccountNameText.setCursorVisible(false);
        mAccountNameText.setFocusable(true);
	}
	*/


    @Override
    public void onSingleChoiceNegative() {

    }

    @Override
    public void onSingleChoicePositive(int day) {

        //TextView et = (mDialogCode == BUDGET) ? mETAccountBudgetDay : mAccountDueButton;
        if(mDialogCode == BUDGET){
            mBudgetStartDay = Long.valueOf(day);
            mETAccountBudgetDay.setText(String.valueOf(mBudgetStartDay)+suffix(mBudgetStartDay)+" of the Month");
        }
        else{
            mAccountDueDate = Long.valueOf(day);
            mAccountDueButton.setText(String.valueOf(mAccountDueDate)+suffix(mAccountDueDate)+" of the Month");
        }

        //et.setText(String.valueOf(mBudgetStartDay)+suffix(mBudgetStartDay)+" of the Month");

    }



    public String suffix(long num){
        String suffix = "";
        if(num == 1 || num == 21 || num == 31) {
            suffix = "st";
        }
        else if(num == 2 || num == 22){
            suffix = "nd";
        }
        else if(num == 3 || num == 23){
            suffix = "rd";
        }
        else{
            suffix = "th";
        }

        return suffix;
    }

}
