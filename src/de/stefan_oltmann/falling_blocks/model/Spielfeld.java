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
import java.util.List;

/**
 * @author Stefan Oltmann
 */
public class Spielfeld {

    /**
     * Das Spielfeld selbst kenn ausschließlich Quadrate und weiß nichts von
     * Bausteinen. Die Quadrate, welche sich gerade auf dem Weg nach unten
     * befinden, werden von einem Baustein-Objekt gehalten und manipuliert. Dies
     * jedoch spielt gar keine Rolle für die übrigen Logiken des Spielfelds
     * (Reihe voll und entfernen, etc.)
     */
    private List<Quadrat> platzierteQuadrate = new ArrayList<Quadrat>();

    /**
     * In diesem Array werden die platzierten Quadrate gehalten.
     */
    private Quadrat[][]   platzierteQuadrateArray;

    private int           breite             = 0;
    private int           hoehe              = 0;

    private Baustein      aktiverBaustein    = null;

    public Spielfeld(int breite, int hoehe) {
        this.breite = breite;
        this.hoehe = hoehe;
        platzierteQuadrateArray = new Quadrat[breite + 1][hoehe + 1];
    }

    public int getBreite() {
        return breite;
    }

    public int getHoehe() {
        return hoehe;
    }

    public List<Quadrat> getPlatzierteQuadrate() {
        return Collections.unmodifiableList(platzierteQuadrate);
    }

    public void setAktiverBaustain(Baustein baustein) {
        if (aktiverBaustein == baustein)
            throw new IllegalArgumentException("Dieser Baustein ist bereits gesetzt!");
        aktiverBaustein = baustein;
//        platzierteQuadrate.addAll(baustein.getQuadrate());
    }

    public Baustein getAktiverBaustein() {
        return aktiverBaustein;
    }

    /**
     * Wird der aktive Baustein "aufgelöst", werden seine Quadrate inaktiv und
     * wandern mit in ein Array, welches dem schnellen Zugriff auf Kästchen
     * dient.
     */
    public void resetAktiverBaustein() {
        for (Quadrat quadrat : aktiverBaustein.getQuadrate()) {
            platzierteQuadrate.add(quadrat);
            platzierteQuadrateArray[quadrat.getRasterX()][quadrat.getRasterY()] = quadrat;
        }

        aktiverBaustein = null;
    }

    public boolean isImSpielfeld(Quadrat quadrat) {
        return isImSpielfeld(quadrat.getRasterX(), quadrat.getRasterY());
    }

    public boolean isImSpielfeld(int rasterX, int rasterY) {
        return !(rasterX < 0 || rasterX >= breite || rasterY < 0 || rasterY > hoehe);
    }

    public Quadrat getPlatziertesQuadrat(int rasterX, int rasterY) {

        if (rasterX < 0 || rasterX > breite)
            throw new IllegalArgumentException("Parameter 'rasterX' muss zwischen 0 und " + breite + " liegen: " + rasterX);

        if (rasterY < 0 || rasterY > hoehe)
            throw new IllegalArgumentException("Parameter 'rasterY' muss zwischen 0 und " + hoehe + " liegen: " + rasterY);

        return platzierteQuadrateArray[rasterX][rasterY];
    }

    /**
     * Prüfen, ob der Baustein weiter fallen kann. Dies ist der Fall, wenn alle
     * seine Quadrate fallen können. Kann dieser das nicht, dann ist es wohl
     * vorbei. :)
     */
    public boolean isKannFallen(Baustein baustein) {

        for (Quadrat quadrat : baustein.getQuadrate())
            if (!isKannFallen(quadrat))
                return false;
        return true;
    }

    public boolean isKannLinks(Baustein baustein) {

        for (Quadrat quadrat : baustein.getQuadrate())
            if (!isKannLinks(quadrat))
                return false;
        return true;
    }

    public boolean isKannRechts(Baustein baustein) {

        for (Quadrat quadrat : baustein.getQuadrate())
            if (!isKannRechts(quadrat))
                return false;
        return true;
    }

