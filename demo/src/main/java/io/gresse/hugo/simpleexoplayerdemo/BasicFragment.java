package io.gresse.hugo.simpleexoplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.gresse.hugo.simpleexoplayer.player.SimpleExoPlayer;

/**
 * Default usage of the SImpleExoplayer
 *
 * Created by Hugo Gresse on 01/04/16.
 */
public class BasicFragment extends Fragment{

    SimpleExoPlayer mSimpleExoPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic, container);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
