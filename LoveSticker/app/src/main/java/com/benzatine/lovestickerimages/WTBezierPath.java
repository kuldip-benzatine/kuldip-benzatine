package com.benzatine.lovestickerimages;

import android.graphics.Path;

/**
 * Extend Path for store extra properties.
 *
 * Created by WaterZhang on 11/26/15.
 */
public class WTBezierPath extends Path {
    /**
     * Path stroke color, each path can have it's own stroke color.
     */
    protected int strokeColor;

    /**
     * Path stroke width, each path can have it's own stroke width.
     */
    protected float strokeWidth;

    /**
     * Define if this path is a eraser.
     */
    protected boolean isEraser;

    /**
     * Define if this path is a circle.
     */
    protected boolean isCircle;

}
