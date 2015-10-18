/*
 * Falling Blocks
 * A simple faling blocks game for Android
 *
 * Copyright (C) 2011 - 2012 Stefan Oltmann
 *
 * Contact : fallingblocks@stefan-oltmann.de
 * Homepage: http://www.stefan-oltmann.de/
 *
 * This file is part of Falling Blocks.
 *
 * Falling Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Falling Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Falling Blocks. If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.falling_blocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Stefan Oltmann
 */
public abstract class AbstractBoxView extends View {

    /**
     * Dieser Faktor wird benötigt um die Einheit DP in Pixel umzurechnen.
     */
    private static final float GESTURE_THRESHOLD_DIP = 16.0f;

    private Bitmap             bitmap;
    private String             displayText           = "-";

    private Paint              paint;

    public AbstractBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = createBitmap(context);
        paint = new Paint();
    }

    abstract protected Bitmap createBitmap(Context context);

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
        invalidate(); // Neu zeichnen
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);

        // http://developer.android.com/guide/practices/screens_support.html#dips-pels
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int mGestureThreshold = (int) (GESTURE_THRESHOLD_DIP * scale + 0.5f);

        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(mGestureThreshold);

        int abzug = 3;
        if (scale > 1.0)
            abzug += 3;

        float textBreite = paint.measureText(displayText);
        canvas.drawText(displayText, (getWidth() - textBreite) / 2, getHeight() - abzug, paint);
    }

}
