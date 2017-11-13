package hu.davidp.player.lastfm.similar.artist;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Artist {
    private String name;

    @XmlElement(name = "image")
    private List<Image> images;

    private String url;

    @XmlElementWrapper(name = "similar")
    @XmlElement(name = "artist")
    private List<Artist> similarArtists;

}
