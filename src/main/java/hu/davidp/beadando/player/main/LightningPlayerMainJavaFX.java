package hu.davidp.beadando.player.main;

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

import hu.davidp.beadando.player.controller.FXMLController;
import hu.davidp.beadando.player.controller.PlayerFX;
import hu.davidp.beadando.player.model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A program fő belépési pontja.
 * 
 * Ez az osztály példányosítja a {@link Model}, a View és a {@link Controller}
 * osztályat.
 * 
 * @author Pintér Dávid
 *
 */
public class LightningPlayerMainJavaFX extends Application {
	/**
	 * Logger objektum naplózáshoz.
	 */
	private static Logger logger = LoggerFactory.getLogger(LightningPlayerMainJavaFX.class);

	/**
	 * A program fő belépési pontja.
	 * 
	 * @param args
	 *            paraméterek
	 * @throws Exception
	 *             ha valami nem várt dolog történik az alkalmazás futtatása
	 *             során
	 */
	public static void main(String[] args) throws Exception {

		launch(args);
	}

	/*
	 * A JavaFX alkalmazások elengedhetetlen metódusa.
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		logger.info("LightningPlayer started");

		Model theModel = new Model();
		FXMLController.setModel(theModel);

		Parent root = FXMLLoader.load(getClass().getResource("/hu/davidp/beadando/player/view/JavaFXView.fxml"));

		Scene scene = new Scene(root);

		PlayerFX.setSceneAndStage(scene, primaryStage);
		PlayerFX.getInstance();

		primaryStage.setScene(scene);
		primaryStage.show();
		logger.info("Application window created");

	}

}
