package io.gresse.hugo.simpleexoplayer.player;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.Util;

import java.util.concurrent.CopyOnWriteArrayList;

import io.gresse.hugo.simpleexoplayer.MediaFile;
import io.gresse.hugo.simpleexoplayer.player.base.DemoPlayer;
import io.gresse.hugo.simpleexoplayer.player.base.EventLogger;
import io.gresse.hugo.simpleexoplayer.player.base.ExtractorRendererBuilder;
import io.gresse.hugo.simpleexoplayer.util.ViewUtils;

/**
 * Based on Exoplayer FullPlayerActivity
 * <p/>
 * When the surface is destroyed, for example when the user switch app, the video is paused by
 * blocking surface.
 * <p/>
 * <p/>
 * Created by Hugo Gresse on 17/11/2014.
 */
public abstract class SimpleExoPlayer implements VideoPlayer, DemoPlayer.Listener, View.OnTouchListener {

    private static final String LOG_TAG = "SimpleExoPlayer";

    protected Context mContext;

    /**
     * Note that only the first listener will received publishProgress event
     */
    protected CopyOnWriteArrayList<VideoPlayerListener> mNativeVideoPlayerListenerList;

    protected EventLogger mEventLogger;
    protected MediaFile mMediaFile;

    protected DemoPlayer mPlayer;
    protected float mVideoWidthHeightRatio;


    protected Handler mSeekHandler;
    protected long mLastPosition;
    protected long mPlayerPosition;

    // The sound volume while not muted
    protected float mSoundVolume;
    protected CountDownTimer mSoundtransitionTimer;

    protected ViewGroup mRootViewGroup;
    protected ViewGroup mVideoContainerView;

    protected boolean mAutoPlay = false;
    protected boolean mIsReady = false;
    protected boolean mIsMute = false;
    protected boolean mRatioAlreadyCalculated = false;
    protected boolean mHasStartedOnce = false;


    public SimpleExoPlayer(Context context,
                          MediaFile mediaFile,
                          VideoPlayerListener nativeVideoPlayerListener) {
        mContext = context;
        mMediaFile = mediaFile;
        mNativeVideoPlayerListenerList = new CopyOnWriteArrayList<>();
        mNativeVideoPlayerListenerList.add(nativeVideoPlayerListener);
    }

    /**
     * Init player
     */
    @Override
    public void init() {
        if (mPlayer == null) {
            mPlayer = new DemoPlayer(getRendererBuilder());
            mPlayer.addListener(this);
            mPlayer.seekTo(mPlayerPosition);
            try {
                mEventLogger = new EventLogger();
                mEventLogger.startSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer.addListener(mEventLogger);
            mPlayer.setInfoListener(mEventLogger);
            mPlayer.setInternalErrorListener(mEventLogger);
        }
    }

    /**
     * Called when surface has changed (entering a new activity with a new layout eg). This will
     * attach the surface contained inside the viewGroup to the player. Also setting additional
     * listener on the given view.
     *
     * @param context   current app context
     * @param viewGroup viewGroup containing the surface
     */
    @Override
    public abstract void attach(Context context, ViewGroup viewGroup);

    /**
     * Update video width/height ratio
     */
    @Override
    public abstract void updateVideoRatio();

    /**
     * @param width                    video width
     * @param height                   video width
     * @param unappliedRotationDegrees ?
     * @param pixelWidthHeightRatio    (optional) pixel ratio
     */
    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        // originaly, ratio was calculated from video. Not anymore : one received and if ratio is
        // diferent, update it

        // mVideoWidthHeightRatio = height == 0 ? 1 : (float) width / height;
        if (!mRatioAlreadyCalculated && mVideoWidthHeightRatio != (float) width / height) {
            mVideoWidthHeightRatio = ((float) width / height) * pixelWidthHeightRatio;
            mRatioAlreadyCalculated = true;
        }
        // setVideoWidthHeightRatio();
    }

    /**
     * Pre load the video to begin buffering while video is not started
     */
    @Override
    public void preLoad() {
        mPlayer.prepare();
    }

    /**
     * Start or resume.
     */
    @Override
    public void start() {
        mAutoPlay = true;
        maybeStartPlayback();
    }

