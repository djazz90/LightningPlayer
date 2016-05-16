package hu.davidp.beadando.player.test;

import static org.junit.Assert.*;

import hu.davidp.beadando.player.controller.PlayListMethods;
import hu.davidp.beadando.player.controller.PlayerFX;
import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

	// private static ActionListenerForNewPlaylist newPlaylist;
	// private static ActionListenerForOpenMp3 openMp3;
	// private static ActionListenerForOpenPlaylist openPlaylist;
	// private static ActionListenerForSavePlaylist savePlaylist;
	private static List<File> filesToPlay;
	private static String OS;
	private static PlayListMethods plm;
	
	static {

		model = new Model();
		// newPlaylist = new ActionListenerForNewPlaylist(view, model);
		// openMp3 = new ActionListenerForOpenMp3(view, model);
		// openPlaylist = new ActionListenerForOpenPlaylist(view, model);
		// savePlaylist = new ActionListenerForSavePlaylist(view, model);
		filesToPlay = new LinkedList<>();
		OS = System.getProperty("os.name");
		plm = new PlayListMethods(model);
		

	}

	@Test
	public void playlistElementTest() throws UnsupportedTagException, InvalidDataException, IOException {

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

	}

	@Test
	public void PlayerFXTest() throws UnsupportedTagException, InvalidDataException, IOException {

		PlaylistElement ple;

		File id3v1only = FileUtils.getFile("src", "test", "resources", "id3v1test.mp3");
		ple = new PlaylistElement(new Mp3File(id3v1only), new File(id3v1only.getAbsolutePath()));
		PlayerFX.getInstance().setActualMedia(ple.asMedia());
		PlayerFX.getInstance().getMp().setMute(true);
		PlayerFX.getInstance().play();
		PlayerFX.getInstance().pause();
		PlayerFX.getInstance().stop();

	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void playlistTest() throws JAXBException, IOException, SAXException {

		filesToPlay.add(FileUtils.getFile("src", "test", "resources", "id3v1test.mp3"));
		filesToPlay.add(FileUtils.getFile("src", "test", "resources", "id3v2test.mp3"));
		filesToPlay.add(FileUtils.getFile("src", "test", "resources", "id3v1+v2test.mp3"));
		filesToPlay.add(FileUtils.getFile("src", "test", "resources", "notagtest.mp3"));

		model.setPlaylist(new LinkedList<>());
		assertNotNull(model.getPlaylist());
		plm.openMp3(filesToPlay);
		assertEquals(4, model.getPlaylist().size());
		assertEquals(4, PlayerFX.getInstance().getActualPlaylistSize());

		PlayerFX.getInstance().setActualMedia(model.getPlaylist().get(0).asMedia());
		PlayerFX.getInstance().getMp().setMute(true);
		PlayerFX.getInstance().play();
		for (int i = 0; i < 3; i++) {
			assertEquals(i, PlayerFX.getInstance().getActualElementinPlaylist());
			PlayerFX.getInstance().getMp().setMute(true);
			PlayerFX.getInstance().next(model);
		}
		PlayerFX.getInstance().stop();

		for (int i = 3; i > 0; i--) {
			assertEquals(i, PlayerFX.getInstance().getActualElementinPlaylist());
			PlayerFX.getInstance().getMp().setMute(true);
			PlayerFX.getInstance().prev(model);
		}
		PlayerFX.getInstance().stop();

		File f = folder.newFile("saved.xml");

		plm.savePlaylist(f);

		model = plm.openPlayList(f);
		assertNotNull(model.getPlaylist());
		assertEquals(4, model.getPlaylist().size());
		assertEquals(4, PlayerFX.getInstance().getActualPlaylistSize());
	}

	@Test(expected = SAXException.class)
	public void openPlaylistTesterForFail() throws SAXException, IOException, JAXBException {
		if (OS.startsWith("Windows")) {

			model.setPlaylist(new LinkedList<>());
			File f = FileUtils.getFile("src", "test", "resources", "examplemalformedWindowsPlaylist.xml");

			PlayListMethods plm2 = new PlayListMethods(model);
			Model m2 = plm2.openPlayList(f);
			m2.getPlaylist();

		} else {

			model.setPlaylist(new LinkedList<>());
			File f = FileUtils.getFile("src", "test", "resources", "examplemalformedUnixPlaylist.xml");
			PlayListMethods plm2 = new PlayListMethods(model);
			Model m2 = plm2.openPlayList(f);
			m2.getPlaylist();
		}
	}

}