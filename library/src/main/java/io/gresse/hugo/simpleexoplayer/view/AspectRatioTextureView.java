package io.gresse.hugo.simpleexoplayer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * A simple AspectRatioTextureView that set it's size depending of video ratio
 *
 * Created by Hugo Gresse on 26/02/15.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AspectRatioTextureView extends TextureView implements TextureView.SurfaceTextureListener, VideoSurfaceInterface {

    public static final int LASTSTATE_AVAILABLE = 0;
    public static final int LASTSTATE_SIZECHANGED = 1;
    public static final int LASTSTATE_DESTROYED = 2;
    public static final int LASTSTATE_UPDATED = 3;

    /**
     * The {@link AspectRatioTextureView} will not resize itself if the fractional difference between its natural
     * aspect ratio and the requested aspect ratio falls below this threshold.
     * <p>
     * This tolerance allows the view to occupy the whole of the screen when the requested aspect
     * ratio is very close, but not exactly equal to, the aspect ratio of the screen. This may reduce
     * the number of view layers that need to be composited by the underlying system, which can help
     * to reduce power consumption.
     */
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;

    private float mVideoAspectRatio;

    /**
     * External listener to we can save is view has been created or not
     */
    @Nullable
    private SurfaceTextureListener mExternalListener;

    private int mLastState;
    private SurfaceTexture mLastSurfaceTexture;
    public boolean mSurfaceAvailable;

    public AspectRatioTextureView(Context context) {
        super(context);
        setInternalListener();
    }

    public AspectRatioTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInternalListener();
    }

    public AspectRatioTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInternalListener();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setInternalListener();
    }

    private void setInternalListener(){
        super.setSurfaceTextureListener(this);
    }

    /**
     * Set the aspect ratio that this {@link AspectRatioTextureView} should satisfy.
     *
     * @param widthHeightRatio The width to height ratio.
     */
    @Override
    public void setVideoWidthHeightRatio(float widthHeightRatio) {
        if (mVideoAspectRatio != widthHeightRatio) {
            mVideoAspectRatio = widthHeightRatio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mVideoAspectRatio == 0) {
            // Aspect ratio not set.
            return;
        }

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float viewAspectRatio = (float) width / height;
        float aspectDeformation = mVideoAspectRatio / viewAspectRatio - 1;
        if (Math.abs(aspectDeformation) <= MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
            // We're within the allowed tolerance.
            return;
        }

        if (aspectDeformation > 0) {
            height = (int) (width / mVideoAspectRatio);
        } else {
            width = (int) (height * mVideoAspectRatio);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    public void setSurfaceTexture(SurfaceTexture surfaceTexture){
        super.setSurfaceTexture(surfaceTexture);
        mSurfaceAvailable = surfaceTexture != null;
    }

    /**
     * Returns the {@link android.view.TextureView.SurfaceTextureListener} currently associated with this
     * texture view.
     *
     * @see #setSurfaceTextureListener(android.view.TextureView.SurfaceTextureListener)
     * @see android.view.TextureView.SurfaceTextureListener
     */
    @Override
    public SurfaceTextureListener getSurfaceTextureListener() {
        return mExternalListener;
    }

    /**
     * Sets the {@link android.view.TextureView.SurfaceTextureListener} used to listen to surface
     * texture events.
     *
     * @see #getSurfaceTextureListener()
     * @see android.view.TextureView.SurfaceTextureListener
     */
    @Override
    public void setSurfaceTextureListener(SurfaceTextureListener listener) {
        if(mExternalListener == null){
            // No listener set before, call last TextureView.SurfaceTextureListener callbacks directly on it

            switch (mLastState){
                case LASTSTATE_AVAILABLE:
                    listener.onSurfaceTextureAvailable(mLastSurfaceTexture, 0, 0);
                    break;
                case LASTSTATE_SIZECHANGED:
                    listener.onSurfaceTextureSizeChanged(mLastSurfaceTexture, 0, 0);
                    break;
                case LASTSTATE_DESTROYED:
                    listener.onSurfaceTextureDestroyed(mLastSurfaceTexture);
                    break;
                case LASTSTATE_UPDATED:
                    listener.onSurfaceTextureUpdated(mLastSurfaceTexture);
                    break;
            }
        }

        mExternalListener = listener;

    }


    /*----------------------------------------
     * TextureView.SurfaceTextureListener
     */

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mLastState = LASTSTATE_AVAILABLE;
        mSurfaceAvailable = true;
        mLastSurfaceTexture = surface;
        if (mExternalListener != null) {
            mExternalListener.onSurfaceTextureAvailable(surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mLastState = LASTSTATE_SIZECHANGED;
        if (mExternalListener != null) {
            mExternalListener.onSurfaceTextureSizeChanged(surface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mLastState = LASTSTATE_DESTROYED;
        mSurfaceAvailable = false;
        if (mExternalListener != null) {
            return mExternalListener.onSurfaceTextureDestroyed(surface);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        mLastState = LASTSTATE_UPDATED;
        if (mExternalListener != null) {
            mExternalListener.onSurfaceTextureUpdated(surface);
        }
    }
}
