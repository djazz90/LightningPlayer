package hu.davidp.beadando.player.controller;

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

import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import hu.davidp.beadando.player.view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Eseményfigyelő lejátszólista betöltéséhez XML fájlból.
 * 
 * @author Pintér Dávid
 *
 */
public class ActionListenerForOpenPlaylist extends Controller implements
		ActionListener {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(ActionListenerForOpenPlaylist.class);
	/**
	 * Szülőosztály konstruktorát meghívó konstruktor.
	 * 
	 * @param theView a View osztály egy példánya
	 * @param theModel a {@link Model} osztály egy példánya
	 */
	public ActionListenerForOpenPlaylist(View theView, Model theModel) {
		super(theView, theModel);
	}

	/**
	 * Mikor kattintást észlel, egy megadott XML fáljt beolvas.
	 * A fájl megadása JFileChooser segítségével történik. Miután a fájl sikeresen betöltődött,
	 * a Model lejátszólistáját feltölti, majd megjeleníti.
	 * Az XML fájl helyességét XSD fájl segítségével ellenőrzi.
	 * 
	 * @see hu.davidp.beadando.player.model.Model
	 * @see javax.swing.JFileChooser
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));

		if (!(jfc.showOpenDialog(jfc) == JFileChooser.CANCEL_OPTION)) {

			try {
				
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				URL url = getClass().getResource("/playlist.xsd");
				Source xmlFile = new StreamSource(jfc.getSelectedFile());
				Schema schema = schemaFactory.newSchema(url);

				Validator validator = schema.newValidator();

				validator.validate(xmlFile);
				JAXBContext context = JAXBContext.newInstance(Model.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				super.theModel.setPlaylist(new LinkedList<PlaylistElement>());
				super.theModel = (Model) (unmarshaller.unmarshal(jfc.getSelectedFile()));
				for (PlaylistElement ple : super.theModel.getPlaylist()) {
					ple.rebuildPlaylistElement();
					
				}
				Controller.playlistsCounter++;
				Player.getInstance().setPlaylistSize(super.theModel.getPlaylist());
				Controller.playlistTableModel = new PlaylistTableModel(
						PlaylistElement.returnColumnNamesForJTable(), 0);

				Controller.playlistSelectionModel = new PlaylistTableSelectionModel();

				super.theView.createTableWithSettings();

				super.theView.getTablePlaylist().setModel(
						Controller.playlistTableModel);
				super.theView.addScrollpaneToTable();
				super.theView.getTablePlaylist().getColumnModel()
						.setSelectionModel(Controller.playlistSelectionModel);
				
				super.theView.getMntmNewPlaylist().setEnabled(
						playlistsCounter < super.theModel.maxPlaylistNum);
				super.theView.getMntmOpenPlaylist().setEnabled(
						playlistsCounter < super.theModel.maxPlaylistNum);
				super.theView.getMntmSavePlaylist().setEnabled(true);
				super.theView.getMntmOpenMp3File().setEnabled(true);
				super.theView.getMntmClosePlaylist().setEnabled(true);
				super.createPlaylistListeners();

				for (PlaylistElement ple : super.theModel.getPlaylist()) {

					Controller.playlistTableModel.addRow(ple);
					
				}
				
				logger.info("XML file successfully opened");
			} catch (SAXException ex) {
				logger.error("The XML file is not valid");
				logger.error("Message: "+ex.getMessage());
			} catch (IOException ex) {
				logger.error("File i/o error");
				
			} catch (JAXBException e1) {
				logger.error("Can't process XML file");
				logger.error("Message: "+e1.getMessage());
			}
			
		}
	}

	/**
	 * Teszteléshez használt metódus, betölti a megadott XML fájlt, helyességét XSD segítségével ellenőrzi.
	 * 
	 * 
	 * @param m a Model egy példánya
	 * @param f a betölteni kívánt fájl
	 * @throws SAXException ha a helyesség ellenőrzése sikertelen
	 * @throws IOException ha a fájl nem elérhető
	 * @throws JAXBException ha hiba lép fel a fájl feldolgozása közben
	 */
	public void performAction(Model m, File f) throws SAXException, IOException,
			JAXBException {
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		URL url = getClass().getResource("/playlist.xsd");
		Source xmlFile = new StreamSource(f);
		Schema schema = schemaFactory.newSchema(url);

		Validator validator = schema.newValidator();

		validator.validate(xmlFile);
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		m.setPlaylist(new LinkedList<PlaylistElement>());
		m = (Model) (unmarshaller.unmarshal(f));
		for (PlaylistElement ple : m.getPlaylist()) {
			ple.rebuildPlaylistElement();
			
		}
		Player.getInstance().setPlaylistSize(m.getPlaylist());
		logger.info("XML file successfully opened");
	}

}
