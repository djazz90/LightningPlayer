package hu.davidp.beadando.player.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.sun.javafx.scene.control.skin.TableHeaderRow;

import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
	private MenuItem fileMenuSavePlist;
	
	
	@FXML
	private MenuItem fileMenuOpenPlist;
	
	
	@FXML
	private Button prevButton;
	@FXML
	private TabPane playListTabPane;

	private ObservableList<PlaylistElement> allItemsInTable;
	
	private static File lastFolder;
	
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
			cell.setSortable(false);
			colNames.add(cell);
		}
		playListTable.getColumns().addAll(colNames);
		playListTable.autosize();
		playListTable.widthProperty().addListener(new ChangeListener<Number>()
		{
		    @Override
		    public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
		    {
		        TableHeaderRow header = (TableHeaderRow) playListTable.lookup("TableHeaderRow");
		        header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
		            @Override
		            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
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
		
		
		//playlistsCounter++;
	}

	@FXML
	public void fileMenuOpenMP3Action(ActionEvent e) {
		if (model.getPlaylist() == null) {
			fileMenuNewPlistAction(e);
		}
		FileChooser fc = new FileChooser();
		fc.setTitle("Open MP3 files");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3 files(*.mp3)", "*.mp3"));
		if (lastFolder != null){
			fc.setInitialDirectory(lastFolder);
		}
		
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
			PlayerFX.getInstance().setPlaylistSize(model.getPlaylist());
			
			
			allItemsInTable.addAll(addedNewPLEs);
			playListTable.setItems(allItemsInTable);
			
			lastFolder = openedFiles.get(0).getParentFile();
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
	@FXML
	public void fileMenuSavePlistAction(ActionEvent e){
		
			FileChooser fc = new FileChooser();
			fc.setTitle("Save playlist files");
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml"));
			
			
			File savedFile = fc.showSaveDialog(PlayerFX.getPlayerStage());
			if (savedFile!=null) {
				try {
					File file;
					String[] splitter = savedFile.toString().split("\\.");
					if (splitter[splitter.length - 1].equals("xml")) {
						file = savedFile;
					} else {
						file = new File(savedFile.toString() + ".xml");
					}

					JAXBContext context = JAXBContext.newInstance(Model.class);
					Marshaller m = context.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

					m.marshal(model, file);
					logger.info("XML file successfully saved");
				} catch (JAXBException e1) {
					logger.error("Can't process XML file");
					e1.printStackTrace();
				}
			}


	}
	@FXML
	public void fileMenuOpenPlistAction(ActionEvent e){
		FileChooser fc = new FileChooser();
		fc.setTitle("Open playlist file");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml"));
		
		
		File openedFile = fc.showOpenDialog(PlayerFX.getPlayerStage());

		if (openedFile != null) {

			try {
				fileMenuNewPlistAction(e);
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				URL url = getClass().getResource("/playlist.xsd");
				Source xmlFile = new StreamSource(openedFile);
				Schema schema = schemaFactory.newSchema(url);

				Validator validator = schema.newValidator();

				validator.validate(xmlFile);
				JAXBContext context = JAXBContext.newInstance(Model.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				//model.setPlaylist(new LinkedList<PlaylistElement>());
				model = (Model) (unmarshaller.unmarshal(openedFile));
				
				
				
				for (PlaylistElement ple : model.getPlaylist()) {
					ple.rebuildPlaylistElement();
					allItemsInTable.add(ple);
				}
				
				playListTable.setItems(allItemsInTable);
				//Controller.playlistsCounter++;
				PlayerFX.getInstance().setPlaylistSize(model.getPlaylist());
				//Controller.playlistTableModel = new PlaylistTableModel(
				//		PlaylistElement.getColumnNamesForTable(), 0);

				//Controller.playlistSelectionModel = new PlaylistTableSelectionModel();

//				super.theView.createTableWithSettings();
//
//				super.theView.getTablePlaylist().setModel(
//						Controller.playlistTableModel);
//				super.theView.addScrollpaneToTable();
//				super.theView.getTablePlaylist().getColumnModel()
//						.setSelectionModel(Controller.playlistSelectionModel);
//				
//				super.theView.getMntmNewPlaylist().setEnabled(
//						playlistsCounter < super.theModel.maxPlaylistNum);
//				super.theView.getMntmOpenPlaylist().setEnabled(
//						playlistsCounter < super.theModel.maxPlaylistNum);
//				super.theView.getMntmSavePlaylist().setEnabled(true);
//				super.theView.getMntmOpenMp3File().setEnabled(true);
//				super.theView.getMntmClosePlaylist().setEnabled(true);
//				super.createPlaylistListeners();

				playListTable.setItems(allItemsInTable);
				
				logger.info("XML file successfully opened");
			} catch (SAXException ex) {
				logger.error("The XML file is not valid");
				logger.error("Message: "+ex.getMessage());
			} catch (IOException ex) {
				logger.error("File i/o error");
				
			} catch (JAXBException e1) {
				logger.error("Can't process XML file");
				logger.error("Message: "+e1.getMessage());
			}
			
		}

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
					PlayerFX.getInstance().autonext(model, this);
					
		            
		        }
		    });
		    return row ;
		});
	}
	
	public static void setModel(Model model) {
		FXMLController.model = model;
	}
	
	public TableView<PlaylistElement> getPlayListTable() {
		return playListTable;
	}

}
