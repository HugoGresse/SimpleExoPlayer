package io.gresse.hugo.simpleexoplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.gresse.hugo.simpleexoplayer.MediaFile;
import io.gresse.hugo.simpleexoplayer.player.SimpleExoPlayer;
import io.gresse.hugo.simpleexoplayer.player.SimpleExoPlayerListener;


/**
 * Default usage of the SImpleExoplayer
 * <p/>
 * Created by Hugo Gresse on 01/04/16.
 */
public class BasicFragment extends Fragment implements SimpleExoPlayerListener {

    private static final String TAG = BasicFragment.class.getSimpleName();

    private SimpleExoPlayer mSimpleExoPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic, container, false);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MediaFile mediaFile = new MediaFile("http://hugo.gresse.io/teads/norway.mp4");

        mediaFile.type = "video/mp4";

        mSimpleExoPlayer = new SimpleExoPlayer(getContext(), mediaFile, this);

        mSimpleExoPlayer.attach(getContext(), (ViewGroup) view, R.layout.player_layout, R.id.textureView);
        mSimpleExoPlayer.init();
        mSimpleExoPlayer.preLoad();
        mSimpleExoPlayer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mSimpleExoPlayer.isPlaying()){
            mSimpleExoPlayer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSimpleExoPlayer.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSimpleExoPlayer.release();
    }

    /***************************
     * Implement SimpleExoPlayerListener
     ***************************/

    @Override
    public void playerIsLoaded() {
        Log.d(TAG, "playerIsLoaded");
    }

    @Override
    public void playerViewAttached() {
        Log.d(TAG, "playerViewAttached");

    }

    @Override
    public void playerError(Exception e) {
        Log.e(TAG, "playerError", e);
    }

    @Override
    public void playerWillStartPlaying() {
        Log.d(TAG, "playerWillStartPlaying");

    }

    @Override
    public void playerStartPlaying() {
        Log.d(TAG, "playerStartPlaying");

    }

    @Override
    public void playerTouch(boolean isBackground) {
        Log.d(TAG, "playerTouch: " + isBackground);

    }

    @Override
    public void playerSurfaceDestroyedShouldPause() {
        Log.d(TAG, "playerplayerSurfaceDestroyedShouldPauseIsLoaded");

    }

    @Override
    public void playerFinishPlaying() {
        Log.d(TAG, "playerFinishPlaying");

    }

    @Override
    public void playerPublishProgress(long milliSecond) {
        Log.d(TAG, "playerPublishProgress: " + milliSecond);

    }
}
