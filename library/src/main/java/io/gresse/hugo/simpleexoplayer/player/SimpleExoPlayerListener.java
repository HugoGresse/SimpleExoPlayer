package io.gresse.hugo.simpleexoplayer.player;

/**
 * A player listener interface for {@link SimpleExoPlayer}
 *
 * Created by Hugo Gresse on 18/11/2014.
 */
public interface SimpleExoPlayerListener {

    /**
     * When the video has started buffering and is ready to begin to play
     * more information : http://google.github.io/ExoPlayer/doc/reference/com/google/android/exoplayer/ExoPlayer.html
     */
    void playerIsLoaded();
    void playerViewAttached();
    void playerError(Exception e);

    void playerWillStartPlaying();
    void playerStartPlaying();

    void playerTouch(boolean isBackground);

    void playerSurfaceDestroyedShouldPause();
    void playerFinishPlaying();

    void playerPublishProgress(long milliSecond);
}
