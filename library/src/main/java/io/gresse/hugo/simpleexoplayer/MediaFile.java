package io.gresse.hugo.simpleexoplayer;

import android.net.Uri;

/**
 * A media File
 *
 * @hide
 */
public class MediaFile {

    public String mediaFileURL;
    public String id;
    public String delivery;
    public String type;
    public int bitrate;
    public int width;
    public int height;
    public boolean scalable;
    public boolean maintainAspectRatio;
    public String apiFramework;


    public MediaFile(String mediaFileURL) {
        this.mediaFileURL = mediaFileURL;
    }

    public MediaFile() {

    }

    public Uri getMediaFileURI() {
        return Uri.parse(mediaFileURL);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
