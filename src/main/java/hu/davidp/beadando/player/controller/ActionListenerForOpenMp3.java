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
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

/**
 * Eseményfigyelő MP3 fájlok megnyitásához. 
 * 
 * @author Pintér Dávid
 *
 */
public class ActionListenerForOpenMp3 extends Controller implements
		ActionListener {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(ActionListenerForOpenMp3.class); 
	/**
	 * Szülőosztály konstruktorát meghívó konstruktor.
	 * 
	 * @param theView a View osztály egy példánya
	 * @param theModel a Model osztály egy példánya
	 */
	public ActionListenerForOpenMp3(View theView, Model theModel) {
		super(theView, theModel);

	}

	/**
	 * Mikor kattintást észlel, egy megadott XML fáljt beolvas a megadott helyről.
	 * A fájl helyének megadása JFileChooser segítségével történik. Miután a fájl sikeresen betöltődött, megjeleníti.
	 * 
	 * @see javax.swing.JFileChooser
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new FileNameExtensionFilter("Mp3 Files", "mp3"));
		jfc.setMultiSelectionEnabled(true);
		
		if (!(jfc.showOpenDialog(jfc)==JFileChooser.CANCEL_OPTION)){
			LinkedList<PlaylistElement> selectedFiles = new LinkedList<>();

			for (File file : jfc.getSelectedFiles()) {
				try {
					selectedFiles.add(new PlaylistElement(new Mp3File(file), file));

				} catch (UnsupportedTagException | InvalidDataException
						| IOException e1) {
					logger.error("File i/o error");
					logger.error("at "+ file.getAbsolutePath());
					e1.printStackTrace();
				}
			}
			logger.info("MP3 files opened");
			for (PlaylistElement ple : selectedFiles) {

				Controller.playlistTableModel.addRow(ple);
			}
			super.theModel.getPlaylist().addAll(selectedFiles);
			Player.getInstance().setPlaylistSize(super.theModel.getPlaylist());

			
			this.theView.getMntmSavePlaylist().setEnabled(super.theModel.getPlaylist().size()>0);;
			
			
		}
		if(Player.getInstance().getActualElementinPlaylist() == super.theModel.getPlaylist().size()-1){
			super.theView.getBtnNext().setEnabled(false);
		}
		if(Player.getInstance().getActualElementinPlaylist() == 0){
			super.theView.getBtnPrev().setEnabled(false);
		}
		

	}
	/**
	 * Teszteléshez használt metódus.
	 * A Model lejátszólistájához hozzáadja a files fájl tömbben megadott fájlokat.
	 * 
	 * @param m a Model osztály egy példánya
	 * @param files A betölteni kívánt fájl tömb
	 */
	public void performAction(Model m, File[] files){
		
		LinkedList<PlaylistElement> selectedFiles = new LinkedList<>();

		for (File file : files) {
			try {
				selectedFiles.add(new PlaylistElement(new Mp3File(file), file));

			} catch (UnsupportedTagException | InvalidDataException
					| IOException e1) {

				e1.printStackTrace();
			}
		}
		
		m.getPlaylist().addAll(selectedFiles);
		Player.getInstance().setPlaylistSize(m.getPlaylist());
		logger.info("MP3 files opened");
	}

}
