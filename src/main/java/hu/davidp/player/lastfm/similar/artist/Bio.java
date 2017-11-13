package hu.davidp.player.lastfm.similar.artist;

import lombok.Data;

@Data
public class Bio {
    private String summary;

    private String content;

    private Links links;

    private String published;

}
