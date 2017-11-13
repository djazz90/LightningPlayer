package hu.davidp.player.lastfm.similar.artist;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Image {
    @XmlValue
    private String content;

    @XmlAttribute
    private String size;
}
