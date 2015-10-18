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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import de.stefan_oltmann.falling_blocks.SpielActivity;

/**
 * @author Stefan Oltmann
 */
public class Baustein {

    private BausteinTyp   typ;
    private List<Quadrat> quadrate;
    private Quadrat       mittelpunkt;

    private Baustein(BausteinTyp typ, List<Quadrat> quadrate, Quadrat mittelpunkt, Bitmap bitmap) {
        this.typ = typ;
        this.quadrate = quadrate;
        this.mittelpunkt = mittelpunkt;
        for (Quadrat quadrat : quadrate)
            quadrat.setBitmap(bitmap);
    }

    public List<Quadrat> getQuadrate() {
        return Collections.unmodifiableList(quadrate);
    }

    public Quadrat getMittelpunkt() {
        return mittelpunkt;
    }

    public void falle() {
        for (Quadrat quadrat : quadrate)
            quadrat.setPosition(quadrat.getRasterX(), quadrat.getRasterY() + 1);
    }

    public void schiebeLinks() {
        for (Quadrat quadrat : quadrate)
            quadrat.setPosition(quadrat.getRasterX() - 1, quadrat.getRasterY());
    }

    public void schiebeRechts() {
        for (Quadrat quadrat : quadrate)
            quadrat.setPosition(quadrat.getRasterX() + 1, quadrat.getRasterY());
    }

    public void drehen() {

        if (typ == BausteinTyp.O)
            return;

        dreheNachbarn(mittelpunkt);
    }

    private void dreheNachbarn(Quadrat quadrat) {

        /* Derzeitigen Zustand abfragen und sichern */
        Quadrat nachbarOben = quadrat.getNachbarOben();
        Quadrat nachbarRechts = quadrat.getNachbarRechts();
        Quadrat nachbarUnten = quadrat.getNachbarUnten();
        Quadrat nachbarLinks = quadrat.getNachbarLinks();

        /* Beziehungen löschen */
        quadrat.setNachbarOben(null);
        quadrat.setNachbarRechts(null);
        quadrat.setNachbarUnten(null);
        quadrat.setNachbarLinks(null);

        if (nachbarOben != null) {

            /* Uns als Nachbarn löschen, damit die Rekursion nicht zurück kommt. */
            nachbarOben.setNachbarUnten(null);

            /*
             * Der Nachbar muss bereits vor der Drehung die neuen Koordinaten
             * haben, damit er korrekt rechnen kann.
             */
            nachbarOben.setPosition(quadrat.getRasterX() + 1, quadrat.getRasterY());

            dreheNachbarn(nachbarOben);

            /* Wer vorher oben war, wird zu rechts */
            quadrat.setNachbarRechts(nachbarOben);
        }

        if (nachbarRechts != null) {

            /* Uns als Nachbarn löschen, damit die Rekursion nicht zurück kommt. */
            nachbarRechts.setNachbarLinks(null);

            /*
             * Der Nachbar muss bereits vor der Drehung die neuen Koordinaten
             * haben, damit er korrekt rechnen kann.
             */
            nachbarRechts.setPosition(quadrat.getRasterX(), quadrat.getRasterY() + 1);

            dreheNachbarn(nachbarRechts);

            /* Wer vorher rechts war, kommt nach unten */
            quadrat.setNachbarUnten(nachbarRechts);
        }

        if (nachbarUnten != null) {

            /* Uns als Nachbarn löschen, damit die Rekursion nicht zurück kommt. */
            nachbarUnten.setNachbarOben(null);

            /*
             * Der Nachbar muss bereits vor der Drehung die neuen Koordinaten
             * haben, damit er korrekt rechnen kann.
             */
            nachbarUnten.setPosition(quadrat.getRasterX() - 1, quadrat.getRasterY());

            dreheNachbarn(nachbarUnten);

            /* Wer vorher unten war, kommt nach links */
            quadrat.setNachbarLinks(nachbarUnten);
        }

        if (nachbarLinks != null) {

            /* Uns als Nachbarn löschen, damit die Rekursion nicht zurück kommt. */
            nachbarLinks.setNachbarRechts(null);

            /*
             * Der Nachbar muss bereits vor der Drehung die neuen Koordinaten
             * haben, damit er korrekt rechnen kann.
             */
            nachbarLinks.setPosition(quadrat.getRasterX(), quadrat.getRasterY() - 1);

            dreheNachbarn(nachbarLinks);

            /* Wer vorher links war, kommt nach oben */
            quadrat.setNachbarOben(nachbarLinks);
        }
    }

    public void onDraw(Canvas canvas) {
        for (Quadrat quadrat : quadrate)
            quadrat.onDraw(canvas);
    }

