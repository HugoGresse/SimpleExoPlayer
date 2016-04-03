package io.gresse.hugo.simpleexoplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.gresse.hugo.simpleexoplayer.view.ExoplayerView;

/**
 * Simple demo fragment that use only the player all-in-one view
 *
 * Created by Hugo Gresse on 01/04/16.
 */
public class ExoPlayerViewFragment extends Fragment {

    ExoplayerView mExoplayerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exoplayerview, container, false);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
