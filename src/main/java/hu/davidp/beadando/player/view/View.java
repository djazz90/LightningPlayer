package hu.davidp.beadando.player.view;

/*
 * #%L
 * LightningPlayer
 * %%
 * Copyright (C) 2015 Debreceni Egyetem Informatikai kar
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


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.JScrollPane;
@Deprecated
public class View {

	private JFrame frmLightningplayer;
	private JButton btnPlay;
	private JButton btnStop;
	private JButton btnNext;
	private JButton btnPrev;
	private JMenuItem mntmNewPlaylist;
	private JMenuItem mntmOpenMp3File;
	private JMenuItem mntmOpenPlaylist;
	private JMenuItem mntmSavePlaylist;
	private JMenuItem mntmClosePlaylist;
	
	private JTable tablePlaylist;
	private JScrollPane scrollPane;
	private JTabbedPane playlistPanel;
	
	
	
	/**
	 * Launch the application.
	 */
	public void showWidow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// View window = new View();
					View.this.frmLightningplayer.setVisible(true);
					View.this.frmLightningplayer.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public View() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLightningplayer = new JFrame();
		frmLightningplayer.setTitle("LightningPlayer");
		frmLightningplayer.setBounds(100, 100, 1200, 500);
		frmLightningplayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLightningplayer.getContentPane().setLayout(
				new MigLayout("", "[:230px:230px][150px,grow]", "[grow][bottom]"));

		JPanel panel = new JPanel();
		frmLightningplayer.getContentPane().add(panel,
				"cell 0 0,growx,aligny bottom");
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		btnPrev = new JButton("Prev");
		btnPrev.setVerticalAlignment(SwingConstants.TOP);
		btnPrev.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(btnPrev);

		btnPlay = new JButton("Play");
		btnPlay.setVerticalAlignment(SwingConstants.TOP);
		panel.add(btnPlay);

		btnNext = new JButton("Next");
		btnNext.setVerticalAlignment(SwingConstants.TOP);
		btnNext.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnNext);

		playlistPanel = new JTabbedPane();
		playlistPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		frmLightningplayer.getContentPane().add(playlistPanel,
				"cell 1 0 1 2,grow");

		JPanel panel_2 = new JPanel();
		frmLightningplayer.getContentPane().add(panel_2,
				"cell 0 1,alignx center,aligny center");
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		btnStop = new JButton("Stop");
		panel_2.add(btnStop);
		btnStop.setVerticalAlignment(SwingConstants.BOTTOM);

		/*
		 * JComboBox comboBox = new JComboBox(); comboBox.setModel(new
		 * DefaultComboBoxModel(new String[] {"Default", "Repeat playlist",
		 * "Repeat track"})); frmLightningplayer.getContentPane().add(comboBox,
		 * "cell 1 2,growx");
		 */

		JMenuBar menuBar = new JMenuBar();
		frmLightningplayer.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmNewPlaylist = new JMenuItem("New Playlist");
		mnFile.add(mntmNewPlaylist);

		mntmOpenMp3File = new JMenuItem("Open Mp3 File(s)");
		mnFile.add(mntmOpenMp3File);

		mntmOpenPlaylist = new JMenuItem("Open Playlist");
		mnFile.add(mntmOpenPlaylist);

		mntmSavePlaylist = new JMenuItem("Save Playlist");
		mnFile.add(mntmSavePlaylist);
		
		mntmClosePlaylist = new JMenuItem("Close Playlist");
		mnFile.add(mntmClosePlaylist);

		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		setMenuItemsAvailabilityToStart();
	}
	
	public JButton getBtnPlay() {
		return btnPlay;
	}

	public JButton getBtnStop() {
		return btnStop;
	}

	public JButton getBtnNext() {
		return btnNext;
	}

	public JButton getBtnPrev() {
		return btnPrev;
	}
	
	public JTabbedPane getPlaylistPanel() {
		return playlistPanel;
	}

	public JTable getTablePlaylist() {
		return tablePlaylist;
	}

	public void setTablePlaylist(JTable tablePlaylist) {
		this.tablePlaylist = tablePlaylist;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public JMenuItem getMntmNewPlaylist() {
		return mntmNewPlaylist;
	}
	
	
	public JMenuItem getMntmOpenPlaylist() {
		return mntmOpenPlaylist;
	}

	public JMenuItem getMntmSavePlaylist() {
		return mntmSavePlaylist;
	}
	
	
	public JMenuItem getMntmClosePlaylist() {
		return mntmClosePlaylist;
	}

	public void addPlayButtonListener(ActionListener listenForPlayButton) {
		btnPlay.addActionListener(listenForPlayButton);

	}

	public void addStopButtonListener(ActionListener listenForStopButton) {
		btnStop.addActionListener(listenForStopButton);

	}
	public void addNextButtonListener(ActionListener listenForNextButton) {
		btnNext.addActionListener(listenForNextButton);

	}
	public void addPrevButtonListener(ActionListener listenForPrevButton) {
		btnPrev.addActionListener(listenForPrevButton);

	}

	public void addNewPlaylistListener(ActionListener listenForNewPlaylist) {
		mntmNewPlaylist.addActionListener(listenForNewPlaylist);

	}

	public void addOpenMp3FileListener(ActionListener listenForOpenMp3File) {
		mntmOpenMp3File.addActionListener(listenForOpenMp3File);
	}
	public void addOpenPlaylistListener(ActionListener listenForOpenPlaylist) {
		mntmOpenPlaylist.addActionListener(listenForOpenPlaylist);
	}
	public void addSavePlaylistListener(ActionListener listenForSavePlaylist) {
		mntmSavePlaylist.addActionListener(listenForSavePlaylist);
	}
	public void addCloseMp3FileListener(ActionListener listenForClosePlaylist) {
		mntmClosePlaylist.addActionListener(listenForClosePlaylist);
	}
	public JMenuItem getMntmOpenMp3File() {
		return mntmOpenMp3File;
	}

	public void createTableWithSettings() {

		tablePlaylist = new JTable();
		tablePlaylist.getTableHeader().setReorderingAllowed(false);

	}

	public void addScrollpaneToTable() {
		

		scrollPane = new JScrollPane(tablePlaylist);
		playlistPanel.addTab("Playlist", null, scrollPane, null);
		

	}
	
	public void addNewPlaylistMouseListener(
			MouseListener listenForMouseclickonPlaylist) {
		tablePlaylist.addMouseListener(listenForMouseclickonPlaylist);
	}
	public void setMenuItemsAvailabilityToStart(){
		mntmNewPlaylist.setEnabled(true);
		mntmOpenMp3File.setEnabled(false);
		mntmSavePlaylist.setEnabled(false);
		mntmClosePlaylist.setEnabled(false);
		mntmOpenPlaylist.setEnabled(true);
	}
}
