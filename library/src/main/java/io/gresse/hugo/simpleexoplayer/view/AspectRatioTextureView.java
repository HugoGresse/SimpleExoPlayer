package io.gresse.hugo.simpleexoplayer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * A simple AspectRatioTextureView that set it's size depending of video ratio
 *
 * Created by Hugo Gresse on 26/02/15.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AspectRatioTextureView extends TextureView implements TextureView.SurfaceTextureListener, VideoSurfaceInterface {

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
    private SurfaceTextureListener mExternalListener;

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
        mExternalListener = listener;
    }


    /*----------------------------------------
     * TextureView.SurfaceTextureListener
     */

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceAvailable = true;
        mExternalListener.onSurfaceTextureAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mExternalListener.onSurfaceTextureSizeChanged(surface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurfaceAvailable = false;
        return mExternalListener.onSurfaceTextureDestroyed(surface);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        mExternalListener.onSurfaceTextureUpdated(surface);
    }
}
