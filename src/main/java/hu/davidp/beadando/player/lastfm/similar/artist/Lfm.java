package hu.davidp.beadando.player.lastfm.similar.artist;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "lfm")
@XmlAccessorType(XmlAccessType.FIELD)
public class Lfm {
    private String status;

    @XmlElement(name = "artist")
    private Artist rootArtist;

}
