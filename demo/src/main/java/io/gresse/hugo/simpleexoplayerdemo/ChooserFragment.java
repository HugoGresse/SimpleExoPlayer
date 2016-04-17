package io.gresse.hugo.simpleexoplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Choose which player mode to test
 *
 * Created by Hugo Gresse on 03/04/16.
 */
public class ChooserFragment extends Fragment {

    Button mBasicButton;
    Button mViewButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chooser, container, false);

        mBasicButton = (Button) view.findViewById(R.id.basicButton);
        mViewButton = (Button) view.findViewById(R.id.viewButton);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBasicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeFragment(new BasicFragment(), true);
            }
        });
        mViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeFragment(new ExoPlayerViewFragment(), true);
            }
        });
    }

}
