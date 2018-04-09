package org.musalahuddin.myexpenseorganizer.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.CameraResultCallback;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.ClearImageCallback;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.TransactionAccountTable;
import org.musalahuddin.myexpenseorganizer.database.TransactionTable;
import org.musalahuddin.myexpenseorganizer.permission.Storage;
import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;
import org.musalahuddin.myexpenseorganizer.serializable.Transaction;

import android.annotation.SuppressLint;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.app.LoaderManager;
import android.content.Loader;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditTransaction extends AppCompatActivity implements OnClickListener,OnItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor>{

    boolean mAddOnly;

    private LoaderManager mManager;
    public static final int ACCOUNTS_CURSOR=1;

    private static final int SELECT_EXPENSE_CATEGORY_REQUEST = 1;
    private static final int SELECT_TRANSACTON_CATEGORY_REQUEST = 2;
    private static final int SELECT_FROM_ACCOUNT_REQUEST = 3;
    private static final int SELECT_TO_ACCOUNT_REQUEST = 4;

    private long mTransactionId = 0L;

    static final int DIALOG_DATE = 0;
    static final int DIALOG_TIME = 1;

    private Calendar mCalendar = Calendar.getInstance();


    private Long mPrimaryAccountId = 0L;
    private String mPrimaryAccountName = "";
    private String mPrimaryAccountDescription = "";

    private Long mSecondaryAccountId = 0L;
    private String mSecondaryAccountName = "";
    private String mSecondaryAccountDescription = "";

    private Long mOrigPrimaryAccountId = 0L;
    private Long mOrigSecondaryAccountId = 0L;

    private double mTransactionAmount = 0;
    private String mTransactionNotes = "";
    private Bitmap mTransactionImage = null;
    private long mTransactionDate = 0L;
    private long mTransactionTime = 0L;
    private long mTransactionDateTime = 0L;
    private Long mExpenseCatId = 0L;
    private String mExpenseCatParentName = "";
    private String mExpenseCatChildName = "";
    private Long mTransactionCatId = 1L;
    private String mTransactionCatName = "";
    private String mTransactionImagePath = "";
    private Uri mTransactionImageUri = null;


    private Spinner mSpTransactionPayee;
    private Spinner mSpTransactionFromAccount, mSpTransactionToAccount;
    private EditText mBtnTransactionAccount, mBtnTransactionPayeeAccount;
    private AutoCompleteTextView mEtTransactionPayeeOther;
    private EditText mEtTransactionAmount;
    private EditText mEtTransactionNotes;
    private ImageButton mImgBtnTransactionCamera;
    private EditText mBtnTransactionDate,mBtnTransactionTime;
    private EditText mBtnTransactionCategory;
    private EditText mBtnTransactionType;
    private ScrollView mContainer;
    private Switch mSwitch;
    private TextView mPayeeLabel;
    private ImageButton mBtnClearTransType;

    private SimpleCursorAdapter mAccountsAdapter;

    private SimpleCursorAdapter mPayeesAdapter;

    public int mFromCount = 0;
    public int mToCount = 0;

    private int cameraButton;
    private static int imageHeight=0;
    private static int imageWidth=0;

    public static Uri contentUri;

    public Bundle extras;

    private final java.text.DateFormat mDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);
    private final java.text.DateFormat mTimeFormat = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT);

    private int getDefaultCameraButton() {
        return R.drawable.camera_button;
    }

    public void setImagePath(String imagePath){
        this.mTransactionImagePath = imagePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_transaction);


        Intent intent = getIntent();
        String action = intent.getAction();
        mAddOnly = action != null && action.equals("myexpenseorganizer.intent.add.transactions");

        //setTitle(mAddOnly ? R.string.transaction_new_title : R.string.transaction_edit_title);

        //actionbar
        //getSupportActionBar().setTitle(mAddOnly ? R.string.transaction_new_title : R.string.transaction_edit_title);
        getSupportActionBar().setTitle(mAddOnly ? "New Transaction" : "Edit Transaction");
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


        mManager = getLoaderManager();

        cameraButton = getDefaultCameraButton();

        //mSpTransactionFrom = (Spinner) findViewById(R.id.spinner_transaction_from);
        mBtnTransactionAccount = (EditText) findViewById(R.id.in_transaction_account);
        //mEtTransactionFromOther = (EditText) findViewById(R.id.in_transaction_from_other);
        mSpTransactionPayee = (Spinner) findViewById(R.id.spinner_transaction_payee);
        mBtnTransactionPayeeAccount = (EditText) findViewById(R.id.in_transaction_payee_account);
        mEtTransactionAmount = (EditText) findViewById(R.id.in_transaction_amount);
        mEtTransactionNotes = (EditText) findViewById(R.id.in_transaction_notes);
        mEtTransactionPayeeOther = (AutoCompleteTextView) findViewById(R.id.in_transaction_payee_other);
        mImgBtnTransactionCamera = (ImageButton) findViewById(R.id.image_transaction_camera);
        mBtnTransactionDate = (EditText) findViewById(R.id.in_transaction_date);
        mBtnTransactionTime = (EditText) findViewById(R.id.in_transaction_time);
        mBtnTransactionCategory = (EditText) findViewById(R.id.in_transaction_category);
        mBtnTransactionType = (EditText) findViewById(R.id.in_transaction_type);
        mContainer = (ScrollView) findViewById(R.id.container_edit_transaction);
        mSwitch = (Switch) findViewById(R.id.mySwitch);
        mPayeeLabel = (TextView) findViewById(R.id.label_transaction_payee);
        mBtnClearTransType = (ImageButton) findViewById(R.id.clear_transaction_type);

        mPayeesAdapter = new SimpleCursorAdapter(this, R.layout.support_simple_spinner_dropdown_item, null,
                new String[]{TransactionAccountTable.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION},
                new int[]{android.R.id.text1},
                0);

        mEtTransactionPayeeOther.setAdapter(mPayeesAdapter);
        mPayeesAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence str) {

               // Log.w("payee1", str.toString());

                if (str == null) {
                    return null;
                }

                //Log.w("payee2", str.toString());
                //return null;

                return MyApplication.getInstance().getContentResolver().query(
                        MyExpenseOrganizerProvider.PAYEES_URI,
                        null, null ,new String[]{str.toString()}, null);

            }
        });

        mPayeesAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                //return cur.getString(1);
                return cur.getString(cur.getColumnIndex(TransactionAccountTable.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION));
            }
        });



        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    mPayeeLabel.setText("Payer");
                }else{
                    mPayeeLabel.setText("Payee");
                }

            }
        });


        //upon orientation change stored in instance state, since new splitTransactions are immediately persisted to DB
        if (savedInstanceState != null) {
            //mCalendar = (Calendar) savedInstanceState.getSerializable("calendar");
            //setDate();
            //setTime();
        }



        //mContainer.requestFocus();

        setup();

        mContainer.requestFocus();

        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       // Log.i("onCreate mPrimaryAccountId:", String.valueOf(mPrimaryAccountId));
       // Log.i("onCreate mSecondaryAccountId:", String.valueOf(mSecondaryAccountId));

        //Toast.makeText(this, "primary Account3 :"+String.valueOf(mPrimaryAccountId), Toast.LENGTH_LONG).show();
    }

    /**
     * set default values
     */
    public void setup(){
        extras = getIntent().getExtras();

        ArrayList<String> list = new ArrayList<String>();
        list.add("Account");
        list.add("Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mAccountsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, new String[] {AccountTable.COLUMN_NAME}, new int[] {android.R.id.text1}, 0);
        mAccountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //mSpTransactionFrom.setAdapter(adapter);
        mSpTransactionPayee.setAdapter(adapter);
        //mSpTransactionFromAccount.setAdapter(mAccountsAdapter);
        //mSpTransactionToAccount.setAdapter(mAccountsAdapter);

        //this setup goes first to ovoid mPrimaryAccountId and mPrimaryAccountId overwrites
        //mSpTransactionFromAccount.setOnItemSelectedListener(this);
        //mSpTransactionToAccount.setOnItemSelectedListener(this);

        //this setup goes second to ovoid mPrimaryAccountId and mPrimaryAccountId overwrites
        //mSpTransactionFrom.setOnItemSelectedListener(this);
        mSpTransactionPayee.setOnItemSelectedListener(this);

        mImgBtnTransactionCamera.setOnClickListener(this);
        mBtnTransactionDate.setOnClickListener(this);
        mBtnTransactionTime.setOnClickListener(this);
        mBtnTransactionCategory.setOnClickListener(this);
        mBtnTransactionType.setOnClickListener(this);
        mBtnTransactionAccount.setOnClickListener(this);
        mBtnTransactionPayeeAccount.setOnClickListener(this);
        mBtnClearTransType.setOnClickListener(this);

        //default to other
        mSpTransactionPayee.setSelection(1);

        if(extras != null){
            Transaction transaction = (Transaction) extras.getSerializable("mtransaction");

            if(transaction != null){
                mTransactionId = transaction.id;
                mPrimaryAccountId = mOrigPrimaryAccountId = transaction.primaryAccountId;
                mPrimaryAccountName = transaction.primaryAccountName;
                mSecondaryAccountId = mOrigSecondaryAccountId = transaction.secondaryAccountId;
                mSecondaryAccountName = transaction.secondaryAccountName;
                //Log.i("secondary Id is set here 1: ", String.valueOf(mSecondaryAccountId) );
                mPrimaryAccountDescription = transaction.primaryAccountDescription;
                mSecondaryAccountDescription = transaction.secondaryAccountDescription;
                mTransactionAmount = Math.abs(transaction.amount/100);
                mTransactionNotes = transaction.notes;
                mTransactionDateTime = transaction.date;
                mExpenseCatId = transaction.expenseCategoryId;
                mExpenseCatParentName = transaction.expenseCategoryParentName;
                mExpenseCatChildName = transaction.expenseCategoryChildName;
                mTransactionCatId = transaction.transactionCategoryId;
                mTransactionCatName = transaction.transactionCategoryName;
                mTransactionImagePath = transaction.imagePath;
                //Log.i("mPrimaryAccountId: ",String.valueOf(mPrimaryAccountId));

                int isDeposit = transaction.isDeposit;
                if(isDeposit == 1){
                    mSwitch.setChecked(true);
                }
            }

            else{
                mPrimaryAccountId = extras.getLong("primaryAccountId");
                mPrimaryAccountName = extras.getString("primaryAccountName");

                //Toast.makeText(this, "primary Account :"+String.valueOf(mPrimaryAccountId), Toast.LENGTH_LONG).show();


            }

            mBtnTransactionAccount.setText(mPrimaryAccountName);

        }



        if(mTransactionId != 0L){


            //Cursor c = mAccountsAdapter.getCursor();
            //Toast.makeText(this, c.getCount(), Toast.LENGTH_LONG).show();
            //Log.i("TransactionId: ",String.valueOf(mTransactionId));
            //Log.i("mSecondaryAccountId: ",String.valueOf(mSecondaryAccountId));
            //Log.i("count: ",String.valueOf(c.getCount()));
            DecimalFormat f = new DecimalFormat("0.00");

            /*
            mSpTransactionFrom.setEnabled(false);
            //mSpTransactionFromAccount.setEnabled(false);
            mBtnTransactionFromAccount.setEnabled(false);
            */

            if(mPrimaryAccountId == 1L){
                //Log.i("matching","secondaryId");

                //mSpTransactionFrom.setSelection(1);
                //mEtTransactionFromOther.setText(mPrimaryAccountDescription);

                //mSpTransactionFrom.setEnabled(false);
               // mEtTransactionFromOther.setEnabled(false);
            }
            else{
                //mSpTransactionFrom.setEnabled(false);
                //mBtnTransactionFromAccount.setEnabled(false);
            }

            if(mSecondaryAccountId == 1L){
                //Log.i("matching","secondaryId");
                //mSpTransactionPayee.setSelection(1);
                mEtTransactionPayeeOther.setText(mSecondaryAccountDescription);
                //mSpTransactionTo.setEnabled(false);
                //mEtTransactionToOther.setEnabled(false);
            }
            else{

                mSpTransactionPayee.setSelection(0);
                //mSpTransactionTo.setEnabled(false);
                //mBtnTransactionToAccount.setEnabled(false);
            }

            mEtTransactionAmount.setText(f.format(mTransactionAmount));
            mEtTransactionNotes.setText(mTransactionNotes);
            mCalendar.setTimeInMillis(mTransactionDateTime);
            mBtnTransactionCategory.setText(mExpenseCatParentName + " : " + mExpenseCatChildName);
            mBtnTransactionType.setText(mTransactionCatName);

            if(!"".equals(mTransactionImagePath)){
                CameraResultCallback callback = new CameraResultCallback() {
                    @Override
                    public void handleCameraResult(Bitmap bitmap, String imagePath, Uri uri) {
                        Log.i("i am here","callback");
                        //Toast.makeText(EditTransaction.this,"getHeigth = " + mImgBtnTransactionCamera.getHeight(), Toast.LENGTH_LONG).show();
                        //Toast.makeText(EditTransaction.this,"getHeigth = " + EditTransaction.imageHeight, Toast.LENGTH_LONG).show();

                        mTransactionImage = bitmap;
                        mTransactionImagePath = imagePath;
                        mTransactionImageUri = uri;

                        //mImgBtnTransactionCamera.setMaxHeight(imageHeight);
                        //mImgBtnTransactionCamera.setMaxWidth(imageWidth);
                        mImgBtnTransactionCamera.setMaxHeight(250);
                        mImgBtnTransactionCamera.setMaxWidth(250);
                        mImgBtnTransactionCamera.setImageBitmap(mTransactionImage);

                        //Toast.makeText(EditTransaction.this,imagePath, Toast.LENGTH_LONG).show();


                    }
                };

                if(imageHeight == 0){
                    imageHeight = mImgBtnTransactionCamera.getHeight();
                }
                if(imageWidth == 0){
                    imageWidth = mImgBtnTransactionCamera.getWidth();
                }

                CameraModule.getBitmap(mTransactionImagePath, this, CameraModule.REQUEST_CODE_CAMERA, callback);
            }
        }



        mManager.initLoader(ACCOUNTS_CURSOR, null, this);




        setDate();
        setTime();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable("calendar", mCalendar);
    }



    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        super.onCreateOptionsMenu(menu);

        //Log.i("onCreateOptionsMenu mPrimaryAccountId:", String.valueOf(mPrimaryAccountId));
       // Log.i("onCreateOptionsMenu mSecondaryAccountId:", String.valueOf(mSecondaryAccountId));
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        //return super.onOptionsItemSelected(item);
        Intent i;

        //Log.i("onOptionsItemSelected mPrimaryAccountId:", String.valueOf(mPrimaryAccountId));
       // Log.i("onOptionsItemSelected mSecondaryAccountId:", String.valueOf(mSecondaryAccountId));


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

       // Log.i("saveState mPrimaryAccountId:", String.valueOf(mPrimaryAccountId));
       // Log.i("saveState mSecondaryAccountId:", String.valueOf(mSecondaryAccountId));


        /*
        if(mPrimaryAccountId == 1L) {
            String payee = mEtTransactionFromOther.getText().toString();
            if (TextUtils.isEmpty(payee.trim())) {
                //Toast.makeText(this, "Please enter amount", Toast.LENGTH_LONG).show();
                mEtTransactionFromOther.setError("Please enter payer");
                return false;
            }
        }
        */

        String transactionAmount = mEtTransactionAmount.getText().toString();
        if(TextUtils.isEmpty(transactionAmount.trim())){
            //Toast.makeText(this, "Please enter amount", Toast.LENGTH_LONG).show();
            mEtTransactionAmount.setError("Please enter amount");
            return false;
        }

        if(mSecondaryAccountId == 0L){
            //Toast.makeText(this, "Please select date", Toast.LENGTH_LONG).show();
            mBtnTransactionPayeeAccount.setError("Please select Account");
            return false;
        }

        if(mSecondaryAccountId == 1L) {
            String payee = mEtTransactionPayeeOther.getText().toString();
            if (TextUtils.isEmpty(payee.trim())) {
                //Toast.makeText(this, "Please enter amount", Toast.LENGTH_LONG).show();
                mEtTransactionPayeeOther.setError("Please enter payee");
                return false;
            }
        }


        if(mTransactionDate == 0L){
            //Toast.makeText(this, "Please select date", Toast.LENGTH_LONG).show();
            mBtnTransactionDate.setError("Please select date");
            return false;
        }

        if(mTransactionTime == 0L){
            //Toast.makeText(this, "Please select time", Toast.LENGTH_LONG).show();
            mBtnTransactionTime.setError("Please select time");
            return false;
        }

        if(mExpenseCatId == 0L){
            //Toast.makeText(this, "Please select category", Toast.LENGTH_LONG).show();
            mBtnTransactionCategory.setError("Please select category");
            return false;
        }

        /*
        if(mTransactionCatId == 0L){
            //Toast.makeText(this, "Please select transaction type", Toast.LENGTH_LONG).show();
            mBtnTransactionType.setError("Please select transaction type");
            return false;
        }
        */

        return true;
    }

    /*
     * save the data
     */
    protected void save(){
        //Toast.makeText(this, "save Account :"+String.valueOf(mPrimaryAccountId), Toast.LENGTH_LONG).show();
        //Toast.makeText(EditTransaction.this,String.valueOf(mPrimaryAccountId), Toast.LENGTH_LONG).show();

        //Log.i("save mPrimaryAccountId:", String.valueOf(mPrimaryAccountId));
       // Log.i("save mSecondaryAccountId:", String.valueOf(mSecondaryAccountId));

        long transactionId = 0L;
        boolean success;
        //String transactionFromOther = mEtTransactionFromOther.getText().toString();
        String transactionPayeeOther = mEtTransactionPayeeOther.getText().toString();
        double transactionAmount = Math.abs(parseDouble(mEtTransactionAmount.getText().toString()));
        String transactionNotes = mEtTransactionNotes.getText().toString();

        if(mTransactionId != 0L){
            TransactionTable.update(mTransactionId,mTransactionCatId,mExpenseCatId,transactionNotes,transactionNotes,mTransactionImagePath,mTransactionDateTime);
            //Toast.makeText(this, mTransactionImagePath, Toast.LENGTH_LONG).show();
            //TransactionAccountTable.update(mTransactionId, transactionAmount);

            if(!mSwitch.isChecked()){
                //withdraw
                TransactionAccountTable.update(mTransactionId, mOrigPrimaryAccountId, mPrimaryAccountId, mSecondaryAccountId,"", transactionPayeeOther, -transactionAmount, 0);
                //deposit
                TransactionAccountTable.update(mTransactionId, mOrigSecondaryAccountId, mSecondaryAccountId, mPrimaryAccountId, transactionPayeeOther, "", transactionAmount, 1);

            }
            else{
                //withdraw
                TransactionAccountTable.update(mTransactionId, mOrigSecondaryAccountId, mSecondaryAccountId, mPrimaryAccountId, transactionPayeeOther, "", -transactionAmount, 0);
                //deposit
                TransactionAccountTable.update(mTransactionId, mOrigPrimaryAccountId, mPrimaryAccountId, mSecondaryAccountId,"", transactionPayeeOther, transactionAmount, 1);

            }


        }
        else{
            transactionId = TransactionTable.create(mTransactionCatId,mExpenseCatId,transactionNotes,transactionNotes,mTransactionImagePath,mTransactionDateTime);
            success = transactionId != -1;

            if(!success){
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            }
            else{
                if(!mSwitch.isChecked()) {
                    //withdraw
                    TransactionAccountTable.create(transactionId, mPrimaryAccountId, mSecondaryAccountId, "", transactionPayeeOther, -transactionAmount, 0);
                    //deposit
                    TransactionAccountTable.create(transactionId, mSecondaryAccountId, mPrimaryAccountId, transactionPayeeOther, "", transactionAmount, 1);
                }
                else{
                    //deposit
                    TransactionAccountTable.create(transactionId, mPrimaryAccountId, mSecondaryAccountId, "", transactionPayeeOther, transactionAmount, 1);
                    //withdraw
                    TransactionAccountTable.create(transactionId, mSecondaryAccountId, mPrimaryAccountId, transactionPayeeOther, "", -transactionAmount, 0);
                }

            }
        }

        TransactionAccountTable.trigger(mOrigPrimaryAccountId,mPrimaryAccountId,mOrigSecondaryAccountId,mSecondaryAccountId);

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

    final ClearImageCallback clearImage = new ClearImageCallback() {
        @Override
        public void clearImage() {
            //Toast.makeText(EditTransaction.this,"clearImage", Toast.LENGTH_LONG).show();
            mTransactionImage = null;
            mTransactionImagePath = "";
            mImgBtnTransactionCamera.setImageResource(cameraButton);
        }

        @Override
        public void viewImage() {
            Intent intent = new Intent(Intent.ACTION_VIEW, mTransactionImageUri);
            intent.putExtra(Intent.EXTRA_STREAM, mTransactionImageUri);
            intent.setDataAndType(mTransactionImageUri, "image/jpeg");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        //Log.i("onClick mPrimaryAccountId:", String.valueOf(mPrimaryAccountId));
        //Log.i("onClick mSecondaryAccountId:", String.valueOf(mSecondaryAccountId));
        switch (v.getId()){

            case R.id.image_transaction_camera:

                if(!Storage.permission(this)){
                    Toast.makeText(this, "You don't have permission to access storage. You can enable it in Application settings->permissions", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    if (imageHeight == 0) {
                        imageHeight = mImgBtnTransactionCamera.getHeight();
                    }
                    if (imageWidth == 0) {
                        imageWidth = mImgBtnTransactionCamera.getWidth();
                    }

                    //Toast.makeText(EditTransaction.this,"getHeight = " + imageHeight, Toast.LENGTH_LONG).show();

                    if (mTransactionImage != null)
                        CameraModule.showPictureLauncher(this, clearImage);
                    else
                        CameraModule.showPictureLauncher(this, null);
                }
                break;

            case R.id.in_transaction_date:
                showDialog(DIALOG_DATE);
                break;

            case R.id.in_transaction_time:
                showDialog(DIALOG_TIME);
                break;

            case R.id.in_transaction_category:
                startSelectExpenseCategory();
                break;

            case R.id.in_transaction_type:
                startSelectTransactionCategory();
                break;

            case R.id.in_transaction_account:
                //Toast.makeText(this, "From Account", Toast.LENGTH_SHORT).show();
                startSelectFromAccount();
                break;

            case R.id.in_transaction_payee_account:
                //Toast.makeText(this, "To Account", Toast.LENGTH_SHORT).show();
                startSelectToAccount();
                break;

            case R.id.clear_transaction_type:
                mTransactionCatId = 1L;
                mBtnTransactionType.setText("");
                break;
        }

    }


    /**
     * calls the activity for selecting (and managing) expense categories
     */
    private void startSelectExpenseCategory(){
        Intent i = new Intent(this, SelectExpenseCategory.class);
        startActivityForResult(i,SELECT_EXPENSE_CATEGORY_REQUEST);
    }

    /**
     * calls the activity for selecting (and managing) transaction types
     */
    private void startSelectTransactionCategory(){
        Intent i = new Intent(this, SelectTransactionCategory.class);
        startActivityForResult(i,SELECT_TRANSACTON_CATEGORY_REQUEST);
    }

    /**
     * calls the activity for selecting accounts
     */
    private void startSelectFromAccount(){
        Bundle b = new Bundle();
        b.putLong("accountId", mSecondaryAccountId);
        Intent i = new Intent(this, SelectAccount.class);
        i.putExtras(b);
        startActivityForResult(i,SELECT_FROM_ACCOUNT_REQUEST);
    }

    /**
     * calls the activity for selecting accounts
     */
    private void startSelectToAccount(){
        Bundle b = new Bundle();
        b.putLong("accountId", mPrimaryAccountId);
        Intent i = new Intent(this, SelectAccount.class);
        i.putExtras(b);
        startActivityForResult(i,SELECT_TO_ACCOUNT_REQUEST);
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

    /**
     * listens on changes in the time dialog and sets the time on hte button
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    mCalendar.set(Calendar.MINUTE,minute);
                    setTime();
                }
            };

    /**
     * sets date on date button
     */
    private void setDate() {
        mTransactionDate = mTransactionDateTime = mCalendar.getTimeInMillis();
        mBtnTransactionDate.setText(mDateFormat.format(mCalendar.getTime()));
    }

    /**
     * sets date on date button
     */
    private void setTime() {
        mTransactionTime = mTransactionDateTime = mCalendar.getTimeInMillis();
        mBtnTransactionTime.setText(mTimeFormat.format(mCalendar.getTime()));
    }

    /**
     * helper for padding integer values smaller than 10 with 0
     * @param c
     * @return
     */
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DATE:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)
                );
            case DIALOG_TIME:
                return new TimePickerDialog(this,
                        mTimeSetListener,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE),
                        android.text.format.DateFormat.is24HourFormat(this)
                );
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == SELECT_EXPENSE_CATEGORY_REQUEST){
            if (intent != null) {
                mExpenseCatId = intent.getLongExtra("exp_cat_id",0);
                mBtnTransactionCategory.setText(intent.getStringExtra("exp_cat_name"));
            }
        }
        else if(requestCode == SELECT_TRANSACTON_CATEGORY_REQUEST){

            if (intent != null) {
                mTransactionCatId = intent.getLongExtra("trans_cat_id",0);
                mBtnTransactionType.setText(intent.getStringExtra("trans_cat_name"));
            }
        }
        else if(requestCode == SELECT_FROM_ACCOUNT_REQUEST){

            if (intent != null) {
                //Toast.makeText(this, intent.getStringExtra("account_name") , Toast.LENGTH_SHORT).show();
                mPrimaryAccountId = intent.getLongExtra("account_id",0);
                mBtnTransactionAccount.setText(intent.getStringExtra("account_name"));
            }
        }
        else if(requestCode == SELECT_TO_ACCOUNT_REQUEST){

            if (intent != null) {
                //Toast.makeText(this, intent.getStringExtra("account_name") , Toast.LENGTH_SHORT).show();
                mSecondaryAccountId = intent.getLongExtra("account_id",0);
                mBtnTransactionPayeeAccount.setText(intent.getStringExtra("account_name"));
            }
        }
        else{
            CameraResultCallback callback = new CameraResultCallback() {
                @Override
                public void handleCameraResult(Bitmap bitmap, String imagePath, Uri uri) {
                    Log.i("i am here","callback");
                    //Toast.makeText(EditTransaction.this,"getHeigth = " + mImgBtnTransactionCamera.getHeight(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(EditTransaction.this,"getHeigth = " + EditTransaction.imageHeight, Toast.LENGTH_LONG).show();

                    mTransactionImage = bitmap;
                    mTransactionImagePath = imagePath;
                    mTransactionImageUri = uri;

                    //mImgBtnTransactionCamera.setMaxHeight(imageHeight);
                    //mImgBtnTransactionCamera.setMaxWidth(imageWidth);
                    mImgBtnTransactionCamera.setMaxHeight(250);
                    mImgBtnTransactionCamera.setMaxWidth(250);
                    mImgBtnTransactionCamera.setImageBitmap(mTransactionImage);

                   // Toast.makeText(EditTransaction.this,imagePath, Toast.LENGTH_LONG).show();


                }
            };



            if(CameraModule.activityResult(this,
                    requestCode, resultCode, intent, callback)){
                Log.i("success","with camera");
            }

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        String[] selectionArgs, projection;
        String sortOrder;

        selection = null;
        selectionArgs=null;
        projection = new String[]{
                AccountTable.COLUMN_ID,
                AccountTable.COLUMN_NAME,
                AccountTable.COLUMN_NUMBER
        };

        sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";

        CursorLoader cursorLoader = new CursorLoader(this,
                AccountTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        mAccountsAdapter.swapCursor(c);
        setFromAccountSelection(c);
        setToAccountSelection(c);

       // Log.i("onLoadFinished mPrimaryAccountId:", String.valueOf(mPrimaryAccountId));
       // Log.i("onLoadFinished mSecondaryAccountId:", String.valueOf(mSecondaryAccountId));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccountsAdapter.swapCursor(null);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        switch(parent.getId()) {

            case R.id.spinner_transaction_payee:
                if(position == 0){
                    mEtTransactionPayeeOther.setText("");
                    mEtTransactionPayeeOther.setVisibility(View.GONE);
                    mBtnTransactionPayeeAccount.setVisibility(View.VISIBLE);
                    //mSpTransactionToAccount.setSelection(0);
                    if(extras != null) {
                        Transaction transaction = (Transaction) extras.getSerializable("mtransaction");

                        if(transaction != null && transaction.secondaryAccountId != 1 && transaction.secondaryAccountId != mPrimaryAccountId) {
                            mSecondaryAccountId = transaction.secondaryAccountId;
                            mSecondaryAccountName = transaction.secondaryAccountName;
                        }
                        else{
                            mSecondaryAccountId = 0L;
                            mSecondaryAccountName = "";
                        }
                    }
                    else {
                        mSecondaryAccountId = 0L;
                        mSecondaryAccountName = "";
                    }
                    mBtnTransactionPayeeAccount.setText(mSecondaryAccountName);
                    // Log.i("secondary Id is set here 2: ", String.valueOf(mSecondaryAccountId) );
                }
                else{
                    mBtnTransactionPayeeAccount.setVisibility(View.GONE);
                    mEtTransactionPayeeOther.setVisibility(View.VISIBLE);

                    mEtTransactionPayeeOther.setThreshold(1);
                    mEtTransactionPayeeOther.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    //mEtTransactionToOther.setText("");
                    if(mToCount>0)
                    mEtTransactionPayeeOther.requestFocus();
                    mToCount++;
                    mSecondaryAccountId = 1L; // default to "other" account
                   // Log.i("secondary Id is set here 3: ", String.valueOf(mSecondaryAccountId) );
                }
                break;

            case R.id.in_transaction_account:
                //Toast.makeText(EditTransaction.this,String.valueOf(id), Toast.LENGTH_LONG).show();
                if(mBtnTransactionAccount.getVisibility() != View.GONE){
                    mPrimaryAccountId = id;
                }
                break;

            case R.id.in_transaction_payee_account:
                //Toast.makeText(EditTransaction.this,String.valueOf(id), Toast.LENGTH_LONG).show();
                if(mBtnTransactionPayeeAccount.getVisibility() != View.GONE){
                    mSecondaryAccountId = id;
                    //Log.i("secondary Id is set here 4: ", String.valueOf(mSecondaryAccountId)+String.valueOf(mSpTransactionToAccount.getVisibility()) );
                }
                break;
        }

        //mContainer.requestFocus();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    public void setFromAccountSelection(Cursor cursor){
        Log.i("count: ",String.valueOf(cursor.getCount()));
        for(int i = 0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            //Log.i("label", cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME)));
            if(mPrimaryAccountId == cursor.getLong(cursor.getColumnIndex(AccountTable.COLUMN_ID))){
                //Log.i("hurray",String.valueOf(i));
                //mSpTransactionFromAccount.setSelection(i);
                break;
            }
        }
    }

    public void setToAccountSelection(Cursor cursor){
        Log.i("count: ",String.valueOf(cursor.getCount()));
        for(int i = 0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            //Log.i("label", cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME)));
            if(mSecondaryAccountId == cursor.getLong(cursor.getColumnIndex(AccountTable.COLUMN_ID))){
                //mSpTransactionToAccount.setSelection(i);
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        //try to prevent cursor leak
        mPayeesAdapter.changeCursor(null);

        super.onPause();
    }


}
