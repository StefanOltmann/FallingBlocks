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
package de.stefan_oltmann.falling_blocks.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import de.stefan_oltmann.falling_blocks.SpielfeldView;

/**
 * @author Stefan Oltmann
 */
public class Quadrat {

    /**
     * Das Kästchen muss seine Position im Raster kennen. Eine erste Idee war,
     * dass das Quadrat nur seine Relation zu den anderen Quadraten kennt und
     * der Baustein seine Position im Raster; damit könnte die Positon jedes
     * Quadrats im Raster ermittelt werden. Dies funktioniert nur nicht, da das
     * Kästchen irgendwann am Boden liegt, eine Reihe voll ist und der Baustein
     * zerstört wird.
     */
    private int     rasterX;
    private int     rasterY;

    private Bitmap  bitmap;

    private Quadrat nachbarOben;
    private Quadrat nachbarUnten;
    private Quadrat nachbarLinks;
    private Quadrat nachbarRechts;

    public Quadrat(int rasterX, int rasterY) {
        setPosition(rasterX, rasterY);
    }

    public int getRasterX() {
        return rasterX;
    }

    public int getRasterY() {
        return rasterY;
    }

    public void setPosition(int rasterX, int rasterY) {
        this.rasterX = rasterX;
        this.rasterY = rasterY;
    }

    public int getPixelX() {
        return rasterX * SpielfeldView.quadratSeitenlaenge;
    }

    public int getPixelY() {
        return rasterY * SpielfeldView.quadratSeitenlaenge;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Quadrat getNachbarOben() {
        return nachbarOben;
    }

    public void setNachbarOben(Quadrat nachbarOben) {
        this.nachbarOben = nachbarOben;
        if (nachbarOben != null && nachbarOben.getNachbarUnten() != this)
            nachbarOben.setNachbarUnten(this);
    }

    public Quadrat getNachbarUnten() {
        return nachbarUnten;
    }

    public void setNachbarUnten(Quadrat nachbarUnten) {
        this.nachbarUnten = nachbarUnten;
        if (nachbarUnten != null && nachbarUnten.getNachbarOben() != this)
            nachbarUnten.setNachbarOben(this);
    }

    public Quadrat getNachbarLinks() {
        return nachbarLinks;
    }

    public void setNachbarLinks(Quadrat nachbarLinks) {
        this.nachbarLinks = nachbarLinks;
        if (nachbarLinks != null && nachbarLinks.getNachbarRechts() != this)
            nachbarLinks.setNachbarRechts(this);
    }

    public Quadrat getNachbarRechts() {
        return nachbarRechts;
    }

    public void setNachbarRechts(Quadrat nachbarRechts) {
        this.nachbarRechts = nachbarRechts;
        if (nachbarRechts != null && nachbarRechts.getNachbarLinks() != this)
            nachbarRechts.setNachbarLinks(this);
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, new Rect(getPixelX(), getPixelY(), getPixelX() + SpielfeldView.quadratSeitenlaenge, getPixelY() + SpielfeldView.quadratSeitenlaenge), null);
    }

    @Override
    public String toString() {
        return "[" + rasterX + ", " + rasterY + "]";
    }

}
