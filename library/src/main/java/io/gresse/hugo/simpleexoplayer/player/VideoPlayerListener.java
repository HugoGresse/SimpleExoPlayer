package io.gresse.hugo.simpleexoplayer.player;

/**
 * Created by Hugo Gresse on 18/11/2014.
 */
public interface VideoPlayerListener {

    /**
     * When the video has started buffering and is ready to begin to play
     * more information : http://google.github.io/ExoPlayer/doc/reference/com/google/android/exoplayer/ExoPlayer.html
     */
    void nativeVideoPlayerIsLoaded();
    void nativeVideoPlayerViewAttached();
    void nativeVideoPlayerOnError(Exception e);

    void nativeVideoPlayerWillStartPlaying();
    void nativeVideoPlayerDidStartPlaying();

    void nativeVideoPlayerDidTouch(boolean isBackground);

    void nativeVideoPlayerSurfaceDestroyedShouldPause();
    void nativeVideoPlayerFinishPlaying();

    void nativeVideoPlayerPublishProgress(long milliSecond);

    void nativeVideoPlayerWillChangeFullscreenMode();
}
