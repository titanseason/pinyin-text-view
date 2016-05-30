package com.uudove.pinyin.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;

/**
 * Displays pinyin and text to the user.<br/>
 * Here is a example how to use this widget in xml.
 * <pre>
 * &lt;com.uudove.pinyin.widget.PinyinTextView
 * android:id="@+id/pinyin_text_view"
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * android:horizontalSpacing="10dp"
 * android:verticalSpacing="10dp"
 * android:textColor="#ff0000"
 * android:textColorHint="#000000"
 * android:textSize="20sp"/&gt;
 * </pre>
 *
 * @author wangjingtao
 */
public class PinyinTextView extends View {
    /**
     * draw only plain text
     */
    public static final int TYPE_PLAIN_TEXT = 1;
    /**
     * draw pinyin and text
     */
    public static final int TYPE_PINYIN_AND_TEXT = 2;
    /**
     * draw type. Must be one value of {@link #TYPE_PINYIN_AND_TEXT} or {@link #TYPE_PLAIN_TEXT}
     */
    private int mDrawType = TYPE_PLAIN_TEXT;

    private static final float PINYIN_TEXT_SIZE_RADIO = 0.8F;

    private static final int[] ATTRS = {android.R.attr.textSize, android.R.attr.textColor,
            android.R.attr.textColorHint, android.R.attr.horizontalSpacing, android.R.attr.verticalSpacing};

    /**
     * Text size in pixels<br/>
     * Def in xml <b>android:textSize=""</b>
     */
    private int mTextSize;

    /**
     * Pinyin text size in pixels, default value equals {@link #mTextSize} * {@value #PINYIN_TEXT_SIZE_RADIO}
     */
    private int mPinyinTextSize;

    /**
     * Text color.<br/>
     * Def attr in xml <b>android:textColor=""</b>
     */
    private int mTextColor;

    /**
     * Pinyin text color<br/>
     * Def attr in xml <b>android:textColorHint=""</b>
     */
    private int mPinyinTextColor;

    /**
     * line spacing (between text and pinyin)<br/>
     * Def attr in xml <b>android:horizontalSpacing=""</b>
     */
    private int mHorizontalSpacing;

    /**
     * spacing between 2 items.<br/>
     * Def attr in xml <b>android:verticalSpacing=""</b>
     */
    private int mVerticalSpacing;

    /**
     * line spacing (between pinyin and text). Equals {@link #mHorizontalSpacing} / 2.
     */
    private int mPinyinTextSpacing;

    // text & pinyin string
    private String mTextString;
    private String mPinyinString;

    // calculated height of text or pinyin
    private int mTextHeight;
    private int mPinyinHeight;

    // Pinyin data
    private List<PinyinCompat> mPinyinCompats = new ArrayList<PinyinCompat>();

