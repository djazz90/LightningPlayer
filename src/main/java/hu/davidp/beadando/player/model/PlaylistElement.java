package hu.davidp.beadando.player.model;

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

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Lejátszólista elem osztály. Ez tartalmazza az összes olyan fontos adatot ami
 * a lejátszólistában megjelenik. Tartalmazza egy MP3 fájl számos metaadatát,
 * illetve a {@link hu.davidp.beadando.player.controller.PlayerFX} számára feldolgozható adatokat szolgáltat.
 *
 * @author Pintér Dávid
 */
@XmlRootElement(name = "track")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@Data
public class PlaylistElement {


    /**
     * A {@link hu.davidp.beadando.player.controller.PlayerFX} objektum által lejátszható formátum.
     */
    @Setter(AccessLevel.NONE)
    @XmlTransient
    private Media media;

    /**
     * A betöltött fájl elérési útja.
     */
    @SuppressWarnings("PMD.ImmutableField")
    @Setter(AccessLevel.NONE)
    private String location;

    /**
     * Az zeneszám hossza.
     */
    @SuppressWarnings("PMD")
    @Setter(AccessLevel.NONE)
    private long duration;

    /**
     * Az MP3 fájl bitrátája.
     */
    @SuppressWarnings("PMD")
    @XmlTransient
    @Setter(AccessLevel.NONE)
    private int bitrate;

    /**
     * Az MP3 fájl ID3v1 tag-je.
     */
    @XmlTransient
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private ID3v1 id3v1Tag;
    /**
     * Az MP3 fájl ID3v2 tag-je.
     */
    @XmlTransient
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private ID3v2 id3v2Tag;

    /**
     * A zeneszám előadója.
     */
    @Setter(AccessLevel.NONE)
    @XmlElement(name = "creator")
    private String artist;

    /**
     * A zeneszám címe.
     */
    @Setter(AccessLevel.NONE)
    private String title;

    /**
     * Az album, amelyben a zeneszám megtalálható.
     */
    @Setter(AccessLevel.NONE)
    private String album;

    /**
     * A zeneszám kiadásának éve.
     */
    @XmlTransient
    @Setter(AccessLevel.NONE)
    private String year;

    /**
     * A zeneszám sorszáma.
     */
    @XmlTransient
    @Setter(AccessLevel.NONE)
    private String trackNum;

    /**
     * A zeneszám műfaja.
     */
    @XmlTransient
    @Setter(AccessLevel.NONE)
    private String genre;
    private static final int URI_OFFSET_START = 5;

    /**
     * Lejátszólista konstruktor. Beállítja az összes mezőt annak függvényében,
     * hogy a beolvasott MP3 fájl milyen taggel vagy tagekkel rendelkezik. Ha
     * több taggel is rendelkezik az MP3 fájl, akkor a kapott metaadatok közül
     * azt választja ki, amelyiknek hosszabb a mérete.
     *
     * @param mp3File a megnyitni kívánt {@link Mp3File}
     * @param file    a megnyitni kívánt file
     */
    public PlaylistElement(final Mp3File mp3File, final File file) {

        this.media = new Media(toUnixURI(file.toURI().toString()));
        this.location = toUnixURI(file.toURI().toString());
        this.duration = mp3File.getLengthInMilliseconds();
        this.bitrate = mp3File.getBitrate();

        if (mp3File.hasId3v1Tag()) {
            id3v1Tag = mp3File.getId3v1Tag();
        } else {
            id3v1Tag = null;
        }

        if (mp3File.hasId3v2Tag()) {
            id3v2Tag = mp3File.getId3v2Tag();
        } else {
            id3v2Tag = null;
        }

        if (id3v2Tag != null) {
            this.artist = ifnullToEmpty(id3v2Tag.getArtist());
            this.title = ifnullToEmpty(id3v2Tag.getTitle());
            this.album = ifnullToEmpty(id3v2Tag.getAlbum());
            this.year = ifnullToEmpty(id3v2Tag.getYear());
            this.trackNum = ifnullToEmpty(id3v2Tag.getTrack());
            this.genre = ifnullToEmpty(id3v2Tag.getGenreDescription());

        } else if (id3v1Tag != null) {
            this.artist = ifnullToEmpty(id3v1Tag.getArtist());
            this.title = ifnullToEmpty(id3v1Tag.getTitle());
            this.album = ifnullToEmpty(id3v1Tag.getAlbum());
            this.year = ifnullToEmpty(id3v1Tag.getYear());
            this.trackNum = ifnullToEmpty(id3v1Tag.getTrack());
            this.genre = ifnullToEmpty(id3v1Tag.getGenreDescription());
        }

        if (mp3File.hasId3v1Tag() && mp3File.hasId3v2Tag()) {
            this.artist = getLongerTag(id3v1Tag.getArtist(),
                id3v2Tag.getArtist());
            this.title = getLongerTag(id3v1Tag.getTitle(), id3v2Tag.getTitle());
            this.album = getLongerTag(id3v1Tag.getAlbum(), id3v2Tag.getAlbum());
            this.year = getLongerTag(id3v1Tag.getYear(), id3v2Tag.getYear());
            this.trackNum = getLongerTag(id3v1Tag.getTrack(),
                id3v2Tag.getTrack());
            this.genre = ifnullToEmpty(id3v2Tag.getGenreDescription());

        } else if (!(mp3File.hasId3v1Tag() || mp3File.hasId3v2Tag())) {
            String[] path = file.getAbsolutePath().split("/");
            if (path.length == 1) {
                path = file.getAbsolutePath().split("\\\\");
            }
            this.title = path[path.length - 1];
            this.artist = ifnullToEmpty(this.artist);
            this.album = ifnullToEmpty(this.album);
            this.year = ifnullToEmpty(this.year);
            this.trackNum = ifnullToEmpty(this.trackNum);
            this.genre = ifnullToEmpty(this.genre);
        }
        if (this.title.isEmpty()) {
            String[] path = file.getAbsolutePath().split("/");
            if (path.length == 1) {
                path = file.getAbsolutePath().split("\\\\");
            }
            this.title = path[path.length - 1];
            //this.title = file.getAbsolutePath();
            this.artist = ifnullToEmpty(this.artist);
            this.album = ifnullToEmpty(this.album);
            this.year = ifnullToEmpty(this.year);
            this.trackNum = ifnullToEmpty(this.trackNum);
            this.genre = ifnullToEmpty(this.genre);

        }

    }

