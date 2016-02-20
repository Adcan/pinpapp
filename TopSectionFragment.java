package com.test.test;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by A.Canpolat on 19/06/2015.
 */
public class TopSectionFragment extends Fragment {

    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_section_fragment, container, false);
        return view;

    }
}
