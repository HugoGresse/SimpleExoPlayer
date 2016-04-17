package io.gresse.hugo.simpleexoplayer.util;

import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.MimeTypeMap;

/**
 * General utilisaty related to image and views
 *
 * Created by Hugo Gresse on 17/11/2014.
 */
public class Utils {

    public static final String LOG_TAG = "ViewUtils";

    /**
     * Determines if given points are inside view
     * @param x - x coordinate of point
     * @param y - y coordinate of point
     * @param view - view object to compare
     * @return true if the points are within view bounds, false otherwise
     */
    @SuppressWarnings("RedundantIfStatement")
    public static boolean isPointInsideView(float x, float y, View view){
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if(( x > viewX && x < (viewX + view.getWidth())) &&
                ( y > viewY && y < (viewY + view.getHeight()))){
            return true;
        }

        return false;
    }

    /**
     * Get the mime type of the given file based on the extension
     *
     * @param url the url
     * @return the mime type like "video/mp4"
     */
    @Nullable
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
