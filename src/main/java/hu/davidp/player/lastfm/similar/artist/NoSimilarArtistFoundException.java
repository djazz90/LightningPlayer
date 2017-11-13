package hu.davidp.player.lastfm.similar.artist;

/**
 * Created by pintyo on 2017.05.06..
 */
public class NoSimilarArtistFoundException extends Exception {
    public NoSimilarArtistFoundException(final String message) {
        super(message);
    }

    public NoSimilarArtistFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
