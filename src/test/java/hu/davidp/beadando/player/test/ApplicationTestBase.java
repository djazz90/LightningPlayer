package hu.davidp.beadando.player.test;

import static org.junit.Assert.*;
import hu.davidp.beadando.player.controller.ActionListenerForNewPlaylist;
import hu.davidp.beadando.player.controller.ActionListenerForOpenMp3;
import hu.davidp.beadando.player.controller.ActionListenerForOpenPlaylist;
import hu.davidp.beadando.player.controller.ActionListenerForSavePlaylist;
import hu.davidp.beadando.player.controller.Player;
import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import hu.davidp.beadando.player.view.View;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


@RunWith(JavaFxJUnit4ClassRunner.class)
public class ApplicationTestBase {
	private static Model model;
	private static View view;

	private static ActionListenerForNewPlaylist newPlaylist;
	private static ActionListenerForOpenMp3 openMp3;
	private static ActionListenerForOpenPlaylist openPlaylist;
	private static ActionListenerForSavePlaylist savePlaylist;
	private static File[] filesToPlay;
	private static String OS;

	static {

		model = new Model();
		view = new View();
		newPlaylist = new ActionListenerForNewPlaylist(view, model);
		openMp3 = new ActionListenerForOpenMp3(view, model);
		openPlaylist = new ActionListenerForOpenPlaylist(view, model);
		savePlaylist = new ActionListenerForSavePlaylist(view, model);
		filesToPlay = new File[4];
		OS = System.getProperty("os.name");
	}

	
	@Test
	public void playlistElementTest() {
		try {

			File id3v1only = FileUtils.getFile("src", "test", "resources", "id3v1test.mp3");
			File id3v2only = FileUtils.getFile("src", "test", "resources", "id3v2test.mp3");
			File both = FileUtils.getFile("src", "test", "resources", "id3v1+v2test.mp3");
			File none = FileUtils.getFile("src", "test", "resources", "notagtest.mp3");

			PlaylistElement ple = new PlaylistElement(new Mp3File(id3v1only), new File(id3v1only.getAbsolutePath()));
			assertEquals(ple.getAlbum(), "");
			assertFalse(ple.getTitle().isEmpty());
			PlaylistElement ple2 = new PlaylistElement(new Mp3File(id3v2only), new File(id3v2only.getAbsolutePath()));
			assertEquals(ple2.getAlbum(), "");
			assertFalse(ple2.getTitle().isEmpty());
			PlaylistElement ple3 = new PlaylistElement(new Mp3File(both), new File(both.getAbsolutePath()));
			assertEquals(ple3.getAlbum(), "id3v1+v2");
			assertFalse(ple3.getTitle().isEmpty());
			PlaylistElement ple4 = new PlaylistElement(new Mp3File(none), new File(none.getAbsolutePath()));
			assertEquals(ple4.getAlbum(), "");
			assertFalse(ple4.getTitle().isEmpty());

		} catch (UnsupportedTagException | InvalidDataException | IOException | NullPointerException e) {

			fail();
		}

	}

	@Test
	public void playerTest() {

		PlaylistElement ple;
		try {
			File id3v1only = FileUtils.getFile("src", "test", "resources", "id3v1test.mp3");
			ple = new PlaylistElement(new Mp3File(id3v1only), new File(id3v1only.getAbsolutePath()));
			Player.getInstance().setActualMedia(ple.asMedia());
			Player.getInstance().getMp().setMute(true);
			Player.getInstance().play();
			Player.getInstance().pause();
			Player.getInstance().stop();
		} catch (UnsupportedTagException e) {

			e.printStackTrace();
			fail();
		} catch (InvalidDataException e) {

			e.printStackTrace();
			fail();
		} catch (IOException e) {

			e.printStackTrace();
			fail();
		}

	}

	
	@Rule
	public TemporaryFolder folder= new TemporaryFolder();
	@Test
	public void playlistTest() throws JAXBException, IOException {
		

		filesToPlay[0] = FileUtils.getFile("src", "test", "resources", "id3v1test.mp3");
		filesToPlay[1] = FileUtils.getFile("src", "test", "resources", "id3v2test.mp3");
		filesToPlay[2] = FileUtils.getFile("src", "test", "resources", "id3v1+v2test.mp3");
		filesToPlay[3] = FileUtils.getFile("src", "test", "resources", "notagtest.mp3");

		newPlaylist.performAction(model);
		assertNotNull(model.getPlaylist());
		openMp3.performAction(model, filesToPlay);
		assertEquals(4, model.getPlaylist().size());
		assertEquals(4, Player.getInstance().getActualPlaylistSize());

		Player.getInstance().setActualMedia(model.getPlaylist().get(0).asMedia());
		Player.getInstance().getMp().setMute(true);
		Player.getInstance().play();
		for (int i = 0; i < 3; i++) {
			assertEquals(i, Player.getInstance().getActualElementinPlaylist());
			Player.getInstance().getMp().setMute(true);
			Player.getInstance().next(model);
		}
		Player.getInstance().stop();

		for (int i = 3; i > 0; i--) {
			assertEquals(i, Player.getInstance().getActualElementinPlaylist());
			Player.getInstance().getMp().setMute(true);
			Player.getInstance().prev(model);
		}
		Player.getInstance().stop();
		
		
		File f = folder.newFile("saved.xml");
		
		savePlaylist.performAction(model, f);
		
		
		
		
	}

	@Test
	public void openPlaylistTester() {
		if (OS.startsWith("Windows")) {

			try {
				Model m2 = new Model();
				newPlaylist.performAction(m2);
				File f = FileUtils.getFile("src", "test", "resources", "exampleWindowsPlaylist.xml");
				openPlaylist.performAction(m2, f);
			} catch (SAXException e) {

				e.printStackTrace();
				fail();
			} catch (IOException e) {

				e.printStackTrace();
				
			} catch (JAXBException e) {

				e.printStackTrace();
				fail();
			}
		} else {
			try {
				Model m2 = new Model();
				newPlaylist.performAction(m2);
				File f = FileUtils.getFile("src", "test", "resources", "exampleUnixPlaylist.xml");
				openPlaylist.performAction(m2, f);
			} catch (SAXException e) {

				e.printStackTrace();
				fail();
			} catch (IOException e) {

				e.printStackTrace();
				
			} catch (JAXBException e) {

				e.printStackTrace();
				fail();
			}
		}
	}

	@Test(expected = SAXException.class)
	public void openPlaylistTesterForFail() throws SAXException, IOException, JAXBException {
		if (OS.startsWith("Windows")) {

			Model m2 = new Model();
			newPlaylist.performAction(m2);
			File f = FileUtils.getFile("src", "test", "resources", "examplemalformedWindowsPlaylist.xml");
			openPlaylist.performAction(m2, f);

		} else {
			Model m2 = new Model();
			newPlaylist.performAction(m2);
			File f = FileUtils.getFile("src", "test", "resources", "examplemalformedUnixPlaylist.xml");
			openPlaylist.performAction(m2, f);
		}
	}
	
	
}