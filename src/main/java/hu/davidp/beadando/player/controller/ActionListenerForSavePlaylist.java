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
import hu.davidp.beadando.player.view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eseményfigyelő a Save Playlist gombhoz.
 * 
 * @author Pintér Dávid
 *
 */
public class ActionListenerForSavePlaylist extends Controller implements ActionListener {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(ActionListenerForSavePlaylist.class);
	/**
	 * Szülőosztály konstruktorát meghívó konstruktor.
	 * 
	 * @param theView a View osztály egy példánya
	 * @param theModel a Model osztály egy példánya
	 */
	public ActionListenerForSavePlaylist(View theView, Model theModel) {
		super(theView, theModel);

	}
	/**
	 * Mikor kattintást észlel, egy megadott lementi a lejátszólista tartalmát egy XML fáljba.
	 * A fájl helyének megadása JFileChooser segítségével történik. Meghívja a {@link #performAction(Model, File)} metódust.
	 * 
	 * @see javax.swing.JFileChooser
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));

		if (!(jfc.showSaveDialog(jfc) == JFileChooser.CANCEL_OPTION)) {
			try {
				this.performAction(super.theModel, jfc.getSelectedFile());
				logger.info("XML file successfully saved");
			} catch (JAXBException e1) {
				logger.error("Can't process XML file");
				e1.printStackTrace();
			}
		}

	}

	/**
	 * A lejátszólista tartalmának mentése XML fájlba.
	 * 
	 * @param model a Model osztály egy példánya
	 * @param f a menteni kívánt fájl
	 * @throws JAXBException ha hiba lép fel a fájl feldolgozása közben
	 */
	public void performAction(Model model, File f) throws JAXBException {
		File file;
		String[] splitter = f.toString().split("\\.");
		if (splitter[splitter.length - 1].equals("xml")) {
			file = f;
		} else {
			file = new File(f.toString() + ".xml");
		}

		JAXBContext context = JAXBContext.newInstance(Model.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		m.marshal(model, file);

	}
}
