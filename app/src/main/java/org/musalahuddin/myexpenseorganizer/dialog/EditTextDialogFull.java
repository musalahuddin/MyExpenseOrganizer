package org.musalahuddin.myexpenseorganizer.dialog;

/**
 * Created by Muhammad on 12/19/2016.
 */
import org.musalahuddin.myexpenseorganizer.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class EditTextDialogFull extends CommitSafeDialogFragment implements OnEditorActionListener {

    public static final String KEY_RESULT = "result";
    public static final String KEY_DIALOG_TITLE = "dialogTitle";
    public static final String KEY_VALUE = "value";
    public static final String KEY_REQUEST_CODE = "requestCode";
    public static final String KEY_INPUT_TYPE = "inputType";
    public static final String KEY_MAX_LENGTH = "maxLenght";

    public interface EditTextDialogListener {
        void onFinishEditDialog(Bundle args);

        void onCancelEditDialog();
    }

    private EditText mEditText;

    public static final EditTextDialogFull newInstance(Bundle args) {
        EditTextDialogFull dialogFragment = new EditTextDialogFull();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = LayoutInflater.from(getActivity());
        //noinspection InflateParams
        View view = li.inflate(R.layout.edit_text_dialog, null);
        mEditText = ((EditText) view.findViewById(R.id.EditTextDialogInput));
        Bundle args = getArguments();
        mEditText.setInputType(args.getInt(KEY_INPUT_TYPE, InputType.TYPE_CLASS_TEXT));
        mEditText.setOnEditorActionListener(this);

        mEditText.setText(args.getString(KEY_VALUE));
        int maxLength = args.getInt(KEY_MAX_LENGTH);
        if (maxLength != 0) {
            mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }
        AlertDialog dialog = builder.setView(view)
                .setTitle(args.getString(KEY_DIALOG_TITLE))
                .create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (getActivity() == null) {
            return;
        }
        ((EditTextDialogListener) getActivity()).onCancelEditDialog();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId ||
                (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
            // Return input text to activity
            EditTextDialogListener activity = (EditTextDialogListener) getActivity();
            if (activity != null) {
                Bundle args = getArguments();
                String result = mEditText.getText().toString();
                if (result.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.no_title_given), Toast.LENGTH_LONG).show();
                } else {
                    args.putString(KEY_RESULT, result);
                    activity.onFinishEditDialog(args);
                    this.dismiss();
                    return true;
                }
            }
        }
        return false;
    }
}