package io.gresse.hugo.simpleexoplayer.player;

import android.content.Context;

import io.gresse.hugo.simpleexoplayer.MediaFile;

/**
 * A factory for player
 *
 * Created by Hugo Gresse on 24/02/15.
 */
public class VideoPlayerFactory {

    public static VideoPlayer buildVideoPlayer(Context context,
                                                   MediaFile mediaFile,
                                                   VideoPlayerListener listener){
        return new DynamicExoPlayer(
                context,
                mediaFile,
                listener);

    }

}
