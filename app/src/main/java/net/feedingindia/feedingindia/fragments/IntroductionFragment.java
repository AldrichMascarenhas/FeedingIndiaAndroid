package net.feedingindia.feedingindia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Aldrich on 10/10/2015.
 */

public class IntroductionFragment extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    public static IntroductionFragment newInstance(int layoutResId) {
        IntroductionFragment IntroductionFragment = new IntroductionFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        IntroductionFragment.setArguments(args);

        return IntroductionFragment;
    }

    private int layoutResId;

    public IntroductionFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }

}