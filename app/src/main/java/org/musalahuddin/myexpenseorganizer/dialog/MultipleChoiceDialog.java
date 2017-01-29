package org.musalahuddin.myexpenseorganizer.dialog;


import org.musalahuddin.myexpenseorganizer.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MultipleChoiceDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public String[] fieldLabels;
    public boolean[] fieldChecks;
    public String title;

    public interface MultipleChoiceDialogListener{
        void multipleChoicePositiveClick(Bundle args);
        void multipleChoiceNegativeClick();
    }

    public static MultipleChoiceDialog newInstance(Bundle args){
        MultipleChoiceDialog dialogFragment = new MultipleChoiceDialog();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = getArguments();

        title = b.getString("title");

        fieldLabels = b.getStringArray("fieldLabels");
        fieldChecks = new boolean[fieldLabels.length];

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMultiChoiceItems(
                        fieldLabels,
                        fieldChecks,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton,
                                                boolean isChecked) {

                                fieldChecks[whichButton] = isChecked;
                            }
                        })
                .setPositiveButton(R.string.alert_dialog_ok,this)
                .setNegativeButton(R.string.alert_dialog_cancel,this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        MultipleChoiceDialogListener activity = (MultipleChoiceDialogListener) getActivity();


        switch(id){
            case AlertDialog.BUTTON_POSITIVE:
                Bundle args = new Bundle();
                args.putBooleanArray("fieldChecks", fieldChecks);
                activity.multipleChoicePositiveClick(args);
                dismiss();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                activity.multipleChoiceNegativeClick();
                dismiss();
                break;
        }
    }

}
