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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.view.View;

/**
 * Controller osztály.
 * Az alkalmazásban az üzleti logikát valósítja meg. Eseményfigyelőket rendel a gombokhoz, változtatja a View-t,
 * adatokat a Model-ből nyeri.
 * 
 * @author Pintér Dávid
 *
 */
@Deprecated
public class Controller {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(Controller.class);
	/**
	 * A View osztály egy példánya.
	 */
	protected View theView;
	
	/**
	 * A {@link Model} osztály egy példánya.
	 */
	protected Model theModel;

		
	/**
	 * A megjelenített lejátszólista kijelölésének megszorításait tartalmazó objektum.
	 */
	protected static PlaylistTableSelectionModel playlistSelectionModel;
	/**
	 * A megjelenített lejátszólista táblázatának modelje.
	 */
	protected static PlaylistTableModel playlistTableModel;

	/**
	 * Aktuális lejátszási listák száma.
	 */
	protected static int playlistsCounter;

	/**
	 * A Controller konstruktora. Paraméterül kap egy {@link View}-t és egy {@link Model}-t.
	 * 
	 * @param theView A View osztály egy példánya
	 * @param theModel A {@link Model} osztály egy példánya
	 */
	public Controller(View theView, Model theModel) {

		this.theView = theView;
		this.theModel = theModel;

	}

	/**
	 * Eseményfigyelőket rendel a View-ban található gombokhoz és menüelemekhez.
	 */
	public void CreateActionListeners() {
		theView.addPlayButtonListener(new ActionListenerForPlayButton(theView, theModel));
		theView.addStopButtonListener(new ActionListenerForStopButton(theView, theModel));
		theView.addNewPlaylistListener(new ActionListenerForNewPlaylist(theView, theModel));
		theView.addOpenMp3FileListener(new ActionListenerForOpenMp3(theView, theModel));
		theView.addSavePlaylistListener(new ActionListenerForSavePlaylist(theView, theModel));
		theView.addOpenPlaylistListener(new ActionListenerForOpenPlaylist(theView, theModel));
		theView.addCloseMp3FileListener(new ActionListenerForClosePlaylist(theView, theModel));
		logger.info("ActionListeners created");
	}

	/**
	 * Eseményfigyelőket rendel egy megjelenített lejátszólistához.
	 */
	public void createPlaylistListeners() {
		theView.addNewPlaylistMouseListener(new PlaylistMouseListener(theView, theModel, this));
		theView.addNextButtonListener(new ActionListenerForNextButton(theView, theModel));
		theView.addPrevButtonListener(new ActionListenerForPrevButton(theView, theModel));
		logger.info("Playlist listeners created");
	}

	/**
	 * Beállítja a gomok elérhetőségét.
	 */
	public void SetButtonsAvailability() {
		theView.getBtnPlay().setEnabled(Player.getInstance().hasMedia());
		theView.getBtnPlay().setText(Player.getInstance().changePlayButtonText());
		theView.getBtnStop().setEnabled(Player.getInstance().hasMedia());
		theView.getBtnNext().setEnabled(Player.getInstance().hasMedia());
		theView.getBtnPrev().setEnabled(Player.getInstance().hasMedia());

	}

}
