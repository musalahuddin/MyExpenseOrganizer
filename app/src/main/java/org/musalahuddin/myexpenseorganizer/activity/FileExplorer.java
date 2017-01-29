package org.musalahuddin.myexpenseorganizer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Muhammad on 1/1/2017.
 */

public class FileExplorer extends AppCompatActivity {

    final String IMAGE_DIRECTORY_NAME = "My Expense Organizer";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");
        //intent.setData(selectedUri);

        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"not working", Toast.LENGTH_LONG).show();
            // if you reach this place, it means there is no any file
            // explorer app installed on your device
        }
    }
}
