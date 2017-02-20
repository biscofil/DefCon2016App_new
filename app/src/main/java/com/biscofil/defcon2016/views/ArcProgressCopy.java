package com.biscofil.defcon2016.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.biscofil.defcon2016.R;
import com.github.lzyzsd.circleprogress.Utils;

public class ArcProgressCopy extends View {
    Paint paint;
    Paint textPaint;

    RectF rectF = new RectF();

    float strokeWidth;
    float suffixTextSize;
    float bottomTextSize;
    String bottomText;
    float textSize;
    int textColor;
    int progress = 0;
    int max;
    int finishedStrokeColor;
    int unfinishedStrokeColor;
    float arcAngle;
    String suffixText = "%";
    float suffixTextPadding;

    float arcBottomHeight;

    int default_finished_color = Color.WHITE;
    int default_unfinished_color = Color.rgb(72, 106, 176);
    int default_text_color = Color.rgb(66, 145, 241);
    float default_suffix_text_size;
    float default_suffix_padding;
    float default_bottom_text_size;
    float default_stroke_width;
    String default_suffix_text;
    int default_max = 100;
    float default_arc_angle = 360 * 0.8f;
    float default_text_size;
    int min_size;

    static final String INSTANCE_STATE = "saved_instance";
    static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    static final String INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding";
    static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";
    static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
    static final String INSTANCE_TEXT_SIZE = "text_size";
    static final String INSTANCE_TEXT_COLOR = "text_color";
    static final String INSTANCE_PROGRESS = "progress";
    static final String INSTANCE_MAX = "max";
    static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    static final String INSTANCE_ARC_ANGLE = "arc_angle";
    static final String INSTANCE_SUFFIX = "suffix";

    public ArcProgressCopy(Context context) {
        this(context, null);
    }

    public ArcProgressCopy(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressCopy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = Utils.sp2px(getResources(), 18);
        min_size = (int) Utils.dp2px(getResources(), 100);
        default_text_size = Utils.sp2px(getResources(), 40);
        default_suffix_text_size = Utils.sp2px(getResources(), 15);
        default_suffix_padding = Utils.dp2px(getResources(), 4);
        default_suffix_text = "%";
        default_bottom_text_size = Utils.sp2px(getResources(), 10);
        default_stroke_width = Utils.dp2px(getResources(), 4);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgressCopy, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.ArcProgressCopy_arc_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.ArcProgressCopy_arc_unfinished_color, default_unfinished_color);
        textColor = attributes.getColor(R.styleable.ArcProgressCopy_arc_text_color, default_text_color);
        textSize = attributes.getDimension(R.styleable.ArcProgressCopy_arc_text_size, default_text_size);
        arcAngle = attributes.getFloat(R.styleable.ArcProgressCopy_arc_angle, default_arc_angle);
        setMax(attributes.getInt(R.styleable.ArcProgressCopy_arc_max, default_max));
        setProgress(attributes.getInt(R.styleable.ArcProgressCopy_arc_progress, 0));
        strokeWidth = attributes.getDimension(R.styleable.ArcProgressCopy_arc_stroke_width, default_stroke_width);
        suffixTextSize = attributes.getDimension(R.styleable.ArcProgressCopy_arc_suffix_text_size, default_suffix_text_size);
        suffixText = TextUtils.isEmpty(attributes.getString(R.styleable.ArcProgressCopy_arc_suffix_text)) ? default_suffix_text : attributes.getString(R.styleable.ArcProgressCopy_arc_suffix_text);
        suffixTextPadding = attributes.getDimension(R.styleable.ArcProgressCopy_arc_suffix_text_padding, default_suffix_padding);
        bottomTextSize = attributes.getDimension(R.styleable.ArcProgressCopy_arc_bottom_text_size, default_bottom_text_size);
        bottomText = attributes.getString(R.styleable.ArcProgressCopy_arc_bottom_text);
    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(default_unfinished_color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.invalidate();
    }

    public float getSuffixTextSize() {
        return suffixTextSize;
    }

    public void setSuffixTextSize(float suffixTextSize) {
        this.suffixTextSize = suffixTextSize;
        this.invalidate();
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
        this.invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getBottomTextSize() {
        return bottomTextSize;
    }

    public void setBottomTextSize(float bottomTextSize) {
        this.bottomTextSize = bottomTextSize;
        this.invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public float getArcAngle() {
        return arcAngle;
    }

    public void setArcAngle(float arcAngle) {
        this.arcAngle = arcAngle;
        this.invalidate();
    }

    public String getSuffixText() {
        return suffixText;
    }

    public void setSuffixText(String suffixText) {
        this.suffixText = suffixText;
        this.invalidate();
    }

    public float getSuffixTextPadding() {
        return suffixTextPadding;
    }

    public void setSuffixTextPadding(float suffixTextPadding) {
        this.suffixTextPadding = suffixTextPadding;
        this.invalidate();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        rectF.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f, MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
        float radius = width / 2f;
        float angle = (360 - arcAngle) / 2f;
        arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
    }

    protected void old_onDraw(Canvas canvas) {
        float startAngle = 270 - arcAngle / 2f;
        float finishedSweepAngle = progress / (float) getMax() * arcAngle;
        float finishedStartAngle = startAngle;
        if (progress == 0) finishedStartAngle = 0.01f;
        paint.setColor(unfinishedStrokeColor);
        canvas.drawArc(rectF, startAngle, arcAngle, false, paint);
        paint.setColor(finishedStrokeColor);
        canvas.drawArc(rectF, finishedStartAngle, finishedSweepAngle, false, paint);

        String text = String.valueOf(getProgress());
        if (!TextUtils.isEmpty(text)) {
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            float textHeight = textPaint.descent() + textPaint.ascent();
            float textBaseline = (getHeight() - textHeight) / 2.0f;
            canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, textBaseline, textPaint);
            textPaint.setTextSize(suffixTextSize);
            float suffixHeight = textPaint.descent() + textPaint.ascent();
            canvas.drawText(suffixText, getWidth() / 2.0f + textPaint.measureText(text) + suffixTextPadding, textBaseline + textHeight - suffixHeight, textPaint);
        }

        if (arcBottomHeight == 0) {
            float radius = getWidth() / 2f;
            float angle = (360 - arcAngle) / 2f;
            arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
        }

        if (!TextUtils.isEmpty(getBottomText())) {
            textPaint.setTextSize(bottomTextSize);
            float bottomTextBaseline = getHeight() - arcBottomHeight - (textPaint.descent() + textPaint.ascent()) / 2;
            canvas.drawText(getBottomText(), (getWidth() - textPaint.measureText(getBottomText())) / 2.0f, bottomTextBaseline, textPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, getSuffixTextPadding());
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
        bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            strokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            suffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
            suffixTextPadding = bundle.getFloat(INSTANCE_SUFFIX_TEXT_PADDING);
            bottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
            bottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            initPainters();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
