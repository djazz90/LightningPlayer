package hu.davidp.beadando.player.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import hu.davidp.beadando.player.controller.command.ActionCommand;
import hu.davidp.beadando.player.controller.command.Command;
import hu.davidp.beadando.player.controller.command.NotEnoughCommandsCreatedException;
import hu.davidp.beadando.player.model.Model;
import hu.davidp.beadando.player.model.PlayerSettings;
import hu.davidp.beadando.player.model.PlaylistElement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//mivel 1 fxml-hez 1 controller tartozik, így a PMD ezen szabályai alól az osztályt fel lehet menteni
@SuppressWarnings({"PMD.TooManyMethods", "PMD.TooManyFields", "PMD.GodClass"})
@Slf4j
public class FXMLController implements Initializable {

    public static final String LOG_DELIMITER = " - ";

    private static final GlyphFont FONT_AWESOME_GLYPH_FONT = GlyphFontRegistry.font("FontAwesome");
    public static final String SIZEOF_PLAYLIST = "sizeof playlist: ";
    public static final String NAVIGATION_STATE_STRING = "Navigation state:";

    private TableView<PlaylistElement> playListTable;

    private ArtistInfoController artistInfoController;

    @FXML
    private BorderPane rootPane;

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
    private MenuItem settingsMenuLoad;

    @FXML
    private MenuItem settingsMenuSave;

    @FXML
    private Button prevButton;
    @FXML
    private Button playButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button stopButton;

    @FXML
    private Button infoButton;

    @FXML
    private Slider seekerSlider;

    @Getter
    @FXML
    private Slider volumeSlider;

    @FXML
    private TabPane playListTabPane;

    @FXML
    private Label statusBarLabel;

    @FXML
    private ToggleButton shuffleButton;

    @FXML
    private ToggleButton repeatButton;

    @FXML
    private ToggleButton repeatOneButton;

    @FXML
    private HBox buttonsHBox;

    private SegmentedButton navigationStateSegmentedButton;

    private Duration duration;

    private File lastFolder;

    private MediaPlayer mediaPlayer;

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
        mediaPlayer = PlayerFX.getInstance().getMp();

        nextButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.STEP_FORWARD));
        prevButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.STEP_BACKWARD));
        stopButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.STOP));
        infoButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.INFO_CIRCLE));

        shuffleButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.RANDOM));
        repeatButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.REFRESH));
        repeatOneButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.REPEAT));

        navigationStateSegmentedButton = new SegmentedButton();
        navigationStateSegmentedButton.getButtons().addAll(shuffleButton, repeatButton, repeatOneButton);
        buttonsHBox.getChildren().add(navigationStateSegmentedButton);

        seekerSlider.setMin(0.0);
        seekerSlider.setMax(MAX_SEEKER_SLIDER_VALUE);

        //rendre 0.0-tól 1.0-ig állítom be a csúszka értékeit, mivel a {@link MediaPlayer}
        //hangereje is ilyen tartományban helyezkedik el.
        volumeSlider.setMin(0.0);
        volumeSlider.setMax(1.0);
        volumeSlider.setValue(PlayerSettings.getVolumeLevel());

        volumeSlider.valueProperty().addListener(observable -> {
            if (volumeSlider.isValueChanging()) {

                PlayerSettings.setVolumeLevel(volumeSlider.getValue());
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(PlayerSettings.getVolumeLevel());
                }

            }
        });

        seekerSlider.valueProperty().addListener(observable -> {
            if (seekerSlider.isValueChanging()) {

                double newPosition = seekerSlider.getValue() / MAX_SEEKER_SLIDER_VALUE;
                Duration newDuration = new Duration(newPosition * duration.toMillis());

                mediaPlayer.seek(newDuration);
            }
        });

        switch (PlayerSettings.getPlaylistAutoSave()) {
            case PLAYLIST_AUTO_SAVE_AND_LOAD:
                openPlaylistFileAndShowOnGui(PlayerSettings.getPlaylistBackupFile());
                break;
            case PLAYLIST_AUTO_SAVE_AND_LOAD_OFF:
            default:
                fileMenuNewPlistAction();
        }



        initializeActionListeners();
        setAvailability();
        loadSettingsToGui();
        PlayerFX.getPlayerStage().setOnCloseRequest(e -> {
            fileMenuExitAction();
        });

        List<PlaylistElement> actualPlaylist = PlayerFX.getInstance().getActualPlaylist();

        if (actualPlaylist == null || actualPlaylist.isEmpty()) {
            fileMenuNewPlistAction();
        }
    }

    private void initializeActionListeners() {

        Map<Node, Command> actionCommandNodeMap = new HashMap<>();
        Map<MenuItem, Command> actionCommandMenuitemMap = new HashMap<>();

        actionCommandMenuitemMap.put(fileMenuNewPlist, new ActionCommand(this::fileMenuNewPlistAction));
        actionCommandMenuitemMap.put(fileMenuOpenMp3, new ActionCommand(this::fileMenuOpenMP3Action));
        actionCommandMenuitemMap.put(fileMenuSavePlist, new ActionCommand(this::fileMenuSavePlistAction));
        actionCommandMenuitemMap.put(fileMenuOpenPlist, new ActionCommand(this::fileMenuOpenPlistAction));
        actionCommandMenuitemMap.put(fileMenuClosePlist, new ActionCommand(this::fileMenuClosePlistAction));
        actionCommandMenuitemMap.put(fileMenuExit, new ActionCommand(this::fileMenuExitAction));
        actionCommandMenuitemMap.put(settingsMenuLoad, new ActionCommand(this::settingsMenuLoadAction));
        actionCommandMenuitemMap.put(settingsMenuSave, new ActionCommand(this::settingsMenuSaveAction));
        actionCommandNodeMap.put(prevButton, new ActionCommand(this::prevButtonAction));
        actionCommandNodeMap.put(playButton, new ActionCommand(this::playButtonAction));
        actionCommandNodeMap.put(nextButton, new ActionCommand(this::nextButtonAction));
        actionCommandNodeMap.put(stopButton, new ActionCommand(this::stopButtonAction));
        actionCommandNodeMap.put(infoButton, new ActionCommand(this::infoButtonAction));
        actionCommandNodeMap.put(shuffleButton, new ActionCommand(this::shuffleButtonAction));
        actionCommandNodeMap.put(repeatButton, new ActionCommand(this::repeatButtonAction));
        actionCommandNodeMap.put(repeatOneButton, new ActionCommand(this::repeatOneButtonAction));

        //a little reminder for the developers to add actions to all buttons or menuitems
        List<Field> allRequiredFields = FieldUtils.getFieldsListWithAnnotation(this.getClass(), FXML.class);
        allRequiredFields = allRequiredFields.stream()
            .filter(e -> {
                Class<?> type = e.getType();
                return type.isAssignableFrom(MenuItem.class)
                    || type.isAssignableFrom(Button.class)
                    || type.isAssignableFrom(ToggleButton.class);
            })
            .collect(Collectors.toList());

        if (allRequiredFields.size()
            != actionCommandNodeMap.size() + actionCommandMenuitemMap.size()) {
            throw new NotEnoughCommandsCreatedException("Not enough commands created for the objects");
        }

        Runnable setAvailabilityRunner = this::setAvailability;
        for (Map.Entry<MenuItem, Command> entry : actionCommandMenuitemMap.entrySet()) {
            Command command = entry.getValue();
            command.addCommand(setAvailabilityRunner);
            entry.getKey().addEventHandler(ActionEvent.ACTION, e -> command.execute());
        }
        for (Map.Entry<Node, Command> entry : actionCommandNodeMap.entrySet()) {
            Command command = entry.getValue();
            command.addCommand(setAvailabilityRunner);
            entry.getKey().addEventHandler(ActionEvent.ACTION, e -> command.execute());
        }
    }

    public void setAvailability() {
        List<PlaylistElement> playlist = PlayerFX.getInstance().getActualPlaylist();

        boolean fileMenuNewPlistEnabled = playListTabPane.getTabs().size() < Model.MAX_PLAYLIST_NUM;
        fileMenuNewPlist.setDisable(!fileMenuNewPlistEnabled);

        boolean fileMenuSavePlistEnabled = playlist != null && !playlist.isEmpty();
        fileMenuSavePlist.setDisable(!fileMenuSavePlistEnabled);

        boolean fileMenuClosePlistEnabled = playlist != null;
        fileMenuClosePlist.setDisable(!fileMenuClosePlistEnabled);

        boolean prevButtonEnabled = playlist != null
            && playlist.size() > 1 && PlayerFX.getInstance().getPlaylistIndex() > 0
            && PlayerFX.getInstance().hasMedia();
        prevButton.setDisable(!prevButtonEnabled);

        boolean nextButtonEnabled = playlist != null
            && playlist.size() > 1
            && (PlayerFX.getInstance().getPlaylistIndex() < playlist.size() - 1
            || PlayerSettings.getNavigationState().equals(PlayerSettings.NavigationState.SHUFFLE)
            || PlayerSettings.getNavigationState().equals(PlayerSettings.NavigationState.REPEAT_PLAYLIST))
            && PlayerFX.getInstance().hasMedia();
        nextButton.setDisable(!nextButtonEnabled);

        boolean playlistIsAvailableAndPlayerHasMedia = playlist != null
            && !playlist.isEmpty() && PlayerFX.getInstance().hasMedia();

        boolean playButtonEnabled = playlistIsAvailableAndPlayerHasMedia;
        playButton.setDisable(!playButtonEnabled);

        boolean stopButtonEnabled = playlistIsAvailableAndPlayerHasMedia;
        stopButton.setDisable(!stopButtonEnabled);

        boolean infoButtonEnabled = playlistIsAvailableAndPlayerHasMedia;
        infoButton.setDisable(!infoButtonEnabled);

        if (PlayerFX.getInstance().getState() == PlayerFX.PlayerState.PLAYING) {
            playButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.PAUSE));
        } else {
            playButton.setGraphic(FONT_AWESOME_GLYPH_FONT.create(FontAwesome.Glyph.PLAY));
        }

        statusBarLabel.setText(PlayerFX.getInstance().getState().prettyPrintName());
        volumeSlider.setValue(PlayerSettings.getVolumeLevel());

        if (PlayerFX.getInstance().hasMedia()) {
            mediaPlayer = PlayerFX.getInstance().getMp();
            mediaPlayer.setOnReady(
                () -> {
                    duration = PlayerFX.getInstance().getActualMedia().getDuration();
                    log.info("Player is ready.");

                    String artist = playlist.get(PlayerFX.getInstance().getPlaylistIndex()).getArtist();
                    String title = playlist.get(PlayerFX.getInstance().getPlaylistIndex()).getTitle();
                    PlayerFX.getPlayerStage().setTitle(artist + LOG_DELIMITER + title);
                }
            );

            mediaPlayer.currentTimeProperty().addListener(
                observable -> updateSeeker()
            );

        } else {
            seekerSlider.setDisable(true);
        }

    }

    private void updateSeeker() {
        if (seekerSlider != null && PlayerFX.getInstance().hasMedia()) {
            Platform.runLater(() -> {

                Duration currentTime = mediaPlayer.getCurrentTime();
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

    private void createDoubleClickTableListener() {
        playListTable.setRowFactory(tv -> {
            TableRow<PlaylistElement> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    PlayerFX.getInstance().setPlaylistIndex(row.getIndex());
                    PlayerFX.getInstance().setActualMedia(PlayerFX.getInstance().getActualPlaylist().get(row.getIndex()).asMedia());
                    PlayerFX.getInstance().play();
                    PlayerFX.getInstance().autonext(this);
                    setAvailability();

                }
            });
            return row;
        });
    }

    public void prevButtonAction() {
        log.info("Prev button clicked.");
        PlayerFX.getInstance().prev();
        getPlayListTable().getSelectionModel().select(PlayerFX.getInstance().getPlaylistIndex());
        PlayerFX.getInstance().autonext(this);
    }

    public void playButtonAction() {
        log.info("Play/Pause button clicked");
        PlayerFX.PlayerState playerState = PlayerFX.getInstance().getState();
        if (playerState == PlayerFX.PlayerState.PAUSED
            || playerState == PlayerFX.PlayerState.STOPPED) {

            PlayerFX.getInstance().play();
            PlayerFX.getInstance().autonext(this);

        } else {

            PlayerFX.getInstance().pause();

        }
    }

    public void nextButtonAction() {
        log.info("Next button clicked.");
        PlayerFX.getInstance().next();
        getPlayListTable().getSelectionModel().select(PlayerFX.getInstance().getPlaylistIndex());
        PlayerFX.getInstance().autonext(this);
    }

    public void stopButtonAction() {
        log.info("Stop button clicked");
        PlayerFX.getInstance().stop();
    }

    public void infoButtonAction() {
        log.info("Info button clicked");
        if (artistInfoController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ArtistInfo.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                log.error("Artistinfocontroller can't be loaded", e);
            }
            this.artistInfoController = loader.getController();
            artistInfoController.initialize(null, null);
        }
        artistInfoController.refreshData();

        if (artistInfoController.getPopOver().isShowing()) {
            artistInfoController.getPopOver().hide();
        } else {
            artistInfoController.getPopOver().show(infoButton);
        }

    }

    public void fileMenuNewPlistAction() {
        PlayerFX.getInstance().setActualPlaylist(FXCollections.observableArrayList());
        Tab tab = new Tab();
        tab.setText("playlist");
        playListTabPane.getTabs().add(tab);

        playListTable = new TableView<>();
        playListTable.setPlaceholder(new Label("Add files to fill your playlist"));
        List<TableColumn<PlaylistElement, String>> colNames = new ArrayList<>();
        for (String string : PlaylistElement.getColumnNamesForTable()) {
            TableColumn<PlaylistElement, String> cell = new TableColumn<>(string);
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
        createDoubleClickTableListener();
    }

    public void fileMenuOpenMP3Action() {
        if ((playListTabPane.getTabs().size()) < (Model.MAX_PLAYLIST_NUM)) {
            fileMenuNewPlistAction();
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Open MP3 files");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3 files(*.mp3)", "*.mp3"));
        if (lastFolder != null) {
            fc.setInitialDirectory(lastFolder);
        }

        List<File> openedFiles = fc.showOpenMultipleDialog(PlayerFX.getPlayerStage());
        if (openedFiles != null) {

            PlayListMethods.openMp3(openedFiles, PlayerFX.getPlayerModel());
            PlayerFX.getInstance().setActualPlaylist(PlayerFX.getPlayerModel().getPlaylist());

            playListTable.setItems(PlayerFX.getInstance().getActualPlaylist());

            lastFolder = openedFiles.get(0).getParentFile();
        }
        log.info(SIZEOF_PLAYLIST + PlayerFX.getInstance().getActualPlaylist().size());
    }

    public void fileMenuSavePlistAction() {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save playlist files");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XSPF files(*.xspf)", "*.xspf"));

        File savedFile = fc.showSaveDialog(PlayerFX.getPlayerStage());
        if (savedFile != null) {
            PlayListMethods.savePlaylist(savedFile, PlayerFX.getPlayerModel());
        }
        log.info(SIZEOF_PLAYLIST + PlayerFX.getPlayerModel().getPlaylist().size());

    }

    public void fileMenuOpenPlistAction() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open playlist file");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XSPF files(*.xspf)", "*.xspf"));

        File openedFile = fc.showOpenDialog(PlayerFX.getPlayerStage());

        openPlaylistFileAndShowOnGui(openedFile);
    }

    private void openPlaylistFileAndShowOnGui(final File file) {

        if (file.exists() && file.canRead()) {
            try {
                fileMenuClosePlistAction();
                fileMenuNewPlistAction();
                PlayerFX.setPlayerModel(PlayListMethods.openPlayList(file));
                PlayerFX.getInstance().setActualPlaylist(PlayerFX.getPlayerModel().getPlaylist());
                playListTable.setItems(PlayerFX.getInstance().getActualPlaylist());

                log.info("XSPF file successfully opened");
            } catch (SAXException ex) {
                log.error("The XSPF file is not valid");
                log.error("Message: " + ex.getMessage());
            } catch (IOException ex) {
                log.error("File i/o error");
            } catch (JAXBException e1) {
                log.error("Can't process XSPF file");
                log.error("Message: " + e1.getMessage());
            }
        } else {
            log.info("The file cannot be read or does not exist! File name:" + file.getName());
        }
        log.info(SIZEOF_PLAYLIST + PlayerFX.getInstance().getActualPlaylist().size());
    }

    public void fileMenuClosePlistAction() {
        if (PlayerFX.getInstance().hasMedia()) {
            PlayerFX.getInstance().stop();
        }

        PlayerFX.getInstance().getActualPlaylist().clear();
        if (!playListTabPane.getTabs().isEmpty()) {
            playListTabPane.getTabs().remove(0);
        }
    }

    public void fileMenuExitAction() {
        log.info("Exiting player...");
        settingsMenuSaveAction();
        Platform.exit();
    }

    public void settingsMenuLoadAction() {
        try {
            PlayerSettings.load();
            loadSettingsToGui();
            if (PlayerFX.getInstance().hasMedia()) {
                PlayerFX.getInstance().getMp().setVolume(PlayerSettings.getVolumeLevel());
            }
            log.info("Settings successfully loaded!");
        } catch (IOException e) {
            log.info("Settings cannot be loaded!", e);
        }
    }

    private void loadSettingsToGui() {
        deSelectNavigationStateButtons();
        switch (PlayerSettings.getNavigationState()) {
            case SHUFFLE:
                shuffleButton.setSelected(true);
                break;
            case REPEAT_PLAYLIST:
                repeatButton.setSelected(true);
                break;
            case REPEAT_SONG:
                repeatOneButton.setSelected(true);
                break;
            case NEXT_SONG:
                deSelectNavigationStateButtons();
                break;
            default:
                throw new UnsupportedOperationException("Unknown NavigationState!");
        }
        volumeSlider.setValue(PlayerSettings.getVolumeLevel());
    }

    public void settingsMenuSaveAction() {
        try {
            PlayerSettings.save();
            log.info("Settings successfully saved!");
            boolean autoSaveEnabled = PlayerSettings.getPlaylistAutoSave()
                .equals(PlayerSettings.PlaylistAutoSave.PLAYLIST_AUTO_SAVE_AND_LOAD);

            if (autoSaveEnabled && PlayerFX.getInstance().getActualPlaylist().size() > 0) {
                PlayListMethods.savePlaylist(PlayerSettings.getPlaylistBackupFile(), PlayerFX.getPlayerModel());
            }
        } catch (IOException e) {
            log.info("Settings cannot be saved!", e);
        }
    }

    public void shuffleButtonAction() {
        if (shuffleButton.isSelected()) {
            PlayerSettings.setNavigationState(PlayerSettings.NavigationState.SHUFFLE);
        }
        setNextSongIfAllUnselected();
        log.info(NAVIGATION_STATE_STRING + LOG_DELIMITER + PlayerSettings.getNavigationState());
    }

    public void repeatButtonAction() {
        if (repeatButton.isSelected()) {
            PlayerSettings.setNavigationState(PlayerSettings.NavigationState.REPEAT_PLAYLIST);
        }
        setNextSongIfAllUnselected();
        log.info(NAVIGATION_STATE_STRING + LOG_DELIMITER + PlayerSettings.getNavigationState());
    }

    public void repeatOneButtonAction() {
        if (repeatOneButton.isSelected()) {
            PlayerSettings.setNavigationState(PlayerSettings.NavigationState.REPEAT_SONG);
        }
        setNextSongIfAllUnselected();
        log.info(NAVIGATION_STATE_STRING + LOG_DELIMITER + PlayerSettings.getNavigationState());
    }

    private void setNextSongIfAllUnselected() {
        //ha egyik sincs kiválasztva, akkor visszaáll next song-ra a
        if (!shuffleButton.isSelected() && !repeatButton.isSelected() && !repeatOneButton.isSelected()) {
            PlayerSettings.setNavigationState(PlayerSettings.NavigationState.NEXT_SONG);
        }
    }

    private void deSelectNavigationStateButtons() {
        Toggle tg = navigationStateSegmentedButton.getToggleGroup().getSelectedToggle();
        if (tg != null) {
            tg.setSelected(false);
        }

    }

    public TableView<PlaylistElement> getPlayListTable() {
        return playListTable;
    }

    public ArtistInfoController getArtistInfoController() {
        return artistInfoController;
    }

    public void setArtistInfoController(final ArtistInfoController artistInfoController) {
        this.artistInfoController = artistInfoController;
    }
}
