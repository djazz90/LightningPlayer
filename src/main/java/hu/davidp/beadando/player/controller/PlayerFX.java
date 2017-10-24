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
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Player osztály. A program lejátszója, MP3 fájlok lejátszását végzi. Működését
 * a {@link MediaPlayer} objektum segítségével végzi. Lejátszólistát a
 * {@link Model}-ből vesz.
 *
 * @author Pintér Dávid
 */
@Slf4j
@SuppressWarnings({"PMD.TooManyMethods"})
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
    private int playlistIndex;

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
    private PlayerState state = PlayerState.STOPPED;

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

                if (playlistIndex != actualPlaylist.size() - 1) {
                    switch (PlayerSettings.getNavigationState()) {
                        case REPEAT_SONG:
                            repeatSong();
                            break;
                        case NEXT_SONG:
                            next();
                            break;
                        case SHUFFLE:
                            shuffle();
                            break;
                        default:
                            break;
                    }

                    fxc.getPlayListTable().getSelectionModel().select(playlistIndex);

                    autonext(fxc);

                } else {
                    switch (PlayerSettings.getNavigationState()) {
                        case REPEAT_SONG:
                            repeatSong();
                            return;
                        case REPEAT_PLAYLIST:
                            repeatPlaylist();
                            return;
                        case SHUFFLE:
                            shuffle();
                            return;
                        default:
                            break;
                    }
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

    public void repeatSong() {
        mp.seek(new Duration(0.0));
    }

    /**
     * A következő lejátszólista elemre lép.
     */
    public void next() {
        switch (PlayerSettings.getNavigationState()) {
            case SHUFFLE:
                shuffle();
                break;
            case REPEAT_PLAYLIST:
                repeatPlaylist();
                break;
            default:
                mp.stop();
                playlistIndex++;
                actualMedia = actualPlaylist
                    .get(playlistIndex).asMedia();
                mp = new MediaPlayer(actualMedia);
                logActualPlaylistElement();
                play();
                break;
        }
    }

    /**
     * Az előző lejátszólista elemre lép.
     */
    public void prev() {
        mp.stop();
        playlistIndex--;
        actualMedia = actualPlaylist
            .get(playlistIndex).asMedia();
        mp = new MediaPlayer(actualMedia);
        logActualPlaylistElement();
        play();

    }

    public void repeatPlaylist() {
        mp.stop();
        playlistIndex = playlistIndex == actualPlaylist.size() - 1 ? 0 : playlistIndex + 1;
        actualMedia = actualPlaylist
            .get(playlistIndex).asMedia();
        mp = new MediaPlayer(actualMedia);
        logActualPlaylistElement();
        play();
    }

    public void shuffle() {
        int randomElementIndex;
        //csak akkor van értelme a véletlenszerű lejátszásnak, ha több mint 1 elem van
        //a lejátszólistában. Egyébként ugyanazt fogja játszani, mint ami eddig ment,
        //azaz úgy viselkedik, mintha adott szám ismétés lenne
        if (actualPlaylist.size() > 1) {
            LinkedList<Integer> temp = new LinkedList<>();
            for (int i = 0; i < actualPlaylist.size(); i++) {
                if (i == playlistIndex) {
                    continue;
                }
                temp.add(i);
            }
            randomElementIndex
                = ThreadLocalRandom.current().nextInt(temp.size());
            playlistIndex = temp.get(randomElementIndex);
        }
        mp.stop();

        log.info("Playlist Index: " + playlistIndex);
        actualMedia = actualPlaylist
            .get(playlistIndex).asMedia();
        mp = new MediaPlayer(actualMedia);
        logActualPlaylistElement();
        play();
    }

    public PlaylistElement getActualPlaylistElement() {
        return actualPlaylist.get(playlistIndex);
    }

    private void logActualPlaylistElement() {
        //get calling method name and log it
        final StackTraceElement callingStackTraceElement = Thread.currentThread().getStackTrace()[2];
        final String callingMethod = callingStackTraceElement.getMethodName();

        log.info("Calling method: {}", callingMethod);
        log.info("Actual playlist element:");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(actualPlaylist.get(PlayerFX.getInstance().getPlaylistIndex()).getArtist())
            .append(" - ").append(actualPlaylist.get(PlayerFX.getInstance().getPlaylistIndex())
            .getTitle()).append(" - ")
            .append(actualPlaylist.get(PlayerFX.getInstance().getPlaylistIndex())
                .getAlbum());
        log.info(stringBuilder.toString());

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
    public int getPlaylistIndex() {
        return playlistIndex;
    }

    /**
     * Beállítja az aktuálisan játszott lejátszólista elem sorszámát.
     *
     * @param playlistIndex a lejátszólista elem sorszáma
     */
    public void setPlaylistIndex(final int playlistIndex) {
        this.playlistIndex = playlistIndex;
    }

    /**
     * Visszaadja a {@link MediaPlayer}-t.
     *
     * @return a lejátszó
     */
    public MediaPlayer getMp() {
        return mp;
    }

    public int getActualPlaylistSize() {
        return actualPlaylist.size();
    }


}
