package io.gresse.hugo.simpleexoplayer.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import io.gresse.hugo.simpleexoplayer.view.AspectRatioTextureView;
import io.gresse.hugo.simpleexoplayer.MediaFile;
import io.gresse.hugo.simpleexoplayer.R;

/**
 * Extends ExoPlayer using a TextureView. The playing keep the same surface to draw onto during all
 * player lifetime.
 * <p/>
 * Created by Hugo Gresse on 02/03/15.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DynamicExoPlayer extends SimpleExoPlayer implements TextureView.SurfaceTextureListener {

    public static final String LOG_TAG = DynamicExoPlayer.class.getSimpleName();

    protected AspectRatioTextureView mTextureView;
    protected SurfaceTexture mSavedSurfaceTexture;
    protected boolean mRequestNewAttach;
    protected boolean mLastTextureDestroyed;
    protected boolean mAllowPlayInBackground = true;

    public DynamicExoPlayer(Context context, MediaFile mediaFile, VideoPlayerListener nativeVideoPlayerListener) {
        super(context, mediaFile, nativeVideoPlayerListener);
    }


    @Override
    public void attach(Context context, ViewGroup viewGroup) {

        if (mTextureView != null) {
            Log.d(LOG_TAG, "attach: removeTextureView");
            ViewGroup parent = (ViewGroup) mTextureView.getParent();
            if (parent != null) {
                parent.getLayoutParams().width = parent.getWidth();
                parent.removeView(mTextureView);
            }
        }

        mContext = context;
        mRootViewGroup = viewGroup;

        if (mRootViewGroup == null) {
            new NullPointerException("Trying to attach a null view, aborting now").printStackTrace();
            return;
        }

        mVideoContainerView =
                (ViewGroup) mRootViewGroup.findViewById(R.id.sep_VideoContainerFrameLayout);
        mRootViewGroup.setOnTouchListener(this);


        // SurfaceView
        AspectRatioTextureView textureView =
                (AspectRatioTextureView) mRootViewGroup.findViewById(R.id.sep_VideoSurfaceLayout);

        if (textureView == null) {
            if (mTextureView != null) {
                Log.d(LOG_TAG, "attach: Restoring last TextureView");
                mVideoContainerView.addView(mTextureView);
                mVideoContainerView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                Log.d(LOG_TAG, "attach: Creating a TextureView");
                // When release after fullscreen finished/skip, the view is not added in normal
                // layout (eg).
                LayoutInflater layoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mTextureView = (AspectRatioTextureView) layoutInflater.inflate(R.layout.layout_textureview, mRootViewGroup, false);


                mVideoContainerView.addView(mTextureView);
            }

            mVideoContainerView.requestLayout();
        } else {
            Log.d(LOG_TAG, "attach: Retrieve TextureView from view");
            mTextureView = textureView;
        }

        ((ViewGroup) mTextureView.getParent()).setBackgroundColor(Color.BLACK);

        mTextureView.setSurfaceTextureListener(this);

        if (mTextureView.mSurfaceAvailable) {
            Log.d(LOG_TAG, "attach: Surface available : attachSurface");
            mSavedSurfaceTexture = mTextureView.getSurfaceTexture();
            attachSurfaceAndInit(mSavedSurfaceTexture);
        } else if (mSavedSurfaceTexture != null && mLastTextureDestroyed) {
            Log.d(LOG_TAG, "attach: Surface not available : set surface to TextureView");
            mTextureView.setSurfaceTexture(mSavedSurfaceTexture);
        } else {
            Log.d(LOG_TAG, "attach: mRequestNewAttach");
            mRequestNewAttach = true;
        }

//        mVideoWidthHeightRatio = (float) NumberUtils.round((float) 16 / 9, 3);
        if (mVideoWidthHeightRatio != 0) {
            updateVideoRatio();
        }

        if (mAutoPlay) {
            Log.d(LOG_TAG, "AutoPlay after attach called");
            maybeStartPlayback();
        }
    }

    /**
     * Update video width/height ratio
     */
    @Override
    public void updateVideoRatio() {
        mTextureView.setVideoWidthHeightRatio(mVideoWidthHeightRatio);
    }

    /**
     * Release the player by clearing all collection, release player. The player should not be used
     * after this. To check if the player has been released, call {@link #isReleased()}
     */
    @Override
    public void release() {
        super.release();

        if (mTextureView != null && mTextureView.getSurfaceTexture() != null) {
            clearSurface(mTextureView.getSurfaceTexture());
        }

        mRequestNewAttach = mLastTextureDestroyed = false;

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelRatio) {
        super.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelRatio);
        updateVideoRatio();
    }

    /*----------------------------------------
    * Protected Methods
    */

    protected void maybeStartPlayback() {

        if (mTextureView == null || mTextureView.getSurfaceTexture() == null && mSavedSurfaceTexture != null) {
            Log.d(LOG_TAG, "maybeStartPlayback mRequestNewAttach true : is attaching surface");
            mAutoPlay = true;
            return;
        }

        if (!mHasStartedOnce) {
            mHasStartedOnce = true;
            for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                listener.nativeVideoPlayerDidStartPlaying();
            }
        }

        if (mSeekHandler == null) {
            startPlayerTimeListener();
        }

        if (!mTextureView.mSurfaceAvailable && mSavedSurfaceTexture == null && !mAllowPlayInBackground) {
            Log.d(LOG_TAG, "Surface not available, format not allowed to play in background, cannot proceed");
            mAutoPlay = true;
            return;
        }

        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(true);
        }

        mAutoPlay = false;
    }

    /**
     * Attach a valid SurfaceTexture to the player
     *
     * @param surfaceTexture the surface to attach to the player
     */
    protected void attachSurfaceAndInit(SurfaceTexture surfaceTexture) {
        Log.d(LOG_TAG, "attachSurfaceAndInit willAutoPlay ? " + mAutoPlay);

        if (mPlayer != null) {
            mRequestNewAttach = false;
            mPlayer.setSurface(new Surface(surfaceTexture));
            if (mAutoPlay) {
                start();
            }
        }
    }


    /*----------------------------------------
    * TextureView.SurfaceTextureListener
    */

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.d(LOG_TAG, "onSurfaceTextureAvailable size=" + width + "x" + height + ", st=" + surfaceTexture);

        mSavedSurfaceTexture = surfaceTexture;

        if (mNativeVideoPlayerListenerList != null) {
            for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                listener.nativeVideoPlayerViewAttached();
            }
        }

        attachSurfaceAndInit(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(LOG_TAG, "onSurfaceTextureDestroyed");

        mLastTextureDestroyed = true;

        if (mSavedSurfaceTexture != null && mRequestNewAttach) {
            mTextureView.setSurfaceTexture(mSavedSurfaceTexture);
            mRequestNewAttach = false;
            if (mAutoPlay) {
                Log.d(LOG_TAG, "call start after previous surface is destroy and new attached");
                start();
            }
        } else if (isPlaying()) {
            Log.d(LOG_TAG, "onSurfaceTextureDestroyed Pause player");

            if (mNativeVideoPlayerListenerList != null && !isReleased()) {
                for (VideoPlayerListener listener : mNativeVideoPlayerListenerList) {
                    listener.nativeVideoPlayerSurfaceDestroyedShouldPause();
                }
            }
            pause();
        }

        return (mSavedSurfaceTexture == null);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * Clear the given surface Texture by attachign a GL context and clearing the surface.
     *
     * @param texture a valid SurfaceTexture
     */
    private void clearSurface(SurfaceTexture texture) {
        if (texture == null) {
            return;
        }

        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, null);

        int[] attribList = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, EGL10.EGL_WINDOW_BIT,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        egl.eglChooseConfig(display, attribList, configs, configs.length, numConfigs);
        EGLConfig config = configs[0];
        EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, new int[]{
                12440, 2,
                EGL10.EGL_NONE
        });
        EGLSurface eglSurface = egl.eglCreateWindowSurface(display, config, texture,
                new int[]{
                        EGL10.EGL_NONE
                });

        egl.eglMakeCurrent(display, eglSurface, eglSurface, context);
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        egl.eglSwapBuffers(display, eglSurface);
        egl.eglDestroySurface(display, eglSurface);
        egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroyContext(display, context);
        egl.eglTerminate(display);
    }


}
