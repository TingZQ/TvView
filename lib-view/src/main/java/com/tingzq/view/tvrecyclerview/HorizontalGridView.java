package com.tingzq.view.tvrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.tingzq.view.R;


public class HorizontalGridView extends BaseGridView {

    private boolean mFadingLowEdge;
    private boolean mFadingHighEdge;

    private Paint mTempPaint = new Paint();
    private Bitmap mTempBitmapLow;
    private LinearGradient mLowFadeShader;
    private int mLowFadeShaderLength;
    private int mLowFadeShaderOffset;
    private Bitmap mTempBitmapHigh;
    private LinearGradient mHighFadeShader;
    private int mHighFadeShaderLength;
    private int mHighFadeShaderOffset;
    private final Rect mTempRect = new Rect();

    public HorizontalGridView(Context context) {
        this(context, null);
    }

    public HorizontalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
    }

    @Override
    public GridLayoutManager getLayoutManager() {
        return new GridLayoutManager(this, HORIZONTAL);
    }

    protected void initAttributes(Context context, AttributeSet attrs) {
        initBaseGridViewAttributes(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalGridView);
        setNumRows(a.getInt(R.styleable.HorizontalGridView_numberOfRows, 1));
        a.recycle();
        updateLayerType();
        mTempPaint = new Paint();
        mTempPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    /**
     * Sets the number of rows.  Defaults to one.
     */
    public void setNumRows(int numRows) {
        mLayoutManager.setNumRows(numRows);
        requestLayout();
    }

    /**
     * Sets the fade out left edge to transparent.   Note turn on fading edge is very expensive
     * that you should turn off when HorizontalGridView is scrolling.
     */
    public final void setFadingLeftEdge(boolean fading) {
        if (mFadingLowEdge != fading) {
            mFadingLowEdge = fading;
            if (!mFadingLowEdge) {
                mTempBitmapLow = null;
            }
            invalidate();
            updateLayerType();
        }
    }

    /**
     * Returns true if left edge fading is enabled.
     */
    public final boolean getFadingLeftEdge() {
        return mFadingLowEdge;
    }

    /**
     * Sets the left edge fading length in pixels.
     */
    public final void setFadingLeftEdgeLength(int fadeLength) {
        if (mLowFadeShaderLength != fadeLength) {
            mLowFadeShaderLength = fadeLength;
            if (mLowFadeShaderLength != 0) {
                mLowFadeShader = new LinearGradient(0, 0, mLowFadeShaderLength, 0,
                        Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
            } else {
                mLowFadeShader = null;
            }
            invalidate();
        }
    }

    /**
     * Returns the left edge fading length in pixels.
     */
    public final int getFadingLeftEdgeLength() {
        return mLowFadeShaderLength;
    }

    /**
     * Sets the distance in pixels between fading start position and left padding edge.
     * The fading start position is positive when start position is inside left padding
     * area.  Default value is 0, means that the fading starts from left padding edge.
     */
    public final void setFadingLeftEdgeOffset(int fadeOffset) {
        if (mLowFadeShaderOffset != fadeOffset) {
            mLowFadeShaderOffset = fadeOffset;
            invalidate();
        }
    }

    /**
     * Returns the distance in pixels between fading start position and left padding edge.
     * The fading start position is positive when start position is inside left padding
     * area.  Default value is 0, means that the fading starts from left padding edge.
     */
    public final int getFadingLeftEdgeOffset() {
        return mLowFadeShaderOffset;
    }

    /**
     * Sets the fade out right edge to transparent.   Note turn on fading edge is very expensive
     * that you should turn off when HorizontalGridView is scrolling.
     */
    public final void setFadingRightEdge(boolean fading) {
        if (mFadingHighEdge != fading) {
            mFadingHighEdge = fading;
            if (!mFadingHighEdge) {
                mTempBitmapHigh = null;
            }
            invalidate();
            updateLayerType();
        }
    }

    /**
     * Returns true if fading right edge is enabled.
     */
    public final boolean getFadingRightEdge() {
        return mFadingHighEdge;
    }

    /**
     * Sets the right edge fading length in pixels.
     */
    public final void setFadingRightEdgeLength(int fadeLength) {
        if (mHighFadeShaderLength != fadeLength) {
            mHighFadeShaderLength = fadeLength;
            if (mHighFadeShaderLength != 0) {
                mHighFadeShader = new LinearGradient(0, 0, mHighFadeShaderLength, 0,
                        Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            } else {
                mHighFadeShader = null;
            }
            invalidate();
        }
    }

    /**
     * Returns the right edge fading length in pixels.
     */
    public final int getFadingRightEdgeLength() {
        return mHighFadeShaderLength;
    }

    /**
     * Returns the distance in pixels between fading start position and right padding edge.
     * The fading start position is positive when start position is inside right padding
     * area.  Default value is 0, means that the fading starts from right padding edge.
     */
    public final void setFadingRightEdgeOffset(int fadeOffset) {
        if (mHighFadeShaderOffset != fadeOffset) {
            mHighFadeShaderOffset = fadeOffset;
            invalidate();
        }
    }

    /**
     * Sets the distance in pixels between fading start position and right padding edge.
     * The fading start position is positive when start position is inside right padding
     * area.  Default value is 0, means that the fading starts from right padding edge.
     */
    public final int getFadingRightEdgeOffset() {
        return mHighFadeShaderOffset;
    }

    private boolean needsFadingLowEdge() {
        if (!mFadingLowEdge) {
            return false;
        }
        final int c = getChildCount();
        for (int i = 0; i < c; i++) {
            View view = getChildAt(i);
            if (mLayoutManager.getOpticalLeft(view) <
                    getPaddingLeft() - mLowFadeShaderOffset) {
                return true;
            }
        }
        return false;
    }

    private boolean needsFadingHighEdge() {
        if (!mFadingHighEdge) {
            return false;
        }
        final int c = getChildCount();
        for (int i = c - 1; i >= 0; i--) {
            View view = getChildAt(i);
            if (mLayoutManager.getOpticalRight(view) > getWidth()
                    - getPaddingRight() + mHighFadeShaderOffset) {
                return true;
            }
        }
        return false;
    }

    private Bitmap getTempBitmapLow() {
        if (mTempBitmapLow == null
                || mTempBitmapLow.getWidth() != mLowFadeShaderLength
                || mTempBitmapLow.getHeight() != getHeight()) {
            mTempBitmapLow = Bitmap.createBitmap(mLowFadeShaderLength, getHeight(),
                    Bitmap.Config.ARGB_8888);
        }
        return mTempBitmapLow;
    }

    private Bitmap getTempBitmapHigh() {
        if (mTempBitmapHigh == null
                || mTempBitmapHigh.getWidth() != mHighFadeShaderLength
                || mTempBitmapHigh.getHeight() != getHeight()) {
                mTempBitmapHigh = Bitmap.createBitmap(mHighFadeShaderLength, getHeight(),
                        Bitmap.Config.ARGB_8888);
        }
        return mTempBitmapHigh;
    }

    @Override
    public void draw(Canvas canvas) {
        final boolean needsFadingLow = needsFadingLowEdge();
        final boolean needsFadingHigh = needsFadingHighEdge();
        if (!needsFadingLow) {
            mTempBitmapLow = null;
        }
        if (!needsFadingHigh) {
            mTempBitmapHigh = null;
        }
        if (!needsFadingLow && !needsFadingHigh) {
            super.draw(canvas);
            return;
        }

        int lowEdge = mFadingLowEdge? getPaddingLeft() - mLowFadeShaderOffset - mLowFadeShaderLength : 0;
        int highEdge = mFadingHighEdge ? getWidth() - getPaddingRight()
                + mHighFadeShaderOffset + mHighFadeShaderLength : getWidth();

        // draw not-fade content
        int save = canvas.save();
        canvas.clipRect(lowEdge + (mFadingLowEdge ? mLowFadeShaderLength : 0), 0,
                highEdge - (mFadingHighEdge ? mHighFadeShaderLength : 0), getHeight());
        super.draw(canvas);
        canvas.restoreToCount(save);

        Canvas tmpCanvas = new Canvas();
        mTempRect.top = 0;
        mTempRect.bottom = getHeight();
        if (needsFadingLow && mLowFadeShaderLength > 0) {
            Bitmap tempBitmap = getTempBitmapLow();
            tempBitmap.eraseColor(Color.TRANSPARENT);
            tmpCanvas.setBitmap(tempBitmap);
            // draw original content
            int tmpSave = tmpCanvas.save();
            tmpCanvas.clipRect(0, 0, mLowFadeShaderLength, getHeight());
            tmpCanvas.translate(-lowEdge, 0);
            super.draw(tmpCanvas);
            tmpCanvas.restoreToCount(tmpSave);
            // draw fading out
            mTempPaint.setShader(mLowFadeShader);
            tmpCanvas.drawRect(0, 0, mLowFadeShaderLength, getHeight(), mTempPaint);
            // copy back to canvas
            mTempRect.left = 0;
            mTempRect.right = mLowFadeShaderLength;
            canvas.translate(lowEdge, 0);
            canvas.drawBitmap(tempBitmap, mTempRect, mTempRect, null);
            canvas.translate(-lowEdge, 0);
        }
        if (needsFadingHigh && mHighFadeShaderLength > 0) {
            Bitmap tempBitmap = getTempBitmapHigh();
            tempBitmap.eraseColor(Color.TRANSPARENT);
            tmpCanvas.setBitmap(tempBitmap);
            // draw original content
            int tmpSave = tmpCanvas.save();
            tmpCanvas.clipRect(0, 0, mHighFadeShaderLength, getHeight());
            tmpCanvas.translate(-(highEdge - mHighFadeShaderLength), 0);
            super.draw(tmpCanvas);
            tmpCanvas.restoreToCount(tmpSave);
            // draw fading out
            mTempPaint.setShader(mHighFadeShader);
            tmpCanvas.drawRect(0, 0, mHighFadeShaderLength, getHeight(), mTempPaint);
            // copy back to canvas
            mTempRect.left = 0;
            mTempRect.right = mHighFadeShaderLength;
            canvas.translate(highEdge - mHighFadeShaderLength, 0);
            canvas.drawBitmap(tempBitmap, mTempRect, mTempRect, null);
            canvas.translate(-(highEdge - mHighFadeShaderLength), 0);
        }
    }

    /**
     * Updates the layer type for this view.
     * If fading edges are needed, use a hardware layer.  This works around the problem
     * that when a child invalidates itself (for example has an animated background),
     * the parent view must also be invalidated to refresh the display list which
     * updates the the caching bitmaps used to draw the fading edges.
     */
    private void updateLayerType() {
        if (mFadingLowEdge || mFadingHighEdge) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            setWillNotDraw(false);
        } else {
            setLayerType(View.LAYER_TYPE_NONE, null);
            setWillNotDraw(true);
        }
    }
}
