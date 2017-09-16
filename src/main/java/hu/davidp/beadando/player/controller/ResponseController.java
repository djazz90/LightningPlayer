package hu.davidp.beadando.player.controller;

import hu.davidp.beadando.player.lastfm.similar.artist.Artist;
import hu.davidp.beadando.player.lastfm.similar.artist.Lfm;
import hu.davidp.beadando.player.lastfm.similar.artist.NoSimilarArtistFoundException;
import hu.davidp.beadando.player.model.PlayerSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pintyo on 2017.04.30..
 */
@Slf4j
public final class ResponseController {

    private static final String AUDIOSCROBBLER_PROTOCOL_NAME = "http";
    private static final String AUDIOSCROBBLER_HOST_NAME = "ws.audioscrobbler.com";
    private static final String AUDIOSCROBBLER_VERSION_PATH = "/2.0/";

    private static final String METHOD_PARAM_NAME = "method";
    private static final String GET_INFO_METHOD_NAME = "artist.getinfo";
    private static final String ARTIST_PARAMETER_NAME = "artist";
    private static final String API_KEY_PARAMETER_NAME = "api_key";
    private static final String LIMIT_PARAMETER_NAME = "limit";
    private static final String LIMIT_PARAMETER_VALUE = "5";

    private static Artist cachedArtist;

    private ResponseController() {
    }

    public static List<Artist> getSimilarAtristsByName(final String artistName) throws JAXBException, NoSimilarArtistFoundException {
        synchronized (ResponseController.class) {
            Lfm response;
            if (cachedArtist == null || !artistName.equals(cachedArtist.getName())) {
                try {
                    //building full URI
                    List<NameValuePair> parameterList = new ArrayList<>();
                    parameterList.add(new BasicNameValuePair(METHOD_PARAM_NAME, GET_INFO_METHOD_NAME));
                    parameterList.add(new BasicNameValuePair(ARTIST_PARAMETER_NAME, artistName));
                    parameterList.add(new BasicNameValuePair(API_KEY_PARAMETER_NAME, PlayerSettings.getApiKey()));
                    parameterList.add(new BasicNameValuePair(LIMIT_PARAMETER_NAME, LIMIT_PARAMETER_VALUE));

                    URIBuilder uriBuilder = new URIBuilder();
                    uriBuilder.setScheme(AUDIOSCROBBLER_PROTOCOL_NAME)
                        .setHost(AUDIOSCROBBLER_HOST_NAME)
                        .setPath(AUDIOSCROBBLER_VERSION_PATH)
                        .setParameters(parameterList)
                        .build();

                    log.info("full calling url: {}", uriBuilder.toString());
                    JAXBContext context = JAXBContext.newInstance(Lfm.class);
                    //az unmarshaller végzi a betöltést az xml fájlból
                    Unmarshaller unmarshaller = context.createUnmarshaller();

                    response = (Lfm) (unmarshaller.unmarshal(new URL(uriBuilder.toString())));
                } catch (Exception e) {
                    //ha nem sikerült találni info-t, akkor a cachet üríteni kell
                    cachedArtist = null;
                    throw new NoSimilarArtistFoundException("No similar artist found for the artist: " + artistName, e);
                }

                log.info(response.getRootArtist().toString());
                cachedArtist = response.getRootArtist();


            }

            List<Artist> returned = cachedArtist.getSimilarArtists();

            if (returned == null || returned.isEmpty()) {
                throw new NoSimilarArtistFoundException("No similar artist found for the artist: " + artistName);
            }

            return returned;
        }
    }

    public static List<Artist> getSimilarAtristsNowPlayling() throws JAXBException, NoSimilarArtistFoundException {
        String currentArtist = PlayerFX.getInstance().getActualPlaylistElement().getArtist();
        return getSimilarAtristsByName(currentArtist);
    }
}
