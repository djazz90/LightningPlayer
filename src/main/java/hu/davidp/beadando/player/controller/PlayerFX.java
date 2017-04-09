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
import hu.davidp.beadando.player.model.PlayerSettings;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Player osztály. A program lejátszója, MP3 fájlok lejátszását végzi. Működését
 * a {@link MediaPlayer} objektum segítségével végzi. Lejátszólistát a
 * {@link Model}-ből vesz.
 *
 * @author Pintér Dávid
 */
@Slf4j
public final class PlayerFX {
    /**
     * {@link MediaPlayer} objektum.
     */
    private MediaPlayer mp;

    /**
     * Az aktuális {@link Media} objektum, amit éppen játszik a lejátszó.
     */
    private Media actualMedia;

    /**
     * Igaz, ha a lejátszóban van {@link Media}.
     */
    private boolean hasMedia;

    /**
     * Az aktuálisan játszott lejátszólista elem sorszáma.
     */
    private int actualElementInPlaylist;

    /**
     * A lejátszó példánya.
     */
    private static PlayerFX instance = null;

    @Getter
    @Setter
    private static Scene playerScene;

    @Getter
    @Setter
    private static Stage playerStage;

    @Getter
    @Setter
    private static Model playerModel;

    @Getter
    @Setter
    @SuppressWarnings("PMD.ImmutableField")
    // a pmd nem látja, hogy getelve és setelve van a kódban.
    private ObservableList<PlaylistElement> actualPlaylist;

    @Getter
    @Setter(AccessLevel.NONE)
    private static PlayerState state = PlayerState.STOPPED;

    public enum PlayerState {
        PLAYING, STOPPED, PAUSED;

        public String prettyPrintName() {
            switch (this) {
                case PLAYING:
                    return "Playing";
                case STOPPED:
                    return "Stopped";
                case PAUSED:
                    return "Paused";
                default:
                    return null;
            }
        }
    }

    /**
     * Singleton konstruktor a lejátszó számára.
     */
    private PlayerFX() {

        hasMedia = false;
        PlayerSettings.initialize();

        actualPlaylist = playerModel.getPlaylist();

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
     */
    public void autonext(final FXMLController fxc) {
        fxc.setAvailability();

        mp.setOnEndOfMedia(
            () -> {

                if (actualElementInPlaylist != actualPlaylist.size() - 1) {
                    next();
                    fxc.getPlayListTable().getSelectionModel().select(actualElementInPlaylist);

                    autonext(fxc);

                } else {

                    mp.stop();
                    state = PlayerState.STOPPED;
                    mp = null;
                    hasMedia = false;
                    fxc.setAvailability();
                }

            });
    }

    /**
     * Az aktuális lejátszólista elem lejátszása.
     */
    public void play() {
        state = PlayerState.PLAYING;
        Platform.runLater(
            () -> {
                mp.setVolume(PlayerSettings.getVolumeLevel());
                mp.play();
            });
    }

    /**
     * A lejátszás szüneteltetése.
     */
    public void pause() {
        state = PlayerState.PAUSED;
        mp.pause();

    }

    /**
     * A lejátszás megállítása.
     */
    public void stop() {
        state = PlayerState.STOPPED;
        mp.stop();

    }

    /**
     * A következő lejátszólista elemre lép.
     */
    public void next() {
        mp.stop();
        actualElementInPlaylist++;
        actualMedia = actualPlaylist
            .get(actualElementInPlaylist).asMedia();
        mp = new MediaPlayer(actualMedia);
        logActualPlaylistElement();
        play();
    }

    /**
     * Az előző lejátszólista elemre lép.
     */
    public void prev() {
        mp.stop();
        actualElementInPlaylist--;
        actualMedia = actualPlaylist
            .get(actualElementInPlaylist).asMedia();
        mp = new MediaPlayer(actualMedia);
        logActualPlaylistElement();
        play();

    }

    private void logActualPlaylistElement() {
        //get calling method name and log it
        final StackTraceElement callingStackTraceElement = Thread.currentThread().getStackTrace()[2];
        final String callingMethod = callingStackTraceElement.getMethodName();

        log.info("Calling method: {}", callingMethod);
        log.info("Actual playlist element:");
        StringBuilder sb = new StringBuilder();
        sb.append(actualPlaylist.get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist())
            .append(" - ").append(actualPlaylist.get(PlayerFX.getInstance().getActualElementInPlaylist())
            .getTitle()).append(" - ")
            .append(actualPlaylist.get(PlayerFX.getInstance().getActualElementInPlaylist())
                .getAlbum());
        log.info(sb.toString());

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
        this.hasMedia = true;
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

}
