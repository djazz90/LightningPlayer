package hu.davidp.beadando.player.model;

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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A lejátszólista adatszerkezetet reprezentáló osztály.
 * 
 * @author Pintér Dávid
 *
 */
@XmlRootElement(name = "playlist")

@XmlAccessorType(XmlAccessType.FIELD)
public class Model {
	
	/**
	 * A lejátszólista.
	 */
	@XmlElement(name = "playlistelement")
	private List<PlaylistElement> playlist = null;

	
	/**
	 * A lejátszólisták maximális száma a programban.
	 */
	@XmlTransient
	public final int maxPlaylistNum = 1;

	

	/**
	 * Visszaadja a lejátszólistát.
	 * 
	 * @return a lejátszólista
	 */
	public List<PlaylistElement> getPlaylist() {
		return playlist;
	}
	
	/**
	 * Beállítja a lejátszólistát.
	 * 
	 * @param playlist a beállított érték
	 */
	public void setPlaylist(List<PlaylistElement> playlist) {
		this.playlist = playlist;
	}

}
