package io.gresse.hugo.simpleexoplayer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import io.gresse.hugo.simpleexoplayer.MediaFile;
import io.gresse.hugo.simpleexoplayer.R;
import io.gresse.hugo.simpleexoplayer.player.SimpleExoPlayer;
import io.gresse.hugo.simpleexoplayer.player.SimpleExoPlayerListener;
import io.gresse.hugo.simpleexoplayer.util.Utils;

/**
 * A view that wrap inner Exoplayer to be very simple to use
 *
 * Created by Hugo Gresse on 01/04/16.
 */
public class ExoplayerView extends AspectRatioTextureView {

    public static final String TAG = ExoplayerView.class.getSimpleName();

    public static final String BUNDLE_STATE_PLAYING = "playing";

    private SimpleExoPlayer         mSimpleExoPlayer;
    private MediaFile               mMediaFile;
    private boolean                 mAutoPlay;
    private boolean                 mPlayInBackground;
    private boolean                 mPreLoad;
    @Nullable
    private SimpleExoPlayerListener mSimpleExoPlayerListener;

    public ExoplayerView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public ExoplayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public ExoplayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExoplayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExoplayerView, defStyleAttr, 0);
        mAutoPlay = a.getBoolean(R.styleable.ExoplayerView_autoPlay, true);
        mPreLoad = a.getBoolean(R.styleable.ExoplayerView_preload, true);
        mPlayInBackground = a.getBoolean(R.styleable.ExoplayerView_playInBackground, false);
        String videoUrl = a.getString(R.styleable.ExoplayerView_videoUrl);
        a.recycle();

        if(!TextUtils.isEmpty(videoUrl)){
            setVideoUrl(videoUrl);
        }

        createPlayer();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.i(TAG, "onAttachedToWindow");

        if(mSimpleExoPlayer == null){
            createPlayer();
        }


        mSimpleExoPlayer.attach(getContext(), this, 0, getId());

        if(mAutoPlay && !mSimpleExoPlayer.isAutoPlay()){
            mSimpleExoPlayer.start();
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.i(TAG, "onSaveInstanceState");

        Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean(BUNDLE_STATE_PLAYING, mSimpleExoPlayer.isPlaying());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.i(TAG, "onRestoreInstanceState");
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            boolean wasPlaying = bundle.getBoolean(BUNDLE_STATE_PLAYING);

            if(mSimpleExoPlayer == null){
                createPlayer();
            }

            if(wasPlaying){
                mSimpleExoPlayer.start();
            }

            state = bundle.getParcelable("instanceState");
        }

        super.onRestoreInstanceState(state);
    }
    /***************************
     * Getter/Setter
     ***************************/

    /**
     * Get the current video url
     * @return the video url
     */
    @Nullable
    public String getVideoUrl(){
        return mMediaFile.mediaFileURL;
    }

    /**
     * Set the video url to load and get the correct mime type from it
     *
     * @param videoUrl the video url, local or remote
     */
    public void setVideoUrl(@NonNull String videoUrl){
        mMediaFile = new MediaFile(videoUrl);
        mMediaFile.type = Utils.getMimeType(videoUrl);
    }

    /***************************
     * Private methods
     ***************************/

    /**
     * Create the player based on current class members
     */
    private void createPlayer(){
        if(mMediaFile == null){
            throw new NullPointerException("No video to play");
        }

        mSimpleExoPlayer = new SimpleExoPlayer(getContext(), mMediaFile, mSimpleExoPlayerListener);
        mSimpleExoPlayer.init();

        if(mPreLoad){
            mSimpleExoPlayer.preLoad();
        }

        if(mPlayInBackground){
            mSimpleExoPlayer.setAllowPlayInBackground(true);
        }
    }
}
