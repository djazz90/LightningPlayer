package hu.davidp.beadando.player.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

//sajnos kell még a TableHeaderRowhoz (mikor letiltom, hogy felcserélhetők legyenek)
@SuppressWarnings("restriction")
public class FXMLController implements Initializable {

	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger = LoggerFactory.getLogger(FXMLController.class);

	private static Model model;
	private static PlayListMethods plm;

	private TableView<PlaylistElement> playListTable;

	// menü
	@FXML
	private MenuBar menuBar;

	@FXML
	private Menu fileMenu;

	@FXML
	private MenuItem fileMenuNewPlist;

	@FXML
	private MenuItem fileMenuOpenMp3;

	@FXML
	private MenuItem fileMenuSavePlist;

	@FXML
	private MenuItem fileMenuOpenPlist;

	@FXML
	private MenuItem fileMenuClosePlist;

	@FXML
	private MenuItem fileMenuExit;

	@FXML
	private Button prevButton;
	@FXML
	private Button playButton;
	@FXML
	private Button nextButton;
	@FXML
	private Button stopButton;

	@FXML
	private TabPane playListTabPane;

	private ObservableList<PlaylistElement> allItemsInTable;

	private static File lastFolder;

	/*
	 * (non-Javadoc) beállítja a gombok láthatóságát, az a alapértelmezett
	 * megjelenést: nincs playlist, nem kattinthatók a gombok, csak azok a
	 * menüitemek, melyek kezdetben eddig is használhatók voltak.
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setAvailability();
	}

	public void setAvailability() {
		boolean fileMenuNewPlistEnabled = model.getPlaylist() == null;
		fileMenuNewPlist.setDisable(!fileMenuNewPlistEnabled);

		boolean fileMenuSavePlistEnabled = (model.getPlaylist() != null) && (!model.getPlaylist().isEmpty());
		fileMenuSavePlist.setDisable(!fileMenuSavePlistEnabled);

		boolean fileMenuClosePlistEnabled = model.getPlaylist() != null;
		fileMenuClosePlist.setDisable(!fileMenuClosePlistEnabled);

		boolean prevButtonEnabled = (model.getPlaylist() != null)
				&& (model.getPlaylist().size() > 1) && (PlayerFX.getInstance().getActualElementinPlaylist() > 0);
		prevButton.setDisable(!prevButtonEnabled);

		boolean nextButtonEnabled = (model.getPlaylist() != null)
				&& (model.getPlaylist().size() > 1)
				&& (PlayerFX.getInstance().getActualElementinPlaylist() < PlayerFX.getInstance().getActualPlaylistSize()
						- 1);
		nextButton.setDisable(!nextButtonEnabled);

		boolean playButtonEnabled = (model.getPlaylist() != null)
				&& (PlayerFX.getInstance().getActualPlaylistSize() != 0);
		playButton.setDisable(!playButtonEnabled);

		boolean stopButtonEnabled = (model.getPlaylist() != null)
				&& (PlayerFX.getInstance().getActualPlaylistSize() != 0);
		stopButton.setDisable(!stopButtonEnabled);

		if (PlayerFX.getInstance().isPlayButtonSaysPlay()) {
			playButton.setText("Play");
		} else {
			playButton.setText("Pause");

		}
	}

	private void tableDoubleClick() {
		playListTable.setRowFactory(tv -> {
			TableRow<PlaylistElement> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					// PlaylistElement rowData = row.getItem();
					PlayerFX.getInstance().setActualElementinPlaylist(row.getIndex());
					PlayerFX.getInstance().setActualMedia(model.getPlaylist().get(row.getIndex()).asMedia());
					PlayerFX.getInstance().play();
					PlayerFX.getInstance().autonext(model, this);
					setAvailability();

				}
			});
			return row;
		});
	}

	@FXML
	public void prevButtonAction(ActionEvent e) {
		PlayerFX.getInstance().prev(model);
		getPlayListTable().getSelectionModel().select(PlayerFX.getInstance().getActualElementinPlaylist());
		PlayerFX.getInstance().autonext(model, this);
		logger.info("Prev button clicked.");
		logger.info("Actual playlist element:");
		StringBuffer sb = new StringBuffer();
		sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getArtist() + " - ")
				.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getTitle() + " - ")
				.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getAlbum());
		logger.info(sb.toString());
		setAvailability();
	}

	@FXML
	public void playButtonAction(ActionEvent e) {
		if (PlayerFX.getInstance().isPlayButtonSaysPlay()) {

			PlayerFX.getInstance().play();
			PlayerFX.getInstance().autonext(model, this);

		} else {

			PlayerFX.getInstance().pause();

		}
		logger.info("Play/Pause button clicked");
		logger.info("Actual playlist element:");
		StringBuffer sb = new StringBuffer();
		sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getArtist() + " - ")
				.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getTitle() + " - ")
				.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getAlbum());
		logger.info(sb.toString());
		setAvailability();
	}

	@FXML
	public void nextButtonAction(ActionEvent e) {
		PlayerFX.getInstance().next(model);
		getPlayListTable().getSelectionModel().select(PlayerFX.getInstance().getActualElementinPlaylist());
		PlayerFX.getInstance().autonext(model, this);
		logger.info("Next button clicked.");
		logger.info("Actual playlist element:");
		StringBuffer sb = new StringBuffer();
		sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getArtist() + " - ")
				.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getTitle() + " - ")
				.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementinPlaylist()).getAlbum());
		logger.info(sb.toString());
		setAvailability();
	}

	@FXML
	public void stopButtonAction(ActionEvent e) {

		PlayerFX.getInstance().stop();
		logger.info("Stop button clicked");
		setAvailability();
	}

	@FXML
	public void fileMenuNewPlistAction(ActionEvent e) {
		model.setPlaylist(new LinkedList<PlaylistElement>());
		Tab tab = new Tab();
		tab.setText("playlist");
		playListTabPane.getTabs().add(tab);

		playListTable = new TableView<PlaylistElement>();
		List<TableColumn<PlaylistElement, String>> colNames = new ArrayList<>();
		for (String string : PlaylistElement.getColumnNamesForTable()) {
			TableColumn<PlaylistElement, String> cell = new TableColumn<PlaylistElement, String>(string);
			cell.setCellValueFactory(new PropertyValueFactory<>(string));
			// itt állítom le az oszlop érték szerinti rendezését
			cell.setSortable(false);
			colNames.add(cell);
		}
		playListTable.getColumns().addAll(colNames);
		playListTable.autosize();
		// megoldja, hogy ne leessen a táblázatban az oszlopokat felcserélni
		// hozzáad egy listenert, ami figyeli hogy változtatták -e a táblázatot
		playListTable.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
				// megkeresi a fejlécet
				TableHeaderRow header = (TableHeaderRow) playListTable.lookup("TableHeaderRow");
				// ha meglett a fejléc annak az újrarendezhetőségét akadályozza
				// meg
				header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						header.setReordering(false);
					}
				});
			}

		});

		playListTabPane.getTabs().get(0).setContent(playListTable);
		allItemsInTable = FXCollections.observableArrayList();
		logger.info("New playlist created");
		tableDoubleClick();
		setAvailability();

	}

	@FXML
	public void fileMenuOpenMP3Action(ActionEvent e) {
		if (model.getPlaylist() == null) {
			fileMenuNewPlistAction(e);
		}

		FileChooser fc = new FileChooser();
		fc.setTitle("Open MP3 files");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3 files(*.mp3)", "*.mp3"));
		if (lastFolder != null) {
			fc.setInitialDirectory(lastFolder);
		}

		List<File> openedFiles = fc.showOpenMultipleDialog(PlayerFX.getPlayerStage());

		if (openedFiles != null) {

			allItemsInTable.addAll(plm.openMp3(openedFiles, model));
			playListTable.setItems(allItemsInTable);

			lastFolder = openedFiles.get(0).getParentFile();
		}
		logger.info("size of ObservableList: "+allItemsInTable.size());
		logger.info("sizeof playlist: "+model.getPlaylist().size());
		setAvailability();
	}

	@FXML
	public void fileMenuSavePlistAction(ActionEvent e) {

		FileChooser fc = new FileChooser();
		fc.setTitle("Save playlist files");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml"));

		File savedFile = fc.showSaveDialog(PlayerFX.getPlayerStage());
		if (savedFile != null) {
			plm.savePlaylist(savedFile, model);
		}
		logger.info("size of ObservableList: "+allItemsInTable.size());
		logger.info("sizeof playlist: "+model.getPlaylist().size());

	}

	@FXML
	public void fileMenuOpenPlistAction(ActionEvent e) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open playlist file");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml"));

		File openedFile = fc.showOpenDialog(PlayerFX.getPlayerStage());

		if (openedFile != null) {

			try {
				fileMenuClosePlistAction(e);
				fileMenuNewPlistAction(e);
				model = plm.openPlayList(openedFile);
				for (PlaylistElement ple : model.getPlaylist()) {
					ple.rebuildPlaylistElement();
					allItemsInTable.add(ple);
				}

				playListTable.setItems(allItemsInTable);
				PlayerFX.getInstance().setPlaylistSize(model.getPlaylist());
				setAvailability();

				playListTable.setItems(allItemsInTable);

				logger.info("XML file successfully opened");
			} catch (SAXException ex) {
				logger.error("The XML file is not valid");
				logger.error("Message: " + ex.getMessage());
			} catch (IOException ex) {
				logger.error("File i/o error");

			} catch (JAXBException e1) {
				logger.error("Can't process XML file");
				logger.error("Message: " + e1.getMessage());
			}

		}
		logger.info("size of ObservableList: "+allItemsInTable.size());
		logger.info("sizeof playlist: "+model.getPlaylist().size());

	}

	@FXML
	public void fileMenuClosePlistAction(ActionEvent e) {
		if (PlayerFX.getInstance().getMp() != null) {
			PlayerFX.getInstance().stop();
		}
		allItemsInTable = null;
		PlayerFX.getInstance().setPlaylistSize(allItemsInTable);
		model.setPlaylist(null);
		if (!playListTabPane.getTabs().isEmpty()) {
			playListTabPane.getTabs().remove(0);
		}

		setAvailability();
	}

	@FXML
	public void fileMenuExitAction(ActionEvent e) {
		PlayerFX.getPlayerStage().close();
	}

	public static void setModel(Model model) {
		FXMLController.model = model;
		plm = new PlayListMethods();
	}

	public TableView<PlaylistElement> getPlayListTable() {
		return playListTable;
	}

}
