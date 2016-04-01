package io.gresse.hugo.simpleexoplayer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * A view that wrap inner Exoplayer to be very simple to use
 *
 * Created by Hugo Gresse on 01/04/16.
 */
public class ExoplayerView extends FrameLayout {

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
        // TODO
    }
}
