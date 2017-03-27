package hu.davidp.beadando.player.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.TooManyFields"})
public class FXMLController implements Initializable {

    public static final String LOG_DELIMITER = " - ";
    /**
     * Logger objektum naplózáshoz.
     */
    private static Logger logger = LoggerFactory.getLogger(FXMLController.class);

    private static Model model;
    private static PlayListMethods plm;
    private static final GlyphFont FONT_AWESOME_GLYPH_FONT = GlyphFontRegistry.font("FontAwesome");


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
    private Slider seekerSlider;

    @FXML
    private TabPane playListTabPane;


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
    public void initialize(final URL location, final ResourceBundle resources) {
        nextButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.STEP_FORWARD));
        prevButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.STEP_BACKWARD));
        stopButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.STOP));

        seekerSlider.setMin(0.0);

        setAvailability();
    }

    public void setAvailability() {
        boolean fileMenuNewPlistEnabled = playListTabPane.getTabs().size() < model.MAX_PLAYLIST_NUM;
        fileMenuNewPlist.setDisable(!fileMenuNewPlistEnabled);

        boolean fileMenuSavePlistEnabled = model.getPlaylist() != null && !model.getPlaylist().isEmpty();
        fileMenuSavePlist.setDisable(!fileMenuSavePlistEnabled);

        boolean fileMenuClosePlistEnabled = model.getPlaylist() != null;
        fileMenuClosePlist.setDisable(!fileMenuClosePlistEnabled);

        boolean prevButtonEnabled = model.getPlaylist() != null
            && model.getPlaylist().size() > 1 && PlayerFX.getInstance().getActualElementInPlaylist() > 0
            && PlayerFX.getInstance().hasMedia();
        prevButton.setDisable(!prevButtonEnabled);

        boolean nextButtonEnabled = model.getPlaylist() != null
            && model.getPlaylist().size() > 1
            && PlayerFX.getInstance().getActualElementInPlaylist() < PlayerFX.getInstance().getActualPlaylistSize()
            - 1 && PlayerFX.getInstance().hasMedia();
        nextButton.setDisable(!nextButtonEnabled);

        boolean playButtonEnabled = model.getPlaylist() != null
            && PlayerFX.getInstance().getActualPlaylistSize() != 0 && PlayerFX.getInstance().hasMedia();
        playButton.setDisable(!playButtonEnabled);

        boolean stopButtonEnabled = model.getPlaylist() != null
            && PlayerFX.getInstance().getActualPlaylistSize() != 0 && PlayerFX.getInstance().hasMedia();
        stopButton.setDisable(!stopButtonEnabled);

        if (PlayerFX.getInstance().isPlayButtonSaysPlay()) {
            playButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.PLAY));
            logger.debug("playbutton font:" + playButton.getFont());
        } else {
            playButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.PAUSE));

        }
    }

    private void tableDoubleClick() {
        playListTable.setRowFactory(tv -> {
            TableRow<PlaylistElement> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    // PlaylistElement rowData = row.getItem();
                    PlayerFX.getInstance().setActualElementInPlaylist(row.getIndex());
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
    public void prevButtonAction(final ActionEvent e) {
        PlayerFX.getInstance().prev(model);
        getPlayListTable().getSelectionModel().select(PlayerFX.getInstance().getActualElementInPlaylist());
        PlayerFX.getInstance().autonext(model, this);
        logger.info("Prev button clicked.");
        logger.info("Actual playlist element:");
        StringBuffer sb = new StringBuffer();
        sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getTitle()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getAlbum());
        logger.info(sb.toString());
        setAvailability();
    }

    @FXML
    public void playButtonAction(final ActionEvent e) {
        if (PlayerFX.getInstance().isPlayButtonSaysPlay()) {

            PlayerFX.getInstance().play();
            PlayerFX.getInstance().autonext(model, this);

        } else {

            PlayerFX.getInstance().pause();

        }
        logger.info("Play/Pause button clicked");
        logger.info("Actual playlist element:");
        StringBuffer sb = new StringBuffer();
        sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getTitle()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getAlbum());
        logger.info(sb.toString());
        setAvailability();
    }

    @FXML
    public void nextButtonAction(final ActionEvent e) {
        PlayerFX.getInstance().next(model);
        getPlayListTable().getSelectionModel().select(PlayerFX.getInstance().getActualElementInPlaylist());
        PlayerFX.getInstance().autonext(model, this);
        logger.info("Next button clicked.");
        logger.info("Actual playlist element:");
        StringBuffer sb = new StringBuffer();
        sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getTitle()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getAlbum());
        logger.info(sb.toString());
        setAvailability();
    }

    @FXML
    public void stopButtonAction(final ActionEvent e) {

        PlayerFX.getInstance().stop();
        logger.info("Stop button clicked");
        setAvailability();
    }

    @FXML
    public void fileMenuNewPlistAction(final ActionEvent e) {
        model.setPlaylist(FXCollections.observableArrayList());
        Tab tab = new Tab();
        tab.setText("playlist");
        playListTabPane.getTabs().add(tab);

        playListTable = new TableView<>();
        List<TableColumn<PlaylistElement, String>> colNames = new ArrayList<>();
        for (String string : PlaylistElement.getColumnNamesForTable()) {
            TableColumn<PlaylistElement, String> cell = new TableColumn<PlaylistElement, String>(string);
            cell.setCellValueFactory(new PropertyValueFactory<>(string));
            // itt állítom le az oszlop érték szerinti rendezését
            cell.setSortable(false);
            colNames.add(cell);
        }
        playListTable.getColumns().addAll(colNames);

        // megoldja, hogy ne lehessen a táblázatban az oszlopokat felcserélni
        // hozzáad egy listenert, ami figyeli hogy változtatták -e a táblázatot
        playListTable.widthProperty().addListener((source, oldWidth, newWidth) -> {
            // megkeresi a fejlécet
            TableHeaderRow header = (TableHeaderRow) playListTable.lookup("TableHeaderRow");
            // ha meglett a fejléc annak az újrarendezhetőségét akadályozza
            // meg
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });

        playListTabPane.getTabs().get(0).setContent(playListTable);
        logger.info("New playlist created");
        tableDoubleClick();
        setAvailability();

    }

    @FXML
    public void fileMenuOpenMP3Action(final ActionEvent e) {
        if ((playListTabPane.getTabs().size()) < (model.MAX_PLAYLIST_NUM)) {
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

            plm.openMp3(openedFiles, model);

            model.setPlaylist(model.getPlaylist());

            playListTable.setItems(model.getPlaylist());

            lastFolder = openedFiles.get(0).getParentFile();
        }
        logger.info("sizeof playlist: " + model.getPlaylist().size());
        setAvailability();
    }

    @FXML
    public void fileMenuSavePlistAction(final ActionEvent e) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save playlist files");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml"));

        File savedFile = fc.showSaveDialog(PlayerFX.getPlayerStage());
        if (savedFile != null) {
            plm.savePlaylist(savedFile, model);
        }
        logger.info("sizeof playlist: " + model.getPlaylist().size());

    }

    @FXML
    public void fileMenuOpenPlistAction(final ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open playlist file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml"));

        File openedFile = fc.showOpenDialog(PlayerFX.getPlayerStage());

        if (openedFile != null) {

            try {
                fileMenuClosePlistAction(e);
                fileMenuNewPlistAction(e);
                model = plm.openPlayList(openedFile);

                playListTable.setItems(model.getPlaylist());
                PlayerFX.getInstance().setPlaylistSize(model.getPlaylist());
                setAvailability();

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
        logger.info("sizeof playlist: " + model.getPlaylist().size());

    }

    @FXML
    public void fileMenuClosePlistAction(final ActionEvent e) {
        if (PlayerFX.getInstance().getMp() != null) {
            PlayerFX.getInstance().stop();
        }

        model.setPlaylist(null);
        if (!playListTabPane.getTabs().isEmpty()) {
            playListTabPane.getTabs().remove(0);
        }

        setAvailability();
    }

    @FXML
    public void fileMenuExitAction(final ActionEvent e) {
        PlayerFX.getPlayerStage().close();
    }

    public static void setModel(final Model model) {
        FXMLController.model = model;
        plm = new PlayListMethods();
    }

    public TableView<PlaylistElement> getPlayListTable() {
        return playListTable;
    }

}