    /**
     * Bausteine können sich nur drehen, wenn sie dadurch nicht außerhalb des
     * Spielfelds kommen. Um dies festzustellen, kopieren wir den aktuellen
     * Baustein und prüfen die einzelnen Quadrate ab.
     */
    public boolean isKannDrehen(Baustein baustein) {

        /*
         * Befindet sich der Baustein aus irgendeinem Grunde (z.B. gerade
         * erstellt) noch zum Teil außerhalb des Spielfelds, kann er sowieso
         * nicht drehen.
         */
        for (Quadrat quadrat : baustein.getQuadrate())
            if (!isImSpielfeld(quadrat))
                return false;

        /*
         * Die Drehung wir "simuliert". Ein geklonter Baustein wird gedreht und
         * dessen Quadrate ausgewertet.
         */

        Baustein bausteinKopie = baustein.erstelleKopie();
        bausteinKopie.drehen();

        /* Außerhalb des Spielfelds? Kein Drehen... */
        for (Quadrat quadrat : bausteinKopie.getQuadrate())
            if (!isImSpielfeld(quadrat))
                return false;

        /* Wird die Drehung einen platzierten Stein berühren? Kein Drehen... */
        for (Quadrat quadrat : bausteinKopie.getQuadrate()) {
            if (getPlatziertesQuadrat(quadrat.getRasterX(), quadrat.getRasterY()) != null)
                return false;
        }

        return true;
    }

    /**
     * Ein Quadrat kann fallen, wenn unter ihm keines ist und auch der Boden
     * noch nicht erreicht wurde.
     */
    private boolean isKannFallen(Quadrat quadrat) {

        if (quadrat.getRasterY() >= hoehe)
            return false;

        if (!isImSpielfeld(quadrat))
            return true;

        return getPlatziertesQuadrat(quadrat.getRasterX(), quadrat.getRasterY() + 1) == null;
    }

    public boolean isKannLinks(Quadrat quadrat) {

        if (quadrat.getRasterX() <= 0)
            return false;

        if (!isImSpielfeld(quadrat))
            return false;

        return getPlatziertesQuadrat(quadrat.getRasterX() - 1, quadrat.getRasterY()) == null;
    }

    public boolean isKannRechts(Quadrat quadrat) {

        if (quadrat.getRasterX() + 1 >= breite)
            return false;

        if (!isImSpielfeld(quadrat))
            return false;

        return getPlatziertesQuadrat(quadrat.getRasterX() + 1, quadrat.getRasterY()) == null;
    }

    /**
     * Schließt volle Reihen und gibt die Anzahl der geschlossenen Reihen
     * zurück.
     */
    public int handleVolleReihen() {

        int geloeschteReihen = 0;

        boolean etwasGeloescht;

        do {

            etwasGeloescht = false;

            for (int reihe = hoehe; reihe > 0; reihe--) {

                boolean reiheVoll = true;

                for (int spalte = 0; spalte < breite; spalte++) {
                    if (platzierteQuadrateArray[spalte][reihe] == null) {
                        reiheVoll = false;
                        break;
                    }
                }

                if (reiheVoll) {
                    loescheReihe(reihe);
                    geloeschteReihen++;
                    etwasGeloescht = true;
                    continue;
                }
            }

        } while (etwasGeloescht);

        return geloeschteReihen;
    }

    private void loescheReihe(int reihe) {

        for (int y = reihe; y > 0; y--) {
            for (int x = 0; x < breite; x++) {

                /*
                 * Quadrate in der zu entfernenden Reihe löschen. In allen
                 * weiteren Reihen nur einen nach unten versetzen.
                 */
                if (y == reihe)
                    platzierteQuadrate.remove(platzierteQuadrateArray[x][y]);

                platzierteQuadrateArray[x][y] = platzierteQuadrateArray[x][y - 1];

                Quadrat quadrat = platzierteQuadrateArray[x][y];

                if (quadrat != null)
                    quadrat.setPosition(x, y);
            }
        }

    }

}
