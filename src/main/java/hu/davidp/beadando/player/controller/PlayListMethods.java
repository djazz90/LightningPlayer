package hu.davidp.beadando.player.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;

public class PlayListMethods {

	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger = LoggerFactory.getLogger(PlayListMethods.class);

	private Model model;

	public PlayListMethods(Model model) {
		this.model = model;
	}

	public LinkedList<PlaylistElement> openMp3(List<File> openedFiles) {
		LinkedList<PlaylistElement> addedNewPLEs = new LinkedList<>();
		for (File file : openedFiles) {
			try {
				addedNewPLEs.add(new PlaylistElement(new Mp3File(file), file));

			} catch (UnsupportedTagException | InvalidDataException
					| IOException e1) {
				logger.error("File i/o error");
				logger.error("at " + file.getAbsolutePath());
				e1.printStackTrace();
			}

		}
		model.getPlaylist().addAll(addedNewPLEs);
		PlayerFX.getInstance().setPlaylistSize(model.getPlaylist());
		return addedNewPLEs;
	}

	public void savePlaylist(File savedFile) {
		try {
			File file;
			String[] splitter = savedFile.toString().split("\\.");
			if (splitter[splitter.length - 1].equals("xml")) {
				file = savedFile;
			} else {
				file = new File(savedFile.toString() + ".xml");
			}

			JAXBContext context = JAXBContext.newInstance(Model.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			m.marshal(model, file);
			logger.info("XML file successfully saved");
		} catch (JAXBException e1) {
			logger.error("Can't process XML file");
			e1.printStackTrace();
		}
	}

	public Model openPlayList(File openedFile) throws SAXException, IOException, JAXBException {
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		URL url = getClass().getResource("/playlist.xsd");
		Source xmlFile = new StreamSource(openedFile);
		Schema schema = schemaFactory.newSchema(url);

		Validator validator = schema.newValidator();

		validator.validate(xmlFile);
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (Model) (unmarshaller.unmarshal(openedFile));

	}
}
