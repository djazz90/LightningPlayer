package hu.davidp.beadando.player.controller;

import hu.davidp.beadando.player.lastfm.similar.artist.Artist;
import hu.davidp.beadando.player.lastfm.similar.artist.Lfm;
import hu.davidp.beadando.player.lastfm.similar.artist.NoSimilarArtistFoundException;
import hu.davidp.beadando.player.model.PlayerSettings;
import lombok.extern.java.Log;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pintyo on 2017.04.30..
 */
@Log
public final class ResponseController {

    private static final String AUDIOSCROBBLER_URL = "http://ws.audioscrobbler.com/2.0/";
    private static final String GET_INFO_METHOD_NAME = "?method=artist.getinfo";
    private static final String ARTIST_PARAMETER_PREFIX = "&artist=";
    private static final String API_KEY_PARAMETER_PREFIX = "&api_key=";

    private ResponseController() {
    }

    public static List<Artist> getSimilarAtristsByName(final String artistName) throws JAXBException, NoSimilarArtistFoundException {

        URL url = null;
        Lfm response = null;

        try {
            StringBuffer urlStringBuffer = new StringBuffer();
            urlStringBuffer.append(AUDIOSCROBBLER_URL)
                .append(GET_INFO_METHOD_NAME)
                .append(ARTIST_PARAMETER_PREFIX).append(artistName)
                .append(API_KEY_PARAMETER_PREFIX).append(PlayerSettings.getApiKey());
            url = new URL(urlStringBuffer.toString());

            JAXBContext context = JAXBContext.newInstance(Lfm.class);
            //az unmarshaller végzi a betöltést az xml fájlból
            Unmarshaller unmarshaller = context.createUnmarshaller();

            response = (Lfm) (unmarshaller.unmarshal(url));
        } catch (Exception e) {
            throw new NoSimilarArtistFoundException("No similar artist found for the artist: " + artistName, e);
        }

        log.info(response.getRootArtist().toString());
        log.info(response.getRootArtist().getSimilarArtists().toString());
        List<Artist> returned = response.getRootArtist().getSimilarArtists();

        if (returned == null) {
            returned = new ArrayList<>();
        }

        return returned;
    }
}
