package com.example.parkingappuser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class CustomNumberPicker extends NumberPicker {

    private Paint borderPaint;
    private float strokeWidth = 4f;

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(strokeWidth);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(getResources().getColor(R.color.white_transparent));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cornerRadius = 10f;
        float offset = strokeWidth / 2f; // Αυτό είναι το κλειδί!

        // Προσθέτουμε offset για να μην κόβεται το περίγραμμα
        float left = offset;
        float top = getHeight() / 3f + offset;
        float right = getWidth() - offset;
        float bottom = getHeight() * 2 / 3f - offset;

        canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, borderPaint);
    }
}