    // text & pinyin paint
    private TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);

    // bounds
    private Rect mBounds = new Rect();

    // for draw plain text
    private StaticLayout mStaticLayout;

    private boolean debugDraw = false; //  for debug, set false when release
    private Paint mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PinyinTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PinyinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinyinTextView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (this.isInEditMode()) { // eclipse preview mode
            return;
        }

        initDefault(); // initialize default value

        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mTextSize = a.getDimensionPixelSize(0, mTextSize);
        mTextColor = a.getColor(1, mTextColor);
        mPinyinTextColor = a.getColor(2, mPinyinTextColor);
        mHorizontalSpacing = a.getDimensionPixelSize(3, mHorizontalSpacing);
        mVerticalSpacing = a.getDimensionPixelSize(4, mVerticalSpacing);
        a.recycle();

        mPinyinTextSpacing = mHorizontalSpacing / 2; // half of line spacing

        setTextSize(mTextSize);
    }

    private void initDefault() {
        Context c = getContext();
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }

        DisplayMetrics dm = r.getDisplayMetrics();

        // Text size default 14sp
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm);
        mPinyinTextSize = (int) (mTextSize * PINYIN_TEXT_SIZE_RADIO);

        // spacing
        mHorizontalSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
        mVerticalSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
        mPinyinTextSpacing = mHorizontalSpacing / 2;

        // set default text color
        mTextColor = 0xff333333;
        mPinyinTextColor = 0xff999999;
        // mPinyinTextColor = 0xffff0000;

        mPaint.setStyle(Paint.Style.FILL);
        mDebugPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Set plain text size in pixels<br/>
     * Def in xml <b>android:textSize=""</b>
     *
     * @param px - text size in pixels
     */
    public void setTextSize(int px) {
        if (px < 2) {
            throw new IllegalArgumentException("Text size must larger than 2px");
        }
        mTextSize = px;

        setPinyinTextSize((int) (px * PINYIN_TEXT_SIZE_RADIO));
    }

    /**
     * Get the plain text size.
     *
     * @return plain text size.
     */
    public int getTextSize() {
        return mTextSize;
    }

    /**
     * Set pinyin text size in pixels. If not set, pinyin text size will be the size of {@link #getTextSize()} *
     * {@value #PINYIN_TEXT_SIZE_RADIO}. <br/><br/>
     * Attention:<br/>
     * Don't use{@link #setTextSize(int)} method after this method is called, which will set pinyin text size to
     * {@link #getTextSize()} *  {@value #PINYIN_TEXT_SIZE_RADIO}.
     *
     * @param px - pinyin text size in pixels
     */
    public void setPinyinTextSize(int px) {
        mPinyinTextSize = px;
        if (mPinyinTextSize <= 0) {
            throw new IllegalArgumentException("Pinyin text size must larger than 1px");
        }

        // calculate text & pinyin height
        calTextHeight();

        requestLayout();
        invalidate();
    }

    /**
     * Set text color.<br/>
     * Def in xml <b>android:textColor=""</b>
     *
     * @param color text color.
     */
    public void setTextColor(int color) {
        mTextColor = color;
        invalidate();
    }

    /**
     * Set pinyin text color.<br/>
     * Def in xml <b>android:textColorHint=""</b>
     *
     * @param color pinyin text color.
     */
    public void setPinyinTextColor(int color) {
        mPinyinTextColor = color;
        invalidate();
    }

    /**
     * Set line spacing in pixels.<br/>
     * The same as method {@link #setHorizontalSpacing(int)}
     *
     * @param px line spacing in pixels.
     *
     * @see #setHorizontalSpacing(int)
     */
    public void setLineSpacing(int px) {
        setHorizontalSpacing(px);
    }

    /**
     * Set line spacing in pixels.<br/>
     * Def in xml <b>android:horizontalSpacing=""</b>
     *
     * @param px line spacing in pixels.
     */
    public void setHorizontalSpacing(int px) {
        mHorizontalSpacing = px;
        mPinyinTextSpacing = mHorizontalSpacing / 2; // half of line spacing
        requestLayout();
        invalidate();
    }

    /**
     * Set vertical spacing between two items.<br/>
     * Def in xml <b>android:verticalSpacing=""</b>
     *
     * @param px line spacing in pixels.
     */
    public void setVerticalSpacing(int px) {
        mVerticalSpacing = px;
        requestLayout();
        invalidate();
    }

    /**
     * Display pinyin and text to user.
     *
     * @param pinyinList Text-Pinyin pair.
     */
    public void setPinyinText(List<Pair<String, String>> pinyinList) {
        mDrawType = TYPE_PINYIN_AND_TEXT; // set draw type

        clearAll(); // clear what is shown

        StringBuilder textBuilder = new StringBuilder();
        StringBuilder pinyinBuilder = new StringBuilder();
        for (Pair<String, String> pair : pinyinList) {
            String src = pair.first;
            String trg = pair.second;
            if (src == null) {
                src = "";
            }
            if (trg == null) {
                trg = "";
            }
            textBuilder.append(src);
            pinyinBuilder.append(trg);

            PinyinCompat compat = new PinyinCompat();
            compat.text = src;
            compat.pinyin = trg;
            compat.textRect = new Rect();
            compat.pinyinRect = new Rect();
            mPinyinCompats.add(compat);
        }

        // string buffer
        mTextString = textBuilder.toString();
        mPinyinString = pinyinBuilder.toString();

        // calculate text & pinyin height
        calTextHeight();

        requestLayout();
        invalidate();
    }

    /**
     * Display only plain text to user, like TextView
     *
     * @param text plain text to display.
     */
    public void setText(String text) {
        mDrawType = TYPE_PLAIN_TEXT; // set draw type

        clearAll();

        this.mTextString = text;

        requestLayout();
        invalidate();
    }

    /**
     * Set whether draw debug rect.
     *
     * @param debugDraw debug mode.
     */
    public void setDebugDraw(boolean debugDraw) {
        this.debugDraw = debugDraw;
    }

    private void clearAll() {
        mPinyinCompats.clear(); // clear

        mTextString = null;
        mPinyinString = null;

        mTextHeight = 0;
        mPinyinHeight = 0;
    }

    // calculate text & pinyin height
    private void calTextHeight() {
        // calculate text height
        if (!TextUtils.isEmpty(mTextString)) {
            mPaint.setTextSize(mTextSize);
            mPaint.getTextBounds(mTextString, 0, mTextString.length(), mBounds);
            mTextHeight = mBounds.height();
        } else {
            mTextHeight = 0;
        }

        // calculate pinyin height
        if (!TextUtils.isEmpty(mPinyinString)) {
            mPaint.setTextSize(mPinyinTextSize);
            mPaint.getTextBounds(mPinyinString, 0, mPinyinString.length(), mBounds);
            mPinyinHeight = mBounds.height();
        } else {
            mPinyinHeight = 0;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAll();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDrawType == TYPE_PINYIN_AND_TEXT && !mPinyinCompats.isEmpty()) {
            measurePinyinText(widthMeasureSpec, heightMeasureSpec);
        } else if (mDrawType == TYPE_PLAIN_TEXT && !TextUtils.isEmpty(mTextString)) {
            measurePlainText(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureDefault(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureDefault(int widthMeasureSpec, int heightMeasureSpec) {

        // max allowed width or height
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // mode
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // measured width and height
        int measuredWidth = modeWidth == MeasureSpec.EXACTLY ? sizeWidth : getPaddingLeft() + getPaddingRight();
        int measuredHeight = modeHeight == MeasureSpec.EXACTLY ? sizeHeight : getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void measurePinyinText(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = this.getPaddingLeft();
        int paddingRight = this.getPaddingRight();
        int paddingTop = this.getPaddingTop();
        int paddingBottom = this.getPaddingBottom();

        // max allowed width or height
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight;
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom;

        // mode
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // measured width and height
        int measuredWidth = modeWidth == MeasureSpec.EXACTLY ? sizeWidth : 0;
        int measuredHeight = modeHeight == MeasureSpec.EXACTLY ? sizeHeight : 0;

        int line = 0;
        int col = 0;
        int lineLength = 0;
        int baseLine = 0; // top of pinyin
        boolean newLine = false;

        for (PinyinCompat compat : mPinyinCompats) {
            int textWidth = getTextWidth(compat.text, mTextSize);
            int pinyinWidth = getTextWidth(compat.pinyin, mPinyinTextSize);

            int maxWidth = Math.max(textWidth, pinyinWidth);

            if (newLine) {
                line++;
                col = 0;
                newLine = false;
            }

            if (lineLength + maxWidth + (col == 0 ? 0 : mVerticalSpacing) > sizeWidth) { // new row
                lineLength = maxWidth;

                baseLine += mTextHeight + mPinyinHeight + mPinyinTextSpacing + mHorizontalSpacing;

                if (modeWidth != MeasureSpec.EXACTLY) {
                    measuredWidth = sizeWidth;
                }

                newLine = true;
            } else {
                if (col != 0 || line != 0) { // not the first item of first row
                    lineLength += mVerticalSpacing;
                }
                lineLength += maxWidth;

                if (modeWidth != MeasureSpec.EXACTLY && measuredWidth < lineLength) {
                    measuredWidth = lineLength;
                    if (measuredWidth > sizeWidth) {
                        measuredWidth = sizeWidth;
                    }
                }
                col++;
            }

            compat.pinyinRect.left = lineLength - maxWidth;
            compat.pinyinRect.right = compat.pinyinRect.left + pinyinWidth;
            compat.pinyinRect.top = baseLine;
            compat.pinyinRect.bottom = compat.pinyinRect.top + mPinyinHeight;

            compat.textRect.left = lineLength - maxWidth;
            compat.textRect.right = compat.textRect.left + textWidth;
            compat.textRect.top = compat.pinyinRect.bottom + mPinyinTextSpacing;
            compat.textRect.bottom = compat.textRect.top + mTextHeight;
        }

        if (modeHeight != MeasureSpec.EXACTLY) {
            measuredHeight = baseLine + mPinyinHeight + mPinyinTextSpacing + mTextHeight + mTextHeight / 4;
            if (measuredHeight > sizeHeight) {
                measuredHeight = sizeHeight;
            }
        }

        setMeasuredDimension(measuredWidth + paddingLeft + paddingRight, measuredHeight + paddingTop + paddingBottom);
    }

    private void measurePlainText(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = this.getPaddingLeft();
        int paddingRight = this.getPaddingRight();
        int paddingTop = this.getPaddingTop();
        int paddingBottom = this.getPaddingBottom();

        // max allowed width or height
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight;
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom;

        // mode
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // calculate text width and height
        mPaint.setTextSize(mTextSize);
        mStaticLayout = new StaticLayout(mTextString, mPaint, sizeWidth, Alignment.ALIGN_NORMAL, 1.0f, 0, false);

        // measured width and height
        int measuredWidth =
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : Math.min(sizeWidth,
                        (int) Math.ceil(Layout.getDesiredWidth(mTextString, mPaint)));
        int measuredHeight = modeHeight == MeasureSpec.EXACTLY ? sizeHeight : mStaticLayout.getHeight();

        setMeasuredDimension(measuredWidth + paddingLeft + paddingRight, measuredHeight + paddingTop + paddingBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isInEditMode()) { // eclipse preview mode
            return;
        }

        if (mDrawType == TYPE_PINYIN_AND_TEXT) {
            drawPinyinAndText(canvas);
        } else if (mDrawType == TYPE_PLAIN_TEXT) {
            drawPlainText(canvas);
        }
    }

    private void drawPinyinAndText(Canvas canvas) {
        int paddingLeft = this.getPaddingLeft();
        int paddingTop = this.getPaddingTop();

        for (int i = 0; i < mPinyinCompats.size(); i++) {
            PinyinCompat compat = mPinyinCompats.get(i);

            // draw text
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);
            compat.textRect.offset(paddingLeft, paddingTop);
            canvas.drawText(compat.text, compat.textRect.left, compat.textRect.bottom, mPaint);

            if (debugDraw) {
                mDebugPaint.setColor(mTextColor);
                canvas.drawRect(compat.textRect, mDebugPaint);
            }

            // draw pinyin
            mPaint.setColor(mPinyinTextColor);
            mPaint.setTextSize(mPinyinTextSize);
            compat.pinyinRect.offset(paddingLeft, paddingTop);
            canvas.drawText(compat.pinyin, compat.pinyinRect.left, compat.pinyinRect.bottom, mPaint);

            if (debugDraw) {
                mDebugPaint.setColor(mPinyinTextColor);
                canvas.drawRect(compat.pinyinRect, mDebugPaint);
            }
        }
    }

    private void drawPlainText(Canvas canvas) {
        if (mStaticLayout != null) {
            int paddingLeft = this.getPaddingLeft();
            int paddingTop = this.getPaddingTop();
            canvas.translate(paddingLeft, paddingTop);

            mPaint.setColor(mTextColor);

            mStaticLayout.draw(canvas);
        }
    }

    private int getTextWidth(String text, int textSize) {
        mPaint.setTextSize(textSize);

        return (int) Math.ceil(Layout.getDesiredWidth(text, mPaint));
    }

    static class PinyinCompat {
        String text;
        String pinyin;

        Rect textRect;
        Rect pinyinRect;
    }

}
