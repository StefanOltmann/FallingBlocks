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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import de.stefan_oltmann.falling_blocks.model.Baustein;

/**
 * @author Stefan Oltmann
 */
public class VorschauView extends View {

    private Bitmap   bitmap;

    private Baustein baustein;

    public VorschauView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.box_next);
    }

    public void setBaustein(Baustein baustein) {
        this.baustein = baustein;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);

        if (baustein == null)
            return;

        Bitmap bitmapVorschau = Bitmap.createBitmap(
                4 * SpielfeldView.quadratSeitenlaenge,
                3 * SpielfeldView.quadratSeitenlaenge,
                Bitmap.Config.RGB_565);

        Canvas bitmapCanvas = new Canvas(bitmapVorschau);

        Baustein bausteinKopie = baustein.erstelleKopie();
        bausteinKopie.falle();

        bitmapCanvas.drawColor(getResources().getColor(R.color.hintergrund));
        bausteinKopie.onDraw(bitmapCanvas);

        canvas.drawBitmap(bitmapVorschau, null, new Rect(5, getHeight() / 3 + 5, getWidth() - 5, getHeight() - 5), null);
    }

    public void anzeigeAktualisieren() {
        postInvalidate(); // View zwingen, neu zu zeichnen
    }

}