    /**
     * A kapott fájl URI-t alakítja át platformfüggetlen URI-ra.
     *
     * @param s az átalakítandó URI String reprezentációja
     * @return az átalakított URI String reprezentációja
     */
    private static String toUnixURI(final String s) {
        StringBuffer sb = new StringBuffer(s);

        sb.insert(URI_OFFSET_START, "//");
        return sb.toString();
    }

    /**
     * A kapott Stringek közül a hosszabbat adja vissza. Meghívja a
     * {@link #ifnullToEmpty(String)} metódust.
     *
     * @param first  első String
     * @param second második String
     * @return a hosszabbat adja vissza
     */
    private static String getLongerTag(final String first, final String second) {
        String firstConverted = ifnullToEmpty(first);
        String secondConverted = ifnullToEmpty(second);
        if (firstConverted.length() > secondConverted.length()) {
            return firstConverted;
        }
        return secondConverted;

    }

    /**
     * Ha az input String értéke null, akkor üres stringet ad vissza. Egyébként
     * visszaadja az eredetit.
     *
     * @param input bemeneti String
     * @return input vagy üres String
     */
    private static String ifnullToEmpty(final String input) {
        if (input == null) {
            return "";
        }
        return input;
    }

    /**
     * Az aktuális lejátszólista elemet újjáépíti úgy, hogy annak fontosabb
     * mezői egy null értéket se tartalmazzanak, illetve beállítja a
     * {@link Media}-t, hogy a Player számára feldolgozható legyen.
     * <p>
     * Lejátszólista beolvasása során használt metódus.
     */
    public void rebuildPlaylistElement() {
        this.media = new Media(this.location);
        this.artist = ifnullToEmpty(this.artist);
        this.album = ifnullToEmpty(this.album);

        media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {

                switch (change.getKey().toString()) {
                    case "year":
                        year = ifnullToEmpty(change.getValueAdded().toString());
                        break;
                    case "genre":
                        this.genre = ifnullToEmpty(change.getValueAdded().toString());
                        break;
                    default:
                        break;
                }
            }
        );

        try {
            Mp3File mp3File = new Mp3File(new File(new URI(this.location).getPath()));
            this.bitrate = mp3File.getBitrate();
        } catch (IOException | UnsupportedTagException | InvalidDataException | URISyntaxException e) {
            e.printStackTrace();
        }

        this.trackNum = ifnullToEmpty(this.trackNum);
    }

    /**
     * Oszlopnevek tárolása a megjelenített táblához.
     *
     * @return az oszlopnevek
     */
    public static String[] getColumnNamesForTable() {

        return new String[]{"Artist", "Title", "Album", "Source"};
    }

    /**
     * Visszaadja az aktuális lejátszólista elem médiáját.
     *
     * @return a {@link Media}
     */
    public Media asMedia() {
        return this.media;
    }
}
