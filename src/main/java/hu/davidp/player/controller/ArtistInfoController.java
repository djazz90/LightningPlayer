package hu.davidp.player.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import hu.davidp.player.lastfm.similar.artist.Artist;
import hu.davidp.player.lastfm.similar.artist.NoSimilarArtistFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.PopOver;

import javax.xml.bind.JAXBException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.controlsfx.control.PopOver.ArrowLocation.TOP_CENTER;

/**
 * Created by pintyo on 2017.06.05..
 */
@Slf4j
public class ArtistInfoController implements Initializable {

    @FXML
    private Label currentArtistLabel;

    @FXML
    private GridPane gridPane;

    private PopOver popOver;
    private static Scene scene;

    private TableView<Artist> similarArtistsTableView;
    private ObservableList<Artist> similarArtistList;

    private static final String[] COL_NAMES = {"Name"};

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        popOver = new PopOver();

        popOver.setTitle("Artist info");
        popOver.setDetachable(false);
        popOver.setAnimated(true);
        popOver.setArrowLocation(TOP_CENTER);
        popOver.setContentNode(gridPane);

        similarArtistList = FXCollections.emptyObservableList();

        similarArtistsTableView = new TableView<>();
        List<TableColumn<Artist, String>> colNames = new ArrayList<>();
        for (String string : COL_NAMES) {
            TableColumn<Artist, String> cell = new TableColumn<>(string);
            cell.setCellValueFactory(new PropertyValueFactory<>(string));
            // itt állítom le az oszlop érték szerinti rendezését
            cell.setSortable(false);
            colNames.add(cell);
        }
        similarArtistsTableView.getColumns().addAll(colNames);

        // megoldja, hogy ne lehessen a táblázatban az oszlopokat felcserélni
        // hozzáad egy listenert, ami figyeli hogy változtatták -e a táblázatot
        similarArtistsTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
            // megkeresi a fejlécet
            TableHeaderRow header = (TableHeaderRow) similarArtistsTableView.lookup("TableHeaderRow");
            // ha meglett a fejléc annak az újrarendezhetőségét akadályozza
            // meg
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });

        similarArtistsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        similarArtistsTableView.setPlaceholder(new Label("No similar artists found!"));
        gridPane.getChildren().add(similarArtistsTableView);
    }

    public PopOver getPopOver() {
        return popOver;
    }

    public void refreshData() {

        try {
            List<Artist> similarArtists = ResponseController.getSimilarAtristsNowPlayling();

            similarArtistList = FXCollections.observableList(similarArtists);
            similarArtistsTableView.setItems(similarArtistList);

        } catch (JAXBException | NoSimilarArtistFoundException e) {
            similarArtistList.clear();
            similarArtistsTableView.setItems(similarArtistList);
            log.warn("Artist not found", e);
        }

    }

}
