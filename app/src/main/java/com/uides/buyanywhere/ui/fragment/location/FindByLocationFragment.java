package com.uides.buyanywhere.ui.fragment.location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uides.buyanywhere.R;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class FindByLocationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_by_location, container, false);
        return rootView;
    }
}
