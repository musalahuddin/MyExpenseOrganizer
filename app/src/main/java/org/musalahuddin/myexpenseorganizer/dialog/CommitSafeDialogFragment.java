package org.musalahuddin.myexpenseorganizer.dialog;

/**
 * Created by Muhammad on 12/19/2016.
 */

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class CommitSafeDialogFragment extends DialogFragment {

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        try {
            return super.show(transaction, tag);
        } catch (IllegalStateException e) {

        }
        return -1;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException e) {

        }
    }
}
