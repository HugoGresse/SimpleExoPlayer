package io.gresse.hugo.simpleexoplayer.player;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Generic interface for video player. Implement this to create your own player.
 * <p/>
 * Created by Hugo Gresse on 24/02/15.
 */
public interface VideoPlayer {

    int SOUND_FADE_PER_SECOND = 1000/30; // change value very 33ms to have a 30fps on sound

    /**
     * Init player
     */
    void init();

    /**
     * Called when surface has changed (entering a new activity with a new layout eg). This will
     * attach the surface contained inside the viewGroup to the player. Also setting additional
     * listener on the given view.
     *
     * @param context   current app context
     * @param viewGroup viewGroup containing the surface
     */
    void attach(Context context, ViewGroup viewGroup);

    /**
     * Update video width/height ratio
     */
    void updateVideoRatio();

    /**
     *
     * @param width
     * @param height
     * @param pixelRatio
     */
    /**
     * Internal called when video size changed
     *
     * @param width video width
     * @param height video height
     * @param unappliedRotationDegrees rotationDegrees
     * @param pixelRatio (optional) pixel ratio
     */
    void onVideoSizeChanged(int width, int height,  int unappliedRotationDegrees, float pixelRatio);

    /**
     * Pre load the video to begin buffering while video is not started
     */
    void preLoad();

    /**
     * Start or resume.
     */
    void start();

    /**
     * Pause the player
     */
    void pause();

    /**
     * Restart the player to the first frame
     */
    void restart();

    /**
     * Mute current video
     */
    void mute();

    /**
     * Mute current video
     *
     * @param transitionDuration the transition duration, not used currently
     */
    void mute(int transitionDuration);

    /**
     * Unmute current video
     */
    void unMute();

    /**
     * Unmute ucrrent video
     * @param transitionDuration the transition duration
     *
     */
    void unMute(int transitionDuration);

    /**
     * Set the player soundVolume
     *
     * @param volume the desired volume, from 0.0g to 1.0f, 1 is 100% volume
     */
    void setSoundVolume(float volume);

    /**
     * Get the player sound volume, from 0.0f to 1.0f
     *
     * @return the sound volume
     */
    float getSoundVolume();

    /**
     * Release the player by clearing all collection, release player. The player should not be used
     * after this. To check if the player has been released, call {@link #isReleased()}
     */
    void release();

    /**
     * Check if player has been released
     *
     * @return true if released, false either
     */
    boolean isReleased();

    /**
     * Check if player is playing a video
     *
     * @return true if is playing, false otherweise
     */
    boolean isPlaying();

    /**
     * Check if the player will resume or start as soon as the player is available
     *
     * @return true if it will play when available
     */
    boolean isAutoPlay();

    /**
     * Check if player is playing sound
     *
     * @return true if no sound playing (muted), false otherweise
     */
    boolean isMuted();

    /**
     * Check if the video is in fullscreen.
     *
     * @return true if in fullscreen, false otherweise
     */
    boolean isFullscreen();

    /**
     * Get current video duration
     *
     * @return video duration
     */
    long getDuration();

    /**
     * Register a new player listener to be notified by player event.
     * See {@link VideoPlayerListener}
     */
    void addPlayerListener(VideoPlayerListener listener);

    /**
     * Unregister athe given listener.
     * See {@link #addPlayerListener(VideoPlayerListener)}
     * See {@link VideoPlayerListener}
     */
    void removePlayerListener(VideoPlayerListener listener);

}
