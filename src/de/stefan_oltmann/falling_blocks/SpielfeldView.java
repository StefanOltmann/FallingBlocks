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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import de.stefan_oltmann.falling_blocks.model.Quadrat;
import de.stefan_oltmann.falling_blocks.model.Spielfeld;

/**
 * @author Stefan Oltmann
 */
public class SpielfeldView extends View {

    public static int quadratSeitenlaenge = 25;

    private Spielfeld spielfeld;

    public SpielfeldView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Spielfeld spielfeld) {
        this.spielfeld = spielfeld;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        /*
         * Hässlich, aber Android lässt mir einfach keine andere Wahl, da ich
         * sonst nicht weiss, wie groß der View ist, um die maximale
         * Kästchen-Höhe zu ermitteln.
         */
        ((SpielActivity) getContext()).setupSpielfeldAndstartGameLoop();

        quadratSeitenlaenge = w / spielfeld.getBreite();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (spielfeld == null)
            return;

        canvas.drawColor(Color.BLACK);

//        drawGitter(canvas);

        if (spielfeld.getAktiverBaustein() != null)
            spielfeld.getAktiverBaustein().onDraw(canvas);

        List<Quadrat> quadrate = new ArrayList<Quadrat>(spielfeld.getPlatzierteQuadrate());

        for (Quadrat quadrat : quadrate)
            quadrat.onDraw(canvas);
    }

//    private void drawGitter(Canvas canvas) {
//
//        Paint paint = new Paint();
//        paint.setColor(R.color.gitter);
//
//        for (int zeilePixel = quadratSeitenlaenge; zeilePixel < getHeight(); zeilePixel += quadratSeitenlaenge)
//            canvas.drawRect(0, zeilePixel-1, getWidth(), zeilePixel+1, paint);
//
//        for (int spaltePixel = quadratSeitenlaenge; spaltePixel < getWidth(); spaltePixel += quadratSeitenlaenge)
//            canvas.drawRect(spaltePixel-1, 0, spaltePixel+1, getHeight(), paint);
//    }

    public void anzeigeAktualisieren() {
        postInvalidate(); // View zwingen, neu zu zeichnen
    }

}
