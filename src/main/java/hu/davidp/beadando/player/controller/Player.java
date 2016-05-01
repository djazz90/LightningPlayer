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

import java.awt.EventQueue;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Player osztály. A program lejátszója, MP3 fájlok lejátszását végzi. Működését
 * a {@link MediaPlayer} objektum segítségével végzi. Lejátszólistát a {@link Model}-ből vesz.
 * 
 * 
 * @author Pintér Dávid
 *
 */
@Deprecated
public class Player {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger= LoggerFactory.getLogger(Player.class);
	/**
	 * {@link MediaPlayer} objektum.
	 */
	private MediaPlayer mp;

	/**
	 * Az aktuális {@link Media} objektum, amit éppen játszik a lejátszó.
	 */
	private Media actualMedia;
	/**
	 * Igaz, ha a Viewban a Play gomb felirata Play.
	 */
	private boolean PlayButtonSaysPlay;

	/**
	 * Igaz, ha a lejátszóban van {@link Media}.
	 */
	private boolean hasMedia;

	/**
	 * Az aktuális lejátszólista mérete, ahonnan a lejátszó játszik.
	 */
	private int actualPlaylistSize;
	/**
	 * Az aktuálisan játszott lejátszólista elem sorszáma.
	 */
	private int actualElementinPlaylist;

	/**
	 * A lejátszó példánya.
	 */
	private static Player instance = null;

	/**
	 * Singleton konstruktor a lejátszó számára.
	 */
	private Player() {

		this.PlayButtonSaysPlay = true;
		this.hasMedia = false;

	}

