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


import hu.davidp.beadando.player.model.PlaylistElement;

import javax.swing.table.DefaultTableModel;

/**
 * Lejátszólista táblamodel.
 * Lejátszólista elemek hozzáadását végzi a megjelenítendő táblához.
 * 
 * @author Pintér Dávid
 *
 */
public class PlaylistTableModel extends DefaultTableModel {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Beállítja hogy a táblának amelyet megjelenít, kezdetkor hány sora és hány oszlopa legyen.
	 * 
	 * 
	 * @param columnNames az oszlopnevek
	 * @param rowCount a sorok száma
	 */
	public PlaylistTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	/**
	 * Beállítja a cellák szerkeszthetőségét hamisra.
	 * 
	 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * A táblázatban lévő új sorok beszúrásáért felelős.
	 * 
	 * @param ple a beszúrandó lejátszólista elem
	 */
	public void addRow(PlaylistElement ple) {
		Object[] output = new Object[] { ple.getArtist(), ple.getTitle(),
				ple.getAlbum(), ple.getSource() };
		super.addRow(output);
	}

}
