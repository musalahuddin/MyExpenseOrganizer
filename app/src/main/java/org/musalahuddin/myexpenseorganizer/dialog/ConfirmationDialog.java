package org.musalahuddin.myexpenseorganizer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Muhammad on 1/29/2017.
 */

public class ConfirmationDialog extends CommitSafeDialogFragment implements OnClickListener {

    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_POSITIVE_BUTTON_LABEL = "positiveButtonLabel";
    public static final String KEY_NEGATIVE_BUTTON_LABEL = "negativeButtonLabel";
    public static final String KEY_POSITION = "position";

    public static final ConfirmationDialog newInstance(Bundle args) {
        ConfirmationDialog dialogFragment = new ConfirmationDialog();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle = getArguments();
        Activity ctx  = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(bundle.getString(KEY_TITLE));
        builder.setMessage(bundle.getString(KEY_MESSAGE));
        builder.setPositiveButton(bundle.getString(KEY_POSITIVE_BUTTON_LABEL),this);
        builder.setNegativeButton(bundle.getString(KEY_NEGATIVE_BUTTON_LABEL),this);
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        ConfirmationDialogListener ctx = (ConfirmationDialogListener) getActivity();
        if (ctx == null)  {
            return;
        }
        ctx.onNegative();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ConfirmationDialogListener ctx = (ConfirmationDialogListener) getActivity();
        if (ctx == null)  {
            return;
        }

        Bundle bundle = getArguments();
        if(which == AlertDialog.BUTTON_POSITIVE){
            //Toast.makeText(getActivity(), String.valueOf(bundle.getInt(KEY_POSITION)), Toast.LENGTH_LONG).show();
            ctx.onPositive(bundle.getInt(KEY_POSITION));
        }
        else if(which == AlertDialog.BUTTON_NEGATIVE){
            ctx.onNegative();
        }
    }

    public interface ConfirmationDialogListener {
        void onNegative();
        void onPositive(int position);
    }
}
