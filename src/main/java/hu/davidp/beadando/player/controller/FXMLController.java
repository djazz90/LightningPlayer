package hu.davidp.beadando.player.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlayerSettings;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.TooManyFields"})
@Slf4j
public class FXMLController implements Initializable {

    public static final String LOG_DELIMITER = " - ";

    private static Model model;
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

    @Getter
    @FXML
    private Slider volumeSlider;

    @FXML
    private TabPane playListTabPane;

    @FXML
    private Label statusBarLabel;

    private static Duration duration;

    private File lastFolder;

    private static final double MAX_SEEKER_SLIDER_VALUE = 200.0;

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
        seekerSlider.setMax(MAX_SEEKER_SLIDER_VALUE);

        //rendre 0.0-tól 1.0-ig állítom be a csúszka értékeit, mivel a {@link MediaPlayer}
        //hangereje is ilyen tartományban helyezkedik el.
        volumeSlider.setMin(0.0);
        volumeSlider.setMax(1.0);
        volumeSlider.setValue(PlayerSettings.getVolumeLevel());

        volumeSlider.valueProperty().addListener(observable -> {
            if (volumeSlider.isValueChanging()) {
                MediaPlayer mediaPlayer = PlayerFX.getInstance().getMp();
                PlayerSettings.setVolumeLevel(volumeSlider.getValue());
                mediaPlayer.setVolume(PlayerSettings.getVolumeLevel());

            }
        });

        setAvailability();

        seekerSlider.valueProperty().addListener((observable) -> {
            if (seekerSlider.isValueChanging()) {
                MediaPlayer mediaPlayer = PlayerFX.getInstance().getMp();

                double newPosition = seekerSlider.getValue() / MAX_SEEKER_SLIDER_VALUE;
                Duration newDuration = new Duration(newPosition * duration.toMillis());

                mediaPlayer.seek(newDuration);
            }

        });

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

        if (PlayerFX.getState() == PlayerFX.PlayerState.PLAYING) {
            playButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.PAUSE));
        } else {
            playButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.PLAY));
        }

        statusBarLabel.setText(PlayerFX.getState().prettyPrintName());

        if (PlayerFX.getInstance().hasMedia()) {

            PlayerFX.getInstance().getMp().setOnReady(
                () -> {
                    duration = PlayerFX.getInstance().getActualMedia().getDuration();
                    log.info("Player is ready.");

                    String artist = model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist();
                    String title = model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getTitle();
                    PlayerFX.getPlayerStage().setTitle(artist + LOG_DELIMITER + title);
                }
            );

            PlayerFX.getInstance().getMp().currentTimeProperty().addListener(
                observable -> updateSeeker()
            );

        }

    }

    private void updateSeeker() {
        if (seekerSlider != null && PlayerFX.getInstance().hasMedia()) {
            Platform.runLater(() -> {

                Duration currentTime = PlayerFX.getInstance().getMp().getCurrentTime();
                seekerSlider.setDisable(duration.isUnknown());
                if (!seekerSlider.isDisabled()
                    && duration.greaterThan(Duration.ZERO)
                    && !seekerSlider.isValueChanging()) {
                    seekerSlider.setValue((currentTime.toMillis() / duration.toMillis())
                        * MAX_SEEKER_SLIDER_VALUE);

                }

            });
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
        log.info("Prev button clicked.");
        log.info("Actual playlist element:");
        StringBuffer sb = new StringBuffer();
        sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getTitle()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getAlbum());
        log.info(sb.toString());
        setAvailability();
    }

    @FXML
    public void playButtonAction(final ActionEvent e) {
        if (PlayerFX.getState() == PlayerFX.PlayerState.PAUSED ||
            PlayerFX.getState() == PlayerFX.PlayerState.STOPPED) {

            PlayerFX.getInstance().play();
            PlayerFX.getInstance().autonext(model, this);

        } else {

            PlayerFX.getInstance().pause();

        }
        log.info("Play/Pause button clicked");
        log.info("Actual playlist element:");
        StringBuffer sb = new StringBuffer();
        sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getTitle()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getAlbum());
        log.info(sb.toString());
        setAvailability();
    }

    @FXML
    public void nextButtonAction(final ActionEvent e) {
        PlayerFX.getInstance().next(model);
        getPlayListTable().getSelectionModel().select(PlayerFX.getInstance().getActualElementInPlaylist());
        PlayerFX.getInstance().autonext(model, this);
        log.info("Next button clicked.");
        log.info("Actual playlist element:");
        StringBuffer sb = new StringBuffer();
        sb.append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getArtist()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getTitle()).append(LOG_DELIMITER)
            .append(model.getPlaylist().get(PlayerFX.getInstance().getActualElementInPlaylist()).getAlbum());
        log.info(sb.toString());
        setAvailability();
    }

    @FXML
    public void stopButtonAction(final ActionEvent e) {

        PlayerFX.getInstance().stop();
        log.info("Stop button clicked");
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
        log.info("New playlist created");
        tableDoubleClick();
        setAvailability();

    }

    @FXML
    public void fileMenuOpenMP3Action(final ActionEvent e) {
        if ((playListTabPane.getTabs().size()) < (Model.MAX_PLAYLIST_NUM)) {
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

            PlayListMethods.openMp3(openedFiles, model);

            model.setPlaylist(model.getPlaylist());

            playListTable.setItems(model.getPlaylist());

            lastFolder = openedFiles.get(0).getParentFile();
        }
        log.info("sizeof playlist: " + model.getPlaylist().size());
        setAvailability();
    }

    @FXML
    public void fileMenuSavePlistAction(final ActionEvent e) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save playlist files");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files(*.xml)", "*.xml"));

        File savedFile = fc.showSaveDialog(PlayerFX.getPlayerStage());
        if (savedFile != null) {
            PlayListMethods.savePlaylist(savedFile, model);
        }
        log.info("sizeof playlist: " + model.getPlaylist().size());

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
                model = PlayListMethods.openPlayList(openedFile);

                playListTable.setItems(model.getPlaylist());
                PlayerFX.getInstance().setPlaylistSize(model.getPlaylist());
                setAvailability();

                log.info("XML file successfully opened");
            } catch (SAXException ex) {
                log.error("The XML file is not valid");
                log.error("Message: " + ex.getMessage());
            } catch (IOException ex) {
                log.error("File i/o error");

            } catch (JAXBException e1) {
                log.error("Can't process XML file");
                log.error("Message: " + e1.getMessage());
            }

        }
        log.info("sizeof playlist: " + model.getPlaylist().size());

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
    }

    public TableView<PlaylistElement> getPlayListTable() {
        return playListTable;
    }

}
