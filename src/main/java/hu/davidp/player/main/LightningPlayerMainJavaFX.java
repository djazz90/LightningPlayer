package hu.davidp.player.main;

/*
 * #%L
 * LightningPlayer
 * %%
 * Copyright (C) 2015 Debreceni Egyetem Informatikai Kar
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

import hu.davidp.player.controller.PlayerFX;
import hu.davidp.player.model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * A program fő belépési pontja.
 * <p>
 * Ez az osztály példányosítja a {@link Model}, a View és a
 * osztályat.
 *
 * @author Pintér Dávid
 */
@Slf4j
public class LightningPlayerMainJavaFX extends Application {

    /**
     * A program fő belépési pontja.
     *
     * @param args paraméterek
     * @throws Exception ha valami nem várt dolog történik az alkalmazás futtatása
     *                   során
     */
    public static void main(final String... args) throws Exception {

        launch(args);
    }

    /*
     * A JavaFX alkalmazások elengedhetetlen metódusa.
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        log.info("LightningPlayer started");

        Model theModel = new Model();

        PlayerFX.setPlayerModel(theModel);
        PlayerFX.setPlayerStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("/JavaFXView.fxml"));

        Scene scene = new Scene(root);

        PlayerFX.setPlayerScene(scene);

        PlayerFX.getInstance();

        primaryStage.setScene(scene);
        primaryStage.show();



        log.info("Application window created");

    }

}
