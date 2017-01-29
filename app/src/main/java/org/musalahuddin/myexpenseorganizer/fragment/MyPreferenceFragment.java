package org.musalahuddin.myexpenseorganizer.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musalahuddin.myexpenseorganizer.R;

public class MyPreferenceFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

    }



}
