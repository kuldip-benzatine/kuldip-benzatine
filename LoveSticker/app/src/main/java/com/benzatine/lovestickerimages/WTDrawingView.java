package com.benzatine.lovestickerimages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;

import static com.benzatine.lovestickerimages.Edit_Image.IsOpenColorImage;

/**
 * WTDrawing
 * <p>
 * Created by Water Zhang on 11/25/15
 */
public class WTDrawingView extends View {

    // Drawing mode
    private static final int DRAW = 1;
    private static final int ERASER = 2;

    // Default config
//    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final float DEFAULT_STROKE_WIDTH = 5.0f;
    private static final float DEFAULT_ERASER_WIDTH = 20.0f;

    Context context;

    private boolean initialized;

    private Paint drawPaint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private Paint eraserPaint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private Canvas drawCanvas;
    private Bitmap drawBitmap;
    private Bitmap undoBitmap;

    private RectF dirtyRect;

    private WTBezierPath drawPath;
    private LinkedList<WTBezierPath> pathArray;

    private int drawingMode;
    private PointF[] points = new PointF[5];
    private int pointIndex;
    private int movedPointCount;

    private int strokeColor;

    /**
     * Stroke width, unit(dp)
     */
    private float strokeWidth;

    /**
     * Eraser width, unit(dp)
     */
    private float eraserWidth;

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        if (strokeColor != 0) {
            this.strokeColor = strokeColor;
            drawPaint.setColor(this.strokeColor);
        }
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth, getResources().getDisplayMetrics());
        drawPaint.setStrokeWidth(this.strokeWidth);
    }

    public float getEraserWidth() {
        return eraserWidth;
    }

    public void setEraserWidth(float eraserWidth) {
        this.eraserWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, eraserWidth, getResources().getDisplayMetrics());
        ;
        eraserPaint.setStrokeWidth(this.eraserWidth);
    }

    public void setEraserMode(boolean drawEraser) {
        drawingMode = drawEraser ? ERASER : DRAW;
    }

    public WTDrawingView(Context c, int width, int height) {
        this(c, null);
        init(width, height);
    }

    public WTDrawingView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
    }

    /**
     * Returns the bitmap of the drawing with the specified background color
     *
     * @param backgroundColor The background color for the bitmap
     * @return The bitmap
     */
    public Bitmap getBitmap(int backgroundColor) {
        if (drawBitmap != null && !drawBitmap.isRecycled()) {
            // create new bitmap
            Bitmap bitmap = Bitmap.createBitmap(drawBitmap.getWidth(), drawBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);

            // draw background if not transparent
            if (backgroundColor != 0) {
                bitmapCanvas.drawColor(backgroundColor);
            }

            // draw bitmap
            bitmapCanvas.drawBitmap(drawBitmap, 0, 0, null);

            return bitmap;
        }

        return null;
    }

    /**
     * Undo last drawing.
     */
    public void undo() {
        Paint emptyPaint = new Paint();
        emptyPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        drawCanvas.drawPaint(emptyPaint);

        if (!pathArray.isEmpty()) {
            pathArray.removeLast();
        }

        for (WTBezierPath path : pathArray) {
            drawPaint.setStyle(path.isCircle ? Paint.Style.FILL : Paint.Style.STROKE);
            if (path.isEraser) {
                drawCanvas.drawPath(path, eraserPaint);
            } else {
                drawPaint.setColor(path.strokeColor);
                drawPaint.setStrokeWidth(path.strokeWidth);
                drawCanvas.drawPath(path, drawPaint);
            }
        }

        // Restore paint
        drawPaint.setStrokeWidth(this.strokeWidth);
        drawPaint.setColor(this.strokeColor);
        eraserPaint.setStrokeWidth(this.eraserWidth);

        invalidate();
    }

    /**
     * Clear all drawings.
     */
    public void clear() {
        Paint emptyPaint = new Paint();
        emptyPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        drawCanvas.drawPaint(emptyPaint);

        pathArray.clear();
        if (drawPath != null) {
            drawPath.reset();
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (IsOpenColorImage) {
            if (Edit_Image.selectColor != 0) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    this.touchesBegan(new PointF(event.getX(), event.getY()));
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    this.touchesMoved(new PointF(event.getX(), event.getY()));
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    this.touchesEnded(new PointF(event.getX(), event.getY()));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (initialized) {
            canvas.drawBitmap(drawBitmap, 0, 0, null);
        } else {
            init(this.getWidth(), this.getHeight());
        }
    }

    private void init(int width, int height) {
        if (!initialized) {
            pathArray = new LinkedList<WTBezierPath>();

            drawingMode = DRAW;

            drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(drawBitmap);

            setStrokeWidth(DEFAULT_STROKE_WIDTH);
//            setStrokeColor(DEFAULT_STROKE_COLOR);
            setEraserWidth(DEFAULT_ERASER_WIDTH);

            drawPaint.setAntiAlias(true);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);

            eraserPaint.setAntiAlias(true);
            eraserPaint.setStyle(Paint.Style.STROKE);
            eraserPaint.setStrokeCap(Paint.Cap.ROUND);
            eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            dirtyRect = new RectF();

            initialized = true;
        }
    }

    private Paint currentPaint() {
        return drawingMode == ERASER ? eraserPaint : drawPaint;
    }

    private void touchesBegan(PointF p) {

        if (drawingMode != DRAW && drawingMode != ERASER) {
            drawingMode = DRAW;
        }

        movedPointCount = 0;
        pointIndex = 0;
        points[0] = p;

        drawPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStyle(Paint.Style.STROKE);

        drawPath = new WTBezierPath();
        drawPath.strokeColor = this.strokeColor;
        drawPath.strokeWidth = this.strokeWidth;
        drawPath.isEraser = drawingMode == ERASER;
    }

    private void touchesMoved(PointF p) {

        movedPointCount++;
        pointIndex++;
        points[pointIndex] = p;

        // We got 5 points here, now we can draw a bezier drawPath,
        // use 4 points to draw a bezier,and the last point is cached for next segment.
        if (pointIndex == 4) {
            points[3] = new PointF((points[2].x + points[4].x) / 2, (points[2].y + points[4].y) / 2);

            moveToPoint(points[0]);
            addCurveToPoint(points[3], points[1], points[2]);

            drawCanvas.drawPath(drawPath, currentPaint());

            // Calc dirty rect
            float pathWidth = currentPaint().getStrokeWidth();
            drawPath.computeBounds(dirtyRect, true);
            dirtyRect.left = dirtyRect.left - pathWidth;
            dirtyRect.top = dirtyRect.top - pathWidth;
            dirtyRect.right = dirtyRect.right + pathWidth;
            dirtyRect.bottom = dirtyRect.bottom + pathWidth;

            Rect invalidRect = new Rect();
            dirtyRect.round(invalidRect);
            invalidate(invalidRect);

            points[0] = points[3];
            points[1] = points[3]; // this is the "magic"
            points[2] = points[4];
            pointIndex = 2;
        }
    }

    private void touchesEnded(PointF p) {

        // Handle if there are no enough points to draw a bezier,
        // draw a circle instead.
        if (movedPointCount < 3) {
            Paint paint = currentPaint();
            drawPath.reset();
            drawPath.isCircle = true;
            drawPath.addCircle(points[0].x, points[0].y, paint.getStrokeWidth(), WTBezierPath.Direction.CW);
            paint.setStyle(Paint.Style.FILL);
            drawCanvas.drawPath(drawPath, paint);
        }

        movedPointCount = 0;
        pointIndex = 0;

        pathArray.add(drawPath);
        invalidate();
    }

    private void moveToPoint(PointF p) {
        drawPath.moveTo(p.x, p.y);
    }

    private void addCurveToPoint(PointF p, PointF controlPoint1, PointF controlPoint2) {
        drawPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p.x, p.y);
    }
}
