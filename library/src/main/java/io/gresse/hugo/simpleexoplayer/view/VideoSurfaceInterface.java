package io.gresse.hugo.simpleexoplayer.view;

/**
 * A global interface that all view using surface should use
 *
 * Created by Hugo Gresse on 14/08/15.
 */
public interface VideoSurfaceInterface {

    /**
     * Set the aspect ratio that this {@link tv.teads.sdk.adContent.ui.player.TeadsTextureView} should satisfy.
     *
     * @param widthHeightRatio The width to height ratio.
     */
    void setVideoWidthHeightRatio(float widthHeightRatio);
}
