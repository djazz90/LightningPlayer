package hu.davidp.beadando.player.controller;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class FXMLController implements Initializable{
	/*
	 * (non-Javadoc)
	 * beállítja a gombok láthatóságát, az a alapértelmezett megjelenést: nincs playlist, nem kattinthatók a gombok, csak azok
	 * a menüitemek, melyek kezdetben eddig is használhatók voltak.
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	//menü
	@FXML
	private MenuBar menuBar;
	
	@FXML
	private Menu fileMenu;
	
	@FXML
	private MenuItem fileMenuNewPlist;
	
	
	@FXML
	private Button prevButton;
	
	
	
	@FXML
	public void prevButtonAction(ActionEvent e){
		
	}

}
