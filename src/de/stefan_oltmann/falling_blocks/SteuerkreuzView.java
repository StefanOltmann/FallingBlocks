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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import de.stefan_oltmann.falling_blocks.model.BenutzerEingabe;

/**
 * @author Stefan Oltmann
 */
public class SteuerkreuzView extends View {

    private Bitmap                        steuerkreuzBitmap;

    private List<BenutzerEingabeListener> listeners = new ArrayList<BenutzerEingabeListener>();

    private Rect                          pfeilObenRect;
    private Rect                          pfeilUntenRect;
    private Rect                          pfeilLinksRect;
    private Rect                          pfeilRechtsRect;

    public SteuerkreuzView(Context context, AttributeSet attrs) {
        super(context, attrs);
        steuerkreuzBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.steuerkreuz);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // @formatter:off
        pfeilObenRect   = new Rect(     w/3,       0, (w/3)*2,     h/3 );
        pfeilUntenRect  = new Rect(     w/3, (w/3)*2, (w/3)*2,       h );
        pfeilLinksRect  = new Rect(       0,     h/3,     w/3, (h/3)*2 );
        pfeilRechtsRect = new Rect( (w/3)*2,     h/3,       w, (h/3)*2 );
        // @formatter:on
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(steuerkreuzBitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);
    }

    private volatile boolean running = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            running = true;

            BenutzerEingabe benutzerEingabe = null;

            if (pfeilObenRect.contains((int) event.getX(), (int) event.getY()))
                benutzerEingabe = BenutzerEingabe.DREHEN;
            else if (pfeilUntenRect.contains((int) event.getX(), (int) event.getY()))
                benutzerEingabe = BenutzerEingabe.FALLEN;
            else if (pfeilLinksRect.contains((int) event.getX(), (int) event.getY()))
                benutzerEingabe = BenutzerEingabe.LINKS_SCHIEBEN;
            else if (pfeilRechtsRect.contains((int) event.getX(), (int) event.getY()))
                benutzerEingabe = BenutzerEingabe.RECHTS_SCHIEBEN;

            final BenutzerEingabe eingabe = benutzerEingabe;

            /* Informiere alle Zuhörer. */
            if (benutzerEingabe != null) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (running) {

                            for (BenutzerEingabeListener listener : listeners)
                                listener.onSteuerkreuzEingabe(eingabe);

                            try {

                                int sleepTime = 200;

                                if (eingabe == BenutzerEingabe.DREHEN)
                                    sleepTime = 500;
                                else if (eingabe == BenutzerEingabe.FALLEN)
                                    sleepTime = 100;

                                Thread.sleep(sleepTime);
                            } catch (InterruptedException ignore) {
                            }
                        }
                    }
                });

                thread.start();
            }

            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            running = false;
            return true;
        }

        return false;
    }

    public void addBenutzerEingabeListener(BenutzerEingabeListener listener) {
        this.listeners.add(listener);
    }

    public static interface BenutzerEingabeListener {
        void onSteuerkreuzEingabe(BenutzerEingabe benutzerEingabe);
    }

}
