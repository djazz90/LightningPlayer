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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eseményfigyelő a Stop gombhoz.
 * 
 * @author Pintér Dávid
 *
 */
public class ActionListenerForStopButton extends Controller implements ActionListener {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(ActionListenerForStopButton.class);
	/**
	 * Szülőosztály konstruktorát meghívó konstruktor.
	 * 
	 * @param theView a View osztály egy példánya
	 * @param theModel a {@link Model} osztály egy példánya
	 */
	public ActionListenerForStopButton(View theView, Model theModel) {
		super(theView, theModel);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Ha kattintást észlel, leállítja a lejátszást.
	 * A Play gomb szövegét visszaállítja "Play"-re
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			Player.getInstance().stop();
			if (!Player.getInstance().isPlayButtonSaysPlay()) {
				super.theView.getBtnPlay().setText("Play");
			}
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		logger.info("Stop button clicked");

	}

}
