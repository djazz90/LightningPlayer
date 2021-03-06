package hu.davidp.player.controller;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import hu.davidp.player.model.Model;
import hu.davidp.player.model.PlaylistElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Slf4j
public final class PlayListMethods {

    private PlayListMethods() {
        //privát üres konstruktor, mert ez egy utility class
    }

    public static void openMp3(final List<File> openedFiles, final Model model) {
        ObservableList<PlaylistElement> addedNewPLEs = FXCollections.observableArrayList();
        for (File file : openedFiles) {
            try {
                addedNewPLEs.add(new PlaylistElement(new Mp3File(file), file));

            } catch (UnsupportedTagException | InvalidDataException
                | IOException e1) {
                log.error("File i/o error");
                log.error("at " + file.getAbsolutePath());
                log.error("", e1);
            }

        }
        model.getPlaylist().addAll(addedNewPLEs);
    }

    public static void savePlaylist(final File savedFile, final Model model) {
        try {
            log.info("Saving file: {}", savedFile.getAbsolutePath());
            File file;
            String[] splitter = savedFile.toString().split("\\.");
            if (splitter[splitter.length - 1].equals("xspf")) {
                file = savedFile;
            } else {
                file = new File(savedFile.toString() + ".xspf");
            }
            //a JAXBContext mutatja meg, hogy hol lépjen be a JAXB api.
            //a newInstance metódusnak pedig átadom a model osztályt
            //a context figyeli a bind-okat(kötődéseket - magukat az annotációkat)
            //a Model osztályban(és a PlaylistElement osztályban)
            JAXBContext context = JAXBContext.newInstance(Model.class);
            //marshaller az xml szerializációhoz
            Marshaller m = context.createMarshaller();
            //JAXB_FORMATTED_OUTPUT felelős hogy a sorok indentálva legyenek
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //maga a mentés
            m.marshal(model, file);
            log.info("XSPF file successfully saved");
        } catch (JAXBException e1) {
            log.error("Can't process XSPF file", e1);
        }
    }

    public static Model openPlayList(final File openedFile) throws SAXException, IOException, JAXBException {
        //beállítja a kívánt sémát
        //W3C_XML_SCHEMA_NS_URI mutatja az alapértelmezett xml sémát
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL url = PlayListMethods.class.getResource("/spiff-xsd-schema.xsd");
        Source xmlFile = new StreamSource(openedFile);
        //valiodációhoz szükséges séma példányosítása
        Schema schema = schemaFactory.newSchema(url);
        //majd validátor
        Validator validator = schema.newValidator();
        //majd maga a validálás
        validator.validate(xmlFile);
        //a JAXBContext mutatja meg, hogy hol lépjen be a JAXB api.
        //a newInstance metódusnak pedig átadom a model osztályt
        //a context figyeli a bind-okat(kötődéseket - magukat az annotációkat)
        //a Model osztályban(és a PlaylistElement osztályban)
        JAXBContext context = JAXBContext.newInstance(Model.class);
        //az unmarshaller végzi a betöltést az xml fájlból
        Unmarshaller unmarshaller = context.createUnmarshaller();

        Model preparedModel = (Model) (unmarshaller.unmarshal(openedFile));
        //betöltés után szükséges a nem tranziens objektumok feltöltése
        // melyet az összes betöltött lejátszólista-elemre elvégzünk
        preparedModel.getPlaylist().forEach(PlaylistElement::rebuildPlaylistElement);

        return preparedModel;

    }
}
