package org.musalahuddin.myexpenseorganizer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountCategoryTable;
import org.musalahuddin.myexpenseorganizer.database.ExpenseChildCategoryTable;
import org.musalahuddin.myexpenseorganizer.database.ExpenseParentCategoryTable;
import org.musalahuddin.myexpenseorganizer.database.TransactionCategoryTable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class CatImporter {

    //private static MyAsyncTask task = null;

    // defining types of imports
    static final int EXPENSE_CATEGORIES_IMPORT = 1;
    static final int TRANSACTION_CATEGORIES_IMPORT = 2;
    static final int ACCOUNT_CATEGORIES_IMPORT = 3;

    // setting default to be 0
    //static int importType = 0;

    public static Result analyzeCatFileWithSAX(InputStream is, int type){

        MyDefaultHandler handler = new MyDefaultHandler(type);
        try{
            Xml.parse(is, Xml.Encoding.UTF_8, handler);
        }catch(IOException e){
            return new Result(false,R.string.parse_error_other_exception,e.getMessage());
        }catch(SAXException e){
            return new Result(false,R.string.parse_error_parse_exception);
        }

        return handler.getResult();
    }

    public static void importExpenseCategories(){
        //importType = EXPENSE_CATEGORIES_IMPORT;
        MyAsyncTask task1 = new MyAsyncTask(MyApplication.getInstance(),EXPENSE_CATEGORIES_IMPORT);
        task1.execute();
    }

    public static void importTransactionCategories(){
        //importType = TRANSACTION_CATEGORIES_IMPORT;
        MyAsyncTask task2 = new MyAsyncTask(MyApplication.getInstance(),TRANSACTION_CATEGORIES_IMPORT);
        task2.execute();
    }

    public static void importAccountCategories(){
        //importType = ACCOUNT_CATEGORIES_IMPORT;
        MyAsyncTask task3 = new MyAsyncTask(MyApplication.getInstance(),ACCOUNT_CATEGORIES_IMPORT);
        task3.execute();
    }

    public static class MyDefaultHandler extends DefaultHandler{
        String tempValue;

        ArrayList<ExpenseCategory> categories;
        ArrayList<AccountCategory> accountCategories;
        ArrayList<TransactionCategory> transactionCategories;

        ExpenseCategory category;
        ExpenseSubCategory subCategory;
        AccountCategory accountCategory;
        TransactionCategory transactionCategory;

        int importType;

        public MyDefaultHandler(int type){
            categories = new ArrayList<ExpenseCategory>();
            accountCategories = new ArrayList<AccountCategory>();
            transactionCategories = new ArrayList<TransactionCategory>();
            importType=type;
        }

        public Result getResult(){
            if(categories.size() > 0){
                return new Result(true,0,categories);
            }
            else if(accountCategories.size() > 0){
                return new Result(true,0,accountCategories);
            }
            else if(transactionCategories.size() > 0){
                return new Result(true,0,transactionCategories);
            }
            else{
                return new Result(false,R.string.parse_error_no_data_found);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            // TODO Auto-generated method stub
            super.characters(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            // TODO Auto-generated method stub
            super.endElement(uri, localName, qName);
            switch(importType) {
                case EXPENSE_CATEGORIES_IMPORT:
                    // if end of category element add to list
                    if(localName.equalsIgnoreCase("category")){
                        categories.add(category);
                    }
                    // if end of sub category add to category
                    else if(localName.equalsIgnoreCase("sub_category")){
                        category.addSubCategory(subCategory);
                    }
                    break;
                case ACCOUNT_CATEGORIES_IMPORT:
                    if(localName.equalsIgnoreCase("category")){
                        accountCategories.add(accountCategory);
                    }
                    break;
                case TRANSACTION_CATEGORIES_IMPORT:
                    if(localName.equalsIgnoreCase("category")){
                        transactionCategories.add(transactionCategory);
                    }
                    break;
            }
        }


        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            // TODO Auto-generated method stub
            super.startElement(uri, localName, qName, attributes);

            switch(importType) {
                case EXPENSE_CATEGORIES_IMPORT:
                    if(localName.equalsIgnoreCase("category")){
                        Log.i("cat: ", attributes.getValue("Na"));
                        category = new ExpenseCategory();
                        category.setId(attributes.getValue("Nb"));
                        category.setName(attributes.getValue("Na"));
                    }
                    else if(localName.equalsIgnoreCase("sub_category")){
                        Log.i("subcat: ", attributes.getValue("Na"));
                        subCategory = new ExpenseSubCategory();
                        subCategory.setId(attributes.getValue("Nb"));
                        subCategory.setName(attributes.getValue("Na"));

                    }
                    break;
                case ACCOUNT_CATEGORIES_IMPORT:
                    if(localName.equalsIgnoreCase("category")){
                        Log.i("act cat: ", attributes.getValue("Na"));
                        accountCategory = new AccountCategory();
                        accountCategory.setName(attributes.getValue("Na"));
                    }
                    break;
                case TRANSACTION_CATEGORIES_IMPORT:
                    if(localName.equalsIgnoreCase("category")){
                        Log.i("trans cat: ", attributes.getValue("Na"));
                        transactionCategory = new TransactionCategory();
                        transactionCategory.setName(attributes.getValue("Na"));
                    }
                    break;
            }
        }

    }

    static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private MyApplication context;
        private int importType = 0;
        InputStream catXML;
        private int max, totalImportedCat;
        Result result;
        int progress = 0;

        private String title;
        private ArrayList<ExpenseCategory> categories;
        private ArrayList<AccountCategory> accountCategories;
        private ArrayList<TransactionCategory> transactionCategories;

        /**
         * @param context
         */
        public MyAsyncTask(Context context, int type) {
            this.context = (MyApplication) context;
            this.importType = type;
        }

        public void setTitle(String title){
            this.title = title;
        }

        public String getTitle(){
            return title;
        }

        public Integer getTotalImportedCat(){
            return totalImportedCat;
        }

        public Integer getProgress(){
            return progress;
        }

        public Integer getMax(){
            return max;
        }

        public void setMax(int max){
            this.max = max;
        }

        public void setResult(Result result){
            this.result = result;
        }

        public Result getResult(){
            return result;
        }

        public void importExpenseCats(){
            int count = 0;
            String name;
            long main_id, sub_id;

            for(ExpenseCategory mainCategory : categories){
                //System.out.println(mainCategory.getId() + " = " + mainCategory.getName());
                name = mainCategory.getName();
                count++;
                main_id = ExpenseParentCategoryTable.find(name);
                if(main_id != -1){
                    Log.i("MyExpense","category with name "+ name + " already defined");
                }
                else{
                    main_id = ExpenseParentCategoryTable.create(name);
                    if(main_id != -1){
                        totalImportedCat++;
                        //if(count % 10 == 0){
                        //publishProgress(count);
                        //}
                    }
                    else{
                        //this should not happen
                        Log.w("MyExpense","could neither retrieve nor store main category " + name);
                        continue;
                    }
                }

                for(ExpenseSubCategory subCategory: mainCategory.getSubCategories()){
                    //System.out.println(">>> "+subCategory.getId() + " = " + subCategory.getName());
                    name = subCategory.getName();
                    sub_id = ExpenseChildCategoryTable.find(name,main_id);
                    if(sub_id != -1){
                        Log.i("MyExpense","sub category with name "+ name + " already defined");
                    }
                    else{
                        sub_id = ExpenseChildCategoryTable.create(name,main_id);
                        if(sub_id != -1){
                            totalImportedCat++;
                        }
                        else{
                            //this should not happen
                            Log.w("MyExpense","could neither retrieve nor store sub category " + name);
                            continue;
                        }
                    }

                }
            }
        }

        public void importAccountCats(){
            int count = 0;
            String name;
            long main_id;
            for(AccountCategory mainCategory : accountCategories){
                //System.out.println(mainCategory.getName());
                name = mainCategory.getName();
                count++;
                main_id = AccountCategoryTable.find(name);
                if(main_id != -1){
                    Log.i("MyExpense","category with name "+ name + " already defined");
                }
                else{
                    main_id = AccountCategoryTable.create(name);
                    if(main_id != -1){
                        totalImportedCat++;
                        //if(count % 10 == 0){
                        //publishProgress(count);
                        //}
                    }
                    else{
                        //this should not happen
                        Log.w("MyExpense","could neither retrieve nor store main category " + name);
                        continue;
                    }
                }
            }
        }

        public void importTransactionCats(){
            int count = 0;
            String name;
            long main_id;
            for(TransactionCategory mainCategory : transactionCategories){
                //System.out.println(mainCategory.getName());
                name = mainCategory.getName();
                count++;
                main_id = TransactionCategoryTable.find(name);
                if(main_id != -1){
                    Log.i("MyExpense","category with name "+ name + " already defined");
                }
                else{
                    main_id = TransactionCategoryTable.create(name);
                    if(main_id != -1){
                        totalImportedCat++;
                        //if(count % 10 == 0){
                        //publishProgress(count);
                        //}
                    }
                    else{
                        //this should not happen
                        Log.w("MyExpense","could neither retrieve nor store main category " + name);
                        continue;
                    }
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(!parseXML()){
                return null;
            }

            totalImportedCat=0;

            if(importType == EXPENSE_CATEGORIES_IMPORT){
                setMax(categories.size());
                importExpenseCats();
            }
            else if(importType == ACCOUNT_CATEGORIES_IMPORT){
                setMax(accountCategories.size());
                importAccountCats();
            }
            else if(importType == TRANSACTION_CATEGORIES_IMPORT){
                setMax(transactionCategories.size());
                importTransactionCats();
            }

            setResult(new Result(true,
                    R.string.import_categories_success,
                    String.valueOf(getTotalImportedCat())
            ));

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

            //Toast.makeText(context, "total: "+String.valueOf(totalImportedCat), Toast.LENGTH_LONG).show();
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        /**
         * return false upon problem or true
         */
        protected boolean parseXML(){
            int rawId = 0;
            Result result;
            switch(importType) {

                case ACCOUNT_CATEGORIES_IMPORT:
                    catXML = context.getResources().openRawResource(R.raw.account_categories);
                    result = analyzeCatFileWithSAX(catXML,importType);
                    if(result.success){
                        accountCategories = (ArrayList<AccountCategory>) result.extra[0];
                        return true;
                    }
                    else{
                        return false;
                    }
                case EXPENSE_CATEGORIES_IMPORT:
                    catXML = context.getResources().openRawResource(R.raw.expense_categories);
                    result = analyzeCatFileWithSAX(catXML,importType);
                    if(result.success){
                        categories = (ArrayList<ExpenseCategory>) result.extra[0];
                        return true;
                    }
                    else{
                        return false;
                    }
                case TRANSACTION_CATEGORIES_IMPORT:
                    catXML = context.getResources().openRawResource(R.raw.transaction_categories);
                    result = analyzeCatFileWithSAX(catXML,importType);
                    if(result.success){
                        transactionCategories = (ArrayList<TransactionCategory>) result.extra[0];
                        return true;
                    }
                    else{
                        return false;
                    }
                default:
                    return false;
            }


            //catXML = activity.getResources().openRawResource(rawId);
            //Result result = analyzeCatFileWithSAX(catXML);
        }


    }

}
