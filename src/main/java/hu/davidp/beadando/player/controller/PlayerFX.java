package hu.davidp.beadando.player.controller;

/*
 * #%L
 * LightningPlayer
 * %%
 * Copyright (C) 2015 Debreceni Egyetem Informatikai Kar
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Player osztály. A program lejátszója, MP3 fájlok lejátszását végzi. Működését
 * a {@link MediaPlayer} objektum segítségével végzi. Lejátszólistát a
 * {@link Model}-ből vesz.
 *
 * @author Pintér Dávid
 */
public final class PlayerFX {
    /**
     * Logger objektum naplózáshoz.
     */
    private static Logger logger = LoggerFactory.getLogger(PlayerFX.class);
    /**
     * {@link MediaPlayer} objektum.
     */
    private MediaPlayer mp;

    /**
     * Az aktuális {@link Media} objektum, amit éppen játszik a lejátszó.
     */
    private Media actualMedia;
    /**
     * Igaz, ha a Viewban a Play gomb felirata Play.
     */
    private boolean playButtonTextIsPlay;

    /**
     * Igaz, ha a lejátszóban van {@link Media}.
     */
    private boolean hasMedia;

    /**
     * Az aktuális lejátszólista mérete, ahonnan a lejátszó játszik.
     */
    private int actualPlaylistSize;
    /**
     * Az aktuálisan játszott lejátszólista elem sorszáma.
     */
    private int actualElementInPlaylist;

    /**
     * A lejátszó példánya.
     */
    private static PlayerFX instance = null;

    private static Scene playerScene;

    private static Stage playerStage;

    /**
     * Singleton konstruktor a lejátszó számára.
     */
    private PlayerFX() {

        this.playButtonTextIsPlay = true;
        this.hasMedia = false;

    }

    /**
     * Visszaadja az aktuális lejátszó objektumot.
     *
     * @return a lejátszólista objektum
     */
    public static PlayerFX getInstance() {
        // if (playerScene == null) {
        // throw new RuntimeException("Nincs beállítva scene!");
        // }
        synchronized (PlayerFX.class) {
            if (instance == null) {
                instance = new PlayerFX();
            }
            return instance;
        }

    }

    /**
     * A lejátszó automatikusan a következő lejátszólista elemre lép, ha az
     * aktuális véget ér.
     *
     * @param m a {@link Model} osztály egy példánya
     */
    public void autonext(final Model m, final FXMLController fxc) {
        fxc.setAvailability();
        try {
            mp.setOnEndOfMedia(
                () -> {

                    if (actualElementInPlaylist != actualPlaylistSize - 1) {
                        next(m);
                        fxc.getPlayListTable().getSelectionModel().select(actualElementInPlaylist);

                        logger.info("Auto next:");
                        logger.info("Actual playlist element:");
                        StringBuffer sb = new StringBuffer();
                        sb.append(m.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist())
                            .append(" - ").append(m.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist())
                            .getTitle()).append(" - ")
                            .append(m.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist())
                                .getAlbum());
                        logger.info(sb.toString());
                        autonext(m, fxc);

                    } else {

                        mp.stop();
                        playButtonTextIsPlay = true;
                        fxc.setAvailability();
                    }

                });

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Az aktuális lejátszólista elem lejátszása.
     */
    public void play() {
        playButtonTextIsPlay = false;
        Platform.runLater(() -> {
            mp.play();
        });
    }

    /**
     * A lejátszás szüneteltetése.
     */
    public void pause() {

        PlayerFX.this.playButtonTextIsPlay = true;
        mp.pause();

    }

    /**
     * A lejátszás megállítása.
     */
    public void stop() {

        playButtonTextIsPlay = true;

        mp.stop();

    }

    /**
     * A következő lejátszólista elemre lép.
     *
     * @param m a {@link Model} osztály egy példánya
     */
    public void next(final Model m) {
        mp.stop();
        actualElementInPlaylist++;
        actualMedia = m
            .getPlaylist()
            .get(actualElementInPlaylist).asMedia();

        mp = new MediaPlayer(actualMedia);
        playButtonTextIsPlay = false;
        play();
    }

    /**
     * Az előző lejátszólista elemre lép.
     *
     * @param m a {@link Model} osztály egy példánya
     */
    public void prev(final Model m) {
        mp.stop();
        actualElementInPlaylist--;
        actualMedia = m
            .getPlaylist()
            .get(actualElementInPlaylist).asMedia();

        mp = new MediaPlayer(actualMedia);
        playButtonTextIsPlay = false;
        play();

    }

    /**
     * Visszaadja az aktuálisan játszott {@link Media}-t.
     *
     * @return az aktuális {@link Media}
     */
    public Media getActualMedia() {
        return actualMedia;
    }

    /**
     * Beállítja az aktuális {@link Media}-t.
     *
     * @param actualMedia a beállítani kívánt {@link Media}.
     */
    public void setActualMedia(final Media actualMedia) {
        this.actualMedia = actualMedia;
        if (this.hasMedia) {
            mp.stop();
        }
        this.mp = new MediaPlayer(actualMedia);
        this.playButtonTextIsPlay = false;
        this.hasMedia = true;
    }

    /**
     * Visszaadja a {@link #playButtonTextIsPlay} értékét.
     *
     * @return igazzal tér vissza, ha a Play gomb szövege Play
     */
    public boolean isPlayButtonSaysPlay() {
        return playButtonTextIsPlay;
    }

    /**
     * Visszaadja az aktuális lejetszólista méretét.
     *
     * @return az aktuális lejátszólista mérete
     */
    public int getActualPlaylistSize() {
        return actualPlaylistSize;
    }

    /**
     * Visszaadja a {@link #hasMedia} értékét.
     *
     * @return igaz, ha van a lejátszónak {@link Media}-ja
     */
    public boolean hasMedia() {
        return hasMedia;
    }

    /**
     * Beállitja a {@link #hasMedia} értékét.
     *
     * @param hasMedia a {@link #hasMedia} új értéke
     */
    public void setHasMedia(final boolean hasMedia) {
        this.hasMedia = hasMedia;
    }

    /**
     * Beállítja az aktuális lejátszólista méretét a kívánt méretre.
     *
     * @param playlist a kívánt méret
     */
    public void setPlaylistSize(final List<PlaylistElement> playlist) {
        if (playlist != null) {
            actualPlaylistSize = playlist.size();
        } else {
            actualPlaylistSize = 0;
        }

    }

    /**
     * Visszaadja az aktuálisan játszott lejátszólista elem sorszámát.
     *
     * @return a lejátszólista elem sorszáma
     */
    public int getActualElementInPlaylist() {
        return actualElementInPlaylist;
    }

    /**
     * Beállítja az aktuálisan játszott lejátszólista elem sorszámát.
     *
     * @param actualElementInPlaylist a lejátszólista elem sorszáma
     */
    public void setActualElementInPlaylist(final int actualElementInPlaylist) {
        this.actualElementInPlaylist = actualElementInPlaylist;
    }

    /**
     * Visszaadja a {@link MediaPlayer}-t.
     *
     * @return a lejátszó
     */
    public MediaPlayer getMp() {
        return mp;
    }

    public static void setSceneAndStage(final Scene s, final Stage st) {
        playerScene = s;
        playerStage = st;
    }

    public static Scene getPlayerScene() {
        return playerScene;
    }

    public static Stage getPlayerStage() {
        return playerStage;
    }

}
