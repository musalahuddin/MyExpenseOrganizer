package org.musalahuddin.myexpenseorganizer.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountView;
import org.musalahuddin.myexpenseorganizer.database.BudgetView;
import org.musalahuddin.myexpenseorganizer.database.MyExpenseOrganizerDatabaseHelper;
import org.musalahuddin.myexpenseorganizer.database.TransactionView;
import org.musalahuddin.myexpenseorganizer.fragment.MyPreferenceFragment;
import org.musalahuddin.myexpenseorganizer.permission.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import android.content.ContentResolver;

public class BackupRestoreActivity extends AppCompatActivity {

    boolean mBackup;

    private SQLiteDatabase db;
    private static MyExpenseOrganizerDatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_restore);

        Intent intent = getIntent();
        String action = intent.getAction();
        mBackup = action != null && action.equals("myexpenseorganizer.intent.manage.backup");

        if(mBackup){
            doBackup();
        }
        else{
            doRestore();
        }
    }

    private void initializeDb() {
        try {
            dbhelper = new MyExpenseOrganizerDatabaseHelper(MyApplication.getInstance());
            db = dbhelper.getReadableDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //notify data changes after restore
        ContentResolver cr = MyApplication.getInstance().getContentResolver();
        cr.notifyChange(AccountView.CONTENT_URI, null);
        cr.notifyChange(TransactionView.CONTENT_URI, null);
        cr.notifyChange(BudgetView.CONTENT_URI, null);
    }

    private void unInitializeDb() {
        if (db != null) {
            db = null;
            Log.d("GM/setDbNull", "Uninitialized");
        }
    }

    private void doBackup(){

        if(!Storage.permission(this)){
            Toast.makeText(this, "You don't have permission to access storage. You can enable it in Application settings->permissions", Toast.LENGTH_LONG).show();
            finish();
        }

        final String BACKUP_DB_PATH = "ExpenseOrganizerBackupV"+MyExpenseOrganizerDatabaseHelper.DATABASE_VERSION;
        String message = "";

        unInitializeDb();

        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                File currentDB = this.getDatabasePath(MyExpenseOrganizerDatabaseHelper.DATABASE_NAME);
                File backupDB = new File(sd, BACKUP_DB_PATH);

                try (FileInputStream fis = new FileInputStream(currentDB)) {
                    try (FileOutputStream fos = new FileOutputStream(backupDB)) {
                        FileChannel src = fis.getChannel();
                        FileChannel dst = fos.getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        fis.close();
                        fos.close();
                    }
                    catch(Exception e){
                        Log.e("exportDb", e.toString());
                        Toast.makeText(this, "Something went wrong with backup.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                catch(Exception e){
                    Log.e("exportDb", e.toString());
                    Toast.makeText(this, "Something went wrong with backup.", Toast.LENGTH_LONG).show();
                    finish();
                }

                message = "backup location: " + backupDB.getPath();
            }
        } catch (Exception e) {
            Log.e("exportDb", e.toString());
            Toast.makeText(this, "Something went wrong with backup.", Toast.LENGTH_LONG).show();
            finish();
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        initializeDb();
        finish();
    }

    private void doRestore(){

        if(!Storage.permission(this)){
            Toast.makeText(this, "You don't have permission to access storage. You can enable it in Application settings->permissions", Toast.LENGTH_LONG).show();
            finish();
        }

        final String BACKUP_DB_PATH = "ExpenseOrganizerBackupV"+MyExpenseOrganizerDatabaseHelper.DATABASE_VERSION;
        String message = "";

        unInitializeDb();

        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB = new File(sd, BACKUP_DB_PATH);
                File currentDB = this.getDatabasePath(MyExpenseOrganizerDatabaseHelper.DATABASE_NAME);

                try (FileInputStream fis = new FileInputStream(backupDB)) {
                    try (FileOutputStream fos = new FileOutputStream(currentDB)) {
                        FileChannel src = fis.getChannel();
                        FileChannel dst = fos.getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        fis.close();
                        fos.close();
                    }
                    catch(Exception e){
                        Log.e("importDb", e.toString());
                        Toast.makeText(this, "Backup doesn't exist", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                catch(Exception e){
                    Log.e("importDb", e.toString());
                    Toast.makeText(this, "Backup doesn't exist", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } catch (Exception e) {
            Log.e("importDb", e.toString());
            Toast.makeText(this, "Something went wrong with restore.", Toast.LENGTH_LONG).show();
            finish();
        }

        Toast.makeText(this, "Database and preferences have been restored.", Toast.LENGTH_LONG).show();
        initializeDb();
        finish();

    }
}