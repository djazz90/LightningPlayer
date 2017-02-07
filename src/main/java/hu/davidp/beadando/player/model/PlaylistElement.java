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

import com.mpatric.mp3agic.*;
import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.IOException;

/**
 * Lejátszólista elem osztály. Ez tartalmazza az összes olyan fontos adatot ami
 * a lejátszólistában megjelenik. Tartalmazza egy MP3 fájl számos metaadatát,
 * illetve a {@link hu.davidp.beadando.player.controller.PlayerFX} számára feldolgozható adatokat szolgáltat.
 *
 * @author Pintér Dávid
 */
@XmlRootElement(name = "track")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlaylistElement {

    // TODO: album artwork
    /**
     * A feldolgozáshoz szükséges {@link Mp3File}.
     */
    @XmlTransient
    private Mp3File mp3File;
    /**
     * A {@link hu.davidp.beadando.player.controller.PlayerFX} objektum által lejátszható formátum.
     */
    @XmlTransient
    private Media media;

    /**
     * A betöltött fájl elérési útja.
     */
    private String location;

    /**
     * Az zeneszám hossza.
     */
    private long duration;

    /**
     * Az MP3 fájl bitrátája.
     */
    @XmlTransient
    private int bitrate;

    /**
     * Az MP3 fájl ID3v1 tag-je.
     */
    @XmlTransient
    private ID3v1 ID3v1tag;
    /**
     * Az MP3 fájl ID3v2 tag-je.
     */
    @XmlTransient
    private ID3v2 ID3v2tag;

    /**
     * A zeneszám előadója.
     */
    @XmlElement(name = "creator")
    private String artist;

    /**
     * A zeneszám címe.
     */
    private String title;

    /**
     * Az album, amelyben a zeneszám megtalálható.
     */
    private String album;

    /**
     * A zeneszám kiadásának éve.
     */
    @XmlTransient
    private String year;

    /**
     * A zeneszám kiadásának éve egészben.
     */
    @XmlTransient
    private Integer yearinInteger;

    /**
     * A zeneszám sorszáma.
     */
    @XmlTransient
    private String trackNum;

    /**
     * A zeneszám sorszáma egészben.
     */
    @XmlTransient
    private Integer tracknoinInteger;

    /**
     * A zeneszám műfaja.
     */
    @XmlTransient
    private String genre;

    /**
     * Üres konstruktor JAXB-hez.
     */
    public PlaylistElement() {
        super();
    }

    /**
     * Lejátszólista konstruktor. Beállítja az összes mezőt annak függvényében,
     * hogy a beolvasott MP3 fájl milyen taggel vagy tagekkel rendelkezik. Ha
     * több taggel is rendelkezik az MP3 fájl, akkor a kapott metaadatok közül
     * azt választja ki, amelyiknek hosszabb a mérete.
     *
     * @param mp3File a megnyitni kívánt {@link Mp3File}
     * @param file    a megnyitni kívánt file
     */
    public PlaylistElement(Mp3File mp3File, File file) {

        this.mp3File = mp3File;
        this.media = new Media(toUnixURI(file.toURI().toString()));
        this.location = mp3File.getFilename();
        this.duration = mp3File.getLengthInMilliseconds();
        this.bitrate = mp3File.getBitrate();
        this.tracknoinInteger = -1;
        this.yearinInteger = -1;

        if (mp3File.hasId3v1Tag()) {
            ID3v1tag = mp3File.getId3v1Tag();
        } else
            ID3v1tag = null;

        if (mp3File.hasId3v2Tag()) {
            ID3v2tag = mp3File.getId3v2Tag();
        } else
            ID3v2tag = null;

        if (ID3v2tag != null) {
            this.artist = ifnullToEmpty(ID3v2tag.getArtist());
            this.title = ifnullToEmpty(ID3v2tag.getTitle());
            this.album = ifnullToEmpty(ID3v2tag.getAlbum());
            this.year = ifnullToEmpty(ID3v2tag.getYear());
            this.trackNum = ifnullToEmpty(ID3v2tag.getTrack());
            try {
                this.yearinInteger = Integer.parseInt(this.year);
                try {
                    this.tracknoinInteger = Integer.parseInt(this.trackNum);
                } catch (NumberFormatException | NullPointerException ex) {
                    // LOG: cant resolve year or track in integer

                }

            } catch (NumberFormatException | NullPointerException ex) {
                // LOG: cant resolve year or track in integer

            }
            this.genre = ifnullToEmpty(ID3v2tag.getGenreDescription());

        } else if (ID3v1tag != null) {
            this.artist = ifnullToEmpty(ID3v1tag.getArtist());
            this.title = ifnullToEmpty(ID3v1tag.getTitle());
            this.album = ifnullToEmpty(ID3v1tag.getAlbum());
            this.year = ifnullToEmpty(ID3v1tag.getYear());
            this.trackNum = ifnullToEmpty(ID3v1tag.getTrack());
            try {
                this.yearinInteger = Integer.parseInt(this.year);
                try {
                    this.tracknoinInteger = Integer.parseInt(this.trackNum);
                } catch (NumberFormatException | NullPointerException ex) {
                    // LOG: cant resolve year or track in integer

                }

            } catch (NumberFormatException | NullPointerException ex) {
                // LOG: cant resolve year or track in integer

            }
            this.genre = ifnullToEmpty(ID3v1tag.getGenreDescription());
        }

        if (mp3File.hasId3v1Tag() && mp3File.hasId3v2Tag()) {
            this.artist = getLongerTag(ID3v1tag.getArtist(),
                ID3v2tag.getArtist());
            this.title = getLongerTag(ID3v1tag.getTitle(), ID3v2tag.getTitle());
            this.album = getLongerTag(ID3v1tag.getAlbum(), ID3v2tag.getAlbum());
            this.year = getLongerTag(ID3v1tag.getYear(), ID3v2tag.getYear());
            this.trackNum = getLongerTag(ID3v1tag.getTrack(),
                ID3v2tag.getTrack());
            try {
                this.yearinInteger = Integer.parseInt(this.year);
                try {
                    this.tracknoinInteger = Integer.parseInt(this.trackNum);
                } catch (NumberFormatException | NullPointerException ex) {
                    // LOG: cant resolve year or track in integer

                }

            } catch (NumberFormatException | NullPointerException ex) {
                // LOG: cant resolve year or track in integer

            }
            this.genre = ifnullToEmpty(ID3v2tag.getGenreDescription());

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
    private static String toUnixURI(String s) {
        StringBuffer sb = new StringBuffer(s);
        sb.insert(5, "//");
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
    private static String getLongerTag(String first, String second) {
        first = ifnullToEmpty(first);
        second = ifnullToEmpty(second);
        if (first.length() > second.length())
            return first;
        return second;

    }

    /**
     * Ha az input String értéke null, akkor üres stringet ad vissza. Egyébként
     * visszaadja az eredetit.
     *
     * @param input bemeneti String
     * @return input vagy üres String
     */
    private static String ifnullToEmpty(String input) {
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
        this.media = new Media(toUnixURI(new File(this.location).toURI().toString()));
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
                }
            }
        );

        try {
            Mp3File mp3File = new Mp3File(new File(this.location));
            this.bitrate = mp3File.getBitrate();
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
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

    /**
     * hashCode metódus.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((album == null) ? 0 : album.hashCode());
        result = prime * result + ((artist == null) ? 0 : artist.hashCode());
        result = prime * result + bitrate;
        result = prime * result + ((genre == null) ? 0 : genre.hashCode());
        result = prime * result + (int) (duration ^ (duration >>> 32));
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((trackNum == null) ? 0 : trackNum.hashCode());
        result = prime
            * result
            + ((tracknoinInteger == null) ? 0 : tracknoinInteger.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        result = prime * result
            + ((yearinInteger == null) ? 0 : yearinInteger.hashCode());
        return result;
    }

    /**
     * equals metódus.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlaylistElement other = (PlaylistElement) obj;
        if (album == null) {
            if (other.album != null)
                return false;
        } else if (!album.equals(other.album))
            return false;
        if (artist == null) {
            if (other.artist != null)
                return false;
        } else if (!artist.equals(other.artist))
            return false;
        if (bitrate != other.bitrate)
            return false;
        if (genre == null) {
            if (other.genre != null)
                return false;
        } else if (!genre.equals(other.genre))
            return false;
        if (duration != other.duration)
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (trackNum == null) {
            if (other.trackNum != null)
                return false;
        } else if (!trackNum.equals(other.trackNum))
            return false;
        if (tracknoinInteger == null) {
            if (other.tracknoinInteger != null)
                return false;
        } else if (!tracknoinInteger.equals(other.tracknoinInteger))
            return false;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.equals(other.year))
            return false;
        if (yearinInteger == null) {
            if (other.yearinInteger != null)
                return false;
        } else if (!yearinInteger.equals(other.yearinInteger))
            return false;
        return true;
    }

    /**
     * toString metódus.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PlaylistElement [location=" + location + ", duration=" + duration
            + ", bitrate=" + bitrate + ", artist=" + artist + ", title="
            + title + ", album=" + album + ", year=" + year
            + ", yearinInteger=" + yearinInteger + ", trackNum=" + trackNum
            + ", tracknoinInteger=" + tracknoinInteger + ", genre=" + genre
            + "]";
    }

    /**
     * Visszaadja a fájl elérési útvonalát.
     *
     * @return a fájl elérési útvonala.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Visszaadja a zeneszám előadóját.
     *
     * @return az előadó
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Visszaadja a zeneszám címét.
     *
     * @return a cím
     */
    public String getTitle() {
        return title;
    }

    /**
     * Visszaadja a zeneszám albumcímét.
     *
     * @return az album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Visszaadja a zeneszám műfaját.
     *
     * @return a műfaj
     */
    public String getGenre() {
        return genre;
    }

}