    public Baustein erstelleKopie() {

        Quadrat cloneMittelpunkt = null;

        List<Quadrat> cloneQuadrate = new ArrayList<Quadrat>();

        Map<Quadrat, Quadrat> originalToCloneMap = new HashMap<Quadrat, Quadrat>();
        Map<Quadrat, Quadrat> cloneToOriginalMap = new HashMap<Quadrat, Quadrat>();

        for (Quadrat quadrat : quadrate) {

            Quadrat cloneQuadrat = new Quadrat(quadrat.getRasterX(), quadrat.getRasterY());

            cloneQuadrate.add(cloneQuadrat);

            if (mittelpunkt == quadrat)
                cloneMittelpunkt = cloneQuadrat;

            originalToCloneMap.put(quadrat, cloneQuadrat);
            cloneToOriginalMap.put(cloneQuadrat, quadrat);
        }

        for (Quadrat cloneQuadrat : cloneQuadrate) {

            Quadrat originalQuadrat = cloneToOriginalMap.get(cloneQuadrat);

            cloneQuadrat.setNachbarOben(originalToCloneMap.get(originalQuadrat.getNachbarOben()));
            cloneQuadrat.setNachbarUnten(originalToCloneMap.get(originalQuadrat.getNachbarUnten()));
            cloneQuadrat.setNachbarLinks(originalToCloneMap.get(originalQuadrat.getNachbarLinks()));
            cloneQuadrat.setNachbarRechts(originalToCloneMap.get(originalQuadrat.getNachbarRechts()));
        }

        return new Baustein(typ, cloneQuadrate, cloneMittelpunkt, quadrate.get(0).getBitmap());
    }

    @Override
    public String toString() {
        return "Baustein '" + typ + "' [" + quadrate + "]";
    }

    private enum BausteinTyp {

        // @formatter:off
        I, J, L, O, S, T, Z;
        // @formatter:on
    }

    /*
     * Statische Builder-Methoden
     */

    public static Baustein createRandom() {

        BausteinTyp[] steinTypen = BausteinTyp.values();

        int zufall = new Random().nextInt(steinTypen.length);

        return create(steinTypen[zufall]);
    }

    public static Baustein create(BausteinTyp bausteinTyp) {

        // @formatter:off
        switch (bausteinTyp) {
            case I: return createI();
            case J: return createJ();
            case L: return createL();
            case O: return createO();
            case S: return createS();
            case T: return createT();
            case Z: return createZ();
        }
        // @formatter:on

        throw new IllegalArgumentException("Ung�ltiger Typ: " + bausteinTyp);
    }

    public static Baustein createI() {

        List<Quadrat> quadrate = new ArrayList<Quadrat>();

        Quadrat links = new Quadrat(0, 0);
        Quadrat mittelPunkt = new Quadrat(1, 0);
        Quadrat rechts1 = new Quadrat(2, 0);
        Quadrat rechts2 = new Quadrat(3, 0);

        mittelPunkt.setNachbarRechts(rechts1);
        rechts1.setNachbarLinks(mittelPunkt);
        rechts1.setNachbarRechts(rechts2);
        rechts2.setNachbarLinks(rechts1);
        links.setNachbarRechts(mittelPunkt);
        mittelPunkt.setNachbarLinks(links);

        quadrate.add(links);
        quadrate.add(mittelPunkt);
        quadrate.add(rechts1);
        quadrate.add(rechts2);

        return new Baustein(BausteinTyp.I, quadrate, mittelPunkt, SpielActivity.QUADRAT_WASSER_BITMAP);
    }

    public static Baustein createJ() {

        List<Quadrat> quadrate = new ArrayList<Quadrat>();

        Quadrat mittelPunkt = new Quadrat(0, 0);
        Quadrat oben = new Quadrat(0, -1);
        Quadrat rechts1 = new Quadrat(1, 0);
        Quadrat rechts2 = new Quadrat(2, 0);

        mittelPunkt.setNachbarOben(oben);
        oben.setNachbarUnten(mittelPunkt);
        mittelPunkt.setNachbarRechts(rechts1);
        rechts1.setNachbarLinks(mittelPunkt);
        rechts1.setNachbarRechts(rechts2);
        rechts2.setNachbarLinks(rechts1);

        quadrate.add(mittelPunkt);
        quadrate.add(oben);
        quadrate.add(rechts1);
        quadrate.add(rechts2);

        return new Baustein(BausteinTyp.J, quadrate, mittelPunkt, SpielActivity.QUADRAT_FEUER_BITMAP);
    }

