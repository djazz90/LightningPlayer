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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Eseményfigyelő az egérkattintásokhoz a lejátszólistában.
 * 
 * @author Pintér Dávid
 *
 */
public class PlaylistMouseListener extends MouseAdapter implements MouseListener {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(PlaylistMouseListener.class);
	/**
	 * A View objektum amin dolgozik.
	 */
	private View view;
	/**
	 * A Model objektum ahonnan az adatokat veszi.
	 */
	private Model model;
	/**
	 * A Controller objektum amivel dolgozik.
	 */
	private Controller c;

	/**Az egérkattintás eseményfigyelőjének konstruktora.
	 * @param view a View objektum amin dolgozik
	 * @param model a {@link Model} objektum ahonnan az adatokat veszi
	 * @param c a {@link Controller} objektum amivel dolgozik
	 */
	public PlaylistMouseListener(View view, Model model, Controller c) {
		this.model = model;
		this.view = view;
		this.c = c;
	}

	/**
	 * Ha dupla kattintást észlel, elindítja azt a lejátszólista elemet, amelyre a mutató aktuálisan pozícionál.
	 * Meghívja továbba a {@link Player#autonext(Model, View)} metódusát. Beállítja a Play, Prev, Next gombok elérhetőségét.
	 * 
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		view.setTablePlaylist((JTable) me.getSource());
		Point p = me.getPoint();
		int row = view.getTablePlaylist().rowAtPoint(p);
		if (me.getClickCount() == 2) {
			Player.getInstance().setActualElementinPlaylist(row);
			Player.getInstance().setActualMedia(model.getPlaylist().get(row).asMedia());
			Player.getInstance().play();
			c.SetButtonsAvailability();
			if (Player.getInstance().getActualElementinPlaylist() == c.theModel.getPlaylist().size() - 1) {
				c.theView.getBtnNext().setEnabled(false);
			}
			if (Player.getInstance().getActualElementinPlaylist() == 0) {
				c.theView.getBtnPrev().setEnabled(false);
			}
			Player.getInstance().autonext(c.theModel, this.view);
			logger.info("PlaylistElement double-clicked");
			logger.info("Actual playlist element:");
			StringBuffer sb = new StringBuffer();
			sb.append(model.getPlaylist().get(Player.getInstance().getActualElementinPlaylist()).getArtist()+" - ")
			.append(model.getPlaylist().get(Player.getInstance().getActualElementinPlaylist()).getTitle()+" - ")
			.append(model.getPlaylist().get(Player.getInstance().getActualElementinPlaylist()).getAlbum());
			logger.info(sb.toString());
		}

		

	}

}
