package io.gresse.hugo.simpleexoplayer;

import android.net.Uri;

/**
 * A media File
 */
public class MediaFile {

    public String mediaFileURL;
    public String id;

    /**
     * The mime type such as:
     * - video/mp4
     * - video/webm
     */
    public String type;
    public int width;
    public int height;
    public boolean maintainAspectRatio;


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
