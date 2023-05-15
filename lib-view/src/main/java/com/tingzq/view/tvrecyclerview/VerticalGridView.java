package com.tingzq.view.tvrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.tingzq.view.R;

public class VerticalGridView extends BaseGridView {

    public VerticalGridView(Context context) {
        this(context, null);
    }

    public VerticalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
    }

    @Override
    public GridLayoutManager getLayoutManager() {
        return new GridLayoutManager(this, VERTICAL);
    }

    protected void initAttributes(Context context, AttributeSet attrs) {
        initBaseGridViewAttributes(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalGridView);
        setNumColumns(a.getInt(R.styleable.VerticalGridView_numberOfColumns, 1));
        a.recycle();
    }

    /**
     * Sets the number of columns.  Defaults to one.
     */
    public void setNumColumns(int numColumns) {
        mLayoutManager.setNumRows(numColumns);
        requestLayout();
    }

}