    /**
     * Pause the player
     */
    @Override
    public void pause() {
        mAutoPlay = false;
        if (!isReleased()) {
            mPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * Mute current video
     */
    @Override
    public void mute() {
        mute(0);
    }

    @Override
    public void mute(int transitionDuration) {
        mIsMute = true;

        if (mSoundtransitionTimer != null) {
            mSoundtransitionTimer.cancel();
        }

        if (mPlayer != null) {
            mPlayer.setVolume(0f);
        }
    }

    /**
     * Unmute current video
     */
    @Override
    public void unMute() {
        unMute(0);
    }

    @Override
    public void unMute(int transitionDuration) {
        mIsMute = false;

        if (mPlayer != null) {
            if (transitionDuration <= 0) {
                mPlayer.setVolume(mSoundVolume);
            } else {
                int stepNumber = (transitionDuration / SOUND_FADE_PER_SECOND);

                // Calculate the volume added each time we increase it. We multiply it with 1.1 because the
                // countdowntimer can be executed a litle after the specified delay.
                final float step = (float) ((mSoundVolume / stepNumber) * 1.2);

                mSoundtransitionTimer = new CountDownTimer(transitionDuration, SOUND_FADE_PER_SECOND) {
                    float newVolume = 0;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Prevent the sound to be changed on a null mPlayer (after a release for example)
                        if (mPlayer == null) {
                            mSoundtransitionTimer.cancel();
                            return;
                        }

                        newVolume += step;

                        if (newVolume > mSoundVolume) {
                            return;
                        }

                        mPlayer.setVolume(newVolume);
                    }

                    @Override
                    public void onFinish() {
                        if (mPlayer != null) {
                            mPlayer.setVolume(mSoundVolume);
                        }
                    }
                }.start();
            }
        }
    }

    @Override
    public void restart() {
        mPlayer.seekTo(0);
        mHasStartedOnce = false;
        maybeStartPlayback();
    }

    @Override
    public void setSoundVolume(float volume) {
        mSoundVolume = volume;

        if (!mIsMute) {
            if (mPlayer != null) {
                mPlayer.setVolume(mSoundVolume);
            }
        }
    }

    @Override
    public float getSoundVolume() {
        return mSoundVolume;
    }

    /**
     * Release the player by clearing all collection, release player. The player should not be used
     * after this. To check if the player has been released, call {@link #isReleased()}
     */
    @Override
    public void release() {
        mHasStartedOnce = false;
        if (mPlayer != null) {
            Log.v(LOG_TAG, "release");
            mNativeVideoPlayerListenerList = null;
            if (mSoundtransitionTimer != null) {
                mSoundtransitionTimer.cancel();
            }
            mPlayerPosition = mPlayer.getCurrentPosition();
            mPlayer.removeListener(this);
            mPlayer.release();
            mPlayer = null;
            mEventLogger.endSession();
            mEventLogger = null;

        }

    }

    /**
     * Check if player has been released
     *
     * @return true if released, false either
     */
    @Override
    public boolean isReleased() {
        return mPlayer == null;
    }

    /**
     * Check if player is playing a video
     *
     * @return true if is playing, false otherweise
     */
    @Override
    public boolean isPlaying() {
        if (null != mPlayer) {
            return mPlayer.getPlayWhenReady();
        } else {
            return false;
        }
    }

    @Override
    public boolean isAutoPlay() {
        return mAutoPlay;
    }

    /**
     * Check if player is playing sound
     *
     * @return true if no sound playing (muted), false otherweise
     */
    @Override
    public boolean isMuted() {
        return mIsMute;
    }

    /**
     * Check if the video is in fullscreen.
     *
     * @return true if in fullscreen, false otherweise
     */
    @Override
    public boolean isFullscreen() {
        return false;
    }

    /**
     * Get current video duration
     *
     * @return video duration
     */
    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    /**
     * Register a new player listener to be notified by player event.
     * See {@link VideoPlayerListener}
     */
    @Override
    public void addPlayerListener(VideoPlayerListener listener) {
        if (mNativeVideoPlayerListenerList == null) {
            mNativeVideoPlayerListenerList = new CopyOnWriteArrayList<>();
        }

        mNativeVideoPlayerListenerList.add(listener);
    }

    /**
     * Unregister athe given listener.
     * See {@link #addPlayerListener(VideoPlayerListener)}
     * See {@link VideoPlayerListener}
     */
    @Override
    public void removePlayerListener(VideoPlayerListener listener) {
        if (mNativeVideoPlayerListenerList != null) {
            mNativeVideoPlayerListenerList.remove(listener);
        }
    }


    /*----------------------------------------
    * Protected Methods
    */

    protected DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(mContext, "ExoPlayerDemo");
        switch (mMediaFile.type) {
            case "video/mp4":
                return new ExtractorRendererBuilder(mContext, userAgent, mMediaFile.getMediaFileURI());
            case "video/webm":
                return new ExtractorRendererBuilder(mContext, userAgent, mMediaFile.getMediaFileURI());
            default:
                throw new IllegalStateException("Unsupported type: " + mMediaFile.type);
        }
    }


