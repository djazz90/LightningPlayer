package hu.davidp.beadando.player.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.stage.FileChooser.ExtensionFilter;

public class FXMLController implements Initializable {

	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger = LoggerFactory.getLogger(FXMLController.class);

	private static Model model;

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
	
	private void setAvailability(){
		boolean prevButtonEnabled = model.getPlaylist() != null;
		prevButton.setDisable(!prevButtonEnabled);
	}
	
	
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
	private Button prevButton;
	@FXML
	private TabPane playListTabPane;

	private ObservableList<PlaylistElement> allItemsInTable;
	
	@FXML
	public void prevButtonAction(ActionEvent e) {
		System.out.println("prev pressed!");
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
			colNames.add(cell);
		}
		playListTable.getColumns().addAll(colNames);
		playListTable.autosize();
		playListTabPane.getTabs().get(0).setContent(playListTable);
		allItemsInTable = FXCollections.observableArrayList();
		logger.info("New playlist created");
		tableDoubleClick();
		setAvailability();
		
		
		//playlistsCounter++;
	}

	@FXML
	public void fileMenuOpenMP3Action(ActionEvent e) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open MP3 files");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3 files(*.mp3)", "*.mp3"));
		List<File> openedFiles = fc.showOpenMultipleDialog(PlayerFX.getPlayerStage());

		if (openedFiles != null) {
			LinkedList<PlaylistElement> addedNewPLEs = new LinkedList<>();
			for (File file : openedFiles) {
				try {
					addedNewPLEs.add(new PlaylistElement(new Mp3File(file), file));

				} catch (UnsupportedTagException | InvalidDataException
						| IOException e1) {
					logger.error("File i/o error");
					logger.error("at " + file.getAbsolutePath());
					e1.printStackTrace();
				}

			}
			model.getPlaylist().addAll(addedNewPLEs);
			model.getPlaylist().stream().forEach(ple -> {
				System.out.println(ple);
			});
			
			
			allItemsInTable.addAll(addedNewPLEs);
			playListTable.setItems(allItemsInTable);
		}

		// if (!(jfc.showOpenDialog(jfc)==JFileChooser.CANCEL_OPTION)){
		// LinkedList<PlaylistElement> selectedFiles = new LinkedList<>();
		//
		// for (File file : jfc.getSelectedFiles()) {
		// try {
		// selectedFiles.add(new PlaylistElement(new Mp3File(file), file));
		//
		// } catch (UnsupportedTagException | InvalidDataException
		// | IOException e1) {
		// logger.error("File i/o error");
		// logger.error("at "+ file.getAbsolutePath());
		// e1.printStackTrace();
		// }
		// }
		// logger.info("MP3 files opened");
		// for (PlaylistElement ple : selectedFiles) {
		//
		// Controller.playlistTableModel.addRow(ple);
		// }
		// super.theModel.getPlaylist().addAll(selectedFiles);
		// Player.getInstance().setPlaylistSize(super.theModel.getPlaylist());
		//
		//
		// this.theView.getMntmSavePlaylist().setEnabled(super.theModel.getPlaylist().size()>0);;
		//
		//
		// }
		// if(Player.getInstance().getActualElementinPlaylist() ==
		// super.theModel.getPlaylist().size()-1){
		// super.theView.getBtnNext().setEnabled(false);
		// }
		// if(Player.getInstance().getActualElementinPlaylist() == 0){
		// super.theView.getBtnPrev().setEnabled(false);
		// }
	}
	private void tableDoubleClick(){
		playListTable.setRowFactory( tv -> {
		    TableRow<PlaylistElement> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	PlaylistElement rowData = row.getItem();
		        	PlayerFX.getInstance().setActualElementinPlaylist(row.getIndex());
					PlayerFX.getInstance().setActualMedia(model.getPlaylist().get(row.getIndex()).asMedia());
					PlayerFX.getInstance().play();
					
					
		            
		        }
		    });
		    return row ;
		});
	}
	
	public static void setModel(Model model) {
		FXMLController.model = model;
	}
}
