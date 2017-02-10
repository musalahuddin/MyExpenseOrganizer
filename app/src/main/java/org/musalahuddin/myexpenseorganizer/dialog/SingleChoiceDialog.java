package org.musalahuddin.myexpenseorganizer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.musalahuddin.myexpenseorganizer.R;

/**
 * Created by Muhammad on 2/4/2017.
 */

public class SingleChoiceDialog extends CommitSafeDialogFragment implements DialogInterface.OnClickListener {

    public static final String KEY_TITLE = "title";
    public static final String KEY_CHOICES = "key_choices";
    public static final String KEY_DAY = "key_day";
    public static final String KEY_POSITIVE_BUTTON_LABEL = "positiveButtonLabel";
    public static final String KEY_NEGATIVE_BUTTON_LABEL = "negativeButtonLabel";

    public int day;

    public static final SingleChoiceDialog newInstance(Bundle args) {
        SingleChoiceDialog dialogFragment = new SingleChoiceDialog();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle = getArguments();

        //int d = bundle.getInt(KEY_DAY);
        //Toast.makeText(getActivity(), String.valueOf((int) bundle.getLong(KEY_DAY)), Toast.LENGTH_LONG).show();

        Activity ctx  = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        //day = bundle.getInt(KEY_DAY);
        builder.setTitle(bundle.getString(KEY_TITLE));
        builder.setSingleChoiceItems(R.array.days_of_month, (int) bundle.getLong(KEY_DAY)-1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                day = whichButton+1;
            }
        });
        builder.setPositiveButton(bundle.getString(KEY_POSITIVE_BUTTON_LABEL),this);
        builder.setNegativeButton(bundle.getString(KEY_NEGATIVE_BUTTON_LABEL),this);
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

        SingleChoiceDialogListener activity = (SingleChoiceDialogListener) getActivity();
        if (activity == null)  {
            return;
        }
        activity.onSingleChoiceNegative();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {


        SingleChoiceDialogListener ctx = (SingleChoiceDialogListener) getActivity();
        if (ctx == null)  {
            return;
        }


        //Bundle bundle = getArguments();
        if(which == AlertDialog.BUTTON_POSITIVE){
            //Toast.makeText(getActivity(), String.valueOf(day), Toast.LENGTH_LONG).show();
            ctx.onSingleChoicePositive(day);
        }
        else if(which == AlertDialog.BUTTON_NEGATIVE){
            ctx.onSingleChoiceNegative();
        }

    }

    public interface SingleChoiceDialogListener {
        void onSingleChoiceNegative();
        void onSingleChoicePositive(int day);
    }

}