    protected abstract void maybeStartPlayback();

    /**
     * Check every second the current time of the player
     */
    protected void startPlayerTimeListener() {
        mSeekHandler = new Handler();
        final int delay = 500; //milliseconds

        mLastPosition = 0;

        mSeekHandler.postDelayed(new Runnable() {
            public void run() {

                if (mPlayer == null || mLastPosition == mPlayer.getCurrentPosition()) {
                    mSeekHandler.postDelayed(this, delay);
                    return;
                }

                mLastPosition = mPlayer.getCurrentPosition();

                if (mNativeVideoPlayerListenerList != null
                        && mNativeVideoPlayerListenerList.get(0) != null) {
                    mNativeVideoPlayerListenerList.get(0).nativeVideoPlayerPublishProgress(mPlayer.getCurrentPosition());
                }

                // rerun handler if next time to sent event is not the end
                mSeekHandler.postDelayed(this, delay);
            }
        }, delay);
    }

    /*----------------------------------------
    * FullPlayer.Listener
    */

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_READY:
                // prevent multiple isReady event sending by sending only the first one
                if (!mIsReady) {
                    mIsReady = true;
                    for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                        listener.nativeVideoPlayerIsLoaded();
                    }
                }
                break;
            case ExoPlayer.STATE_ENDED:
                Log.d(LOG_TAG, "State Ended");
                if (mNativeVideoPlayerListenerList != null) {
                    for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                        listener.nativeVideoPlayerFinishPlaying();
                    }
                }
                // prevent Player for sending more than one finish playing event

                break;
        }
        Log.d(LOG_TAG, "Player state change : " + playbackState);
    }

    @Override
    public void onError(Exception e) {
        Log.e(LOG_TAG, "Playback failed", e);
        if (mNativeVideoPlayerListenerList != null) {
            for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                listener.nativeVideoPlayerOnError(e);
            }
        }
        this.release();
    }


    /*----------------------------------------
    * Touch event
    */

    private boolean mIsNativeClick = false;
    private float mStartNativeX;
    private float mStartNativeY;
    private final float SCROLL_THRESHOLD = 10;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartNativeX = event.getX();
                mStartNativeY = event.getY();
                mIsNativeClick = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mIsNativeClick && (Math.abs(mStartNativeX - event.getX()) > SCROLL_THRESHOLD
                        || Math.abs(mStartNativeY - event.getY()) > SCROLL_THRESHOLD)) {
                    mIsNativeClick = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsNativeClick && !isReleased() && mNativeVideoPlayerListenerList != null) {

                    if (ViewUtils.isPointInsideView(event.getRawX(), event.getRawY(), mVideoContainerView)) {
                        Log.d(LOG_TAG, "nativeVideoPlayerDidTouch");
                        for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                            listener.nativeVideoPlayerDidTouch(false);
                        }
                    } else {
                        Log.d(LOG_TAG, "nativeVideoPlayerDidTouchBackground");
                        for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                            listener.nativeVideoPlayerDidTouch(true);
                        }
                    }

                    return true;
                }
        }

        return false;
    }

}