package org.musalahuddin.myexpenseorganizer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Muhammad on 12/19/2016.
 */

public class ListDialog extends DialogFragment {


    public static final ListDialog newInstance(Bundle args) {
        ListDialog dialogFragment = new ListDialog();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        String[] options = new String[] {"View/Edit", "Delete"};
        return new AlertDialog.Builder(getActivity())
                .setTitle("dil")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /*
                        // User clicked so do some stuff
                        String[] items = getResources().getStringArray(R.array.select_dialog_items);
                        new AlertDialog.Builder(getActivity())
                                .setMessage("You selected: " + which + " , " + items[which])
                                .show();
                        */
                    }
                })
                .create();
    }
}
