package com.biscofil.defcon2016.views;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.biscofil.defcon2016.Utils;

public class MyArcProgress extends ArcProgressCopy {

    float max = 5.0f;
    float res = 100.0f;

    public void setMyMax() {
        setMax((int) (max * res));
    }

    public MyArcProgress(Context context) {
        super(context, null);
        setMyMax();
    }

    public MyArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setMyMax();
    }

    public MyArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMyMax();
    }

    public void setValue(double f) {
        if (f < 0.0 || f > max) {
            throw new RuntimeException("0 < x < " + max);
        }
        //f = new Random().nextDouble() * 5.0; per prova
        int out = (int) (f * res);
        setProgress(out);
        int c = Utils.val2col(f);
        setFinishedStrokeColor(c);
        setTextColor(c);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = 270.0F - this.arcAngle / 2.0F;
        float finishedSweepAngle = (float) this.progress / (float) this.getMax() * this.arcAngle;
        float finishedStartAngle = startAngle;
        if (this.progress == 0) {
            finishedStartAngle = 0.01F;
        }

        this.paint.setColor(this.unfinishedStrokeColor);
        canvas.drawArc(this.rectF, startAngle, this.arcAngle, false, this.paint);
        this.paint.setColor(this.finishedStrokeColor);
        canvas.drawArc(this.rectF, finishedStartAngle, finishedSweepAngle, false, this.paint);
        String text = String.valueOf(this.getProgress() / res);
        float bottomTextBaseline;
        float angle;
        if (!TextUtils.isEmpty(text)) {
            this.textPaint.setColor(this.textColor);
            this.textPaint.setTextSize(this.textSize);
            bottomTextBaseline = this.textPaint.descent() + this.textPaint.ascent();
            angle = ((float) this.getHeight() - bottomTextBaseline) / 2.0F;
            canvas.drawText(text, ((float) this.getWidth() - this.textPaint.measureText(text)) / 2.0F, angle, this.textPaint);
            this.textPaint.setTextSize(this.suffixTextSize);
            float suffixHeight = this.textPaint.descent() + this.textPaint.ascent();
            canvas.drawText(this.suffixText, (float) this.getWidth() / 2.0F + this.textPaint.measureText(text) + this.suffixTextPadding, angle + bottomTextBaseline - suffixHeight, this.textPaint);
        }

        if (this.arcBottomHeight == 0.0F) {
            bottomTextBaseline = (float) this.getWidth() / 2.0F;
            angle = (360.0F - this.arcAngle) / 2.0F;
            this.arcBottomHeight = bottomTextBaseline * (float) (1.0D - Math.cos((double) (angle / 180.0F) * 3.141592653589793D));
        }

        if (!TextUtils.isEmpty(this.getBottomText())) {
            this.textPaint.setTextSize(this.bottomTextSize);
            bottomTextBaseline = (float) this.getHeight() - this.arcBottomHeight - (this.textPaint.descent() + this.textPaint.ascent()) / 2.0F;
            canvas.drawText(this.getBottomText(), ((float) this.getWidth() - this.textPaint.measureText(this.getBottomText())) / 2.0F, bottomTextBaseline, this.textPaint);
        }

    }


}
