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

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import de.stefan_oltmann.falling_blocks.SteuerkreuzView.BenutzerEingabeListener;
import de.stefan_oltmann.falling_blocks.model.Baustein;
import de.stefan_oltmann.falling_blocks.model.BenutzerEingabe;
import de.stefan_oltmann.falling_blocks.model.Spielfeld;

/**
 * @author Stefan Oltmann
 */
public class SpielActivity extends Activity implements BenutzerEingabeListener {

    public static final int SPIELFELD_BREITE = 10;

    private Handler         handler          = new Handler();

    private SpielfeldView   spielfeldView;
    private Spielfeld       spielfeld;

    private int             punkte           = 0;
    private int             level            = 1;
    private int             reihen           = 0;

    private VorschauView    vorschauView;

    private SteuerkreuzView steuerkreuzView;

    private PunkteView      punkteTextView;
    private LevelView       levelTextView;
    private ReihenView      reihenTextView;

    private Baustein        naechsterBaustein;

    private boolean         pausiert         = false;

    public static Bitmap    QUADRAT_BLATT_BITMAP;
    public static Bitmap    QUADRAT_BLITZ_BITMAP;
    public static Bitmap    QUADRAT_BLUME_BITMAP;
    public static Bitmap    QUADRAT_FEUER_BITMAP;
    public static Bitmap    QUADRAT_SONNE_BITMAP;
    public static Bitmap    QUADRAT_TORNADO_BITMAP;
    public static Bitmap    QUADRAT_WASSER_BITMAP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        /* Abbildungen für die Steine laden */
        QUADRAT_BLATT_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.quadrat_blatt);
        QUADRAT_BLITZ_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.quadrat_blitz);
        QUADRAT_BLUME_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.quadrat_blume);
        QUADRAT_FEUER_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.quadrat_feuer);
        QUADRAT_SONNE_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.quadrat_sonne);
        QUADRAT_TORNADO_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.quadrat_tornado);
        QUADRAT_WASSER_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.quadrat_wasser);

        /* Views konfigurieren */
        spielfeldView = (SpielfeldView) findViewById(R.id.spielFeldView);

        vorschauView = (VorschauView) findViewById(R.id.vorschauView);

        steuerkreuzView = (SteuerkreuzView) findViewById(R.id.steuerkreuzView);
        steuerkreuzView.addBenutzerEingabeListener(this);

        punkteTextView = (PunkteView) findViewById(R.id.punkteView);
        levelTextView = (LevelView) findViewById(R.id.levelView);
        reihenTextView = (ReihenView) findViewById(R.id.reihenView);
    }

    public void setupSpielfeldAndstartGameLoop() {

        int spielfeldBreite = SPIELFELD_BREITE;
        int pixelSeitenlaenge = spielfeldView.getWidth() / spielfeldBreite;
        int spielfeldHoehe = (spielfeldView.getHeight() / pixelSeitenlaenge) - 1;

        spielfeld = new Spielfeld(spielfeldBreite, spielfeldHoehe);
        spielfeldView.init(spielfeld);

        new Thread(new GameLoopRunnable()).start();
    }

    public void onBenutzerEingabe(BenutzerEingabe benutzerEingabe) {

        Baustein baustein = spielfeld.getAktiverBaustein();

        if (baustein == null || benutzerEingabe == null)
            return;

        switch (benutzerEingabe) {
            case FALLEN:
                if (spielfeld.isKannFallen(baustein))
                    baustein.falle();
                break;
            case LINKS_SCHIEBEN:
                if (spielfeld.isKannLinks(baustein))
                    baustein.schiebeLinks();
                break;
            case RECHTS_SCHIEBEN:
                if (spielfeld.isKannRechts(baustein))
                    baustein.schiebeRechts();
                break;
            case DREHEN:
                if (spielfeld.isKannDrehen(baustein))
                    baustein.drehen();
                break;
        }

        spielfeldView.anzeigeAktualisieren();
    }

    public int berechneWartezeitZwischenFallen() {
        return Math.max(100, 1000 - (level * 100));
    }

    /**
     * Ein Thread für das Herabfallen-Lassen der Bausteine und Auswertung, ob
     * ein neuer benötigt wird.
     */
    private class GameLoopRunnable implements Runnable {

        @Override
        public void run() {

            naechsterBaustein = Baustein.createRandom();

            while (!isFinishing()) {

                while (pausiert) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {
                    }
                }

                Baustein baustein = spielfeld.getAktiverBaustein();

                /*
                 * Ist kein Baustein aktiv, wird ein neuer in das Spielfeld
                 * gegeben.
                 */
                if (baustein == null) {
                    baustein = naechsterBaustein;
                    spielfeld.setAktiverBaustain(baustein);

                    naechsterBaustein = Baustein.createRandom();
                    vorschauView.setBaustein(naechsterBaustein);

                    /*
                     * Mittelpunkt der Figur in die Mitte des Feldes
                     * verschieben.
                     */
                    for (int i = 0; i < (spielfeld.getBreite() / 2) - 1; i++)
                        baustein.schiebeRechts();

                    /*
                     * Wenn ein Frisch erstellter Baustein nicht mehr fallen
                     * kann, ist das Spiel vorbei.
                     */
                    if (!spielfeld.isKannFallen(baustein))
                        break; /* Quit Game Loop = Game Over */
                }

                if (spielfeld.isKannFallen(baustein))
                    baustein.falle();
                else {
                    punkte += (4 + level);
                    spielfeld.resetAktiverBaustein();
                }

                int reihenEntfernt = spielfeld.handleVolleReihen();

                if (reihenEntfernt > 0) {
                    reihen += reihenEntfernt;
                    punkte += (reihenEntfernt + level) * reihenEntfernt;

                    /* Alle 10 Reihen das Level hochsetzen */
                    if (reihen % 10 == 0)
                        level++;
                }

                aktualisiereAnzeige();

                try {
                    Thread.sleep(berechneWartezeitZwischenFallen());
                } catch (InterruptedException ignore) {
                }
            }

            if (!isFinishing())
                handler.post(new Runnable() {

                    @Override
                    public void run() {

                        AlertDialog alertDialog = new AlertDialog.Builder(SpielActivity.this)
                                .setTitle(getResources().getText(R.string.game_over))
                                .setMessage(getResources().getText(R.string.punkte) + ": " + punkte)
                                .create();

                        alertDialog.show();
                    }
                });
        }
    }

    public void aktualisiereAnzeige() {

        handler.post(new Runnable() {

            @Override
            public void run() {
                punkteTextView.setDisplayText(String.valueOf(punkte));
                levelTextView.setDisplayText(String.valueOf(level));
                reihenTextView.setDisplayText(String.valueOf(reihen));
            }
        });

        vorschauView.anzeigeAktualisieren();
        spielfeldView.anzeigeAktualisieren();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pausiert = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausiert = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausiert = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        BenutzerEingabe benutzerEingabe = null;

        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                benutzerEingabe = BenutzerEingabe.DREHEN;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                benutzerEingabe = BenutzerEingabe.LINKS_SCHIEBEN;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                benutzerEingabe = BenutzerEingabe.RECHTS_SCHIEBEN;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                benutzerEingabe = BenutzerEingabe.FALLEN;
                break;
        }

        if (benutzerEingabe == null) {
            return super.onKeyDown(keyCode, event);
        }

        onBenutzerEingabe(benutzerEingabe);

        return true;
    }

    @Override
    public void onSteuerkreuzEingabe(BenutzerEingabe benutzerEingabe) {
        onBenutzerEingabe(benutzerEingabe);
    }

}