    public static Baustein createL() {

        List<Quadrat> quadrate = new ArrayList<Quadrat>();

        Quadrat links2 = new Quadrat(0, 0);
        Quadrat links1 = new Quadrat(1, 0);
        Quadrat mittelPunkt = new Quadrat(2, 0);
        Quadrat oben = new Quadrat(2, -1);

        links2.setNachbarRechts(links1);
        links1.setNachbarLinks(links2);
        links1.setNachbarRechts(mittelPunkt);
        mittelPunkt.setNachbarLinks(links1);
        mittelPunkt.setNachbarOben(oben);
        oben.setNachbarUnten(mittelPunkt);

        quadrate.add(links2);
        quadrate.add(links1);
        quadrate.add(mittelPunkt);
        quadrate.add(oben);

        return new Baustein(BausteinTyp.L, quadrate, mittelPunkt, SpielActivity.QUADRAT_BLATT_BITMAP);
    }

    public static Baustein createO() {

        List<Quadrat> quadrate = new ArrayList<Quadrat>();

        Quadrat mittelPunkt = new Quadrat(0, 0);
        Quadrat rechts1 = new Quadrat(1, 0);
        Quadrat oben1 = new Quadrat(0, -1);
        Quadrat oben2 = new Quadrat(1, -1);

        mittelPunkt.setNachbarRechts(rechts1);
        rechts1.setNachbarLinks(mittelPunkt);
        mittelPunkt.setNachbarOben(oben1);
        oben1.setNachbarUnten(mittelPunkt);
        oben1.setNachbarRechts(oben2);
        oben2.setNachbarLinks(oben1);
        rechts1.setNachbarOben(oben2);
        oben2.setNachbarUnten(rechts1);

        quadrate.add(mittelPunkt);
        quadrate.add(rechts1);
        quadrate.add(oben1);
        quadrate.add(oben2);

        return new Baustein(BausteinTyp.O, quadrate, mittelPunkt, SpielActivity.QUADRAT_BLUME_BITMAP);
    }

    public static Baustein createS() {

        List<Quadrat> quadrate = new ArrayList<Quadrat>();

        Quadrat unten2 = new Quadrat(0, 0);
        Quadrat unten1 = new Quadrat(1, 0);
        Quadrat mittelPunkt = new Quadrat(1, -1);
        Quadrat rechts = new Quadrat(2, -1);

        unten2.setNachbarRechts(unten1);
        unten1.setNachbarLinks(unten2);
        mittelPunkt.setNachbarUnten(unten1);
        mittelPunkt.setNachbarRechts(rechts);
        rechts.setNachbarLinks(mittelPunkt);
        unten1.setNachbarOben(mittelPunkt);

        quadrate.add(unten2);
        quadrate.add(unten1);
        quadrate.add(mittelPunkt);
        quadrate.add(rechts);

        return new Baustein(BausteinTyp.S, quadrate, mittelPunkt, SpielActivity.QUADRAT_BLITZ_BITMAP);
    }

    public static Baustein createT() {

        List<Quadrat> quadrate = new ArrayList<Quadrat>();

        Quadrat links = new Quadrat(0, 0);
        Quadrat mittelPunkt = new Quadrat(1, 0);
        Quadrat oben1 = new Quadrat(1, -1);
        Quadrat rechts = new Quadrat(2, 0);

        links.setNachbarRechts(mittelPunkt);
        mittelPunkt.setNachbarLinks(links);
        mittelPunkt.setNachbarOben(oben1);
        mittelPunkt.setNachbarRechts(rechts);
        oben1.setNachbarUnten(mittelPunkt);
        rechts.setNachbarLinks(mittelPunkt);

        quadrate.add(links);
        quadrate.add(mittelPunkt);
        quadrate.add(oben1);
        quadrate.add(rechts);

        return new Baustein(BausteinTyp.T, quadrate, mittelPunkt, SpielActivity.QUADRAT_SONNE_BITMAP);
    }

    public static Baustein createZ() {

        List<Quadrat> quadrate = new ArrayList<Quadrat>();

        Quadrat oben2 = new Quadrat(0, 0);
        Quadrat oben1 = new Quadrat(1, 0);
        Quadrat mittelPunkt = new Quadrat(1, 1);
        Quadrat rechts = new Quadrat(2, 1);

        oben2.setNachbarRechts(oben1);
        oben1.setNachbarLinks(oben2);
        oben1.setNachbarUnten(mittelPunkt);
        mittelPunkt.setNachbarOben(oben1);
        mittelPunkt.setNachbarRechts(rechts);
        rechts.setNachbarLinks(mittelPunkt);

        quadrate.add(oben2);
        quadrate.add(oben1);
        quadrate.add(mittelPunkt);
        quadrate.add(rechts);

        return new Baustein(BausteinTyp.Z, quadrate, mittelPunkt, SpielActivity.QUADRAT_TORNADO_BITMAP);
    }

}