	/**
	 * Visszaadja az aktuális lejátszó objektumot.
	 * 
	 * @return a lejátszólista objektum
	 */
	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();
		}
		return instance;
	}

	/**
	 * A lejátszó automatikusan a következő lejátszólista elemre lép, ha az
	 * aktuális véget ér.
	 * 
	 * @param m a {@link Model} osztály egy példánya
	 * @param v a View osztály egy példánya
	 */
	public void autonext(Model m, View v) {

		EventQueue.invokeLater(new Runnable() {

			
			@Override
			public void run() {

				try {
					Player.this.mp.setOnEndOfMedia(new Runnable() {

						@Override
						public void run() {

							mp.stop();
							if (actualElementinPlaylist > 0
									&& actualElementinPlaylist < actualPlaylistSize - 1) {
								v.getBtnNext().setEnabled(true);
								v.getBtnPrev().setEnabled(true);
							}
							if (actualElementinPlaylist == 0)
								v.getBtnPrev().setEnabled(false);
							if (actualElementinPlaylist == actualPlaylistSize - 1)
								v.getBtnNext().setEnabled(false);

							if (actualElementinPlaylist != actualPlaylistSize - 1) {
								actualElementinPlaylist++;
								Player.this.mp = new MediaPlayer(m
										.getPlaylist()
										.get(actualElementinPlaylist).asMedia());
								Player.this.PlayButtonSaysPlay = false;
								mp.play();
								v.getTablePlaylist().setRowSelectionInterval(
										getActualElementinPlaylist(),
										getActualElementinPlaylist());
								logger.info("Auto next:");
								logger.info("Actual playlist element:");
								StringBuffer sb = new StringBuffer();
								sb.append(m.getPlaylist().get(Player.getInstance().getActualElementinPlaylist()).getArtist()+" - ")
								.append(m.getPlaylist().get(Player.getInstance().getActualElementinPlaylist()).getTitle()+" - ")
								.append(m.getPlaylist().get(Player.getInstance().getActualElementinPlaylist()).getAlbum());
								logger.info(sb.toString());
								Player.this.autonext(m, v);

							} else {

								mp.stop();
								Player.this.PlayButtonSaysPlay = true;
								v.getBtnPlay().setText(
										Player.this.changePlayButtonText());
							}

						}

					});
				} catch (NullPointerException e) {

				}
			}

		});

	}
	
	/**
	 * Az aktuális lejátszólista elem lejátszása.
	 */
	public void play() {

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				Player.this.PlayButtonSaysPlay = false;

				mp.play();

			}

		});

	}

	/**
	 * A lejátszás szüneteltetése.
	 */
	public void pause() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				Player.this.PlayButtonSaysPlay = true;
				mp.pause();
				

			}

		});
	}
	
	/**
	 * A lejátszás megállítása.
	 */
	public void stop() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				Player.this.PlayButtonSaysPlay = true;
				mp.stop();

			}
		});
	}

	/**
	 * A következő lejátszólista elemre lép.
	 * 
	 * @param m a {@link Model} osztály egy példánya
	 */
	public void next(Model m) {

		mp.stop();
		actualElementinPlaylist++;
		this.mp = new MediaPlayer(m.getPlaylist().get(actualElementinPlaylist)
				.asMedia());

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				Player.this.PlayButtonSaysPlay = false;
				mp.play();

			}
		});
	}

	/**
	 * Az előző lejátszólista elemre lép.
	 * 
	 * @param m a {@link Model} osztály egy példánya
	 */
	public void prev(Model m) {
		mp.stop();
		actualElementinPlaylist--;
		this.mp = new MediaPlayer(m.getPlaylist().get(actualElementinPlaylist)
				.asMedia());

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				Player.this.PlayButtonSaysPlay = false;
				mp.play();
				mp.getOnEndOfMedia();

			}
		});
	}
	
	/**
	 * Visszaadja az aktuálisan játszott {@link Media}-t.
	 * 
	 * @return az aktuális {@link Media}
	 */
	public Media getActualMedia() {
		return actualMedia;
	}
	
	/**
	 * Beállítja az aktuális {@link Media}-t. 
	 * 
	 * @param actualMedia a beállítani kívánt {@link Media}.
	 */
	public void setActualMedia(Media actualMedia) {
		this.actualMedia = actualMedia;
		if (this.hasMedia) {
			mp.stop();
		}
		this.mp = new MediaPlayer(actualMedia);
		this.PlayButtonSaysPlay = false;
		this.hasMedia = true;
	}

	/**
	 * Beállítja a Play gomb megjelenített szövegét.
	 * 
	 * @return a szöveg amire át kell állítani a gomb szövegét
	 */
	public String changePlayButtonText() {
		if (PlayButtonSaysPlay) {
			return "Play";
		}
		return "Pause";
	}

	/**
	 * Visszaadja a {@link #PlayButtonSaysPlay} értékét.
	 * 
	 * @return igazzal tér vissza, ha a Play gomb szövege Play
	 */
	public boolean isPlayButtonSaysPlay() {
		return PlayButtonSaysPlay;
	}
	
	/**
	 * Visszaadja az aktuális lejetszólista méretét.
	 * 
	 * @return az aktuális lejátszólista mérete
	 */
	public int getActualPlaylistSize() {
		return actualPlaylistSize;
	}

	/**
	 * Visszaadja a {@link #hasMedia} értékét.
	 * 
	 * @return igaz, ha van a lejátszónak {@link Media}-ja
	 */
	public boolean hasMedia() {
		return hasMedia;
	}
	
	/**
	 * Beállitja a {@link #hasMedia} értékét.
	 * 
	 * @param hasMedia a {@link #hasMedia} új értéke
	 */
	public void setHasMedia(boolean hasMedia) {
		this.hasMedia = hasMedia;
	}

	/**
	 * Beállítja az aktuális lejátszólista méretét a kívánt méretre. 
	 * 
	 * @param playlist a kívánt méret
	 */
	public void setPlaylistSize(List<PlaylistElement> playlist) {
		actualPlaylistSize = playlist.size();
	}

	/**
	 * Visszaadja az aktuálisan játszott lejátszólista elem sorszámát.
	 * 
	 * @return a lejátszólista elem sorszáma
	 */
	public int getActualElementinPlaylist() {
		return actualElementinPlaylist;
	}

	/**
	 * Beállítja az aktuálisan játszott lejátszólista elem sorszámát.
	 * 
	 * @param actualElementinPlaylist a lejátszólista elem sorszáma
	 */
	public void setActualElementinPlaylist(int actualElementinPlaylist) {
		this.actualElementinPlaylist = actualElementinPlaylist;
	}

	/**Visszaadja a {@link MediaPlayer}-t.
	 * 
	 * @return a lejátszó
	 */
	public MediaPlayer getMp() {
		return mp;
	}

}
