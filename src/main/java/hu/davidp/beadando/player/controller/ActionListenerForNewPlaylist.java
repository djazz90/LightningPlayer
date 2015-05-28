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
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eseményfigyelő a New Playlist menüelemhez.
 * 
 * @author Pintér Dávid
 *
 */
public class ActionListenerForNewPlaylist extends Controller implements
		ActionListener {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(ActionListenerForNewPlaylist.class); 
	/**
	 * Szülőosztály konstruktorát meghívó konstruktor.
	 * 
	 * @param theView a View osztály egy példánya
	 * @param theModel a {@link Model} osztály egy példánya
	 */
	public ActionListenerForNewPlaylist(View theView, Model theModel) {
		super(theView, theModel);
	}
	
	/**
	 * Mikor kattintást észlel, új lejátszólistát jelenít meg.
	 * Meghívja a {@link #performAction(Model)} metódust.
	 * 
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.performAction(super.theModel);
		Controller.playlistTableModel = new PlaylistTableModel(
				PlaylistElement.returnColumnNamesForJTable(), 0);
		
		Controller.playlistSelectionModel =new PlaylistTableSelectionModel();
		super.theView.createTableWithSettings();

		super.theView.getTablePlaylist().setModel(Controller.playlistTableModel);
		super.theView.addScrollpaneToTable();
		super.theView.getTablePlaylist().getColumnModel()
				.setSelectionModel(Controller.playlistSelectionModel);
		super.theView.getMntmNewPlaylist().setEnabled(playlistsCounter < super.theModel.maxPlaylistNum);
		super.theView.getMntmOpenPlaylist().setEnabled(playlistsCounter < super.theModel.maxPlaylistNum);
		super.theView.getMntmSavePlaylist().setEnabled(false);
		super.theView.getMntmOpenMp3File().setEnabled(true);
		super.theView.getMntmClosePlaylist().setEnabled(true);
		createPlaylistListeners();
		
	}
	
	/**
	 * Létrehoz egy valódi lejátszólistát a modelben.
	 * 
	 * @param m A Model osztály egy példánya
	 */
	public void performAction(Model m){
		

		m.setPlaylist(new LinkedList<PlaylistElement>());
		logger.info("New playlist created");
		
		playlistsCounter++;
		
		
		
	}

}
