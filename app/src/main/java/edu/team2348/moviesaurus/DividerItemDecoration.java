package edu.team2348.moviesaurus;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class for drawing dividing lines between each Movie view
 * @author Thomas
 * @version 1.0
 */
class DividerItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * int array for list divider attributes
     */
    private static final int[] ATTRS = new int[] {android.R.attr.listDivider};

    /**
     * Horizontal linear layout manager
     */
    private static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    /**
     * Vertical linear layout manager
     */
    private static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    /**
     * The drawable object for the divider
     */
    private final Drawable mDivider;

    /**
     * the int representing the orientation
     */
    private int mOrientation = LinearLayoutManager.VERTICAL;

//    private static final String TAG = "DividerItemDecoration";


    /**
     * Constructor that creates the DividerItemDecoration
     * @param context The context in which to draw the decoration
     * @param orientation vertical or horizontal
     */
    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    /**
     * Set orientation of the dividing lines
     * @param orientation int representing vertical or horizontal
     */
    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * Method to draw vertical line decorations
     * @param c the Canvas that will be drawn onto
     * @param parent the RecyclerView containing the items
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * Method to draw horizontal line decorations
     * @param c the Canvas that will be drawn onto
     * @param parent RecyclerView Containing the items
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


}
