package io.gresse.hugo.simpleexoplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * General utilisaty related to image and views
 *
 * Created by Hugo Gresse on 17/11/2014.
 */
public class ViewUtils {

    public static final String LOG_TAG = "ViewUtils";

    /**
     * Convert given dp to pixel
     * @param context app context
     * @param dp density pixel
     * @return the value od dp in Pixel format
     */
    @SuppressWarnings("unused")
    public static int dpToPixel(Context context, int dp){
        float d = context.getResources().getDisplayMetrics().density;
        return (int)(dp * d); // margin in pixels
    }

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
     * Set background image to given view manaing android version
     * @param view the view to set background on
     * @param drawable the drawable to display in background
     */
    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable drawable){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN){
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Set background image to given view from a bitmap
     * @param view the view to set background
     * @param bitmap image to be in background
     */
    @SuppressWarnings("deprecation, unused")
    public static void setBackground(View view, Bitmap bitmap){
        BitmapDrawable drawable;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            drawable = new BitmapDrawable(bitmap);
        } else {
            drawable = new BitmapDrawable(view.getContext().getResources(), bitmap);
        }

        ViewUtils.setBackground(view, drawable);
    }
}
