package io.gresse.hugo.simpleexoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * A simple surfaceView that set the correct size depending of the video ratio
 *
 * Created by Hugo Gresse on 02/03/15.
 */
public class AspectRatioSurfaceView extends SurfaceView implements VideoSurfaceInterface {

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

    private boolean mIsSurfaceCreated = false;

    public AspectRatioSurfaceView(Context context) {
        super(context);
    }

    public AspectRatioSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public boolean isSurfaceCreated() {
        return mIsSurfaceCreated;
    }

    public void setSurfaceCreated(boolean isSurfaceCreated) {
        mIsSurfaceCreated = isSurfaceCreated;
    }

    @Override
    public void setVideoWidthHeightRatio(float widthHeightRatio) {
        if (mVideoAspectRatio != widthHeightRatio) {
            mVideoAspectRatio = widthHeightRatio;
            requestLayout();
        }
    }
}
